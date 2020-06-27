package application.android.refresh.ui.layouts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class LayoutsCreateViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LayoutsCreateViewModel(refreshRepository) as T
    }
}