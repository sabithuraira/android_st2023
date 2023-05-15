package bps.sumsel.st2023.ui.detail_sls

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentDetailSlsBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelAuthFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class DetailSlsFragment : Fragment() {
    private var _binding: FragmentDetailSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentDetailSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val factory: ViewModelAuthFactory = ViewModelAuthFactory.getInstance(requireActivity(), pref)
        val viewModel: DetailSlsViewModel by viewModels { factory }

        sls = DetailSlsFragmentArgs.fromBundle(arguments as Bundle).sls

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRuta.layoutManager = layoutManager

        sls?.let {
            parentActivity.setActionBarTitle(it.nama_sls)
            viewModel.getRuta(it, binding.edtSearch.text.toString())
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                sls?.let {
                    viewModel.getRuta(it, binding.edtSearch.text.toString())
                }
            }
        })

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

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            //WHEN NOT LOGIN AS PPL, DISABLED TAMBAH RUTA BUTTON CONTROL
            if(user.jabatan !=1){
                setHasOptionsMenu(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_sls, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                findNavController().navigate(
                    DetailSlsFragmentDirections.actionDetailSlsFragmentToRumahTanggaFragment(sls!!, RutaEntity(0))
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
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