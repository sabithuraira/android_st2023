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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentHomeBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.helper.Injection
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.room.entity.SlsEntity

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")
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
        val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(pref, Injection.slsRepository(requireContext())) }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvProgresSls.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvProgresSls.addItemDecoration(itemDecoration)

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.name!="") {
                binding.txtHalo.text = "Halo ${user.name}"
            }
        }

        viewModel.resultData.observe(this) {  result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }
                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        loadSls(view, result.data)
                    }
                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //syncronize
        binding.linearSync.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Sinkronisasi")
            builder.setMessage("Anda yakin ingin melakukan sinkronisasi? Semua data yang belum diunggah akan hilang.")

            builder.setPositiveButton("Ya") { dialog, _ ->
                viewModel.syncSls()
                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.show()
        }

        viewModel.getSls()
    }

    private fun loadSls(view: View, data: List<SlsEntity>?){
        data?.let {
            val slsAdapter = SlsHomeAdapter(ArrayList(data))
            slsAdapter.setOnClickCallBack(object : SlsHomeAdapter.onClickCallBack {
                override fun onItemClicked(data: SlsEntity) {
                    editData(view, data)
                }
            })

            binding.rvProgresSls.apply {
                adapter = slsAdapter
            }
        }
    }

    private fun editData(view: View, data: SlsEntity){
//        view.findNavController().navigate(
//            TodosFragmentDirections.actionTodosFragmentToFormFragment(data)
//        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}