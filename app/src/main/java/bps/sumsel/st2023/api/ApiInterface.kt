package bps.sumsel.st2023.api

import bps.sumsel.st2023.request.RequestRutaMany
import bps.sumsel.st2023.request.RequestSlsMany
import bps.sumsel.st2023.response.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    //AUTH
    @POST("login")
    fun login(@Body params: RequestBody): Call<ResponseLogin>

    @POST("change_password")
    fun changePassword(@Header("Authorization") token: String, @Body params: RequestBody): Call<ResponseStringStatus>

    //SLS
    @GET("sls/{jenis}/{kode_petugas}/petugas")
    fun listSlsPetugas(
        @Path("jenis") jenis: Int,
        @Path("kode_petugas") kode_petugas: String,
    ): Call<ResponseSlsPetugas>

    @POST("sls")
    fun storeSls(@Body params: RequestBody): Call<ResponseSingleSls>

    @PATCH("sls/{id}")
    fun updateSls(@Path("id") id: String, @Body params: RequestBody): Call<ResponseSingleSls>

    @DELETE("sls/{id}")
    fun deleteSls(@Path("id") id: String): Call<ResponseStringData>

    @POST("ruta/many")
    fun storeRutaMany(
        @Header("Authorization") token: String,
        @Body params: RequestRutaMany
    ): Call<ResponseStringStatus>

    @POST("sls/update_progress")
    fun updateSlsProgress(
        @Header("Authorization") token: String,
        @Body params: RequestSlsMany
    ): Call<ResponseStringStatus>
}