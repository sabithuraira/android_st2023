package bps.sumsel.st2023.ui.edit_sls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class EditSlsViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    fun updateRuta(data: RutaEntity) = slsRepository.updateRuta(data)

    val resultSingleRuta = slsRepository.resultSingleRuta
}