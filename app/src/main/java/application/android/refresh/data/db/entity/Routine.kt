package application.android.refresh.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import application.android.refresh.data.internal.PracticeCard

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey val id: Long,
    val userId: String,
    val name: String,
    val layoutIds: List<Long>,
    val finishedCards: MutableList<PracticeCard>
)