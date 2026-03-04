package com.example.musroyale

import Match
import Torneo
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DialogTitle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.imageview.ShapeableImageView
// IMPORTANTE: Importa tu biblioteca de carga de imágenes (ej: Glide)
import com.bumptech.glide.Glide

class TournamentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String
    private var torneoId = ""

    private lateinit var bracketContainer: LinearLayout
    private lateinit var participantsContainer: LinearLayout // NUEVO
    private lateinit var btnAction: com.google.android.material.button.MaterialButton
    private lateinit var txtStatus: TextView
    private lateinit var title: TextView
    private var isTimerRunning = false
    private var entradaIniciada = false // Para evitar múltiples entradas automáticas
    private var timerEjecutandose = false // Para que no se duplique el hilo del timer
    private var countdownFinished = false // Para saber si ya pasó la espera inicial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_lobby)

        db = FirebaseFirestore.getInstance()
        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("userRegistrado", "") ?: ""

        bracketContainer = findViewById(R.id.bracketContainer)
        participantsContainer = findViewById(R.id.participantsContainer) // NUEVO
        btnAction = findViewById(R.id.btnAction)
        txtStatus = findViewById(R.id.txtStatus)
        title = findViewById(R.id.txtTournamentTitle)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        buscarTorneoActivo()
    }

    private fun buscarTorneoActivo() {
        db.collection("Tournaments")
            .whereNotEqualTo("estado", "FINALIZADO")
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    torneoId = documents.documents[0].id
                    escucharEstadoDelTorneo()
                }
            }
    }

    private fun escucharEstadoDelTorneo() {
        db.collection("Tournaments").document(torneoId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val torneo = snapshot?.toObject(Torneo::class.java) ?: return@addSnapshotListener

                val ahora = System.currentTimeMillis()
                val esperaMs = 1 * 60 * 1000 // 1 min bloqueo
                val inscripcionMs = 1 * 60 * 1000 // 1 min inscripción (tiempo límite)

                val tiempoHabilitarInscripcion = torneo.createdAt + esperaMs
                val tiempoCierreTorneo = tiempoHabilitarInscripcion + inscripcionMs

                when {
                    // FASE 1: Bloqueo inicial
                    ahora < tiempoHabilitarInscripcion -> {
                        iniciarContadorReal(tiempoHabilitarInscripcion)
                    }

                    // FASE 2: Inscripción abierta con tiempo límite
                    torneo.estado == "INSCRIPCION" -> {
                        isTimerRunning = false
                        actualizarUI(torneo)

                        // Si el tiempo límite ya pasó y el torneo sigue en "INSCRIPCION"
                        // significa que no se llenó, pero el tiempo se agotó.
                        if (ahora >= tiempoCierreTorneo) {
                            iniciarTorneoAutomaticamente()
                        } else if (!timerEjecutandose) {
                            // Iniciamos un timer visual o interno para cerrar el torneo al minuto
                            prepararCierrePorTiempo(tiempoCierreTorneo)
                        }
                    }
                    torneo.estado == "EN_CURSO" -> {
                        timerEjecutandose = false // Limpiamos el timer
                        actualizarUI(torneo)
                    }

                    else -> actualizarUI(torneo)
                }
            }
    }
    private fun prepararCierrePorTiempo(tiempoFinal: Long) {
        timerEjecutandose = true
        val restante = tiempoFinal - System.currentTimeMillis()

        object : android.os.CountDownTimer(restante, 1000) {
            override fun onTick(ms: Long) {
                // Puedes usar un TextView para mostrar "Cierre en: 00:XX"
                txtStatus.text = "Inscripciones cierran en: ${ms / 1000}s"
            }
            override fun onFinish() {
                timerEjecutandose = false
                iniciarTorneoAutomaticamente()
            }
        }.start()
    }
    private fun prepararInicioAutomatico(tiempoFinal: Long) {
        timerEjecutandose = true
        val restante = tiempoFinal - System.currentTimeMillis()

        object : android.os.CountDownTimer(restante, 1000) {
            override fun onTick(ms: Long) {
                // Opcional: mostrar en algún texto cuánto queda para que empiece el torneo
            }
            override fun onFinish() {
                timerEjecutandose = false
                // Solo el primer usuario que llegue aquí disparará la lógica
                iniciarTorneoAutomaticamente()
            }
        }.start()
    }
    private fun iniciarContadorReal(tiempoFinal: Long) {
        if (isTimerRunning) return
        isTimerRunning = true
        btnAction.isEnabled = false

        object : android.os.CountDownTimer(tiempoFinal - System.currentTimeMillis(), 1000) {
            override fun onTick(ms: Long) {
                val min = (ms / 60000)
                val seg = (ms % 60000) / 1000
                btnAction.text = String.format("ESPERA: %02d:%02d", min, seg)
            }

            override fun onFinish() {
                isTimerRunning = false
                btnAction.isEnabled = true
                btnAction.text = "INSCRIBIRSE"
                // No necesitamos llamar a buscarTorneo, el SnapshotListener detectará el paso del tiempo
            }
        }.start()
    }
    private fun actualizarUI(torneo: Torneo) {
        participantsContainer.removeAllViews()
        bracketContainer.removeAllViews()

        // --- 1. DIBUJAR LISTA DE TODOS LOS PARTICIPANTES ---
        for (userId in torneo.inscritos) {
            val participantView = LayoutInflater.from(this).inflate(R.layout.item_tournament_player, null)
            val avatarImg = participantView.findViewById<ShapeableImageView>(R.id.avatarParticipant)
            val nameTxt = participantView.findViewById<TextView>(R.id.nameParticipant)

            db.collection("Users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        val userName = doc.getString("username") ?: "Desconocido"
                        val avatarFilename = doc.getString("avatarActual") ?: "avadef"
                        nameTxt.text = userName

                        // Cargar imagen local
                        val drawableId = when (avatarFilename.replace(".png", "")) {
                            "avadef" -> R.drawable.avadef
                            else -> R.drawable.avadef
                        }
                        Glide.with(this).load(drawableId).circleCrop().into(avatarImg)
                    }
                }
            participantsContainer.addView(participantView)
        }

        // --- 2. LÓGICA DE ESTADO (EN_CURSO / INSCRIPCION) ---
        if (torneo.estado == "EN_CURSO") {

            // --- 2a. BUSCAR MI PARTIDA ---
            val miMatch = torneo.matches.values.find {
                it.equipo1.contains(currentUserId) || it.equipo2.contains(currentUserId)
            }

            if (miMatch != null) {
                txtStatus.text = ""

                val myMatchView = LayoutInflater.from(this).inflate(R.layout.item_my_match, null)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Ancho completo
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Altura según contenido
                )
                layoutParams.setMargins(20, 20, 20, 20)

                myMatchView.layoutParams = layoutParams
                myMatchView.findViewById<TextView>(R.id.txtRoundTitle).text = miMatch.rondaNombre

                configurarNuevoDisenoPartida(myMatchView, miMatch)

                bracketContainer.addView(myMatchView)

                // --- CUENTA ATRÁS 24 HORAS EN EL BOTÓN ---
                if (!entradaIniciada) {
                    entradaIniciada = true
                    iniciarCuentaAtras(torneo.startTime, miMatch)
                }
                btnAction.text = "JOKATU / JUGAR"
                btnAction.setBackgroundResource(R.drawable.bg_button_magic)
                btnAction.setOnClickListener {
                    entrarAPartidaTorneo(miMatch)
                }
            }
        } else {
            // --- 3. LÓGICA DE INSCRIPCIÓN ---
            btnAction.setOnClickListener(null)
            val yaEstoyInscrito = torneo.inscritos.contains(currentUserId)

            if (yaEstoyInscrito) {
                btnAction.text = "INSCRITO (Click para salir)"
                btnAction.setBackgroundColor(Color.parseColor("#B22222"))
                btnAction.isEnabled = true
                btnAction.setOnClickListener { desinscribirUsuario() }
                txtStatus.text = "Inscripciones abiertas"
            } else {
                btnAction.text = "INSCRIBIRSE"
                btnAction.setBackgroundResource(R.drawable.bg_button_medieval)
                btnAction.isEnabled = true
                btnAction.setOnClickListener { inscribirUsuario() }
                txtStatus.text = "Inscripciones abiertas"
            }
        }
    }
    private fun entrarAPartidaTorneo(match: Match) {
        btnAction.isEnabled = false // Bloqueo inmediato del botón

        val intent = Intent(this, PartidaActivity::class.java).apply {
            putExtra("ES_TORNEO", true)
            putExtra("ID_TORNEO", torneoId)
            putExtra("EXTRA_PARAM", "UNIRSE_PRIVADA")
            putExtra("EXTRA_CODE", match.id)
        }

        // Tu orden específico
        val delay: Long = when (currentUserId) {
            match.equipo1[0] -> 0L      // Creador E1
            match.equipo2[0] -> 500L    // Etsai (Creador E2)
            match.equipo1[1] -> 1000L   // Taldekide E1
            match.equipo2[1] -> 1500L   // Taldekide E2
            else -> 0L
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Verificamos una última vez antes de saltar
            startActivity(intent)
            finish()
        }, delay)
    }


    // --- NUEVA FUNCIÓN PARA BUSCAR DATOS DE USUARIOS ---
    private fun configurarNuevoDisenoPartida(view: android.view.View, match: Match) {
        val txtTeam1Names = view.findViewById<TextView>(R.id.txtTeam1Names)
        val txtTeam2Names = view.findViewById<TextView>(R.id.txtTeam2Names)
        val imgP1 = view.findViewById<ShapeableImageView>(R.id.imgP1)
        val imgP2 = view.findViewById<ShapeableImageView>(R.id.imgP2)
        val imgP3 = view.findViewById<ShapeableImageView>(R.id.imgP3)
        val imgP4 = view.findViewById<ShapeableImageView>(R.id.imgP4)

        // Función auxiliar local para cargar usuario
        fun cargarUsuario(userId: String, imageView: ShapeableImageView, textView: TextView, esPrimeroDelEquipo: Boolean) {
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        val name = doc.getString("username") ?: "Desconocido"

                        val currentText = textView.text.toString()
                        if (currentText.contains("Jugador") || currentText.isEmpty()) {
                            textView.text = name
                        } else {
                            textView.text = "$currentText & $name"
                        }

                        val avatarFilename = doc.getString("avatarActual") ?: "avadef"
                        val drawableId = when (avatarFilename.replace(".png", "")) {
                            "avadef" -> R.drawable.avadef
                            else -> R.drawable.avadef
                        }
                        Glide.with(this).load(drawableId).circleCrop().into(imageView)
                    }
                }
        }

        // Cargar Equipo 1
        txtTeam1Names.text = "" // Limpiar texto inicial
        cargarUsuario(match.equipo1[0], imgP1, txtTeam1Names, true)
        cargarUsuario(match.equipo1[1], imgP2, txtTeam1Names, false)

        // Cargar Equipo 2
        txtTeam2Names.text = "" // Limpiar texto inicial
        cargarUsuario(match.equipo2[0], imgP3, txtTeam2Names, true)
        cargarUsuario(match.equipo2[1], imgP4, txtTeam2Names, false)
    }
    private fun iniciarCuentaAtras(fechaInicioMs: Long, match: Match) {
        val tiempoPreparacion = 30000L
        btnAction.isEnabled = false

        object : android.os.CountDownTimer(tiempoPreparacion, 1000) {
            override fun onTick(ms: Long) {
                val seg = ms / 1000
                btnAction.text = String.format("ENTRANDO EN 00:%02d", seg)
            }

            override fun onFinish() {
                btnAction.text = "¡ENTRANDO!"
                // LLAMADA AUTOMÁTICA AL FINALIZAR EL TIMER
                entrarAPartidaTorneo(match)
            }
        }.start()
    }
    private fun inscribirUsuario() {
        if (torneoId.isEmpty() || currentUserId.isEmpty()) return

        db.collection("Tournaments").document(torneoId).get()
            .addOnSuccessListener { document ->
                val torneo = document.toObject(Torneo::class.java) ?: return@addOnSuccessListener

                if (torneo.inscritos.size >= torneo.maxJugadores) {
                    Toast.makeText(this, "Torneo lleno", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                db.collection("Tournaments").document(torneoId)
                    .update("inscritos", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                        // SI SE LLENA CON ESTA INSCRIPCIÓN:
                        if (torneo.inscritos.size + 1 >= torneo.maxJugadores) {
                            iniciarTorneoAutomaticamente() // Inicio inmediato por lleno
                        }
                    }
            }
    }

    private fun iniciarTorneoAutomaticamente() {
        db.collection("Tournaments").document(torneoId).get()
            .addOnSuccessListener { document ->
                val torneo = document.toObject(Torneo::class.java) ?: return@addOnSuccessListener

                // Si por alguna razón se intenta iniciar con menos de 4, lo borramos
                if (torneo.inscritos.size < 4) {
                    db.collection("Tournaments").document(torneoId).delete()
                    finish()
                    return@addOnSuccessListener
                }

                val participantes = torneo.inscritos.shuffled()
                val nuevosMatches = mutableMapOf<String, Match>()

                // Lógica de rondas (Semifinales, Cuartos...)
                val nombreRonda = when {
                    participantes.size <= 4 -> "Semifinales"
                    participantes.size <= 8 -> "Cuartos"
                    else -> "Ronda 1"
                }

                var matchCount = 1
                for (i in 0 until participantes.size step 4) {
                    if (i + 3 < participantes.size) {
                        val matchId = "m${matchCount}"
                        nuevosMatches[matchId] = Match(
                            id = matchId,
                            equipo1 = listOf(participantes[i], participantes[i+1]),
                            equipo2 = listOf(participantes[i+2], participantes[i+3]),
                            ronda = 1,
                            rondaNombre = nombreRonda,
                            estado = "ESPERA"
                        )
                        matchCount++
                    }
                }

                val updates = mapOf(
                    "estado" to "EN_CURSO",
                    "matches" to nuevosMatches,
                    "startTime" to System.currentTimeMillis(),
                    "rondaActual" to 1
                )

                db.collection("Tournaments").document(torneoId).update(updates)
            }
    }


    private fun desinscribirUsuario() { // NUEVO
        db.collection("Tournaments").document(torneoId)
            .update("inscritos", com.google.firebase.firestore.FieldValue.arrayRemove(currentUserId))
    }
}