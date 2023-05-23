package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "sls")
@Parcelize
data class SlsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") var id: Int = 0,

    @ColumnInfo(name="encId") var encId: String = "",
    @ColumnInfo(name="kode_prov") var kode_prov: String = "",
    @ColumnInfo(name="kode_kab") var kode_kab: String = "",
    @ColumnInfo(name="kode_kec") var kode_kec: String = "",
    @ColumnInfo(name="kode_desa") var kode_desa: String = "",
    @ColumnInfo(name="id_sls") var id_sls: String = "",
    @ColumnInfo(name="id_sub_sls") var id_sub_sls: String = "",
    @ColumnInfo(name="nama_sls") var nama_sls: String = "",
    @ColumnInfo(name="sls_op") var sls_op: Int = 0,
    @ColumnInfo(name="jenis_sls") var jenis_sls: Int = 0,
    @ColumnInfo(name="jml_art_tani") var jml_art_tani: Int = 0,
    @ColumnInfo(name="jml_keluarga_tani") var jml_keluarga_tani: Int = 0,
    @ColumnInfo(name="sektor1") var sektor1: Int = 0,
    @ColumnInfo(name="sektor2") var sektor2: Int = 0,
    @ColumnInfo(name="sektor3") var sektor3: Int = 0,
    @ColumnInfo(name="sektor4") var sektor4: Int = 0,
    @ColumnInfo(name="sektor5") var sektor5: Int = 0,
    @ColumnInfo(name="sektor6") var sektor6: Int = 0,
    @ColumnInfo(name="jml_keluarga_tani_st2023") var jml_keluarga_tani_st2023: Int = 0,
    @ColumnInfo(name="jml_nr") var jml_nr: Int = 0,
    @ColumnInfo(name="jml_dok_ke_pml") var jml_dok_ke_pml: Int = 0,
    @ColumnInfo(name="jml_dok_ke_koseka") var jml_dok_ke_koseka: Int = 0,
    @ColumnInfo(name="jml_dok_ke_bps") var jml_dok_ke_bps: Int = 0,
    @ColumnInfo(name="status_selesai_pcl") var status_selesai_pcl: Int = 0,
    @ColumnInfo(name="kode_pcl") var kode_pcl: String = "",
    @ColumnInfo(name="kode_pml") var kode_pml: String = "",
    @ColumnInfo(name="kode_koseka") var kode_koseka: String = "",
    @ColumnInfo(name="status_sls") var status_sls: Int = 0,
    @ColumnInfo(name="nama_desa") var nama_desa: String = "",
    @ColumnInfo(name="nama_kec") var nama_kec: String = "",
    @ColumnInfo(name="pendampingan_pml") var pendampingan_pml: String = "",
    @ColumnInfo(name="pendampingan_koseka") var pendampingan_koseka: String = "",
): Parcelable