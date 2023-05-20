package bps.sumsel.st2023.ui.setting

import android.content.Context
import androidx.lifecycle.*
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.AuthRepository
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class SettingViewModel(
    private val authRepository: AuthRepository, private val slsRepository: SlsRepository
) : ViewModel() {
    fun getAuthUser() = authRepository.getAuthUser()

    fun logout() = authRepository.logout()

    val resultData = slsRepository.resultData

    fun getSls() = slsRepository.getSls()

    val resultDataRuta = slsRepository.resultDataRuta

    fun getRutaAll() = slsRepository.getRutaAll()

    fun emptyData() = slsRepository.emptyData()

    fun insertData(sls: List<SlsEntity>, ruta: List<RutaEntity>) =
        slsRepository.insertData(sls, ruta)
}

class SettingViewModelFactory private constructor(
    private val authRepository: AuthRepository, private val slsRepository: SlsRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(authRepository, slsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SettingViewModelFactory? = null
        fun getInstance(context: Context): SettingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingViewModelFactory(
                    Injection.authRepository(context),
                    Injection.slsRepository(context)
                )
            }.also { instance = it }
    }
}