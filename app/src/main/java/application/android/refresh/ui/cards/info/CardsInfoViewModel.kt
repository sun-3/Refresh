package application.android.refresh.ui.cards.info

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CardsInfoViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {

    val isOkayToExit = MutableLiveData<Boolean>()
    val cardName = MutableLiveData<String>()

    fun cardDetails(id: Long): LiveData<Card> = liveData {
        emitSource(refreshRepository.getCard(id))
    }

    fun layoutDetails(id: Long): LiveData<Layout> = liveData {
        emitSource(refreshRepository.getLayout(id))
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            refreshRepository.deleteCard(card)
            isOkayToExit.postValue(true)
        }
    }
}