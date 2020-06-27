package application.android.refresh.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class CardsViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardsViewModel(refreshRepository) as T
    }
}