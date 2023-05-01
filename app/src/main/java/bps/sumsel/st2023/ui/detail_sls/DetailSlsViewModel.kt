package bps.sumsel.st2023.ui.detail_sls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.SlsEntity

class DetailSlsViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    fun getRuta(data: SlsEntity) = slsRepository.getRuta(data)

    val resultDataRuta = slsRepository.resultDataRuta

}