package application.android.refresh.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: Long,
    val layoutId: Long,
    val layoutName: String,
    val front: String,
    val back: String,
    val backExtraTitle: String,
    val backExtra: String)