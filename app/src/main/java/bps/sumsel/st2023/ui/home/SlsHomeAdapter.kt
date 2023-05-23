package bps.sumsel.st2023.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.MainActivity
import bps.sumsel.st2023.databinding.RowSlsDashboardBinding
import bps.sumsel.st2023.repository.ResultData
import bps.sumsel.st2023.room.entity.RekapRutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsHomeAdapter(
    private val listData: ArrayList<SlsEntity>,
    private val resultRekapRuta: LiveData<ResultData<List<RekapRutaEntity>?>>,
    private val context: Context?,
    private val lifecycleOwner: LifecycleOwner,
    private val parentActivity: MainActivity
) :
    RecyclerView.Adapter<SlsHomeAdapter.DataViewHolder>() {
    private lateinit var onClickCallBack: OnClickCallBack

    fun setOnClickCallBack(data: OnClickCallBack) {
        this.onClickCallBack = data
    }

    interface OnClickCallBack {
        fun onItemClicked(data: SlsEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            RowSlsDashboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val curData = listData[position]
        holder.binding.txtTitle.text = "[" + curData.id_sls + "] " + curData.nama_sls
        holder.binding.txtDescription.text = curData.nama_desa + ", " + curData.nama_kec

        holder.binding.txtRutaPml.text = curData.jml_dok_ke_pml.toString()
        holder.binding.txtRutaKoseka.text = curData.jml_dok_ke_koseka.toString()

        holder.itemView.setOnClickListener {
            onClickCallBack.onItemClicked(listData[holder.adapterPosition])
        }

        resultRekapRuta.observe(lifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        parentActivity.setLoading(true)
                    }

                    is ResultData.Success -> {
                        parentActivity.setLoading(false)

                        result.data?.let { d ->
                            d.forEach {
                                if (it.kode_kab == curData.kode_kab &&
                                    it.kode_kec == curData.kode_kec &&
                                    it.kode_desa == curData.kode_desa &&
                                    it.id_sls == curData.id_sls &&
                                    it.id_sub_sls == curData.id_sub_sls
                                ) {
                                    holder.binding.txtRutaPcl.text = it.jumlah.toString()
                                }
                            }
                        }
                    }

                    is ResultData.Error -> {
                        parentActivity.setLoading(false)

                        Toast.makeText(context, "Error" + result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    class DataViewHolder(val binding: RowSlsDashboardBinding) :
        RecyclerView.ViewHolder(binding.root)
}