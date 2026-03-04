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
                val esperaMs = 15 * 60 * 1000 // 15 min bloqueo
                val inscripcionMs = 10 * 60 * 1000 // 10 min inscripción

                val tiempoHabilitarInscripcion = torneo.createdAt + esperaMs
                val tiempoInicioTorneo = tiempoHabilitarInscripcion + inscripcionMs

                when {
                    ahora < tiempoHabilitarInscripcion -> {
                        iniciarContadorReal(tiempoHabilitarInscripcion)
                    }

                    // FASE 2: Inscripciones abiertas por 10 minutos
                    torneo.estado == "INSCRIPCION" && ahora < tiempoInicioTorneo -> {
                        // Detenemos cualquier timer de espera y mostramos UI de inscripción
                        isTimerRunning = false
                        actualizarUI(torneo)

                        // Si nadie ha disparado el inicio automático, lo preparamos
                        if (!timerEjecutandose) {
                            prepararInicioAutomatico(tiempoInicioTorneo)
                        }
                    }

                    // FASE 3: El tiempo se acabó, empezar torneo
                    torneo.estado == "INSCRIPCION" && ahora >= tiempoInicioTorneo -> {
                        iniciarTorneoAutomaticamente()
                    }

                    else -> actualizarUI(torneo)
                }
            }
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
        btnAction.isEnabled = false

        // Evitamos crear múltiples timers
        if (isTimerRunning) return
        isTimerRunning = true

        val tiempoRestante = tiempoFinal - System.currentTimeMillis()

        object : android.os.CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(ms: Long) {
                val min = (ms / 60000)
                val seg = (ms % 60000) / 1000
                btnAction.text = String.format("ESPERA: %02d:%02d", min, seg)
            }

            override fun onFinish() {
                isTimerRunning = false
                btnAction.isEnabled = true
                btnAction.text = "INSCRIBIRSE"
                // Refrescamos para habilitar el botón medieval
                buscarTorneoActivo()
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
                iniciarCuentaAtras(torneo.startTime)

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
        val intent = Intent(this, PartidaActivity::class.java).apply {
            putExtra("ES_TORNEO", true)
            putExtra("ID_TORNEO", torneoId)
            putExtra("EXTRA_PARAM", "UNIRSE_PRIVADA")
            putExtra("EXTRA_CODE", match.id)
        }

        val delay: Long = when {
            match.equipo1[0] == currentUserId -> 0L
            match.equipo2[0] == currentUserId -> 500L
            match.equipo1[1] == currentUserId -> 1000L
            match.equipo2[1] == currentUserId -> 1500L
            else -> 0L
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
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
    private fun iniciarCuentaAtras(fechaInicioMs: Long) {
        val ahora = System.currentTimeMillis()
        val diferenciaMs = fechaInicioMs - ahora

        if (diferenciaMs > 0) {
            btnAction.isEnabled = false

            object : android.os.CountDownTimer(diferenciaMs, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutos = (millisUntilFinished  / 60000)
                    val segundos = (millisUntilFinished % 60000) / 1000
                    btnAction.text = String.format("JUGAR EN %02d:%02d", minutos, segundos)
                }

                override fun onFinish() {
                    btnAction.isEnabled = true
                    btnAction.text = "¡JUGAR AHORA!"
                    // Asumiendo que usas tu drawable de botón mágico
                    btnAction.setBackgroundResource(R.drawable.bg_button_magic)
                }
            }.start()
        } else {
            // Ya pasó el tiempo
            btnAction.isEnabled = true
            btnAction.text = "¡JUGAR AHORA!"
            btnAction.setBackgroundResource(R.drawable.bg_button_magic)
        }
    }
    private fun inscribirUsuario() {
        if (torneoId.isEmpty() || currentUserId.isEmpty()) return

        // Obtenemos el documento para comprobar la capacidad actual
        db.collection("Tournaments").document(torneoId).get()
            .addOnSuccessListener { document ->
                val torneo = document.toObject(Torneo::class.java) ?: return@addOnSuccessListener

                // --- COMPROBAR SI YA ESTÁ LLENO USANDO maxJugadores ---
                if (torneo.inscritos.size >= torneo.maxJugadores) {
                    Toast.makeText(this, "Torneo lleno", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Añadir usuario
                db.collection("Tournaments").document(torneoId)
                    .update("inscritos", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                        Toast.makeText(this, "¡Inscrito!", Toast.LENGTH_SHORT).show()

                        // --- COMPROBAR SI SE LLENÓ JUSTO AHORA ---
                        // Comparamos el tamaño nuevo (+1) con maxJugadores
                        if (torneo.inscritos.size + 1 >= torneo.maxJugadores) {
                            iniciarTorneoAutomaticamente()
                        }
                    }
            }
    }

    private fun iniciarTorneoAutomaticamente() {
        db.collection("Tournaments").document(torneoId).get()
            .addOnSuccessListener { document ->
                val torneo = document.toObject(Torneo::class.java) ?: return@addOnSuccessListener
                val participantes = torneo.inscritos.shuffled()

                // --- VALIDACIÓN: Mínimo 4 jugadores para un 2vs2 ---
                if (participantes.size < 4) {
                    db.collection("Tournaments").document(torneoId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Torneo cancelado: Participantes insuficientes", Toast.LENGTH_LONG).show()
                            finish() // Cerramos el lobby
                        }
                    return@addOnSuccessListener
                }

                // --- Lógica normal de generación de matches ---
                val totalJugadores = participantes.size
                val nuevosMatches = mutableMapOf<String, Match>()

                val nombreRonda = when {
                    totalJugadores <= 4 -> "Semifinales"
                    totalJugadores <= 8 -> "Cuartos"
                    totalJugadores <= 16 -> "Octavos"
                    else -> "Ronda 1"
                }

                var matchCount = 1
                for (i in 0 until totalJugadores step 4) {
                    if (i + 3 < totalJugadores) {
                        val matchId = "m${matchCount}"
                        val match = Match(
                            id = matchId,
                            equipo1 = listOf(participantes[i], participantes[i+1]),
                            equipo2 = listOf(participantes[i+2], participantes[i+3]),
                            ronda = 1,
                            rondaNombre = nombreRonda,
                            estado = "ESPERA"
                        )
                        nuevosMatches[matchId] = match
                        matchCount++
                    }
                }

                val tiempoInicio = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                val updates = mapOf(
                    "estado" to "EN_CURSO",
                    "matches" to nuevosMatches,
                    "startTime" to tiempoInicio,
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