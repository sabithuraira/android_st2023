package bps.sumsel.st2023.ui.rumah_tangga

import android.location.Location
import androidx.lifecycle.*
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.home.HomeViewModel

class RumahTanggaViewModel(
    private val pref: AuthDataStore,
    private val slsRepository: SlsRepository) : ViewModel() {

    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }

    fun updateRuta(data: RutaEntity, isFinish: Boolean) = slsRepository.updateRuta(data, isFinish)
    fun delete(data: RutaEntity) = slsRepository.deleteRuta(data)
    fun setSingleRuta(data: RutaEntity?) = slsRepository.setSingleRuta(data)

    val resultSingleRuta = slsRepository.resultSingleRuta
    val resultLastNurt = slsRepository.resultLastNurt

    fun getLastNurt(data: SlsEntity) = slsRepository.getLastNumber(data)


//    private val _curLocation = MutableLiveData<Location?>()
//    val curLocation: LiveData<Location?> = _curLocation
//
//    fun setLocation(data: Location?){
//        _curLocation.value = data
//    }

}