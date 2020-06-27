package application.android.refresh.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class RoutinesViewModelFactory(private val refreshRepository: RefreshRepository) : ViewModelProvider
.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoutinesViewModel(refreshRepository) as T
    }
}