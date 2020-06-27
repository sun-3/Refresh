package application.android.refresh.ui.routines.info

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.RoutineCard
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutinesInfoViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var firstLaunch: Boolean = true
    val shouldSetupCard = MutableLiveData<Boolean>()
    val isDataReady = MutableLiveData<Boolean>()
    val isDone = MutableLiveData<Boolean>()
    val cardsLeft = MutableLiveData<Int>()
    val routineName = MutableLiveData<String>()
    var routine: Routine? = null
    private val cardList = arrayListOf<Card>()
    private val layoutList = arrayListOf<Layout>()

    // Card related
    var cardId = 0L
    var questionTitle = ""
    var question = ""
    var answerTitle = ""
    var answer = ""
    var extraTitle = ""
    var extra = ""

    fun routineDetails(id: Long): LiveData<Routine> = liveData {
        emitSource(refreshRepository.getRoutine(id))
    }

    fun prepareCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                routineName.postValue(it.name)
                it.layoutIds.forEach { id ->
                    layoutList.add(refreshRepository.getLayoutAtOnce(id))
                    refreshRepository.getCardsWithLayoutId(id).forEach { card ->
                        if (validate(card)) {
                            cardList.add(card)
                        }
                    }

                }
                cardList.shuffle()
            }
            shouldSetupCard.postValue(true)
        }
    }

    private fun validate(card: Card): Boolean {
        val routineCard = routine?.finishedCards?.find {
            it.id == card.id
        } ?: return true

        if (routineCard.sessionDelay < System.currentTimeMillis() && !routineCard.completed) {
            return true
        }

        return false
    }

    private fun validateWithRoutineCard(routineCard: RoutineCard): Boolean {
        return (routineCard.sessionDelay < System.currentTimeMillis() && !routineCard.completed)
    }

    fun getNextCard() {
        viewModelScope.launch {
            if (cardList.size == 0) {
                isDone.postValue(true)
            } else {
                isDone.postValue(false)
                val card = cardListGetLast()
                val layout: Layout? = layoutList.find {
                    it.id == card.layoutId
                }
                if (layout != null) {
                    cardId = card.id
                    questionTitle = layout.front
                    question = card.front
                    answerTitle = layout.back
                    answer = card.back
                    extraTitle = layout.backExtra
                    extra = card.backExtra
                    isDataReady.postValue(true)
                }
            }
        }
    }

    fun completeCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                var routineCardFound = false
                val updatedFinishedCards = it.finishedCards
                updatedFinishedCards.find { routineCard ->
                    routineCard.id == cardId

                }?.let { matchingRoutineCard ->
                    matchingRoutineCard.completed = true
                    routineCardFound = true
                }

                if (!routineCardFound) {
                    updatedFinishedCards.add(
                        RoutineCard(
                            cardId, true,
                            0L
                        )
                    )
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                val updatedFinishedCards = it.finishedCards
                val newCardList: ArrayList<Card> = arrayListOf()
                updatedFinishedCards.map { routineCard ->
                    routineCard.completed = false
                    if (validateWithRoutineCard(routineCard)) {
                        newCardList.add(refreshRepository.getCardAtOnce(routineCard.id))
                    }
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
                cardListAddAll(newCardList)
                getNextCard()
            }
        }
    }

    fun resetDelay() {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                val updatedFinishedCards = it.finishedCards
                val newCardList: ArrayList<Card> = arrayListOf()
                updatedFinishedCards.map { routineCard ->
                    routineCard.sessionDelay = 0L
                    if (validateWithRoutineCard(routineCard)) {
                        newCardList.add(refreshRepository.getCardAtOnce(routineCard.id))
                    }
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
                cardListAddAll(newCardList)
                getNextCard()
            }
        }
    }

    private fun cardListAddAll(list: List<Card>) {
        cardList.removeAll(list)
        cardList.addAll(list)
        cardsLeft.postValue(cardList.size)
    }

    private fun cardListGetLast(): Card {
        val card = cardList.removeAt(cardList.size - 1)
        cardsLeft.postValue(cardList.size)
        return card
    }

    fun setCardDelay(cardId: Long, delay: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                var routineCardFound = false
                val updatedFinishedCards = it.finishedCards
                updatedFinishedCards.find { routineCard ->
                    routineCard.id == cardId

                }?.let { matchingRoutineCard ->
                    matchingRoutineCard.sessionDelay = delay
                    routineCardFound = true
                }

                if (!routineCardFound) {
                    updatedFinishedCards.add(
                        RoutineCard(cardId, false, delay)
                    )
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
            }
        }
    }
}