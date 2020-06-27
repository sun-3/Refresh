package application.android.refresh.ui.cards.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class CardsUpdateViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardsUpdateViewModel(refreshRepository) as T
    }
}