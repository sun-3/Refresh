package application.android.refresh.ui.layouts.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository

class LayoutsSearchViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var list: List<Layout> = listOf()
    val layoutList: LiveData<List<Layout>> = liveData {
        emitSource(refreshRepository.getLayoutList())
    }

    fun searchLayouts(searchText: String): LiveData<List<Layout>> = liveData {
        emitSource(refreshRepository.searchLayoutByText(searchText))
        }
}