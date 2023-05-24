package bps.sumsel.st2023.ui.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.databinding.FragmentLoginBinding
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.ui.setting.SettingFragment

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isShowPassword = 1
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(requireActivity())
        val viewModel: LoginViewModel by viewModels {
            factory
        }

        binding.txtVersi.text = "Versi " + SettingFragment().getAppVersion(context!!)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            viewModel.login(email, password)
        }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token != "") {
                findNavController().navigate(LoginFragmentDirections.actionNavigationLoginToNavigationHome())
            }
        }

        viewModel.resultData.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }
                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                    }
                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.showPassBtn.setOnClickListener {
            changePasswordType()
        }
    }

    private fun changePasswordType() {
        if (isShowPassword == 1) {
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            isShowPassword = 0
        } else {
            binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            isShowPassword = 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}