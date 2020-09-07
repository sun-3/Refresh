package application.android.refresh.data.internal

data class RoutineCard(
    val id: Long,
    val name: String,
    val totalCards: Int,
    val completedCards: Int,
    val layoutNames: List<String>
)