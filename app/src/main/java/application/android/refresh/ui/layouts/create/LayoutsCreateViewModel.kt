package application.android.refresh.ui.layouts.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.data.repository.RefreshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LayoutsCreateViewModel(private val refreshRepository: RefreshRepository) : ViewModel() {

    val isOkayToExit = MutableLiveData<Boolean>()

    fun addLayout(layout: Layout) {
        viewModelScope.launch(Dispatchers.IO) {
            isOkayToExit.postValue(false)
            coroutineScope {
                launch {
                    refreshRepository.addLayout(layout)
                }
            }
            isOkayToExit.postValue(true)
        }
    }

}