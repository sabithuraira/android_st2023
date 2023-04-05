package bps.sumsel.st2023.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
        const val baseUrl = "http://192.168.5.127/laravel-app/public/api/"

        fun getApiService(): ApiInterface{
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}