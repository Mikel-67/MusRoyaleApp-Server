package com.example.musroyale

import Torneo
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musroyale.R
import com.google.firebase.firestore.FirebaseFirestore

class CrearTorneoActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_torneo) // Necesitarás crear este XML
        db = FirebaseFirestore.getInstance()

        val etNombre = findViewById<EditText>(R.id.etNombreTorneo)
        val etMaxJugadores = findViewById<EditText>(R.id.etMaxJugadores)
        val btnCrear = findViewById<Button>(R.id.btnFinalizarCreacion)

        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString()
            val maxJ = etMaxJugadores.text.toString().toIntOrNull() ?: 8

            if (nombre.isNotEmpty()) {
                crearTorneoEnFirestore(nombre, maxJ)
            }
        }
    }

    private fun crearTorneoEnFirestore(nombre: String, maxJugadores: Int) {
        val tiempoActual = System.currentTimeMillis() // Capturamos el momento exacto
        val torneoId = "TORNEO_" + System.currentTimeMillis() // ID único basado en tiempo

        val nuevoTorneo = Torneo(
            id = torneoId,
            nombre = nombre,
            estado = "INSCRIPCION",
            maxJugadores = maxJugadores,
            inscritos = listOf(),
            rondaActual = 1,
            matches = mapOf(),
            createdAt = tiempoActual
        )

        db.collection("Tournaments").document(torneoId).set(nuevoTorneo)
            .addOnSuccessListener {
                Toast.makeText(this, "Torneo creado: $torneoId", Toast.LENGTH_SHORT).show()
                finish() // Volver atrás
            }
    }
}