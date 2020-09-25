package application.android.refresh.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import application.android.refresh.data.db.entity.Routine

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines WHERE userId = :userId")
    fun getRoutines(userId: String): LiveData<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutine(id: Long): LiveData<Routine>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(routine: Routine)

    @Update
    fun update(routine: Routine)

    @Delete
    fun delete(routine: Routine)
}