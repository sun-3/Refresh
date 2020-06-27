package application.android.refresh.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import application.android.refresh.data.db.entity.Layout

@Dao
interface LayoutDao {

    @Query("SELECT * FROM layouts")
    fun getLayouts(): LiveData<List<Layout>>

    @Query("SELECT * FROM layouts")
    fun getLayoutsAtOnce(): List<Layout>

    @Query("SELECT * FROM layouts WHERE id = :layoutId")
    fun getLayout(layoutId: Long): LiveData<Layout>

    @Query("SELECT * FROM layouts WHERE id = :layoutId")
    fun getLayoutAtOnce(layoutId: Long): Layout

    @Query("SELECT * FROM layouts WHERE front LIKE '%' || :searchText || '%' OR back LIKE '%' || :searchText || '%' OR backExtra LIKE '%' || :searchText || '%'")
    fun searchLayoutsByText(searchText: String): LiveData<List<Layout>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(layout: Layout)

    @Update
    fun update(layout: Layout)

    @Delete
    fun delete(layout: Layout)
}