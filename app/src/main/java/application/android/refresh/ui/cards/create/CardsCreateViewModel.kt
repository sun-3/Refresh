package application.android.refresh.ui.cards.create

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.*

class CardsCreateViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {

    val isOkayToExit = MutableLiveData<Boolean>()

    fun layoutDetails(id: Long): LiveData<Layout> = liveData {
       emitSource(refreshRepository.getLayout(id))
    }

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            isOkayToExit.postValue(false)
            coroutineScope {
                launch {
                    refreshRepository.addCard(card)
                }
            }
            isOkayToExit.postValue(true)
        }
    }

}