package bps.sumsel.st2023.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestSlsMany(
    @field:SerializedName("data")
    val data: List<RequestSlsProgress>? = null,
) : Parcelable

@Parcelize
data class RequestSlsProgress(
    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("status_selesai_pcl")
    val statusSelesaiPcl: Int? = 0,
) : Parcelable