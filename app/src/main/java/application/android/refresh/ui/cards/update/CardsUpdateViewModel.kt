package application.android.refresh.ui.cards.update

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.*

class CardsUpdateViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {

    val isOkayToExit = MutableLiveData<Boolean>()

    fun cardDetails(id: Long): LiveData<Card> = liveData {
        emitSource(refreshRepository.getCard(id))
    }

    fun layoutDetails(id: Long): LiveData<Layout> = liveData {
        emitSource(refreshRepository.getLayout(id))
    }

    fun updateCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            isOkayToExit.postValue(false)
            coroutineScope {
                launch {
                    refreshRepository.updateCard(card)
                }
            }
            isOkayToExit.postValue(true)
        }
    }

}