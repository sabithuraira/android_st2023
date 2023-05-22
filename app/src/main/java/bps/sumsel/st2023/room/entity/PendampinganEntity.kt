package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PendampinganEntity(
    @ColumnInfo(name = "time") var time: String = "",
    @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
) : Parcelable
