package application.android.refresh.ui.layouts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class LayoutsViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {
    var list: List<Layout> = listOf()
    val groupieAdapter = GroupAdapter<GroupieViewHolder>()
    var groupieList: List<LayoutItem> = listOf()
    val layoutList: LiveData<List<Layout>> = liveData {
        emitSource(refreshRepository.getLayoutList())
    }

    fun searchLayouts(searchText: String): LiveData<List<Layout>> = liveData {
        emitSource(refreshRepository.searchLayoutByText(searchText))
    }
}