package bps.sumsel.st2023.ui.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentLoginBinding
import bps.sumsel.st2023.datastore.UserStore

//private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isShowPassword = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val pref = AuthDataStore.getInstance(requireContext().dataStore)
//        val viewModel: LoginViewModel by viewModels { AuthViewModelFactory(pref) }

        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(requireActivity())
        val viewModel: LoginViewModel by viewModels {
            factory
        }

        binding.btnLogin.setOnClickListener {
            viewModel.saveUser(
                UserStore("123456", "myusername", "myname")
            )
        }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token!="") {
                findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
            }
        }

        binding.showPassBtn.setOnClickListener {
            changePasswordType()
        }
    }

    private fun changePasswordType(){
        if(isShowPassword==1){
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            isShowPassword = 0
        }
        else{
            binding.edtPassword.transformationMethod =  PasswordTransformationMethod.getInstance()
            isShowPassword = 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}