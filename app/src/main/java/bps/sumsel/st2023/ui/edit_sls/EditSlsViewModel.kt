package bps.sumsel.st2023.ui.edit_sls

import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.SlsEntity

class EditSlsViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    fun setSingleData(data: SlsEntity) = slsRepository.setSingleData(data)

    val resultSingleData = slsRepository.resultSingleData

    fun updateSls(data: SlsEntity) = slsRepository.updateSls(data)
}