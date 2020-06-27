package application.android.refresh.data.repository

import androidx.lifecycle.LiveData
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine

interface RefreshRepository {
    // Layouts
    suspend fun addLayout(layout: Layout)
    suspend fun updateLayout(layout: Layout)
    suspend fun deleteLayout(layout: Layout)
    suspend fun getLayout(layoutId: Long): LiveData<Layout>
    suspend fun getLayoutList(): LiveData<List<Layout>>
    suspend fun getLayoutsAtOnce(): List<Layout>
    suspend fun getLayoutAtOnce(id: Long): Layout
    suspend fun searchLayoutByText(searchText: String): LiveData<List<Layout>>

    // Cards
    suspend fun addCard(card: Card)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    suspend fun getCardList(): LiveData<List<Card>>
    suspend fun getCard(id: Long): LiveData<Card>
    suspend fun getCardAtOnce(id: Long): Card
    suspend fun getCardsAtOnce(): List<Card>
    suspend fun getCardsWithLayoutId(layoutId: Long): List<Card>
    suspend fun searchCardsByText(searchText: String): LiveData<List<Card>>

    // Routines
    suspend fun addRoutine(routine: Routine)
    suspend fun updateRoutine(routine: Routine)
    suspend fun deleteRoutine(routine: Routine)
    suspend fun getRoutine(id: Long): LiveData<Routine>
    suspend fun getRoutineList(): LiveData<List<Routine>>
}