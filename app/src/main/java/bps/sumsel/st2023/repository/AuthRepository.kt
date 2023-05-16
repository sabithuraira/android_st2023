package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.response.ResponseLogin
import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.response.ResponseStringStatus
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthRepository private constructor(
    private val apiService: ApiInterface,
    private val pref: AuthDataStore,
) {
    //    val result = MediatorLiveData<ResultData<UserStore>>()
    private val _resultData = MutableLiveData<ResultData<UserStore>>()
    val resultData: LiveData<ResultData<UserStore>> = _resultData

    fun login(username: String, password: String) {
        _resultData.value = ResultData.Loading

        if (username.isEmpty() || password.isEmpty()) {
            _resultData.value = ResultData.Error("Username atau password harus diisi")
        } else {
            val jsonObject = JSONObject()
            jsonObject.put("email", username)
            jsonObject.put("password", password)

            val jsonObjectString = jsonObject.toString()
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            val client = apiService.login(requestBody)

            client.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        val user = response.body()?.data?.user
                        val jabatan = response.body()?.data?.jabatan

                        val userStore = UserStore(
                            response.body()?.data?.accessToken ?: "",
                            user?.email ?: "",
                            user?.name ?: "",
                            jabatan ?: 0,
                        )

                        _resultData.value = ResultData.Success(userStore)
                        saveUser(userStore)
                    } else {
                        val errorMessage: ResponseStringData = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            ResponseStringData::class.java
                        )
                        _resultData.value = ResultData.Error(errorMessage.datas ?: "")
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    _resultData.value = ResultData.Error(t.message.toString())
                }
            })
        }
    }

    fun logout() {
        saveUser(
            UserStore("", "", "", 0)
        )
    }

    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserStore) {
        runBlocking {
            pref.saveUser(user)
        }
    }

    private val _resultStatus = MutableLiveData<ResultData<String>>()
    val resultStatus: LiveData<ResultData<String>> = _resultStatus

    fun changePassword(password: String) {
        _resultStatus.value = ResultData.Loading

        val user = runBlocking { pref.getUser().first() }

        val jsonObject = JSONObject()

        jsonObject.put("email", user.user)
        jsonObject.put("password", password)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val client = apiService.changePassword(user.token, requestBody)

        client.enqueue(object : Callback<ResponseStringStatus> {
            override fun onResponse(
                call: Call<ResponseStringStatus>,
                response: Response<ResponseStringStatus>
            ) {
                if (response.isSuccessful) {
                    val status = response.body()?.status

                    _resultStatus.value = ResultData.Success(status!!)
                } else {
                    val errorMessage: ResponseStringData = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        ResponseStringData::class.java
                    )
                    _resultStatus.value = ResultData.Error(errorMessage.datas ?: "")
                }
            }

            override fun onFailure(call: Call<ResponseStringStatus>, t: Throwable) {
                _resultStatus.value = ResultData.Error(t.message.toString())
            }
        })
    }

    fun setStatusNull() {
        _resultStatus.value = ResultData.Success("")
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiInterface,
            pref: AuthDataStore
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, pref)
            }.also { instance = it }
    }
}