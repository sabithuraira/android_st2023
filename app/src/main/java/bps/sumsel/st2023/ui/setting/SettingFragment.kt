package bps.sumsel.st2023.ui.setting

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentSettingBinding
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: List<SlsEntity>? = null
    private var ruta: List<RutaEntity>? = null
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: SettingViewModelFactory =
            SettingViewModelFactory.getInstance(requireActivity())
        val viewModel: SettingViewModel by viewModels {
            factory
        }
        settingViewModel = viewModel

        parentActivity = requireActivity() as MainActivity

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            if (user.token == "") {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                binding.userName.text = user.name
            }
        }
        viewModel.getSls()
        viewModel.getRutaAll()

        viewModel.resultData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)

                        val data = result.data

                        sls = data
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)

                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultDataRuta.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)

                        val data = result.data

                        ruta = data
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)

                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.version.text = getAppVersion(context!!)

        binding.relativeLogout.setOnClickListener {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Confirm")
            builder.setMessage("Anda yakin ingin melakukan logout?")

            builder.setPositiveButton("Ya") { _, _ ->
                viewModel.logout()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.show()
        }

        binding.relativeChangePassword.setOnClickListener {
            findNavController().navigate(
                SettingFragmentDirections.actionNavigationSettingToChangePasswordFragment()
            )
        }

        binding.relativeBackup.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, "BACKUP_ST23_" + Calendar.getInstance().time)
            }
            backup.launch(intent)
        }

        binding.relativeRestore.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
            }
            restore.launch(intent)
        }
    }

    private val backup =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val gson = Gson()

                val slsJson = gson.toJson(sls).toByteArray()
                val newLine = System.getProperty("line.separator")?.toByteArray()
                val rutaJson = gson.toJson(ruta).toByteArray()

                val uri = it.data?.data

                try {
                    val outputStream =
                        uri?.let { u -> context!!.contentResolver.openOutputStream(u) }

                    outputStream?.write(slsJson)
                    outputStream?.write(newLine)
                    outputStream?.write(rutaJson)

                    outputStream?.close()

                    Toast.makeText(context, "Backup data berhasil", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Backup data gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }

    private val restore =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val gson = Gson()

                val uri = it.data?.data

                try {
                    val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

                    builder.setTitle("Restore Data")
                    builder.setMessage("Apakah yakin ingin merestore data?")

                    builder.setPositiveButton("Ya") { dialog, _ ->
                        val inputStream =
                            uri?.let { u -> context!!.contentResolver.openInputStream(u) }

                        val r = BufferedReader(InputStreamReader(inputStream))

                        val slsJson = r.readLine()
                        val slsType = object : TypeToken<List<SlsEntity>>() {}.type
                        val slsData = gson.fromJson<List<SlsEntity>>(slsJson.toString(), slsType)

                        val rutaJson = r.readLine()
                        val rutaType = object : TypeToken<List<RutaEntity>>() {}.type
                        val rutaData =
                            gson.fromJson<List<RutaEntity>>(rutaJson.toString(), rutaType)

                        runBlocking {
                            settingViewModel.emptyData()
                            settingViewModel.insertData(slsData, rutaData)
                        }

                        inputStream?.close()

                        dialog.dismiss()

                        Toast.makeText(context, "Restore data berhasil", Toast.LENGTH_SHORT)
                            .show()
                    }

                    builder.setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Restore data gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }

    fun getAppVersion(
        context: Context,
    ): String? {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }

            packageInfo.versionName

        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}