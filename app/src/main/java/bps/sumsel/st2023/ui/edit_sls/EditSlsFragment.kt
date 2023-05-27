package bps.sumsel.st2023.ui.edit_sls

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.FragmentEditSlsBinding
import bps.sumsel.st2023.enum.EnumJabatan
import bps.sumsel.st2023.enum.EnumStatusSLS
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.repository.ViewModelFactory
import bps.sumsel.st2023.room.entity.SlsEntity

class EditSlsFragment : Fragment() {
    private var _binding: FragmentEditSlsBinding? = null
    private val binding get() = _binding!!
    private lateinit var parentActivity: MainActivity
    private var sls: SlsEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditSlsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivity = requireActivity() as MainActivity

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: EditSlsViewModel by viewModels { factory }

        sls = EditSlsFragmentArgs.fromBundle(arguments as Bundle).sls

        sls?.let {
            parentActivity.setActionBarTitle(it.nama_sls)
            viewModel.setSingleData(it)
        }

        viewModel.resultSingleData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)
                        val data = result.data

                        setView(view, data, viewModel)
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)
                        Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnProgres.setOnClickListener {
            sls?.let {
                it.status_selesai_pcl = 0

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    it.nama_sls + " belum selesai dikerjakan",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSelesai.setOnClickListener {
            sls?.let {
                it.status_selesai_pcl = 1

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    it.nama_sls + " sudah selesai dikerjakan",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSavePml.setOnClickListener {
            sls?.let {
                it.jml_dok_ke_pml = binding.edtDokPml.text.toString().toIntOrNull() ?: 0

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    "Jumlah dokumen yang diterima PML berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSaveKoseka.setOnClickListener {
            sls?.let {
                it.jml_dok_ke_koseka = binding.edtDokKoseka.text.toString().toIntOrNull() ?: 0

                viewModel.updateSls(it)

                requireActivity().onBackPressedDispatcher.onBackPressed()

                Toast.makeText(
                    context,
                    "Jumlah dokumen yang diterima KOSEKA berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    context,
                    "Error, Terjadi kesalahan. Mohon kembali ke halaman utama aplikasi dan ulangi lagi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun setView(view: View, data: SlsEntity?, viewModel: EditSlsViewModel) {
        data?.let {
            binding.edtNamaSls.setText(it.nama_sls)
            binding.edtIdDesa.setText(it.kode_prov + it.kode_kab + it.kode_kec + it.kode_desa)

            binding.edtNamaSls.isEnabled = false
            binding.edtIdDesa.isEnabled = false

            viewModel.getRekapRuta()

            viewModel.resultRekapRuta.observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is ResultData.Loading -> {
                            parentActivity.setLoading(true)
                        }

                        is ResultData.Success -> {
                            parentActivity.setLoading(false)

                            result.data?.let { d ->
                                d.forEach { r ->
                                    if (r.kode_kab == data.kode_kab &&
                                        r.kode_kec == data.kode_kec &&
                                        r.kode_desa == data.kode_desa &&
                                        r.id_sls == data.id_sls &&
                                        r.id_sub_sls == data.id_sub_sls
                                    ) {
                                        binding.txtProgresPcl.text = r.jumlah.toString()
                                    }
                                }
                            }
                        }

                        is ResultData.Error -> {
                            parentActivity.setLoading(false)

                            Toast.makeText(context, "Error " + result.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            binding.txtProgresPml.text = it.jml_dok_ke_pml.toString()
            binding.txtProgresKoseka.text = it.jml_dok_ke_koseka.toString()

            val user = viewModel.user

            when (user.jabatan) {
                EnumJabatan.PCL.kode -> {
                    binding.layoutPcl.visibility = View.VISIBLE
                    binding.layoutPerubahanBatas.visibility = View.VISIBLE
                    binding.layoutPml.visibility = View.GONE
                    binding.layoutKoseka.visibility = View.GONE

                    if (it.status_selesai_pcl == 0) {
                        binding.btnProgres.isEnabled = false
                        binding.btnProgres.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.gray
                            )
                        )
                        binding.btnSelesai.isEnabled = true
                        binding.btnSelesai.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_900
                            )
                        )
                    } else {
                        binding.btnProgres.isEnabled = true
                        binding.btnProgres.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_900
                            )
                        )
                        binding.btnSelesai.isEnabled = false
                        binding.btnSelesai.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.gray
                            )
                        )
                    }
                    binding.switchBerubahBatas.isChecked =
                        it.status_sls == EnumStatusSLS.BERUBAH_BATAS.kode

                    binding.switchBerubahBatas.setOnCheckedChangeListener { buttonView, isChecked ->
                        sls?.let {s ->
                            if (isChecked && (s.status_sls == EnumStatusSLS.AKTIF.kode)) {
                                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)

                                builder.setTitle("SLS Berubah Batas")
                                builder.setMessage("Apakah yakin " + s.nama_sls + " mengalami perubahan batas?")

                                builder.setPositiveButton("Ya") { dialog, _ ->
                                    s.status_sls = EnumStatusSLS.BERUBAH_BATAS.kode

                                    viewModel.updateSls(s)

                                    dialog.dismiss()

                                    Toast.makeText(
                                        context,
                                        s.nama_sls + " ditandai sebagai mengalami perubahan batas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                builder.setNegativeButton("Batal") { dialog, _ ->
                                    buttonView.isChecked = false

                                    dialog.dismiss()
                                }

                                builder.show()
                            } else if (!isChecked && (s.status_sls == EnumStatusSLS.BERUBAH_BATAS.kode)) {
                                s.status_sls = EnumStatusSLS.AKTIF.kode

                                viewModel.updateSls(s)

                                Toast.makeText(
                                    context,
                                    s.nama_sls + " ditandai sebagai tidak mengalami perubahan batas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                EnumJabatan.PML.kode -> {
                    binding.layoutPcl.visibility = View.GONE
                    binding.layoutPerubahanBatas.visibility = View.GONE
                    binding.layoutPml.visibility = View.VISIBLE
                    binding.layoutKoseka.visibility = View.GONE

                    binding.edtDokPml.setText(it.jml_dok_ke_pml.toString())
                }
                EnumJabatan.KOSEKA.kode -> {
                    binding.layoutPcl.visibility = View.GONE
                    binding.layoutPerubahanBatas.visibility = View.GONE
                    binding.layoutPml.visibility = View.GONE
                    binding.layoutKoseka.visibility = View.VISIBLE

                    binding.edtDokKoseka.setText(it.jml_dok_ke_koseka.toString())
                }
            }
        } ?: run {
            view.findNavController().navigate(
                EditSlsFragmentDirections.actionEditSlsFragmentToNavigationSls()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}