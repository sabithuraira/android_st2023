package bps.sumsel.st2023.ui.sls

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import bps.sumsel.st2023.databinding.FragmentSlsBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelAuthFactory
import bps.sumsel.st2023.room.entity.SlsEntity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

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

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val factory: ViewModelAuthFactory =
            ViewModelAuthFactory.getInstance(requireActivity(), pref)
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

                        val slsAdapter =
                            SlsAdapter(ArrayList(data), viewModel.getAuthUser(), viewLifecycleOwner)
                        slsAdapter.setOnClickCallBack(object : SlsAdapter.OnClickCallBack {
                            override fun onItemPendampingan(data: SlsEntity, userStore: UserStore) {
                                pendampinganData(view, data, userStore)
                            }

                            override fun onItemChoose(data: SlsEntity) {
                                chooseData(view, data)
                            }

                            override fun onItemProgress(data: SlsEntity) {
                                progressData(view, data)
                            }

                            override fun onItemUpload(data: SlsEntity) {
                                uploadData(viewModel, data)
                            }
                        })

                        binding.rvSls.apply {
                            adapter = slsAdapter
                        }
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultUploadRuta.observe(viewLifecycleOwner) { result ->
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
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        viewModel.resultUploadSls.observe(viewLifecycleOwner) { result ->
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
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        viewModel.resultUpload.observe(viewLifecycleOwner) { result ->
            if (result.first == 2) {
                parentActivity.setLoading(false)

                result.second?.let {
                    viewModel.syncSls(it)
                } ?: run {
                    viewModel.syncSls()
                }
                viewModel.resultUpload.value = Pair(0, null)

                Toast.makeText(context, "Upload data berhasil", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.getSlsByName(binding.edtSearch.text.toString())

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.getSlsByName(binding.edtSearch.text.toString())
            }
        })

        binding.btnUpload.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Upload Data")
            builder.setMessage("Anda yakin ingin mengupload data?")

            builder.setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                viewModel.upload(context!!)
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun pendampinganData(view: View, data: SlsEntity, userStore: UserStore) {
        view.findNavController().navigate(
            SlsFragmentDirections.actionNavigationSlsToPendampinganFragment(data, userStore)
        )
    }

    private fun chooseData(view: View, data: SlsEntity) {
        view.findNavController().navigate(
            SlsFragmentDirections.actionNavigationSlsToDetailSlsFragment(data)
        )
    }

    private fun progressData(view: View, data: SlsEntity) {
        view.findNavController().navigate(
            SlsFragmentDirections.actionNavigationSlsToEditSlsFragment(data)
        )
    }

    private fun uploadData(viewModel: SlsViewModel, data: SlsEntity) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

        builder.setTitle("Upload Data")
        builder.setMessage("Anda yakin ingin mengupload data?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            dialog.dismiss()
            viewModel.upload(data, context!!)
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}