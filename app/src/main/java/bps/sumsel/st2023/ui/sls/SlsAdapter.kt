package bps.sumsel.st2023.ui.sls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.databinding.RowSlsBinding
import bps.sumsel.st2023.room.entity.SlsEntity

class SlsAdapter(private val listData: ArrayList<SlsEntity>): RecyclerView.Adapter<SlsAdapter.DataViewHolder>() {
    private lateinit var OnClickCallBack: onClickCallBack

    fun setOnClickCallBack(data: onClickCallBack){
        this.OnClickCallBack = data
    }

    interface onClickCallBack{
        fun onItemClicked(data: SlsEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(RowSlsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val curTodo = listData[position]
        holder.binding.txtTitle.text = curTodo.id_sls
        holder.binding.txtDescription.text = curTodo.nama_sls

//        holder.txtName.setOnClickListener {
//            Toast.makeText(holder.txtName.context, "I click txtName", Toast.LENGTH_SHORT).show()
//        }

        holder.itemView.setOnClickListener {
            OnClickCallBack.onItemClicked(listData[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    class DataViewHolder(val binding: RowSlsBinding) : RecyclerView.ViewHolder(binding.root)
}