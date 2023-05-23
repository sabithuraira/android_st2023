package bps.sumsel.st2023.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.ui.edit_sls.EditSlsViewModel
import bps.sumsel.st2023.ui.pendampingan.PendampinganViewModel

class ViewModelFactory private constructor(private val slsRepository: SlsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when{
            modelClass.isAssignableFrom(EditSlsViewModel::class.java) -> return  EditSlsViewModel(slsRepository) as T
            modelClass.isAssignableFrom(PendampinganViewModel::class.java) -> return  PendampinganViewModel(slsRepository) as T
        }

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