package application.android.refresh.ui.routines.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class RoutinesInfoViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory
    () {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoutinesInfoViewModel(refreshRepository) as T
    }
}