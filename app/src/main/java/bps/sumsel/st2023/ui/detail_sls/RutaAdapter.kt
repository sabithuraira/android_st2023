package bps.sumsel.st2023.ui.detail_sls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.R
import bps.sumsel.st2023.databinding.RowRutaBinding
import bps.sumsel.st2023.room.entity.RutaEntity

class RutaAdapter(private val listData: ArrayList<RutaEntity>) :
    RecyclerView.Adapter<RutaAdapter.DataViewHolder>() {
    private lateinit var OnClickCallBack: onClickCallBack

    fun setOnClickCallBack(data: onClickCallBack) {
        this.OnClickCallBack = data
    }

    interface onClickCallBack {
        fun onItemClicked(data: RutaEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            RowRutaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val curData = listData[position]
        holder.binding.txtName.text = "${get4digitRuta(curData.nurt)} - ${curData.kepala_ruta}"

        holder.binding.txtStatusUpload.text = getUploadStatus(curData.status_upload)

        if(curData.status_data==0){
            holder.binding.txtStatusData.text = "ERROR/BELUM SELESAI"
            holder.binding.txtStatusData.setTextColor(
                ContextCompat.getColor(holder.binding.txtStatusData.context,
                R.color.red_900))
            holder.binding.lineData.setBackgroundColor(ContextCompat.getColor(holder.binding.txtStatusData.context, R.color.red_900))
        }
        else{
            holder.binding.txtStatusData.text = "CLEAN"
            holder.binding.txtStatusData.setTextColor(
                ContextCompat.getColor(holder.binding.txtStatusData.context,
                    R.color.green_900))
            holder.binding.lineData.setBackgroundColor(ContextCompat.getColor(holder.binding.txtStatusData.context, R.color.green_900))

        }

        holder.itemView.setOnClickListener {
            OnClickCallBack.onItemClicked(listData[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    fun get4digitRuta(data: Int): String {
        var result = data.toString()

        var lenData = result.length
        while (lenData < 4) {
            result = "0${result}"
            lenData = result.length
        }

        return result
    }

//    private fun getWawancaraStatus(startTime: String, endTime: String): String {
//        return if (startTime != "") {
//            if (endTime == "") "Sedang Wawancara"
//            else "Sudah Wawancara"
//        } else {
//            "Belum Wawancara"
//        }
//    }

    private fun getUploadStatus(statusUpload: Int): String {
        return when (statusUpload) {
            1 -> "Sudah Upload"
            2 -> "Perlu Upload Ulang"
            3 -> "Akan Dihapus Saat Upload"
            else -> "Belum Upload"
        }
    }

    class DataViewHolder(val binding: RowRutaBinding) : RecyclerView.ViewHolder(binding.root)
}