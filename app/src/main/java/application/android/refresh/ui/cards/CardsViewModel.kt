package application.android.refresh.ui.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.repository.RefreshRepository

class CardsViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var list: List<Card> = listOf()
    val cardList: LiveData<List<Card>> = liveData {
        emitSource(refreshRepository.getCardList())
    }

    fun searchCards(searchText: String): LiveData<List<Card>> = liveData {
        emitSource(refreshRepository.searchCardsByText(searchText))
    }
}