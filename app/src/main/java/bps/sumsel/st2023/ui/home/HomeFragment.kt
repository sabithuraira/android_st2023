package bps.sumsel.st2023.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentHomeBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.room.entity.RekapRutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.setting.SettingFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity
        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val viewModel: HomeViewModel by viewModels {
            HomeViewModelFactory(
                pref,
                Injection.slsRepository(requireContext())
            )
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvProgresSls.layoutManager = layoutManager

        binding.txtVersi.text = "Versi " + SettingFragment().getAppVersion(context!!)

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            var jabatan = ""
            if (user.name != "") {
                when (user.jabatan) {
                    1 -> {
                        jabatan = "PCL"
                    }
                    2 -> {
                        jabatan = "PML"
                    }
                    3 -> {
                        jabatan = "KOSEKA"
                    }
                }
                binding.txtHalo.text = "Halo ${user.name} (${jabatan})"
            }
        }

        viewModel.getSls()
        viewModel.getRekapSls()
        viewModel.getRekapRuta()

        viewModel.resultData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultRekapSls.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {}

                    is ResultData.Success -> {
                        result.data?.let {
                            if (it.isNotEmpty()) {
                                binding.txtJumlahSls.text = it.first().jumlah.toString()
                            } else {
                                binding.txtJumlahSls.text = "0"
                            }
                        }
                    }

                    is ResultData.Error -> {
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultRekapRuta.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {}

                    is ResultData.Success -> {
                        result.data?.let { d ->
                            if (d.isNotEmpty()) {
                                val jumlah: List<Int> = d.map { m -> m.jumlah }
                                binding.txtJumlahRuta.text = jumlah.sum().toString()
                            } else {
                                binding.txtJumlahRuta.text = "0"
                            }
                        }
                    }

                    is ResultData.Error -> {
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultRekap.observe(viewLifecycleOwner) { result ->
            if (result.first != null && result.second != null) {
                loadSls(view, result.first, result.second)
            }
        }

        //synchronize
        binding.linearSync.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Sinkronisasi")
            builder.setMessage("Anda yakin ingin melakukan sinkronisasi? Semua data yang belum diunggah akan hilang.")

            builder.setPositiveButton("Ya") { dialog, _ ->
                viewModel.syncSls()
                viewModel.getRekapSls()
                viewModel.getRekapRuta()

                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun loadSls(view: View, data: List<SlsEntity>?, rekap: List<RekapRutaEntity>?) {
        data?.let {

            val slsAdapter = SlsHomeAdapter(ArrayList(data), ArrayList(rekap))
            slsAdapter.setOnClickCallBack(object : SlsHomeAdapter.OnClickCallBack {
                override fun onItemClicked(data: SlsEntity) {
                    editData(view, data)
                }
            })

            binding.rvProgresSls.apply {
                adapter = slsAdapter
            }
        }
    }

    private fun editData(view: View, data: SlsEntity) {
        view.findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToDetailSlsFragment(data)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}