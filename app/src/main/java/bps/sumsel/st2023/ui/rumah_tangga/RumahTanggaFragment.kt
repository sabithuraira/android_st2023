package bps.sumsel.st2023.ui.rumah_tangga

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentRumahTanggaBinding
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity
import bps.sumsel.st2023.ui.detail_sls.DetailSlsFragmentDirections

class RumahTanggaFragment : Fragment() {
    private var _binding: FragmentRumahTanggaBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null
    private var ruta: RutaEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRumahTanggaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: RumahTanggaViewModel by viewModels {
            factory
        }

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
                if(it.id==0){
                    viewModel.updateRuta(
                        RutaEntity(
                            0, "",
                            sls!!.kode_prov, sls!!.kode_kab, sls!!.kode_kec,
                            sls!!.kode_desa, sls!!.id_sls, sls!!.id_sub_sls,
                            binding.edtNurt.text.toString().toInt(), 0,
                            binding.edtNamaKk.text.toString(),
                        ))
                }
                else{
                    it.kepala_ruta = binding.edtNamaKk.text.toString()
                    it.nurt = binding.edtNurt.text.toString().toInt()
                    it.is_upload = 0


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
                    viewModel.updateRuta(ruta!!)
                    //minus entri current time
                }

            } ?:run {
                Toast.makeText(context, "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

            builder.setTitle("Hapus Data")
            builder.setMessage("Anda yakin ingin menghapus data ini?")

            builder.setPositiveButton("Ya") { dialog, _ ->
                ruta?.let {
                    viewModel.delete(it)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.show()
        }
    }

    private fun setView(view: View, data: RutaEntity?){
        data?.let{
            if(it.id==0){
                binding.linearUsahaTani.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
                parentActivity.setActionBarTitle("Tambah Rumah Tangga")
            }
            else{
                binding.linearUsahaTani.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE

                parentActivity.setActionBarTitle("Perbaharui ${it.kepala_ruta}")

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}