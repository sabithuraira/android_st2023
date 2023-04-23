package bps.sumsel.st2023.ui.setting

import android.content.Context
import androidx.lifecycle.*
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.AuthRepository

class SettingViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun getAuthUser() = authRepository.getAuthUser()
    fun logout() = authRepository.logout()

//    val resultData = authRepository.resultData
}

class SettingViewModelFactory private constructor(
    private val authRepository: AuthRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SettingViewModelFactory? = null
        fun getInstance(context: Context): SettingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingViewModelFactory(Injection.authRepository(context))
            }.also { instance = it }
    }
}