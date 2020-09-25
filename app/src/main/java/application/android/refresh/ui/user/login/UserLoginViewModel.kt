package application.android.refresh.ui.user.login

import android.util.Log
import androidx.lifecycle.*
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class UserLoginViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    val layouts = ArrayList<Layout>()
    val cards = ArrayList<Card>()
    val routines = ArrayList<Routine>()
    val isDone = MutableLiveData<Boolean>()

    fun getDataFromDB(userId: String): LiveData<Boolean> = liveData {
        emitSource(refreshRepository.getDataFromDB(userId, layouts, cards, routines))
    }

    fun addDataToDB() {
        viewModelScope.launch(Dispatchers.IO) {
            layouts.forEach {
                refreshRepository.addLayout(it)
            }

            cards.forEach {
                refreshRepository.addCard(it)
            }

            routines.forEach {
                refreshRepository.addRoutine(it)
            }

            isDone.postValue(true)
        }
    }

}