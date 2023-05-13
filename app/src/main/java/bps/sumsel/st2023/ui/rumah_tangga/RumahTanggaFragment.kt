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
import android.widget.EditText
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
import bps.sumsel.st2023.enum.EnumStatusData
import bps.sumsel.st2023.enum.EnumStatusUpload
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.detail_sls.DetailSlsFragmentDirections
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        _binding = FragmentRumahTanggaBinding.inflate(inflater, container, false)
        return binding.root
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
                        ruta = result.data
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        ruta?.let {
            if (ruta?.id == 0) {
                setView(view, it)

                val currentTime: Date = Calendar.getInstance().time

                it.encId = ""
                it.kode_prov = sls!!.kode_prov
                it.kode_kab = sls!!.kode_kab
                it.kode_kec = sls!!.kode_kec
                it.kode_desa = sls!!.kode_desa
                it.id_sls = sls!!.id_sls
                it.id_sub_sls = sls!!.id_sub_sls

                it.start_time = currentTime.toString()
                it.start_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                it.start_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                it.status_upload = EnumStatusUpload.NOT_UPLOADED.kode

                it.status_data = EnumStatusData.ERROR.kode

                viewModel.updateRuta(it, false)
            } else {
                viewModel.setSingleRuta(it)
            }
        } ?: run {
            Toast.makeText(context,
                "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                Toast.LENGTH_SHORT
            ).show()
        }

        getLastLocation()

        binding.btnSave.setOnClickListener {
            ruta?.let {
                val currentTime: Date = Calendar.getInstance().time

                it.end_time = currentTime.toString()
                it.end_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                it.end_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                it.nurt = binding.edtNurt.text.toString().toInt()
                it.kepala_ruta = binding.edtNamaKk.text.toString()
                it.jumlah_art = binding.edtJmlArt.text.toString().toIntOrNull() ?: 0
                it.jumlah_unit_usaha = binding.edtJmlUnitUsaha.text.toString().toIntOrNull() ?: 0

                it.jml_308_sawah = binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
                it.jml_308_bukan_sawah =
                    binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
                it.jml_308_rumput_sementara =
                    binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
                it.jml_308_rumput_permanen =
                    binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
                it.jml_308_belum_tanam =
                    binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
                it.jml_308_tanaman_tahunan =
                    binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
                it.jml_308_ternak_bangunan_lain =
                    binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
                it.jml_308_kehutanan = binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
                it.jml_308_budidaya = binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
                it.jml_308_lahan_lainnya =
                    binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0

                it.status_upload =
                    if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

                validation()

                if(listError.isNotEmpty()){
                    it.status_data = EnumStatusData.ERROR.kode
                }
                else{
                    it.status_data = EnumStatusData.CLEAN.kode
                }

                viewModel.updateRuta(it, false)

                Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    RumahTanggaFragmentDirections.actionRumahTanggaFragmentToDetailSlsFragment(sls!!)
//                    DetailSlsFragmentDirections.actionDetailSlsFragmentToRumahTanggaFragment(sls!!, RutaEntity(0))
                )
            } ?: run {
                Toast.makeText(context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Hapus Data")
            builder.setMessage("Anda yakin ingin menghapus data ini?")

            builder.setPositiveButton("Ya") { dialog, _ ->
               ruta?.let {
                    if (it.status_upload == EnumStatusUpload.NOT_UPLOADED.kode) {
                        viewModel.delete(it)
                        Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        it.status_upload = EnumStatusUpload.DELETED_AFTER_UPLOADED.kode
                        viewModel.updateRuta(it, true)
                        Toast.makeText(context, "Data akan dihapus saat diupload", Toast.LENGTH_SHORT).show()
                    }
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
//    }
    }

//    private fun delete() {
//        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
//
//        builder.setTitle("Hapus Data")
//        builder.setMessage("Anda yakin ingin menghapus data ini?")
//
//        builder.setPositiveButton("Ya") { dialog, _ ->
//     a      ruta?.let {
//                if (it.status_upload == EnumStatusUpload.NOT_UPLOADED.kode) {
//                    viewModel.delete(it)
//
//                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
//                } else {
//                    it.status_upload = EnumStatusUpload.DELETED_AFTER_UPLOADED.kode
//
//                    viewModel.updateRuta(it, true)
//
//                    Toast.makeText(context, "Data akan dihapus saat diupload", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            dialog.dismiss()
//        }
//
//        builder.setNegativeButton("Batal") { dialog, _ ->
//            dialog.dismiss()
//        }
//
//        builder.show()
//    }

//    private fun checkWawancara(startTime: String, endTime: String) {
//        if (startTime != "") {
//            if (endTime == "") wawancaraStarted()
//            else wawancaraEnded()
//        } else {
//            wawancaraDeleted()
//        }
//    }

//    private fun wawancaraEnded() {
//        binding.btnStartWawancara.visibility = View.GONE
//        binding.btnEndWawancara.visibility = View.GONE
//        binding.btnReWawancara.visibility = View.VISIBLE
//    }
//
//    private fun wawancaraDeleted() {
//        binding.btnStartWawancara.visibility = View.VISIBLE
//        binding.btnEndWawancara.visibility = View.GONE
//        binding.btnReWawancara.visibility = View.GONE
//    }

    private var listError = mutableListOf<String>()
    private fun validation(){
        checkErrorEmpty(binding.edtNurt, "Nomor Urut Ruta Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtNamaKk, "Nama Kepala Keluarga Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtJmlArt, "Jumlah ART Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtJmlUnitUsaha, "Jumlah Unit Usaha Tidak Boleh Kosong")

        var totalLahan = 0
//        val luasLahan = binding.edtLuasSawah.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
        totalLahan += binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0

        if(totalLahan==0){
            binding.edtJmlUnitUsaha.error = "Total Luas Lahan tidak boleh 0"
            listError.add("Total Luas Lahan tidak boleh 0")
        }

        checkErrorEmpty(binding.edtLuasSawah, "Luas Sawah Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasBukanSawah, "Luas Bukan Sawah Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasRumputSementara, "Luas Rumput Sementara Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasRumputPermanen, "Luas Rumput Permanen Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasBelumTanam, "Luas Belum Tanam Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasTanamanTahunan, "Luas Tanaman Tahunan Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasTernakBangunanLain, "Luas Ternak Bangunan Lain Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasKehutanan, "Luas Kegiatan Kehutanan Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasBudidaya, "Luas Kegiatan Budidaya Perikanan Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtLuasLahanLainnya, "Luas Lahan Lainnya Tidak Boleh Kosong")
    }

    private fun checkErrorEmpty(edt: EditText, errMsg: String){
        if(edt.text.toString().isBlank()){
            edt.error = errMsg
            listError.add(errMsg)
        }
        else{
            edt.error = null
        }
    }

    private fun setView(view: View, data: RutaEntity?) {
        data?.let {
            if(data.end_time==""){
                binding.btnDelete.visibility = View.GONE
                binding.linearUsahaTani.visibility = View.GONE
            }
            else{
                binding.btnDelete.visibility = View.VISIBLE
                binding.linearUsahaTani.visibility = View.VISIBLE

                /////////
                binding.edtNurt.setText(it.nurt.toString())
                binding.edtNurt.isEnabled = false
                binding.edtNamaKk.setText(it.kepala_ruta)
                binding.edtJmlArt.setText(it.jumlah_art.toString())
                binding.edtJmlUnitUsaha.setText(it.jumlah_unit_usaha.toString())

                binding.edtLuasSawah.setText(it.jml_308_sawah.toString())
                binding.edtLuasBukanSawah.setText(it.jml_308_bukan_sawah.toString())
                binding.edtLuasRumputSementara.setText(it.jml_308_rumput_sementara.toString())
                binding.edtLuasRumputPermanen.setText(it.jml_308_rumput_permanen.toString())
                binding.edtLuasBelumTanam.setText(it.jml_308_belum_tanam.toString())
                binding.edtLuasTanamanTahunan.setText(it.jml_308_tanaman_tahunan.toString())
                binding.edtLuasTernakBangunanLain.setText(it.jml_308_ternak_bangunan_lain.toString())
                binding.edtLuasKehutanan.setText(it.jml_308_kehutanan.toString())
                binding.edtLuasBudidaya.setText(it.jml_308_budidaya.toString())
                binding.edtLuasLahanLainnya.setText(it.jml_308_lahan_lainnya.toString())
                //////

                validation()
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