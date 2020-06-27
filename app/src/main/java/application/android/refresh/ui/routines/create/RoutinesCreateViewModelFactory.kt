package application.android.refresh.ui.routines.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class RoutinesCreateViewModelFactory(private val refreshRepository: RefreshRepository) :
    ViewModelProvider
    .NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoutinesCreateViewModel(refreshRepository) as T
    }
}