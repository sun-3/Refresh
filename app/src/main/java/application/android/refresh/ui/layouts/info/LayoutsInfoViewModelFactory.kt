package application.android.refresh.ui.layouts.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class LayoutsInfoViewModelFactory(private val refreshRepository: RefreshRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LayoutsInfoViewModel(refreshRepository) as T
    }
}