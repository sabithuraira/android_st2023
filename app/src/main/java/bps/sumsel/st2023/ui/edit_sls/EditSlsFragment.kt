package bps.sumsel.st2023.ui.edit_sls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentDetailSlsBinding
import bps.sumsel.st2023.databinding.FragmentEditSlsBinding
import bps.sumsel.st2023.databinding.FragmentRumahTanggaBinding
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.detail_sls.DetailSlsFragmentArgs

class EditSlsFragment : Fragment() {
    private var _binding: FragmentEditSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}