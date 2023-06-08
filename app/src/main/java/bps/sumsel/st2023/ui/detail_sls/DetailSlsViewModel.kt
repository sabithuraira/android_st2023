package bps.sumsel.st2023.ui.detail_sls

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.SlsEntity

class DetailSlsViewModel(
    private val pref: AuthDataStore,
    private val slsRepository: SlsRepository
    ) : ViewModel() {
    fun getRuta(data: SlsEntity, keyword: String, sortBy: String) = slsRepository.getRuta(data, keyword, sortBy)

    val resultDataRuta = slsRepository.resultDataRuta

    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }
}