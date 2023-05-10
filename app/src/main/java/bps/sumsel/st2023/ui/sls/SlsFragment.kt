package bps.sumsel.st2023.ui.sls

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentSlsBinding
import bps.sumsel.st2023.enum.EnumStatusUpload
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsFragment : Fragment() {
    private var _binding: FragmentSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: SlsViewModel by viewModels {
            factory
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSls.layoutManager = layoutManager

        viewModel.getSls()

        viewModel.resultData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        val data = result.data

                        val slsAdapter = SlsAdapter(ArrayList(data))
                        slsAdapter.setOnClickCallBack(object : SlsAdapter.onClickCallBack {
                            override fun onItemClicked(data: SlsEntity) {
                                editData(view, data)
                            }
                        })

                        binding.rvSls.apply {
                            adapter = slsAdapter
                        }
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnUpload.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Upload Data")
            builder.setMessage("Anda yakin ingin mengupload data?")

            builder.setPositiveButton("Ya") { dialog, _ ->
                viewModel.storeRutaMany()

                viewModel.resultUpload.observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultData.Loading -> {
                                parentActivity.setLoading(true)
                            }

                            is ResultData.Success -> {
                                parentActivity.setLoading(false)

                                Toast.makeText(context, "Upload berhasil", Toast.LENGTH_SHORT).show()
                            }

                            is ResultData.Error -> {
                                parentActivity.setLoading(false)
                                Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun editData(view: View, data: SlsEntity) {
        view.findNavController().navigate(
            SlsFragmentDirections.actionNavigationSlsToDetailSlsFragment(data)
        )
    }
}