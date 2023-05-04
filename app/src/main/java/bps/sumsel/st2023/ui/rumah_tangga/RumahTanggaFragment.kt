package bps.sumsel.st2023.ui.rumah_tangga

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
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
import java.util.*

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
    ): View? {
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        _binding = FragmentRumahTanggaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.input_ruta, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val itemFinish = menu.findItem(R.id.action_finish)
        val itemDelete = menu.findItem(R.id.action_delete)

        if(ruta==null){
            itemFinish.isVisible = false
            itemDelete.isVisible= false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_finish -> {
                // Navigate to settings screen.
                true
            }
            R.id.action_delete -> {
                // Save profile changes.
                true
            }
            R.id.action_save -> {
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

        viewModel.resultSingleRuta.observe(this) {  result ->
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

        binding.btnSave.setOnClickListener {
            ruta?.let{
                val currentTime: Date = Calendar.getInstance().time

                if(it.id==0){
                    getLastLocation()
                    viewModel.updateRuta(
                        RutaEntity(
                            0, "",
                            sls!!.kode_prov, sls!!.kode_kab, sls!!.kode_kec,
                            sls!!.kode_desa, sls!!.id_sls, sls!!.id_sub_sls,
                            binding.edtNurt.text.toString().toInt(), 0,
                            binding.edtNamaKk.text.toString(),
                            currentTime.toString(),
                            "",
                            curLocation?.latitude.toString(),
                        "",
                            curLocation?.longitude.toString(),
                        ""
                        ), false)
                }
                else{
                    it.kepala_ruta = binding.edtNamaKk.text.toString()
                    it.nurt = binding.edtNurt.text.toString().toInt()
                    it.is_upload = 0

                    if(it.end_time==null || it.end_time==""){
                        it.end_time = currentTime.toString()
                    }

                    if(it.end_latitude==null || it.end_latitude==""){
                        it.end_latitude = curLocation?.latitude.toString()
                    }

//                binding.edtLuasSawah.text.toString().toInt()
//                binding.edtLuasBukanSawah.text.toString().toInt()
//                binding.edtLuasPadangRumputSementara.text.toString().toInt()
//                binding.edtLuasPadangRumputPermanen.text.toString().toInt()
//                binding.edtLuasSementaraBelumTanam.text.toString().toInt()
//                binding.edtLuasTanamanTahunan.text.toString().toInt()
//                binding.edtLuasPertanianLainnya.text.toString().toInt()
//                binding.edtLuasKegiatanKehutanan.text.toString().toInt()
//                binding.edtLuasBudidayaPerikanan.text.toString().toInt()
//                binding.edtLuasLainnya.text.toString().toInt()
                    viewModel.updateRuta(ruta!!, false)
                    //minus entri current time
                }

                Toast.makeText(context, "Data berhasil diperbaharui", Toast.LENGTH_SHORT).show()
            } ?:run {
                Toast.makeText(context, "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi", Toast.LENGTH_SHORT).show()
            }
        }

//        binding.btnDelete.setOnClickListener {
//            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
//
//            builder.setTitle("Hapus Data")
//            builder.setMessage("Anda yakin ingin menghapus data ini?")
//
//            builder.setPositiveButton("Ya") { dialog, _ ->
//                ruta?.let {
//                    viewModel.delete(it)
//                }
//                dialog.dismiss()
//            }
//
//            builder.setNegativeButton("Batal") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//            val alert: AlertDialog = builder.create()
//            alert.show()
//        }

        binding.btnFinish.setOnClickListener {
            ruta?.let{
                val currentTime: Date = Calendar.getInstance().time

                it.kepala_ruta = binding.edtNamaKk.text.toString()
                it.nurt = binding.edtNurt.text.toString().toInt()
                it.is_upload = 0

                if(it.end_time==null || it.end_time==""){
                    it.end_time = currentTime.toString()
                }

                if(it.end_latitude==null || it.end_latitude==""){
                    it.end_latitude = curLocation?.latitude.toString()
                }

//                binding.edtLuasSawah.text.toString().toInt()
//                binding.edtLuasBukanSawah.text.toString().toInt()
//                binding.edtLuasPadangRumputSementara.text.toString().toInt()
//                binding.edtLuasPadangRumputPermanen.text.toString().toInt()
//                binding.edtLuasSementaraBelumTanam.text.toString().toInt()
//                binding.edtLuasTanamanTahunan.text.toString().toInt()
//                binding.edtLuasPertanianLainnya.text.toString().toInt()
//                binding.edtLuasKegiatanKehutanan.text.toString().toInt()
//                binding.edtLuasBudidayaPerikanan.text.toString().toInt()
//                binding.edtLuasLainnya.text.toString().toInt()
                viewModel.updateRuta(ruta!!, true)
                //minus entri current time

            } ?:run {
                Toast.makeText(context, "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi", Toast.LENGTH_SHORT).show()
            }
        }

        getLastLocation()
    }

    private fun setView(view: View, data: RutaEntity?){
        data?.let{
            if(it.id==0){
                binding.linearUsahaTani.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
                binding.txtHeaderName.text= "Tambah Rumah Tangga"
            }
            else{
                binding.linearUsahaTani.visibility = View.VISIBLE
                binding.btnFinish.visibility = View.VISIBLE

                binding.txtHeaderName.text= "Perbaharui ${it.kepala_ruta}"

                binding.edtNurt.setText(it.nurt.toString())
                binding.edtNamaKk.setText(it.kepala_ruta)
                binding.edtJmlRuta.setText("")
                binding.edtJmlUnitUsaha.setText("")
            }
        } ?: run {
            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Anda harus mengizinkan aplikasi ini mendeteksi lokasi untuk menjalankan operasi ini", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    RumahTanggaFragmentDirections.actionRumahTanggaFragmentToDetailSlsFragment(sls!!)
                )
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
    private fun getLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    curLocation = location
                } else {
                    Toast.makeText(requireContext(),
                        "Location is not found. Try Again",
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