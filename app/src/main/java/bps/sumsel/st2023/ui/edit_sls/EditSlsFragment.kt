package bps.sumsel.st2023.ui.edit_sls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentEditSlsBinding
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.SlsEntity

class EditSlsFragment : Fragment() {
    private var _binding: FragmentEditSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: EditSlsViewModel by viewModels { factory }

        sls = EditSlsFragmentArgs.fromBundle(arguments as Bundle).sls

        viewModel.setSingleData(sls!!)

        viewModel.resultSingleData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        val data = result.data

                        setView(view, data)
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnProgres.setOnClickListener {
            sls?.let {
                it.status_selesai_pcl = 0

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    it.nama_sls + " belum selesai dikerjakan",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSelesai.setOnClickListener {
            sls?.let {
                it.status_selesai_pcl = 1

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    it.nama_sls + " sudah selesai dikerjakan",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun setView(view: View, data: SlsEntity?) {
        data?.let {
            binding.edtNamaSls.setText(it.nama_sls)
            binding.edtIdDesa.setText(it.kode_prov + it.kode_kab + it.kode_kec + it.kode_desa)

            if (it.status_selesai_pcl == 0) {
                binding.btnProgres.isEnabled = false
                binding.btnProgres.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    )
                )
                binding.btnSelesai.isEnabled = true
            } else {
                binding.btnProgres.isEnabled = true
                binding.btnSelesai.isEnabled = false
                binding.btnSelesai.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    )
                )
            }
        } ?: run {
            view.findNavController().navigate(
                EditSlsFragmentDirections.actionEditSlsFragmentToNavigationSls()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}