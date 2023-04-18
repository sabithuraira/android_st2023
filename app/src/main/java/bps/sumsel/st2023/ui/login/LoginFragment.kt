package bps.sumsel.st2023.ui.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentLoginBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.ui.sls.SlsViewModel

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val viewModel: LoginViewModel by viewModels { LoginViewModelFactory(pref) }

        binding.btnLogin.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
            viewModel.saveUser(
                UserStore("123456", "myusername", "myname")
            )
        }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token!="") {
                findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}