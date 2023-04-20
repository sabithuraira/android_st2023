package bps.sumsel.st2023.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseStringData(
    @field:SerializedName("data")
    val datas: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable