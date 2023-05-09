package bps.sumsel.st2023.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "ruta")
@Parcelize
data class RutaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") var id: Int = 0,

    @ColumnInfo(name="encId") var encId: String = "",
    @ColumnInfo(name="kode_prov") var kode_prov: String = "",
    @ColumnInfo(name="kode_kab") var kode_kab: String = "",
    @ColumnInfo(name="kode_kec") var kode_kec: String = "",
    @ColumnInfo(name="kode_desa") var kode_desa: String = "",
    @ColumnInfo(name="id_sls") var id_sls: String = "",
    @ColumnInfo(name="id_sub_sls") var id_sub_sls: String = "",

    @ColumnInfo(name="nurt") var nurt: Int = 0,
    @ColumnInfo(name="kepala_ruta") var kepala_ruta: String = "",
    @ColumnInfo(name="jumlah_art") var jumlah_art: Int = 0,
    @ColumnInfo(name="jumlah_unit_usaha") var jumlah_unit_usaha: Int = 0,

    @ColumnInfo(name="subsektor1_a") var subsektor1_a: Byte = 0,
    @ColumnInfo(name="subsektor1_b") var subsektor1_b: Byte = 0,
    @ColumnInfo(name="subsektor2_a") var subsektor2_a: Byte = 0,
    @ColumnInfo(name="subsektor2_b") var subsektor2_b: Byte = 0,
    @ColumnInfo(name="subsektor3_a") var subsektor3_a: Byte = 0,
    @ColumnInfo(name="subsektor3_b") var subsektor3_b: Byte = 0,
    @ColumnInfo(name="subsektor4_a") var subsektor4_a: Byte = 0,
    @ColumnInfo(name="subsektor4_b") var subsektor4_b: Byte = 0,
    @ColumnInfo(name="subsektor4_c") var subsektor4_c: Byte = 0,
    @ColumnInfo(name="subsektor5_a") var subsektor5_a: Byte = 0,
    @ColumnInfo(name="subsektor5_b") var subsektor5_b: Byte = 0,
    @ColumnInfo(name="subsektor5_c") var subsektor5_c: Byte = 0,
    @ColumnInfo(name="subsektor6_a") var subsektor6_a: Byte = 0,
    @ColumnInfo(name="subsektor6_b") var subsektor6_b: Byte = 0,
    @ColumnInfo(name="subsektor6_c") var subsektor6_c: Byte = 0,
    @ColumnInfo(name="subsektor7_a") var subsektor7_a: Byte = 0,

    @ColumnInfo(name="jml_308_sawah") var jml_308_sawah: Int = 0,
    @ColumnInfo(name="jml_308_bukan_sawah") var jml_308_bukan_sawah: Int = 0,
    @ColumnInfo(name="jml_308_rumput_sementara") var jml_308_rumput_sementara: Int = 0,
    @ColumnInfo(name="jml_308_rumput_permanen") var jml_308_rumput_permanen: Int = 0,
    @ColumnInfo(name="jml_308_belum_tanam") var jml_308_belum_tanam: Int = 0,
    @ColumnInfo(name="jml_308_ternak_bangunan_lain") var jml_308_ternak_bangunan_lain: Int = 0,
    @ColumnInfo(name="jml_308_kehutanan") var jml_308_kehutanan: Int = 0,
    @ColumnInfo(name="jml_308_budidaya") var jml_308_budidaya: Int = 0,
    @ColumnInfo(name="jml_308_lahan_lainnya") var jml_308_lahan_lainnya: Int = 0,

    @ColumnInfo(name="daftar_komoditas") var daftar_komoditas: String = "",

    @ColumnInfo(name="start_time") var start_time: String = "",
    @ColumnInfo(name="end_time") var end_time: String = "",
    @ColumnInfo(name="start_latitude") var start_latitude: Double = 0.0,
    @ColumnInfo(name="end_latitude") var end_latitude: Double = 0.0,
    @ColumnInfo(name="start_longitude") var start_longitude: Double = 0.0,
    @ColumnInfo(name="end_longitude") var end_longitude: Double = 0.0,

    @ColumnInfo(name="is_upload") var is_upload: Int = 0,

    @ColumnInfo(name="created_by") var created_by: Int = 0,
    @ColumnInfo(name="updated_by") var updated_by: Int = 0,
    @ColumnInfo(name="created_at") var created_at: String = "",
    @ColumnInfo(name="updated_at") var updated_at: String = "",

): Parcelable