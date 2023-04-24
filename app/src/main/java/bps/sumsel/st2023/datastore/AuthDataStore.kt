package bps.sumsel.st2023.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataStore private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var INSTANCE: AuthDataStore? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthDataStore {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthDataStore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private val TOKEN_KEY =  stringPreferencesKey("token_setting")
    private val USER_KEY =  stringPreferencesKey("user_setting")
    private val NAME_KEY =  stringPreferencesKey("name_setting")
    private val JABATAN_KEY =  intPreferencesKey("jabatan_setting")

    fun getUser(): Flow<UserStore> {
        return dataStore.data.map { preferences ->
            UserStore(
                preferences[TOKEN_KEY] ?: "",
                preferences[USER_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[JABATAN_KEY] ?: 0,
            )
        }
    }

    suspend fun saveUser(userStore: UserStore) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = userStore.token
            preferences[USER_KEY] = userStore.user
            preferences[NAME_KEY] = userStore.name
            preferences[JABATAN_KEY] = userStore.jabatan
        }
    }
}

data class UserStore(
    val token: String,
    val user: String,
    val name: String,
    val jabatan: Int
)