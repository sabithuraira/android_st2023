package bps.sumsel.st2023.ui.pendampingan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentPendampinganBinding
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.enum.EnumJabatan
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.PendampinganEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PendampinganFragment : Fragment() {
    private var _binding: FragmentPendampinganBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private lateinit var pendampinganViewModel: PendampinganViewModel
    private var sls: SlsEntity? = null
    private var user: UserStore? = null
    private val gson = Gson()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var curLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        _binding = FragmentPendampinganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: PendampinganViewModel by viewModels {
            factory
        }

        pendampinganViewModel = viewModel

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvPendampingan.layoutManager = layoutManager

        sls = PendampinganFragmentArgs.fromBundle(arguments as Bundle).sls
        user = PendampinganFragmentArgs.fromBundle(arguments as Bundle).user

        sls?.let {
            parentActivity.setActionBarTitle(it.nama_sls)
            viewModel.setSingleData(it)
        }

        createLocationRequest()
        createLocationCallback()
        startLocationUpdates()

        viewModel.resultSingleData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        val data = result.data

                        if (user!!.jabatan == EnumJabatan.PML.kode) {
                            val pendampinganList = stringToJson(data?.pendampingan_pml)

                            pendampinganList?.let {
                                val pendampinganAdapter = PendampinganAdapter(ArrayList(it))

                                pendampinganAdapter.setOnClickCallBack(object :
                                    PendampinganAdapter.OnClickCallBack {
                                    override fun onItemDelete(position: Int) {
                                        deletePendampingan(position, user!!.jabatan)
                                    }
                                })

                                binding.rvPendampingan.apply {
                                    adapter = pendampinganAdapter
                                }
                            } ?: run {
                                binding.rvPendampingan.apply {
                                    adapter = null
                                }
                            }
                        } else if (user!!.jabatan == EnumJabatan.KOSEKA.kode) {
                            val pendampinganList = stringToJson(data?.pendampingan_koseka)

                            pendampinganList?.let {
                                val pendampinganAdapter = PendampinganAdapter(ArrayList(it))

                                pendampinganAdapter.setOnClickCallBack(object :
                                    PendampinganAdapter.OnClickCallBack {
                                    override fun onItemDelete(position: Int) {
                                        deletePendampingan(position, user!!.jabatan)
                                    }
                                })

                                binding.rvPendampingan.apply {
                                    adapter = pendampinganAdapter
                                }
                            } ?: run {
                                binding.rvPendampingan.apply {
                                    adapter = null
                                }
                            }
                        }
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pendampingan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                addPendampingan(user!!.jabatan)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun stringToJson(data: String?): List<PendampinganEntity>? {
        val pendampinganType = object : TypeToken<List<PendampinganEntity>>() {}.type
        val pendampinganData = gson.fromJson<List<PendampinganEntity>>(data, pendampinganType)

        return pendampinganData
    }

    private fun addPendampingan(jabatan: Int) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

        builder.setTitle("Pendampingan")
        builder.setMessage("Anda yakin ingin melakukan pendampingan sekarang?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            sls?.let {
                val pendampinganList = mutableListOf<PendampinganEntity>()

                val currentTime: Date = Calendar.getInstance().time
                val currentTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                val pendampingan = PendampinganEntity(
                    currentTimeFormat.format(currentTime).toString(),
                    curLocation!!.latitude,
                    curLocation!!.longitude
                )

                when (jabatan) {
                    EnumJabatan.PML.kode -> {
                        val pendampinganListOriginal = stringToJson(it.pendampingan_pml)

                        pendampinganListOriginal?.forEach { p ->
                            pendampinganList.add(p)
                        }

                        pendampinganList.add(pendampingan)

                        it.pendampingan_pml = gson.toJson(pendampinganList)
                    }
                    EnumJabatan.KOSEKA.kode -> {
                        val pendampinganListOriginal = stringToJson(it.pendampingan_koseka)

                        pendampinganListOriginal?.forEach { p ->
                            pendampinganList.add(p)
                        }

                        pendampinganList.add(pendampingan)

                        it.pendampingan_koseka = gson.toJson(pendampinganList)
                    }
                }

                pendampinganViewModel.updateSls(it)
                pendampinganViewModel.setSingleData(it)

                Toast.makeText(context, "Pendampingan berhasil ditambahkan", Toast.LENGTH_SHORT)
                    .show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun deletePendampingan(position: Int, jabatan: Int) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

        builder.setTitle("Hapus Pendampingan")
        builder.setMessage("Anda yakin ingin menghapus pendampingan ini?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            sls?.let {
                val pendampinganList = mutableListOf<PendampinganEntity>()

                when (jabatan) {
                    EnumJabatan.PML.kode -> {
                        val pendampinganListOriginal = stringToJson(it.pendampingan_pml)

                        pendampinganListOriginal?.forEach { p ->
                            pendampinganList.add(p)
                        }

                        pendampinganList.removeAt(position)

                        it.pendampingan_pml = gson.toJson(pendampinganList)
                    }
                    EnumJabatan.KOSEKA.kode -> {
                        val pendampinganListOriginal = stringToJson(it.pendampingan_koseka)

                        pendampinganListOriginal?.forEach { p ->
                            pendampinganList.add(p)
                        }

                        pendampinganList.removeAt(position)

                        it.pendampingan_koseka = gson.toJson(pendampinganList)
                    }
                }

                pendampinganViewModel.updateSls(it)
                pendampinganViewModel.setSingleData(it)

                Toast.makeText(context, "Pendampingan berhasil dihapus", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK ->
                    Log.i(
                        ContentValues.TAG,
                        "onActivityResult: All location settings are satisfied."
                    )
                Activity.RESULT_CANCELED ->
                    Toast.makeText(
                        requireContext(),
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(10)
            maxWaitTime = TimeUnit.SECONDS.toMillis(10)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener { getLastLocation() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(requireActivity(), sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(
                        ContentValues.TAG,
                        "onLocationResult: " + location.latitude + ", " + location.longitude
                    )
                    curLocation = location
                }
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(ContentValues.TAG, "Error : " + exception.message)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }

            else -> {
                Toast.makeText(
                    context,
                    "Anda harus mengizinkan aplikasi ini mendeteksi lokasi untuk menjalankan operasi ini",
                    Toast.LENGTH_SHORT
                ).show()

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    curLocation = location
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lokasi tidak ditemukan. Coba lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
}