package bps.sumsel.st2023.ui.sls

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.databinding.RowSlsBinding
import bps.sumsel.st2023.datastore.UserStore
import bps.sumsel.st2023.enum.EnumJabatan
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsAdapter(private val listData: ArrayList<SlsEntity>, private val user: LiveData<UserStore>, private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<SlsAdapter.DataViewHolder>() {
    private lateinit var onClickCallBack: OnClickCallBack

    fun setOnClickCallBack(data: OnClickCallBack) {
        this.onClickCallBack = data
    }

    interface OnClickCallBack {
        fun onItemPendampingan(data: SlsEntity)

        fun onItemChoose(data: SlsEntity)

        fun onItemProgress(data: SlsEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            RowSlsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        user.observe(lifecycleOwner) {
            if (it.jabatan == EnumJabatan.PML.kode) {
                holder.binding.btnPendampingan.visibility = View.VISIBLE
            } else {
                holder.binding.btnPendampingan.visibility = View.GONE
            }
        }

        val curData = listData[position]

        holder.binding.txtTitle.text = "[" + curData.id_sls + "] " + curData.nama_sls
        holder.binding.txtDescription.text = curData.nama_desa + ", " + curData.nama_kec

        if (curData.status_selesai_pcl == 0) {
            holder.binding.txtStatusProgres.visibility = View.VISIBLE
            holder.binding.txtStatusSelesai.visibility = View.GONE
        } else {
            holder.binding.txtStatusProgres.visibility = View.GONE
            holder.binding.txtStatusSelesai.visibility = View.VISIBLE
        }

        holder.binding.btnPendampingan.setOnClickListener {
            onClickCallBack.onItemPendampingan(listData[holder.adapterPosition])
        }

        holder.binding.btnPilih.setOnClickListener {
            onClickCallBack.onItemChoose(listData[holder.adapterPosition])
        }

        holder.binding.btnProgress.setOnClickListener {
            onClickCallBack.onItemProgress(listData[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    class DataViewHolder(val binding: RowSlsBinding) : RecyclerView.ViewHolder(binding.root)
}