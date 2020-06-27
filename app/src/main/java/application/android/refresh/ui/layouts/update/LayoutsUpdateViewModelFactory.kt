package application.android.refresh.ui.layouts.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class LayoutsUpdateViewModelFactory(private val refreshRepository: RefreshRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LayoutsUpdateViewModel(refreshRepository) as T
    }
}