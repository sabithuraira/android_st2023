package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class SlsEntity(
    @PrimaryKey
    @ColumnInfo(name="id") var id: Int = 0,

    @ColumnInfo(name="kode_prov") var kode_prov: String = "",
    @ColumnInfo(name="kode_kab") var kode_kab: String = "",
    @ColumnInfo(name="kode_kec") var kode_kec: String = "",
    @ColumnInfo(name="kode_desa") var kode_desa: String = "",
    @ColumnInfo(name="id_sls") var id_sls: String = "",
    @ColumnInfo(name="id_sub_sls") var id_sub_sls: String = "",
    @ColumnInfo(name="nama_sls") var nama_sls: String = "",
    @ColumnInfo(name="sls_op") var sls_op: String = "",
    @ColumnInfo(name="jenis_sls") var jenis_sls: String = "",
    @ColumnInfo(name="jml_art_tani") var jml_art_tani: String = "",
    @ColumnInfo(name="jml_keluarga_tani") var jml_keluarga_tani: String = "",
    @ColumnInfo(name="sektor1") var sektor1: String = "",
    @ColumnInfo(name="sektor2") var sektor2: String = "",
    @ColumnInfo(name="sektor3") var sektor3: String = "",
    @ColumnInfo(name="sektor4") var sektor4: String = "",
    @ColumnInfo(name="sektor5") var sektor5: String = "",
    @ColumnInfo(name="sektor6") var sektor6: String = "",
    @ColumnInfo(name="jml_keluarga_tani_st2023") var jml_keluarga_tani_st2023: String = "",
    @ColumnInfo(name="jml_nr") var jml_nr: String = "",
    @ColumnInfo(name="jml_dok_ke_pml") var jml_dok_ke_pml: String = "",
    @ColumnInfo(name="jml_dok_ke_koseka") var jml_dok_ke_koseka: String = "",
    @ColumnInfo(name="jml_dok_ke_bps") var jml_dok_ke_bps: String = "",
    @ColumnInfo(name="status_selesai_pcl") var status_selesai_pcl: String = "",
    @ColumnInfo(name="kode_pcl") var kode_pcl: String = "",
    @ColumnInfo(name="kode_pml") var kode_pml: String = "",
    @ColumnInfo(name="kode_koseka") var kode_koseka: String = "",
    @ColumnInfo(name="status_sls") var status_sls: String = "",
): Parcelable