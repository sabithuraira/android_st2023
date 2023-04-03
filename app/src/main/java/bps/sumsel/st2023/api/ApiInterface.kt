package bps.sumsel.st2023.api

import bps.sumsel.st2023.response.ResponseDestroy
import bps.sumsel.st2023.response.ResponseSingleSls
import bps.sumsel.st2023.response.ResponseSls
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("sls")
    fun listSls(): Call<ResponseSls>

    @POST("sls")
    fun storeSls(@Body params: RequestBody): Call<ResponseSingleSls>

    @PATCH("sls/{id}")
    fun updateSls(@Path("id") id: String, @Body params: RequestBody): Call<ResponseSingleSls>

    @DELETE("sls/{id}")
    fun deleteSls(@Path("id") id: String): Call<ResponseDestroy>
}