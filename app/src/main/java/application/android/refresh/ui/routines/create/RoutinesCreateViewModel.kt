package application.android.refresh.ui.routines.create

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutinesCreateViewModel(val refreshRepository: RefreshRepository) : ViewModel() {
    var routineName: String = ""
    val layoutNames: ArrayList<String> = arrayListOf()
    val alreadySelectedLayouts: ArrayList<Boolean> = arrayListOf()
    val selectedIndices: ArrayList<Int> = arrayListOf()
    val selectedLayoutIds: ArrayList<Long> = arrayListOf()
    var selectedLayoutCount: Int = 0
    var layouts: List<Layout> = listOf()
    val shouldSetupUI: LiveData<Boolean> = liveData {
        emitSource(_shouldSetupUI)
    }
    val shouldExit: LiveData<Boolean> = liveData {
        emitSource(_shouldExit)
    }
    private var _shouldSetupUI: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _shouldExit: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        getLayouts()
    }

    private fun getLayouts() = viewModelScope.launch(Dispatchers.IO) {
        layouts = refreshRepository.getLayoutsAtOnce()
        layouts.forEach {
            layoutNames.add(it.name)
            alreadySelectedLayouts.add(false)
        }

        _shouldSetupUI.postValue(true)
    }

    fun generateSelectedLayouts() {
        viewModelScope.launch {
            selectedLayoutIds.clear()
            selectedIndices.forEach {
                selectedLayoutIds.add(layouts[it].id)
            }
            selectedLayoutCount = selectedIndices.size
        }
    }

    fun addRoutine(routine: Routine) = viewModelScope.launch(Dispatchers.IO) {
        refreshRepository.addRoutine(routine)
        _shouldExit.postValue(true)
    }
}
