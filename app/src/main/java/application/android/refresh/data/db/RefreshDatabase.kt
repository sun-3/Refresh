package application.android.refresh.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine

@Database(entities = [Layout::class, Card::class, Routine::class], version = 1)
@TypeConverters(Converters::class)
abstract class RefreshDatabase : RoomDatabase() {
    abstract fun layoutDao(): LayoutDao
    abstract fun cardDao(): CardDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile private var instance: RefreshDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                RefreshDatabase::class.java, "refresh-database")
                .build()
    }
}