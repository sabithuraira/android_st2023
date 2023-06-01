package bps.sumsel.st2023.ui.rumah_tangga

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentRumahTanggaBinding
import bps.sumsel.st2023.datastore.AuthDataStore
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.enum.EnumStatusData
import bps.sumsel.st2023.enum.EnumStatusUpload
import bps.sumsel.st2023.enum.EnumSubsektor
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelAuthFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class RumahTanggaFragment : Fragment() {
    private var _binding: FragmentRumahTanggaBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null
    private var ruta: RutaEntity? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var curLocation: Location? = null
    private var lastPlusOne: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        _binding = FragmentRumahTanggaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity = requireActivity() as MainActivity

        val pref = AuthDataStore.getInstance(requireContext().dataStore)
        val factory: ViewModelAuthFactory =
            ViewModelAuthFactory.getInstance(requireActivity(), pref)
        val viewModel: RumahTanggaViewModel by viewModels { factory }

        sls = RumahTanggaFragmentArgs.fromBundle(arguments as Bundle).sls
        ruta = RumahTanggaFragmentArgs.fromBundle(arguments as Bundle).ruta
        viewModel.setSingleRuta(ruta)

        viewModel.resultSingleRuta.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        if (result.data != null) {
                            ruta = result.data
                            setView(view, ruta)
                        } else {
                            viewModel.setSingleRuta(ruta)
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.resultLastNurt.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        lastPlusOne = result.data + 1
                        binding.edtNurt.setText(lastPlusOne.toString())
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.getAuthUser().observe(this) { user: UserStore ->
            //WHEN NOT LOGIN AS PPL, DISABLED ALL CONTROL (BUTTON, EDITTEXT)
            if (user.jabatan != 1) {
                binding.btnDelete.visibility = View.GONE
                binding.btnSave.visibility = View.GONE

                binding.edtNurt.isEnabled = false
                binding.edtNamaKk.isEnabled = false
                binding.edtJmlArt.isEnabled = false
                binding.edtJmlUnitUsaha.isEnabled = false

//                binding.edtLuasSawah.isEnabled = false
//                binding.edtLuasBukanSawah.isEnabled = false
//                binding.edtLuasRumputSementara.isEnabled = false
//                binding.edtLuasRumputPermanen.isEnabled = false
//                binding.edtLuasBelumTanam.isEnabled = false
//                binding.edtLuasTanamanTahunan.isEnabled = false
//                binding.edtLuasTernakBangunanLain.isEnabled = false
//                binding.edtLuasKehutanan.isEnabled = false
//                binding.edtLuasBudidaya.isEnabled = false
//                binding.edtLuasLahanLainnya.isEnabled = false
            }
        }

        ruta?.let {
            if (it.id == 0) {
                viewModel.getLastNurt(sls!!)

                createLocationRequest()
                createLocationCallback()
                startLocationUpdates()

                val currentTime: Date = Calendar.getInstance().time
                val currentTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                it.encId = ""
                it.kode_prov = sls!!.kode_prov
                it.kode_kab = sls!!.kode_kab
                it.kode_kec = sls!!.kode_kec
                it.kode_desa = sls!!.kode_desa
                it.id_sls = sls!!.id_sls
                it.id_sub_sls = sls!!.id_sub_sls

                it.start_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                it.start_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                it.status_upload = EnumStatusUpload.NOT_UPLOADED.kode

                it.status_data = EnumStatusData.ERROR.kode

                it.start_time = currentTimeFormat.format(currentTime).toString()
            }
        } ?: run {
            Toast.makeText(
                context,
                "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSave.setOnClickListener {
            ruta?.let {
                if (it.end_time == "") {
                    val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

                    builder.setTitle("Simpan Data")
                    builder.setMessage(
                        HtmlCompat.fromHtml(
                            "Anda yakin ingin menyimpan data ini? <b>Harap menyimpan setelah wawancara selesai dilakukan!</b>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    )

                    builder.setPositiveButton("Ya") { dialog, _ ->
                        val currentTime: Date = Calendar.getInstance().time
                        val currentTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                        it.end_time = currentTimeFormat.format(currentTime).toString()
                        it.end_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                        it.end_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0

                        it.nurt = binding.edtNurt.text.toString().toIntOrNull() ?: 0
                        it.kepala_ruta = binding.edtNamaKk.text.toString()
                        it.jumlah_art = binding.edtJmlArt.text.toString().toIntOrNull() ?: 0
                        it.jumlah_unit_usaha =
                            binding.edtJmlUnitUsaha.text.toString().toIntOrNull() ?: 0

//                        it.jml_308_sawah = binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_bukan_sawah =
//                            binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_rumput_sementara =
//                            binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_rumput_permanen =
//                            binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_belum_tanam =
//                            binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_tanaman_tahunan =
//                            binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_ternak_bangunan_lain =
//                            binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_kehutanan =
//                            binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_budidaya =
//                            binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
//                        it.jml_308_lahan_lainnya =
//                            binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0

                        it.status_upload =
                            if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

                        validation()

                        if (listError.isNotEmpty()) {
                            it.status_data = EnumStatusData.ERROR.kode
                        } else {
                            it.status_data = EnumStatusData.CLEAN.kode
                        }

                        stopLocationUpdates()
                        viewModel.updateRuta(it, false)

                        Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.show()
                } else {
                    it.nurt = binding.edtNurt.text.toString().toIntOrNull() ?: 0
                    it.kepala_ruta = binding.edtNamaKk.text.toString()
                    it.jumlah_art = binding.edtJmlArt.text.toString().toIntOrNull() ?: 0
                    it.jumlah_unit_usaha =
                        binding.edtJmlUnitUsaha.text.toString().toIntOrNull() ?: 0

//                    it.jml_308_sawah = binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_bukan_sawah =
//                        binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_rumput_sementara =
//                        binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_rumput_permanen =
//                        binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_belum_tanam =
//                        binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_tanaman_tahunan =
//                        binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_ternak_bangunan_lain =
//                        binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_kehutanan =
//                        binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_budidaya = binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
//                    it.jml_308_lahan_lainnya =
//                        binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0

                    it.status_upload =
                        if (it.status_upload == EnumStatusUpload.UPLOADED.kode) EnumStatusUpload.CHANGED_AFTER_UPLOADED.kode else EnumStatusUpload.NOT_UPLOADED.kode

                    validation()

                    if (listError.isNotEmpty()) {
                        it.status_data = EnumStatusData.ERROR.kode
                    } else {
                        it.status_data = EnumStatusData.CLEAN.kode
                    }

                    viewModel.updateRuta(it, false)

                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

            } ?: run {
                Toast.makeText(
                    context,
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
                        Toast.makeText(
                            context,
                            "Data akan dihapus saat diupload",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        generateChipSubsektor(requireActivity())

        binding.cbApakahMenggunaakanLahan.setOnCheckedChangeListener { _, isChecked ->
            ruta?.apakah_menggunakan_lahan = if (isChecked) 1 else 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun generateChipSubsektor(context: Context) {
        enumValues<EnumSubsektor>().forEach {
            binding.chipSubsektor.addView(addChip(context, it))
        }
    }

    private fun addChip(context: Context, subsektor: EnumSubsektor): Chip {
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val curChip = Chip(
            context,
            null,
            com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice
        ).apply {
            id = subsektor.code
            layoutParams = lp
            text = subsektor.label
            textSize = 13F
            isCheckable = true
            isClickable = true
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setChipBackgroundColorResource(R.color.green_900)
        }

        when (subsektor.code) {
            1 -> if (ruta?.subsektor1_a?.toInt() == 1) curChip.isChecked = true
            2 -> if (ruta?.subsektor1_b?.toInt() == 1) curChip.isChecked = true

            3 -> if (ruta?.subsektor2_a?.toInt() == 1) curChip.isChecked = true
            4 -> if (ruta?.subsektor2_b?.toInt() == 1) curChip.isChecked = true

            5 -> if (ruta?.subsektor3_a?.toInt() == 1) curChip.isChecked = true
            6 -> if (ruta?.subsektor3_b?.toInt() == 1) curChip.isChecked = true

            7 -> if (ruta?.subsektor4_a?.toInt() == 1) curChip.isChecked = true
            8 -> if (ruta?.subsektor4_b?.toInt() == 1) curChip.isChecked = true
            9 -> if (ruta?.subsektor4_c?.toInt() == 1) curChip.isChecked = true

            10 -> if (ruta?.subsektor5_a?.toInt() == 1) curChip.isChecked = true
            11 -> if (ruta?.subsektor5_b?.toInt() == 1) curChip.isChecked = true
            12 -> if (ruta?.subsektor5_c?.toInt() == 1) curChip.isChecked = true

            13 -> if (ruta?.subsektor6_a?.toInt() == 1) curChip.isChecked = true
            14 -> if (ruta?.subsektor6_b?.toInt() == 1) curChip.isChecked = true
            15 -> if (ruta?.subsektor6_c?.toInt() == 1) curChip.isChecked = true
            16 -> if (ruta?.subsektor7_a?.toInt() == 1) curChip.isChecked = true

            17 -> if (ruta?.jml_308_sawah == 1) curChip.isChecked = true
        }

        curChip.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, result ->
            when (subsektor.code) {
                1 -> ruta?.subsektor1_a = if (result) 1 else 0
                2 -> ruta?.subsektor1_b = if (result) 1 else 0

                3 -> ruta?.subsektor2_a = if (result) 1 else 0
                4 -> ruta?.subsektor2_b = if (result) 1 else 0

                5 -> ruta?.subsektor3_a = if (result) 1 else 0
                6 -> ruta?.subsektor3_b = if (result) 1 else 0

                7 -> ruta?.subsektor4_a = if (result) 1 else 0
                8 -> ruta?.subsektor4_b = if (result) 1 else 0
                9 -> ruta?.subsektor4_c = if (result) 1 else 0

                10 -> ruta?.subsektor5_a = if (result) 1 else 0
                11 -> ruta?.subsektor5_b = if (result) 1 else 0
                12 -> ruta?.subsektor5_c = if (result) 1 else 0

                13 -> ruta?.subsektor6_a = if (result) 1 else 0
                14 -> ruta?.subsektor6_b = if (result) 1 else 0
                15 -> ruta?.subsektor6_c = if (result) 1 else 0
                16 -> ruta?.subsektor7_a = if (result) 1 else 0

                17 -> ruta?.jml_308_sawah = if (result) 1 else 0
            }
        })

        return curChip
    }

    private var listError = mutableListOf<String>()
    private fun validation() {
        listError.clear()
        checkErrorEmpty(binding.edtNurt, "Nomor Urut Ruta Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtNamaKk, "Nama Kepala Keluarga Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtJmlArt, "Jumlah ART Tidak Boleh Kosong")
        checkErrorEmpty(binding.edtJmlUnitUsaha, "Jumlah Unit Usaha Tidak Boleh Kosong")

//        var totalLahan = 0
//        totalLahan += binding.edtLuasSawah.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasBukanSawah.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasRumputSementara.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasRumputPermanen.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasBelumTanam.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasTanamanTahunan.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasTernakBangunanLain.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasKehutanan.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasBudidaya.text.toString().toIntOrNull() ?: 0
//        totalLahan += binding.edtLuasLahanLainnya.text.toString().toIntOrNull() ?: 0
//
//        if (ruta?.apakah_menggunakan_lahan == 1) {
//            if (totalLahan == 0) {
//                binding.edtJmlUnitUsaha.error =
//                    "Jika menggunakan lahan, Total Luas Lahan tidak boleh 0"
//                listError.add("Total Luas Lahan tidak boleh 0")
//            }
//        }

        var totalSubsektor = 0
        totalSubsektor += ruta?.subsektor1_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor1_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor2_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor2_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor3_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor3_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor4_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor4_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor4_c?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor5_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor5_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor5_c?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor6_a?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor6_b?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor6_c?.toInt() ?: 0
        totalSubsektor += ruta?.subsektor7_a?.toInt() ?: 0
        totalSubsektor += ruta?.jml_308_sawah ?: 0

        if (totalSubsektor == 0) {
            binding.edtJmlUnitUsaha.error = "Minimal memilih 1 subsektor"
            listError.add("Minimal memilih 1 subsektor")
        }

//        checkErrorEmpty(binding.edtLuasSawah, "Luas Sawah Tidak Boleh Kosong")
//        checkErrorEmpty(binding.edtLuasBukanSawah, "Luas Bukan Sawah Tidak Boleh Kosong")
//        checkErrorEmpty(binding.edtLuasRumputSementara, "Luas Rumput Sementara Tidak Boleh Kosong")
//        checkErrorEmpty(binding.edtLuasRumputPermanen, "Luas Rumput Permanen Tidak Boleh Kosong")
//        checkErrorEmpty(binding.edtLuasBelumTanam, "Luas Belum Tanam Tidak Boleh Kosong")
//        checkErrorEmpty(binding.edtLuasTanamanTahunan, "Luas Tanaman Tahunan Tidak Boleh Kosong")
//        checkErrorEmpty(
//            binding.edtLuasTernakBangunanLain,
//            "Luas Ternak Bangunan Lain Tidak Boleh Kosong"
//        )
//        checkErrorEmpty(binding.edtLuasKehutanan, "Luas Kegiatan Kehutanan Tidak Boleh Kosong")
//        checkErrorEmpty(
//            binding.edtLuasBudidaya,
//            "Luas Kegiatan Budidaya Perikanan Tidak Boleh Kosong"
//        )
//        checkErrorEmpty(binding.edtLuasLahanLainnya, "Luas Lahan Lainnya Tidak Boleh Kosong")
    }

    private fun checkErrorEmpty(edt: EditText, errMsg: String) {
        if (edt.text.toString().isBlank() || edt.text.toString().toIntOrNull() == 0) {
            edt.error = errMsg
            listError.add(errMsg)
        } else {
            edt.error = null
        }
    }

    private fun setView(view: View, data: RutaEntity?) {
        data?.let {
            binding.linearLuas.visibility = View.GONE

//            binding.edtNurt.isEnabled = false
            if (data.end_time == "") {
                binding.btnDelete.visibility = View.GONE

                binding.edtNurt.setText("")
                binding.edtNamaKk.setText("")
                binding.edtJmlArt.setText("")
                binding.edtJmlUnitUsaha.setText("")

//                binding.edtLuasSawah.setText("")
//                binding.edtLuasBukanSawah.setText("")
//                binding.edtLuasRumputSementara.setText("")
//                binding.edtLuasRumputPermanen.setText("")
//                binding.edtLuasBelumTanam.setText("")
//                binding.edtLuasTanamanTahunan.setText("")
//                binding.edtLuasTernakBangunanLain.setText("")
//                binding.edtLuasKehutanan.setText("")
//                binding.edtLuasBudidaya.setText("")
//                binding.edtLuasLahanLainnya.setText("")
            } else {
                binding.btnDelete.visibility = View.VISIBLE

                /////////
                binding.edtNurt.setText(it.nurt.toString())
                binding.edtNamaKk.setText(it.kepala_ruta)
                binding.edtJmlArt.setText(it.jumlah_art.toString())
                binding.edtJmlUnitUsaha.setText(it.jumlah_unit_usaha.toString())

//                binding.edtLuasSawah.setText(it.jml_308_sawah.toString())
//                binding.edtLuasBukanSawah.setText(it.jml_308_bukan_sawah.toString())
//                binding.edtLuasRumputSementara.setText(it.jml_308_rumput_sementara.toString())
//                binding.edtLuasRumputPermanen.setText(it.jml_308_rumput_permanen.toString())
//                binding.edtLuasBelumTanam.setText(it.jml_308_belum_tanam.toString())
//                binding.edtLuasTanamanTahunan.setText(it.jml_308_tanaman_tahunan.toString())
//                binding.edtLuasTernakBangunanLain.setText(it.jml_308_ternak_bangunan_lain.toString())
//                binding.edtLuasKehutanan.setText(it.jml_308_kehutanan.toString())
//                binding.edtLuasBudidaya.setText(it.jml_308_budidaya.toString())
//                binding.edtLuasLahanLainnya.setText(it.jml_308_lahan_lainnya.toString())
                binding.cbApakahMenggunaakanLahan.isChecked = it.apakah_menggunakan_lahan == 1
                //////

                validation()
            }
        } ?: run {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED ->
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
                        resolutionLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
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
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
                    curLocation = location

                    ruta?.let {
                        if (it.start_latitude==0.0 || it.start_longitude==0.0) {
                            it.start_latitude = curLocation?.latitude.toString().toDoubleOrNull() ?: 0.0
                            it.start_longitude = curLocation?.longitude.toString().toDoubleOrNull() ?: 0.0
                        }
                    }
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
            Log.e(TAG, "Error : " + exception.message)
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
                    Toast.makeText(requireContext(), "Lokasi tidak ditemukan. Coba lagi", Toast.LENGTH_SHORT).show()
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

        ruta?.let {
            if (it.id == 0) stopLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        ruta?.let {
            if (it.id == 0) stopLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()

        ruta?.let {
            if (it.id == 0) startLocationUpdates()
        }
    }
}
