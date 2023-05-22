package bps.sumsel.st2023.ui.pendampingan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bps.sumsel.st2023.databinding.RowPendampinganBinding
import bps.sumsel.st2023.room.entity.PendampinganEntity

class PendampinganAdapter(private val listData: ArrayList<PendampinganEntity>) :
    RecyclerView.Adapter<PendampinganAdapter.DataViewHolder>() {
    private lateinit var onClickCallBack: OnClickCallBack

    fun setOnClickCallBack(data: OnClickCallBack) {
        this.onClickCallBack = data
    }

    interface OnClickCallBack {
        fun onItemDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            RowPendampinganBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val curData = listData[position]

        holder.binding.txtTitle.text = curData.time

        holder.binding.btnHapus.setOnClickListener {
            onClickCallBack.onItemDelete(position)
        }
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    class DataViewHolder(val binding: RowPendampinganBinding) :
        RecyclerView.ViewHolder(binding.root)
}