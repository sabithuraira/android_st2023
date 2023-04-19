package bps.sumsel.st2023.ui.setting

import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: AuthDataStore) : ViewModel() {
    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserStore) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}