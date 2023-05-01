package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "ruta")
@Parcelize
data class RutaEntity(
    @PrimaryKey
    @ColumnInfo(name="id") var id: Int = 0,

    @ColumnInfo(name="encId") var encId: String = "",
    @ColumnInfo(name="kode_prov") var kode_prov: String = "",
    @ColumnInfo(name="kode_kab") var kode_kab: String = "",
    @ColumnInfo(name="kode_kec") var kode_kec: String = "",
    @ColumnInfo(name="kode_desa") var kode_desa: String = "",
    @ColumnInfo(name="id_sls") var id_sls: String = "",
    @ColumnInfo(name="id_sub_sls") var id_sub_sls: String = "",

    @ColumnInfo(name="nurt") var nurt: Int = 0,
    @ColumnInfo(name="sektor") var sektor: Int = 0,
    @ColumnInfo(name="kepala_ruta") var kepala_ruta: String = "",
    @ColumnInfo(name="start_time") var start_time: String = "",
    @ColumnInfo(name="end_time") var end_time: String = "",

    @ColumnInfo(name="start_latitude") var start_latitude: String = "",
    @ColumnInfo(name="end_latitude") var end_latitude: String = "",
    @ColumnInfo(name="start_longitude") var start_longitude: String = "",
    @ColumnInfo(name="end_longitude") var end_longitude: String = "",

    @ColumnInfo(name="created_by") var created_by: Int = 0,
    @ColumnInfo(name="updated_by") var updated_by: Int = 0,
    @ColumnInfo(name="created_at") var created_at: String = "",
    @ColumnInfo(name="updated_at") var updated_at: String = "",
): Parcelable