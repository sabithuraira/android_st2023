package bps.sumsel.st2023.ui.home

import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.SlsRepository

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