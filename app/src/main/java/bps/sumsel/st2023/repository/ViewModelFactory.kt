package bps.sumsel.st2023.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.ui.detail_sls.DetailSlsViewModel
import bps.sumsel.st2023.ui.sls.SlsViewModel

class ViewModelFactory private constructor(private val slsRepository: SlsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when{
            modelClass.isAssignableFrom(SlsViewModel::class.java) -> return  SlsViewModel(slsRepository) as T
            modelClass.isAssignableFrom(DetailSlsViewModel::class.java) -> return  DetailSlsViewModel(slsRepository) as T
        }
//        if (modelClass.isAssignableFrom(SlsViewModel::class.java)) {
//            return SlsViewModel(slsRepository) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.slsRepository(context))
            }.also { instance = it }
    }
}