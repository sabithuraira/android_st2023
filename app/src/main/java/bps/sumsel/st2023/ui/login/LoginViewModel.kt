package bps.sumsel.st2023.ui.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.AuthRepository
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.ui.sls.SlsViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val pref: AuthDataStore,
    private val authRepository: AuthRepository
    ) : ViewModel() {
    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserStore) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun login(username: String, password: String) = authRepository.login(username, password)
}

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")
class LoginViewModelFactory private constructor(
    private val pref: AuthDataStore,
    private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                val pref = AuthDataStore.getInstance(context.dataStore)
                instance ?: LoginViewModelFactory(pref, Injection.authRepository(context))
            }.also { instance = it }
    }
}