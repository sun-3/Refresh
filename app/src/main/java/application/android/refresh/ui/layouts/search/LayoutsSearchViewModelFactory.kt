package application.android.refresh.ui.layouts.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class LayoutsSearchViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider
.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LayoutsSearchViewModel(refreshRepository) as T
    }
}