package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class RekapRutaEntity(
    @ColumnInfo(name = "jumlah") var jumlah: Int = 0,
    @ColumnInfo(name = "kode_prov") var kode_prov: String = "",
    @ColumnInfo(name = "kode_kab") var kode_kab: String = "",
    @ColumnInfo(name = "kode_kec") var kode_kec: String = "",
    @ColumnInfo(name = "kode_desa") var kode_desa: String = "",
    @ColumnInfo(name = "id_sls") var id_sls: String = "",
    @ColumnInfo(name = "id_sub_sls") var id_sub_sls: String = "",
) : Parcelable

@Parcelize
data class RekapSlsEntity(
    @ColumnInfo(name = "jumlah") var jumlah: Int = 0,
) : Parcelable