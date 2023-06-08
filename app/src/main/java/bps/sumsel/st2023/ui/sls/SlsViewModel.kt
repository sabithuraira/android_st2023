package bps.sumsel.st2023.ui.sls

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.SlsRepository
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsViewModel(private val pref: AuthDataStore, private val slsRepository: SlsRepository) :
    ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }

    val text: LiveData<String> = _text

    fun getSls() = slsRepository.getSls()

    val resultData = slsRepository.resultData

    fun getSlsByName(keyword: String) = slsRepository.getSlsByName(keyword)

    fun upload(context: Context) = slsRepository.upload(context)
    fun upload(sls: SlsEntity, context: Context) = slsRepository.upload(sls, context)

    val resultUploadRuta = slsRepository.resultUploadRuta
    val resultUploadSls = slsRepository.resultUploadSls
    val resultUpload = slsRepository.resultUpload

    fun syncSls() = slsRepository.syncSls()

    fun syncSls(sls: SlsEntity) = slsRepository.syncSls(sls)

    fun getAuthUser(): LiveData<UserStore> {
        return pref.getUser().asLiveData()
    }
}