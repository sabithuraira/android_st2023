package bps.sumsel.st2023.ui.edit_sls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentDetailSlsBinding
import bps.sumsel.st2023.databinding.FragmentEditSlsBinding
import bps.sumsel.st2023.databinding.FragmentRumahTanggaBinding

class EditSlsFragment : Fragment() {

    private var _binding: FragmentEditSlsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditSlsBinding.inflate(inflater, container, false)
        return binding.root
    }
}