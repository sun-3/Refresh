package application.android.refresh.ui.layouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class LayoutsViewModelFactory(private val refreshRepository: RefreshRepository) : ViewModelProvider
.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LayoutsViewModel(refreshRepository) as T
    }
}