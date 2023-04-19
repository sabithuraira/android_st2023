package bps.sumsel.st2023.ui.login

import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: AuthDataStore) : ViewModel() {
    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserStore) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}