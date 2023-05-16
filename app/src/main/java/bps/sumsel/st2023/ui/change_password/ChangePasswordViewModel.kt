package bps.sumsel.st2023.ui.change_password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.AuthRepository

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun setStatusNull() = authRepository.setStatusNull()

    fun changePassword(password: String) = authRepository.changePassword(password)

    val resultStatus = authRepository.resultStatus
}

class ChangePasswordViewModelFactory private constructor(
    private val authRepository: AuthRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ChangePasswordViewModelFactory? =
            null

        fun getInstance(context: Context): ChangePasswordViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ChangePasswordViewModelFactory(
                    Injection.authRepository(context)
                )
            }.also { instance = it }
    }
}