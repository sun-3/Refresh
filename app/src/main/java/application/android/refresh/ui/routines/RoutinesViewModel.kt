package application.android.refresh.ui.routines

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.internal.RoutineCard
import application.android.refresh.data.repository.RefreshRepository
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutinesViewModel(val refreshRepository: RefreshRepository) : ViewModel() {
    var list: List<Routine> = listOf()
    var groupieList: List<RoutineItem> = listOf()
    var groupieAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter<GroupieViewHolder>()
    var updatedGroupieList: ArrayList<RoutineItem> = arrayListOf()
    val updateAdapter = MutableLiveData<Boolean>()
    val routineList: LiveData<List<Routine>> = liveData {
        emitSource(refreshRepository.getRoutineList())
    }

    fun prepareRoutineCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            updatedGroupieList = arrayListOf()
            list.forEach {
                val layoutNames = arrayListOf<String>()
                var totalCards = 0
                var maxLoopIterations = 5
                if (it.layoutIds.isNotEmpty()) {
                    for (i in it.layoutIds.indices) {
                        if (maxLoopIterations-- >= 0) {
                            refreshRepository.getLayoutAtOnce(it.layoutIds[i])?.let { layout ->
                                layoutNames.add(layout.name)
                            }
                        }
                        totalCards += refreshRepository.cardsCountWithLayoutId(it.layoutIds[i])
                    }
                }
                if (layoutNames.isNullOrEmpty()) {
                   refreshRepository.deleteRoutine(it)
                } else {
                    updatedGroupieList.add(
                        RoutineItem(
                            RoutineCard(
                                it.id, it.name, totalCards,
                                it.finishedCards.size, layoutNames
                            )
                        )
                    )
                }
            }
            updateAdapter.postValue(true)
        }
    }
}