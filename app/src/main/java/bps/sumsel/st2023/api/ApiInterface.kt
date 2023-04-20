package bps.sumsel.st2023.api

import bps.sumsel.st2023.response.ResponseStringData
import bps.sumsel.st2023.response.ResponseLogin
import bps.sumsel.st2023.response.ResponseSingleSls
import bps.sumsel.st2023.response.ResponseSls
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    //AUTH
    @POST("login")
    fun login(@Body params: RequestBody): Call<ResponseLogin>

    //SLS
    @GET("sls")
    fun listSls(): Call<ResponseSls>

    @POST("sls")
    fun storeSls(@Body params: RequestBody): Call<ResponseSingleSls>

    @PATCH("sls/{id}")
    fun updateSls(@Path("id") id: String, @Body params: RequestBody): Call<ResponseSingleSls>

    @DELETE("sls/{id}")
    fun deleteSls(@Path("id") id: String): Call<ResponseStringData>

}