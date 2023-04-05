package bps.sumsel.st2023.helper

import android.content.Context
import bps.sumsel.st2023.api.ApiConfig
import bps.sumsel.st2023.room.St2023Database

object Injection {
    fun provideRepository(context: Context): NewsRepository {
        val apiService = ApiConfig.getApiService()
        val database = St2023Database.getInstance(context)
        val dao = database.newsDao()
        val appExecutors = AppExecutors()
        return NewsRepository.getInstance(apiService, dao, appExecutors)
    }
}