package bps.sumsel.st2023.ui.setting

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentSettingBinding
import bps.sumsel.st2023.datastore.UserStore

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: SettingViewModelFactory = SettingViewModelFactory.getInstance(requireActivity())
        val viewModel: SettingViewModel by viewModels {
            factory
        }

        parentActivity = requireActivity() as MainActivity

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token=="") {
                findNavController().navigate(R.id.action_navigation_setting_to_navigation_login)
            }
        }

        binding.relativeLogout.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Confirm")
            builder.setMessage("Anda yakin ingin melakukan logout?")

            builder.setPositiveButton("Ya") { _, _ ->
                viewModel.logout()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}