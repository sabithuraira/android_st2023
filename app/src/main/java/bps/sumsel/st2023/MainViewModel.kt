package bps.sumsel.st2023

import androidx.lifecycle.ViewModel
import bps.sumsel.st2023.repository.SlsRepository

class MainViewModel(private val slsRepository: SlsRepository) : ViewModel() {
    fun getSls() = slsRepository.getSls()
}
