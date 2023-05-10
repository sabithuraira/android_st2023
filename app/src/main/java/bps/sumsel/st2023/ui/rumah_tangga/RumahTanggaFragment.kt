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
import bps.sumsel.st2023.enum.EnumStatusUpload
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
                it.start_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                it.start_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                it.status_upload = if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

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
                it.end_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                it.end_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                it.status_upload = if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

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
                    it.start_latitude = 0.0
                    it.start_longitude = 0.0

                    it.end_time = ""
                    it.end_latitude = 0.0
                    it.end_longitude = 0.0

                    it.status_upload = if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

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
                it.encId = ""
                it.kode_prov = sls!!.kode_prov
                it.kode_kab = sls!!.kode_kab
                it.kode_kec = sls!!.kode_kec
                it.kode_desa = sls!!.kode_desa
                it.id_sls = sls!!.id_sls
                it.id_sub_sls = sls!!.id_sub_sls

                it.nurt = binding.edtNurt.text.toString().toInt()
                it.kepala_ruta = binding.edtNamaKk.text.toString()

                it.status_upload = EnumStatusUpload.NOT_UPLOADED.kode
            } else {
                it.nurt = binding.edtNurt.text.toString().toInt()
                it.kepala_ruta = binding.edtNamaKk.text.toString()
                it.jumlah_art = binding.edtJmlArt.text.toString().toIntOrNull() ?: 0
                it.jumlah_unit_usaha = binding.edtJmlUnitUsaha.text.toString().toIntOrNull() ?: 0

                it.jml_308_sawah = binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
                it.jml_308_bukan_sawah = binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
                it.jml_308_rumput_sementara = binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
                it.jml_308_rumput_permanen = binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
                it.jml_308_belum_tanam = binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
//                it.jml_308_tanaman_tahunan = binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
                it.jml_308_ternak_bangunan_lain = binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
                it.jml_308_kehutanan = binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
                it.jml_308_budidaya = binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
                it.jml_308_lahan_lainnya = binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0

                it.status_upload = if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode
            }

            viewModel.updateRuta(it, false)

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
                if (it.status_upload == EnumStatusUpload.NOT_UPLOADED.kode) {
                    viewModel.delete(it)
                } else {
                    it.status_upload = EnumStatusUpload.DELETED_AFTER_UPLOADED.kode

                    viewModel.updateRuta(it, true)
                }
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

                binding.edtJmlArt.visibility = View.GONE
                binding.lblJmlArt.visibility = View.GONE
                binding.edtJmlUnitUsaha.visibility = View.GONE
                binding.lblJmlUnitUsaha.visibility = View.GONE

                binding.txtHeaderName.text = "Tambah Rumah Tangga"
            } else {
                binding.linearWawancara.visibility = View.VISIBLE
                binding.linearUsahaTani.visibility = View.VISIBLE

                binding.edtJmlArt.visibility = View.VISIBLE
                binding.lblJmlArt.visibility = View.VISIBLE
                binding.edtJmlUnitUsaha.visibility = View.VISIBLE
                binding.lblJmlUnitUsaha.visibility = View.VISIBLE

                binding.txtHeaderName.text = "Perbarui ${it.kepala_ruta}"

                checkWawancara(it.start_time, it.end_time)

                binding.edtNurt.setText(if (it.nurt.toString() == "0") "" else it.nurt.toString())
                binding.edtNamaKk.setText(it.kepala_ruta)
                binding.edtJmlArt.setText(if (it.jumlah_art.toString() == "0") "" else it.jumlah_art.toString())
                binding.edtJmlUnitUsaha.setText(if (it.jumlah_unit_usaha.toString() == "0") "" else it.jumlah_unit_usaha.toString())

                binding.edtLuasSawah.setText(if (it.jml_308_sawah.toString() == "0") "" else it.jml_308_sawah.toString())
                binding.edtLuasBukanSawah.setText(if (it.jml_308_bukan_sawah.toString() == "0") "" else it.jml_308_bukan_sawah.toString())
                binding.edtLuasRumputSementara.setText(if (it.jml_308_rumput_sementara.toString() == "0") "" else it.jml_308_rumput_sementara.toString())
                binding.edtLuasRumputPermanen.setText(if (it.jml_308_rumput_permanen.toString() == "0") "" else it.jml_308_rumput_permanen.toString())
                binding.edtLuasBelumTanam.setText(if (it.jml_308_belum_tanam.toString() == "0") "" else it.jml_308_belum_tanam.toString())
//                binding.edtLuasTanamanTahunan.setText(if (it.jml_308_tanaman_tahunan.toString() == "0") "" else it.jml_308_tanaman_tahunan.toString())
                binding.edtLuasTernakBangunanLain.setText(if (it.jml_308_ternak_bangunan_lain.toString() == "0") "" else it.jml_308_ternak_bangunan_lain.toString())
                binding.edtLuasKehutanan.setText(if (it.jml_308_kehutanan.toString() == "0") "" else it.jml_308_kehutanan.toString())
                binding.edtLuasBudidaya.setText(if (it.jml_308_budidaya.toString() == "0") "" else it.jml_308_budidaya.toString())
                binding.edtLuasLahanLainnya.setText(if (it.jml_308_lahan_lainnya.toString() == "0") "" else it.jml_308_lahan_lainnya.toString())
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