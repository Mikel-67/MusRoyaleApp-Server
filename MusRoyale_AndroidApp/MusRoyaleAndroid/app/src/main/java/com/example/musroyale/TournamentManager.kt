import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object TournamentManager {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Paso 1: Cuando el torneo se llena, activamos la cuenta atrás de 24h.
     */
    fun cerrarInscripcionesYProgramar(torneoId: String) {
        val unDiaEnMilis = 24 * 60 * 60 * 1000
        val startTimestamp = System.currentTimeMillis() + unDiaEnMilis

        db.collection("Tournaments").document(torneoId).update(
            "estado", "ESPERANDO_INICIO",
            "startTime", startTimestamp
        ).addOnSuccessListener {
            Log.d("Tournament", "Torneo $torneoId lleno. Empieza en 24h.")
        }
    }

    /**
     * Paso 2: Generar los emparejamientos (se llama cuando el tiempo expira)
     */

}