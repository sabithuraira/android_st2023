package bps.sumsel.st2023.helper

import android.content.Context
import bps.sumsel.st2023.api.ApiConfig
import bps.sumsel.st2023.repository.AuthRepository
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.St2023Database

object Injection {
    fun slsRepository(context: Context): SlsRepository {
        val apiService = ApiConfig.getApiService()
        val database = St2023Database.getInstance(context)
        val dao = database.slsDao()
        return SlsRepository.getInstance(apiService, dao)
    }

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
}