package application.android.refresh.ui.layouts.info

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LayoutsInfoViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var layout: Layout? = null
    val isOkayToExit = MutableLiveData<Boolean>()
    val layoutName = MutableLiveData<String>()
    val count = MutableLiveData<Int>()

    fun layoutDetails(id: Long): LiveData<Layout> = liveData {
        emitSource(refreshRepository.getLayout(id))
    }

    fun cardCount() {
        viewModelScope.launch(Dispatchers.IO) {
            layout?.let {
                val cardCount = refreshRepository.cardsCountWithLayoutId(it.id)
                count.postValue(cardCount)
            }
        }
    }

    fun deleteLayout() {
        viewModelScope.launch(Dispatchers.IO) {
            layout?.let {
                refreshRepository.deleteLayout(it)
            }
            isOkayToExit.postValue(true)
        }
    }
}