package bps.sumsel.st2023.ui.detail_sls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.databinding.FragmentDetailSlsBinding
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.sls.SlsAdapter
import bps.sumsel.st2023.ui.sls.SlsFragmentDirections
import bps.sumsel.st2023.ui.sls.SlsViewModel

class DetailSlsFragment : Fragment() {
    private var _binding: FragmentDetailSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: DetailSlsViewModel by viewModels {
            factory
        }

        sls = DetailSlsFragmentArgs.fromBundle(arguments as Bundle).sls

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRuta.layoutManager = layoutManager

        sls?.let {
            parentActivity.setActionBarTitle(it.nama_sls)
            viewModel.getRuta(it)
        }

        viewModel.resultDataRuta.observe(viewLifecycleOwner){ result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }
                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        val data = result.data

                        data?.let {
                            val rutaAdapter = RutaAdapter(ArrayList(data))
                            rutaAdapter.setOnClickCallBack(object : RutaAdapter.onClickCallBack {
                                override fun onItemClicked(data: RutaEntity) {
                                    editData(view, data)
                                }
                            })

                            binding.rvRuta.apply {
                                adapter = rutaAdapter
                            }
                        }
                    }
                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            view.findNavController().navigate(
                //in case need to create, insert data with 0 id
                DetailSlsFragmentDirections.actionDetailSlsFragmentToRumahTanggaFragment(sls!!, RutaEntity(0))
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editData(view: View, data: RutaEntity){
        view.findNavController().navigate(
            DetailSlsFragmentDirections.actionDetailSlsFragmentToRumahTanggaFragment(sls!!, data)
        )
    }
}