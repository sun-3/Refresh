package application.android.refresh.ui.routines

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.repository.RefreshRepository
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class RoutinesViewModel(val refreshRepository: RefreshRepository) : ViewModel() {
    var list: List<Routine> = listOf()
    var groupieList: List<RoutineItem> = listOf()
    var groupieAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter<GroupieViewHolder>()
    val routineList: LiveData<List<Routine>> = liveData {
        emitSource(refreshRepository.getRoutineList())
    }
}