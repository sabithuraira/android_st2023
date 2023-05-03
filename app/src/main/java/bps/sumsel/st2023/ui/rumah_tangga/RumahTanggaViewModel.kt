package bps.sumsel.st2023.ui.rumah_tangga

import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.RutaEntity

class RumahTanggaViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    fun updateRuta(data: RutaEntity) = slsRepository.updateRuta(data)
    fun delete(data: RutaEntity) = slsRepository.deleteRuta(data)
    fun setSingleRuta(data: RutaEntity?) = slsRepository.setSingleRuta(data)

    val resultSingleRuta = slsRepository.resultSingleRuta
}