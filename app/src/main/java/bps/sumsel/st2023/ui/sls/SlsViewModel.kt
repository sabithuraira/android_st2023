package bps.sumsel.st2023.ui.sls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository

class SlsViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }

    val text: LiveData<String> = _text

    fun getSls() = slsRepository.getSls()

    fun syncSls() = slsRepository.syncSls()

    val resultData = slsRepository.resultData

//    private fun synsSls(){
//
//    }
}