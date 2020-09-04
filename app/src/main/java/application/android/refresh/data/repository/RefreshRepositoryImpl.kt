package application.android.refresh.data.repository

import androidx.lifecycle.LiveData
import application.android.refresh.data.db.CardDao
import application.android.refresh.data.db.LayoutDao
import application.android.refresh.data.db.RoutineDao
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.db.entity.Routine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefreshRepositoryImpl(private val layoutDao: LayoutDao, private val cardDao: CardDao,
                            private val routineDao: RoutineDao):
    RefreshRepository {

    override suspend fun addLayout(layout: Layout) {
        withContext(Dispatchers.IO) {
            layoutDao.insert(layout)
        }
    }

    override suspend fun updateLayout(layout: Layout) {
        withContext(Dispatchers.IO) {
            val id = layout.id
            getCardsWithLayoutId(id).map {
                val card = Card(it.id, it.layoutId, it.layoutName, it.front, it.back, layout.backExtra, it.backExtra)
                updateCard(card)
            }
            return@withContext layoutDao.update(layout)
        }
    }

    override suspend fun getCardsAtOnce(): List<Card> {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.getCardsAtOnce()
        }
    }

    override suspend fun deleteLayout(layout: Layout) {
        withContext(Dispatchers.IO) {
            val id = layout.id
            getCardsWithLayoutId(id).map {
                deleteCard(it)
            }
            return@withContext layoutDao.delete(layout)
        }
    }

    override suspend fun getLayout(layoutId: Long): LiveData<Layout> {
        return withContext(Dispatchers.IO) {
            return@withContext layoutDao.getLayout(layoutId)
        }
    }

    override suspend fun getLayoutList(): LiveData<List<Layout>> {
        return withContext(Dispatchers.IO) {
            return@withContext layoutDao.getLayouts()
        }
    }

    override suspend fun getLayoutsAtOnce(): List<Layout> {
        return withContext(Dispatchers.IO) {
            return@withContext layoutDao.getLayoutsAtOnce()
        }
    }

    override suspend fun searchLayoutByText(searchText: String): LiveData<List<Layout>> {
        return withContext(Dispatchers.IO) {
            return@withContext layoutDao.searchLayoutsByText(searchText)
        }
    }


    override suspend fun addCard(card: Card) {
        withContext(Dispatchers.IO) {
            cardDao.insert(card)
        }
    }

    override suspend fun updateCard(card: Card) {
        withContext(Dispatchers.IO) {
            cardDao.update(card)
        }
    }

    override suspend fun deleteCard(card: Card) {
        withContext(Dispatchers.IO) {
            cardDao.delete(card)
        }
    }

    override suspend fun getCardsWithLayoutId(layoutId: Long): List<Card> {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.getCardsWithLayoutId(layoutId)
        }
    }

    override suspend fun searchCardsByText(searchText: String): LiveData<List<Card>> {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.searchCardsByText(searchText)
        }    }

    override suspend fun getCardList(): LiveData<List<Card>> {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.getCards()
        }
    }

    override suspend fun getCard(id: Long): LiveData<Card> {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.getCard(id)
        }
    }

    override suspend fun addRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            routineDao.insert(routine)
        }
    }

    override suspend fun deleteRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            routineDao.delete(routine)
        }
    }

    override suspend fun getRoutine(id: Long): LiveData<Routine> {
        return withContext(Dispatchers.IO) {
            return@withContext routineDao.getRoutine(id)
        }
    }

    override suspend fun getRoutineList(): LiveData<List<Routine>> {
        return withContext(Dispatchers.IO) {
            return@withContext routineDao.getRoutines()
        }
    }

    override suspend fun getLayoutAtOnce(id: Long): Layout {
        return withContext(Dispatchers.IO) {
            return@withContext layoutDao.getLayoutAtOnce(id)
        }
    }

    override suspend fun updateRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
           routineDao.update(routine)
        }
    }

    override suspend fun getCardAtOnce(id: Long): Card {
        return withContext(Dispatchers.IO) {
            return@withContext cardDao.getCardAtOnce(id)
        }
    }
}