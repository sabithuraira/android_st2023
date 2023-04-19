package bps.sumsel.st2023.ui.setting

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
import bps.sumsel.st2023.databinding.FragmentSettingBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.AuthViewModelFactory

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val viewModel: SettingViewModel by viewModels { AuthViewModelFactory(pref) }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token=="") {
                findNavController().navigate(R.id.action_navigation_setting_to_navigation_login)
            }
        }

        binding.relativeLogout.setOnClickListener {
            viewModel.saveUser(
                UserStore("", "", "")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}