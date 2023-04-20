package bps.sumsel.st2023.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import bps.sumsel.st2023.api.ApiInterface
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.response.ResponseLogin
import bps.sumsel.st2023.response.ResponseStringData
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository  private constructor(
    private val apiService: ApiInterface
) {
    private val result = MediatorLiveData<ResultData<UserStore>>()

    fun login(username: String, password: String): LiveData<ResultData<UserStore>> {
        result.value = ResultData.Loading

        val jsonObject = JSONObject()
        jsonObject.put("Username", username)
        jsonObject.put("Password", password)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val client = apiService.login(requestBody)

        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data?.user
                    val userStore = UserStore(
                        response.body()?.data?.accessToken ?: "",
                        user?.email ?: "",
                        user?.name ?: ""
                    )

                    result.value = ResultData.Success(userStore)
                } else {
                    val errorMessage: ResponseStringData = Gson().fromJson(response.errorBody()!!.charStream(), ResponseStringData::class.java)
                    result.value = ResultData.Error(errorMessage.datas ?: "")
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                result.value = ResultData.Error(t.message.toString())
            }
        })

        return result
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiInterface
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}