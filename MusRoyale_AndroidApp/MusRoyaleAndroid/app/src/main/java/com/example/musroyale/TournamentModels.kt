data class Torneo(
    val id: String = "",
    val nombre: String = "",
    val estado: String = "INSCRIPCION", // INSCRIPCION, EN_CURSO, FINALIZADO
    val maxJugadores: Int = 8,
    val inscritos: List<String> = listOf(),
    val rondaActual: Int = 1,
    val matches: Map<String, Match> = mapOf(),
    val startTime: Long = 0L,
    val createdAt: Long = 0L // <--- AÑADE ESTA LÍNEA
)

data class Match(
    val id: String = "",
    val equipo1: List<String> = listOf(), // IDs de usuarios
    val equipo2: List<String> = listOf(),
    val ganador: String = "", // "equipo1", "equipo2", o ""
    val ronda: Int = 1,
    val rondaNombre: String = "", // <-- NUEVO
    val estado: String = "ESPERA" // "ESPERA", "JUGANDO", "FINALIZADO"
)