package application.android.refresh.ui.cards.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class CardsInfoViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardsInfoViewModel(refreshRepository) as T
    }
}