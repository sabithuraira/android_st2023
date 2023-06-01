package bps.sumsel.st2023.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.databinding.RowSlsDashboardBinding
import bps.sumsel.st2023.room.entity.RekapRutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsHomeAdapter(
    private val listData: ArrayList<SlsEntity>,
    private val listRekap: ArrayList<RekapRutaEntity>
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

        for (ir in listRekap) {
            if (ir.kode_kab == curData.kode_kab &&
                ir.kode_kec == curData.kode_kec &&
                ir.kode_desa == curData.kode_desa &&
                ir.id_sls == curData.id_sls &&
                ir.id_sub_sls == curData.id_sub_sls
            ) {
                holder.binding.txtRutaPcl.text = ir.jumlah.toString()
                break
            } else {
                holder.binding.txtRutaPcl.text = "0"
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    class DataViewHolder(val binding: RowSlsDashboardBinding) :
        RecyclerView.ViewHolder(binding.root)
}