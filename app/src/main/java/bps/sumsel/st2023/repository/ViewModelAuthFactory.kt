package bps.sumsel.st2023.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.ui.detail_sls.DetailSlsViewModel
import bps.sumsel.st2023.ui.rumah_tangga.RumahTanggaViewModel

class ViewModelAuthFactory private constructor(
    private val pref: AuthDataStore,
    private val slsRepository: SlsRepository) :
        ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when{
//            modelClass.isAssignableFrom(SlsViewModel::class.java) -> return  SlsViewModel(slsRepository) as T
            modelClass.isAssignableFrom(DetailSlsViewModel::class.java) -> return  DetailSlsViewModel(pref, slsRepository) as T
//            modelClass.isAssignableFrom(EditSlsViewModel::class.java) -> return  EditSlsViewModel(slsRepository) as T
            modelClass.isAssignableFrom(RumahTanggaViewModel::class.java) -> return  RumahTanggaViewModel(pref, slsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }


    companion object {
        @Volatile
        private var instance: ViewModelAuthFactory? = null
        fun getInstance(context: Context, pref: AuthDataStore): ViewModelAuthFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelAuthFactory(pref, Injection.slsRepository(context))
            }.also { instance = it }
    }
}