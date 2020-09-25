package application.android.refresh.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import application.android.refresh.data.repository.RefreshRepository

class UserLoginViewModelFactory(private val refreshRepository: RefreshRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserLoginViewModel(refreshRepository) as T
    }
}