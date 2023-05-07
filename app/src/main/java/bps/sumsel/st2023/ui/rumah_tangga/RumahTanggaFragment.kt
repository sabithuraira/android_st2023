package bps.sumsel.st2023.ui.rumah_tangga

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentRumahTanggaBinding
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Calendar
import java.util.Date

class RumahTanggaFragment : Fragment() {
    private var _binding: FragmentRumahTanggaBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null
    private var ruta: RutaEntity? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var curLocation: Location? = null
    val viewModel: RumahTanggaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        _binding = FragmentRumahTanggaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.input_ruta, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemFinish = menu.findItem(R.id.action_finish)
        val itemDelete = menu.findItem(R.id.action_delete)

        ruta?.let {
            if (it.id == 0) {
                itemFinish.isVisible = false
                itemDelete.isVisible = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_finish -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }

            R.id.action_delete -> {
                delete()
                true
            }

            R.id.action_save -> {
                save()
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: RumahTanggaViewModel by viewModels { factory }

        sls = RumahTanggaFragmentArgs.fromBundle(arguments as Bundle).sls
        ruta = RumahTanggaFragmentArgs.fromBundle(arguments as Bundle).ruta

        viewModel.resultSingleRuta.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        setView(view, result.data)
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.setSingleRuta(ruta)

        getLastLocation()

        binding.btnStartWawancara.setOnClickListener {
            ruta?.let {
                val currentTime: Date = Calendar.getInstance().time

                it.start_time = currentTime.toString()
                it.start_latitude = curLocation?.latitude.toString()
                it.start_longitude = curLocation?.longitude.toString()

                viewModel.updateRuta(it, false)

                wawancaraStarted()

                Toast.makeText(context, "Wawancara dimulai", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnEndWawancara.setOnClickListener {
            ruta?.let {
                val currentTime: Date = Calendar.getInstance().time

                it.end_time = currentTime.toString()
                it.end_latitude = curLocation?.latitude.toString()
                it.end_longitude = curLocation?.longitude.toString()

                viewModel.updateRuta(it, false)

                wawancaraEnded()

                Toast.makeText(context, "Wawancara diakhiri", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnReWawancara.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Ulangi Wawancara")
            builder.setMessage("Anda yakin ingin mengulangi wawancara?")

            builder.setPositiveButton("Ya") { dialog, _ ->
                ruta?.let {
                    it.start_time = ""
                    it.start_latitude = ""
                    it.start_longitude = ""
                    it.end_time = ""
                    it.end_latitude = ""
                    it.end_longitude = ""

                    viewModel.updateRuta(it, false)

                    wawancaraDeleted()

                    Toast.makeText(context, "Wawancara berhasil dihapus", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(
                        context,
                        "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun save() {
        ruta?.let {
            if (it.id == 0) {
                viewModel.updateRuta(
                    RutaEntity(
                        0, "",
                        sls!!.kode_prov, sls!!.kode_kab, sls!!.kode_kec,
                        sls!!.kode_desa, sls!!.id_sls, sls!!.id_sub_sls,
                        binding.edtNurt.text.toString().toInt(), 0,
                        binding.edtNamaKk.text.toString(),
                        "", "",
                        "", "",
                        "", "",
                        0
                    ), false
                )
            } else {
                it.kepala_ruta = binding.edtNamaKk.text.toString()
                it.nurt = binding.edtNurt.text.toString().toInt()

//                    binding.edtLuasSawah.text.toString().toInt()
//                    binding.edtLuasBukanSawah.text.toString().toInt()
//                    binding.edtLuasPadangRumputSementara.text.toString().toInt()
//                    binding.edtLuasPadangRumputPermanen.text.toString().toInt()
//                    binding.edtLuasSementaraBelumTanam.text.toString().toInt()
//                    binding.edtLuasTanamanTahunan.text.toString().toInt()
//                    binding.edtLuasPertanianLainnya.text.toString().toInt()
//                    binding.edtLuasKegiatanKehutanan.text.toString().toInt()
//                    binding.edtLuasBudidayaPerikanan.text.toString().toInt()
//                    binding.edtLuasLainnya.text.toString().toInt()

                viewModel.updateRuta(it, false)
            }

            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(
                context,
                "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun delete() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

        builder.setTitle("Hapus Data")
        builder.setMessage("Anda yakin ingin menghapus data ini?")

        builder.setPositiveButton("Ya") { dialog, _ ->
            ruta?.let {
                viewModel.delete(it)
            }

            dialog.dismiss()

            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun checkWawancara(startTime: String, endTime: String) {
        if (startTime != "") {
            if (endTime == "") wawancaraStarted()
            else wawancaraEnded()
        } else {
            wawancaraDeleted()
        }
    }

    private fun wawancaraStarted() {
        binding.btnStartWawancara.visibility = View.GONE
        binding.btnEndWawancara.visibility = View.VISIBLE
        binding.btnReWawancara.visibility = View.GONE
    }

    private fun wawancaraEnded() {
        binding.btnStartWawancara.visibility = View.GONE
        binding.btnEndWawancara.visibility = View.GONE
        binding.btnReWawancara.visibility = View.VISIBLE
    }

    private fun wawancaraDeleted() {
        binding.btnStartWawancara.visibility = View.VISIBLE
        binding.btnEndWawancara.visibility = View.GONE
        binding.btnReWawancara.visibility = View.GONE
    }

    private fun setView(view: View, data: RutaEntity?) {
        data?.let {
            if (it.id == 0) {
                binding.linearWawancara.visibility = View.GONE
                binding.linearUsahaTani.visibility = View.GONE

                binding.txtHeaderName.text = "Tambah Rumah Tangga"
            } else {
                binding.linearWawancara.visibility = View.VISIBLE
                binding.linearUsahaTani.visibility = View.VISIBLE

                binding.txtHeaderName.text = "Perbaharui ${it.kepala_ruta}"

                checkWawancara(it.start_time, it.end_time)

                binding.edtNurt.setText(it.nurt.toString())
                binding.edtNamaKk.setText(it.kepala_ruta)
                binding.edtJmlRuta.setText("")
                binding.edtJmlUnitUsaha.setText("")
            }
        } ?: run {
            view.findNavController().navigate(
                RumahTanggaFragmentDirections.actionRumahTanggaFragmentToDetailSlsFragment(sls!!)
            )
        }
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
                findNavController().navigate(
                    RumahTanggaFragmentDirections.actionRumahTanggaFragmentToDetailSlsFragment(sls!!)
                )
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
    }
}