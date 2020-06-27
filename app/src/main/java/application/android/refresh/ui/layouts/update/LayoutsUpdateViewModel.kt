package application.android.refresh.ui.layouts.update

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LayoutsUpdateViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {

    val isOkayToExit = MutableLiveData<Boolean>()

    fun layoutDetails(id: Long): LiveData<Layout> = liveData {
        emitSource(refreshRepository.getLayout(id))
    }

    fun updateLayout(layout: Layout) {
        viewModelScope.launch {
            isOkayToExit.postValue(false)
            coroutineScope {
                launch {
                    refreshRepository.updateLayout(layout)
                }
            }
            isOkayToExit.postValue(true)
        }
    }

}