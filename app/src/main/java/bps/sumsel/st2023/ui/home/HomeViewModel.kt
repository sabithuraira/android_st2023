package bps.sumsel.st2023.ui.home

import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.RekapRutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class HomeViewModel(
    private val pref: AuthDataStore,
    private val slsRepository: SlsRepository
) : ViewModel() {
    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun getSls() = slsRepository.getSls()

    fun syncSls() = slsRepository.syncSls()

    val resultData = slsRepository.resultData

    fun getRekapSls() = slsRepository.getRekapSls()

    val resultRekapSls = slsRepository.resultRekapSls

    fun getRekapRuta() = slsRepository.getRekapRuta()

    val resultRekapRuta = slsRepository.resultRekapRuta

    val resultRekap = MediatorLiveData<Pair<List<SlsEntity>?, List<RekapRutaEntity>?>>()

    init {
        resultRekap.addSource(resultData) { value ->
            when (value) {
                is ResultData.Loading -> {
                    resultRekap.value = Pair(null, null)
                }

                is ResultData.Success -> {
                    value.data.let {
                        resultRekap.value = Pair(it, resultRekap.value?.second)
                    }
                }

                is ResultData.Error -> {
                    resultRekap.value = Pair(null, null)
                }
            }
        }

        resultRekap.addSource(resultRekapRuta) { value ->
            when (value) {
                is ResultData.Loading -> {
                    resultRekap.value = Pair(null, null)
                }

                is ResultData.Success -> {
                    value.data.let {
                        resultRekap.value = Pair(resultRekap.value?.first, it)
                    }
                }

                is ResultData.Error -> {
                    resultRekap.value = Pair(null, null)
                }
            }
        }
    }
}

class HomeViewModelFactory(
    private val pref: AuthDataStore,
    private val slsRepository: SlsRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(pref, slsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}