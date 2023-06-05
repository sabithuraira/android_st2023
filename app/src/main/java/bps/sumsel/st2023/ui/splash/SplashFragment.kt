package bps.sumsel.st2023.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import bps.sumsel.st2023.databinding.FragmentSplashBinding
import bps.sumsel.st2023.ui.setting.SettingFragment

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtVersi.text = "Versi " + SettingFragment().getAppVersion(context!!)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            try {
                Navigation.findNavController(view).navigate(
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                )
            } catch (e: Exception) {
            }
        }, 2000)

        binding.layout.setOnClickListener {
            Navigation.findNavController(view).navigate(
                SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            )
        }
    }
}