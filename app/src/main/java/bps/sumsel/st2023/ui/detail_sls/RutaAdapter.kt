package bps.sumsel.st2023.ui.detail_sls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.databinding.RowRutaBinding
import bps.sumsel.st2023.databinding.RowSlsBinding
import bps.sumsel.st2023.room.entity.RutaEntity
import bps.sumsel.st2023.room.entity.SlsEntity

class RutaAdapter(private val listData: ArrayList<RutaEntity>): RecyclerView.Adapter<RutaAdapter.DataViewHolder>() {
    private lateinit var OnClickCallBack: onClickCallBack

    fun setOnClickCallBack(data: onClickCallBack){
        this.OnClickCallBack = data
    }

    interface onClickCallBack{
        fun onItemClicked(data: RutaEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(RowRutaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val curTodo = listData[position]
        holder.binding.txtName.text = "${get4digitRuta(curTodo.nurt)} - ${curTodo.kepala_ruta}"

        holder.itemView.setOnClickListener {
            OnClickCallBack.onItemClicked(listData[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    fun get4digitRuta(data: Int): String{
        var result = data.toString()

        var lenData = result.length
        while (lenData < 4) {
            result = "0${result}"
            lenData = result.length
        }

        return result
    }

    class DataViewHolder(val binding: RowRutaBinding) : RecyclerView.ViewHolder(binding.root)
}