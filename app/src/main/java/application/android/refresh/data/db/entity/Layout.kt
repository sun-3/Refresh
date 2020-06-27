package application.android.refresh.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layouts")
data class Layout(
    @PrimaryKey val id: Long,
    val name: String,
    val front: String,
    val back: String,
    val backExtra: String)