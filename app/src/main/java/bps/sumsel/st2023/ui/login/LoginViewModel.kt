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
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.response.ResponseLogin
import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.ui.sls.SlsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val slsRepository: SlsRepository,
    private val authRepository: AuthRepository
    ) : ViewModel() {
    fun getAuthUser() = authRepository.getAuthUser()
    fun setEmptyUser() = authRepository.logout()
    fun emptyData() = slsRepository.emptyData()

    fun login(username: String, password: String){
        slsRepository.emptyData()
        authRepository.login(username, password)
    }

    val resultData = authRepository.resultData
}

class LoginViewModelFactory private constructor(
    private val slsRepository: SlsRepository,
    private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(slsRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    Injection.slsRepository(context),
                    Injection.authRepository(context)
                )
            }.also { instance = it }
    }
}