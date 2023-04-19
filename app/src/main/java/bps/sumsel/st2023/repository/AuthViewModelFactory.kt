package bps.sumsel.st2023.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.ui.login.LoginViewModel
import bps.sumsel.st2023.ui.setting.SettingViewModel

class AuthViewModelFactory(private val pref: AuthDataStore) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
//            return SettingViewModel(pref) as T
//        }

        when{
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> return SettingViewModel(pref) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(pref) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}