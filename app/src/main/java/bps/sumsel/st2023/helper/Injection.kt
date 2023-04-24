package bps.sumsel.st2023.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import bps.sumsel.st2023.api.ApiConfig
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.repository.AuthRepository
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.St2023Database

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")
object Injection {
    fun slsRepository(context: Context): SlsRepository {
        val apiService = ApiConfig.getApiService()
        val database = St2023Database.getInstance(context)
        val dao = database.slsDao()
        val pref = AuthDataStore.getInstance(context.dataStore)
        return SlsRepository.getInstance(apiService, dao, pref)
    }

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val pref = AuthDataStore.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, pref)
    }
}