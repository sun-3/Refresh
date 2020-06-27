package application.android.refresh.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey val id: Long,
    val name: String,
    val layoutIds: List<Long>,
    val finishedCards: MutableList<RoutineCard>
)