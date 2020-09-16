package application.android.refresh.ui.routines.info

import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.internal.PracticeCard
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutinesInfoViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var firstLaunch: Boolean = true
    var questionMode = 1
    var routine: Routine? = null
    val shouldSetupCard = MutableLiveData<Boolean>()
    val isDataReady = MutableLiveData<Boolean>()
    val isOkayToExit = MutableLiveData<Boolean>()
    val isDone = MutableLiveData<Boolean>()
    val cardsLeft = MutableLiveData<Int>()
    val routineName = MutableLiveData<String>()
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
                    val layout: Layout? =refreshRepository.getLayoutAtOnce(id)

                    if (layout != null) {
                        layoutList.add(layout)
                        refreshRepository.getCardsWithLayoutId(layout.id).forEach { card ->
                            if (validate(card)) {
                                cardList.add(card)
                            }
                        }
                    }
                }
                cardList.shuffle()
            }
            shouldSetupCard.postValue(true)
        }
    }

    private fun validate(card: Card): Boolean {
        val practiceCard = routine?.finishedCards?.find {
            it.id == card.id
        } ?: return true

        if (practiceCard.sessionDelay < System.currentTimeMillis() && !practiceCard.completed) {
            return true
        }

        return false
    }

    private fun validateReset(practiceCard: PracticeCard): Boolean {
        return (practiceCard.sessionDelay < System.currentTimeMillis()
                && !practiceCard.completed
                && (isDone.value == true || practiceCard.id != cardId))
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
                var practiceCardFound = false
                val updatedFinishedCards = it.finishedCards
                updatedFinishedCards.find { practiceCard ->
                    practiceCard.id == cardId

                }?.let { matchingpracticeCard ->
                    matchingpracticeCard.completed = true
                    practiceCardFound = true
                }

                if (!practiceCardFound) {
                    updatedFinishedCards.add(
                        PracticeCard(
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
                updatedFinishedCards.map { practiceCard ->
                    practiceCard.completed = false
                    if (validateReset(practiceCard)) {
                        val card: Card? = refreshRepository.getCardAtOnce(practiceCard.id)
                        if (card != null) {
                            newCardList.add(card)
                        }
                    }
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
                cardListAddAll(newCardList)

                if (isDone.value == true) {
                    getNextCard()
                }
            }
        }
    }

    fun resetDelay() {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                val updatedFinishedCards = it.finishedCards
                val newCardList: ArrayList<Card> = arrayListOf()
                updatedFinishedCards.map { practiceCard ->
                    practiceCard.sessionDelay = 0L
                    if (validateReset(practiceCard)) {
                        val card: Card? = refreshRepository.getCardAtOnce(practiceCard.id)
                        if (card != null) {
                            newCardList.add(card)
                        }
                    }
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
                cardListAddAll(newCardList)

                if (isDone.value == true) {
                    getNextCard()
                }
            }
        }
    }

    private fun cardListAddAll(list: List<Card>) {
        if (list.isNotEmpty()) {
            cardList.removeAll(list)
            cardList.addAll(list)
            cardsLeft.postValue(cardList.size)
        }
    }

    private fun cardListGetLast(): Card {
        val card = cardList.removeAt(cardList.size - 1)
        cardsLeft.postValue(cardList.size)
        return card
    }

    fun setCardDelay(cardId: Long, delay: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            routine?.let {
                var practiceCardFound = false
                val updatedFinishedCards = it.finishedCards
                updatedFinishedCards.find { practiceCard ->
                    practiceCard.id == cardId

                }?.let { matchingpracticeCard ->
                    matchingpracticeCard.sessionDelay = delay
                    practiceCardFound = true
                }

                if (!practiceCardFound) {
                    updatedFinishedCards.add(
                        PracticeCard(cardId, false, delay)
                    )
                }
                val updatedRoutine = Routine(it.id, it.name, it.layoutIds, updatedFinishedCards)
                refreshRepository.updateRoutine(updatedRoutine)
            }
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch(Dispatchers.IO) {
            refreshRepository.deleteRoutine(routine)
            isOkayToExit.postValue(true)
        }
    }
}