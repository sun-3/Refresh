package application.android.refresh.data.db.entity

data class RoutineCard(
    val id: Long,
    var completed: Boolean,
    var sessionDelay: Long
)