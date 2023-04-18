package bps.sumsel.st2023.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bps.sumsel.st2023.databinding.FragmentHomeBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.helper.Injection

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(pref, Injection.slsRepository(requireContext())) }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.name!="") {
                binding.txtHalo.text = "Halo ${user.name}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}