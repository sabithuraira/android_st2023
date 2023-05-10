package bps.sumsel.st2023.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestRutaMany(
    @field:SerializedName("data")
    val data: List<RequestRuta>? = null,
) : Parcelable

@Parcelize
data class RequestRuta(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("kode_prov")
    val kodeProv: String? = null,

    @field:SerializedName("kode_kab")
    val kodeKab: String? = null,

    @field:SerializedName("kode_kec")
    val kodeKec: String? = null,

    @field:SerializedName("kode_desa")
    val kodeDesa: String? = null,

    @field:SerializedName("id_sls")
    val idSls: String? = null,

    @field:SerializedName("id_sub_sls")
    val idSubSls: String? = null,

    @field:SerializedName("nurt")
    val nurt: Int? = null,

    @field:SerializedName("kepala_ruta")
    val kepalaRuta: String? = null,

    @field:SerializedName("jumlah_art")
    val jumlahArt: Int? = null,

    @field:SerializedName("jumlah_unit_usaha")
    val jumlahUnitUsaha: Int? = null,

    @field:SerializedName("subsektor1_a")
    val subsektor1A: Byte? = null,

    @field:SerializedName("subsektor1_b")
    val subsektor1B: Byte? = null,

    @field:SerializedName("subsektor2_a")
    val subsektor2A: Byte? = null,

    @field:SerializedName("subsektor2_b")
    val subsektor2B: Byte? = null,

    @field:SerializedName("subsektor3_a")
    val subsektor3A: Byte? = null,

    @field:SerializedName("subsektor3_b")
    val subsektor3B: Byte? = null,

    @field:SerializedName("subsektor4_a")
    val subsektor4A: Byte? = null,

    @field:SerializedName("subsektor4_b")
    val subsektor4B: Byte? = null,

    @field:SerializedName("subsektor4_c")
    val subsektor4C: Byte? = null,

    @field:SerializedName("subsektor5_a")
    val subsektor5A: Byte? = null,

    @field:SerializedName("subsektor5_b")
    val subsektor5B: Byte? = null,

    @field:SerializedName("subsektor5_c")
    val subsektor5C: Byte? = null,

    @field:SerializedName("subsektor6_a")
    val subsektor6A: Byte? = null,

    @field:SerializedName("subsektor6_b")
    val subsektor6B: Byte? = null,

    @field:SerializedName("subsektor6_c")
    val subsektor6C: Byte? = null,

    @field:SerializedName("subsektor7_a")
    val subsektor7A: Byte? = null,

    @field:SerializedName("jml_308_sawah")
    val jml308Sawah: Int? = null,

    @field:SerializedName("jml_308_bukan_sawah")
    val jml308BukanSawah: Int? = null,

    @field:SerializedName("jml_308_rumput_sementara")
    val jml308RumputSementara: Int? = null,

    @field:SerializedName("jml_308_rumput_permanen")
    val jml308RumputPermanen: Int? = null,

    @field:SerializedName("jml_308_belum_tanam")
    val jml308BelumTanam: Int? = null,

    @field:SerializedName("jml_308_ternak_bangunan_lain")
    val jml308TernakBangunanLain: Int? = null,

    @field:SerializedName("jml_308_kehutanan")
    val jml308Kehutanan: Int? = null,

    @field:SerializedName("jml_308_budidaya")
    val jml308Budidaya: Int? = null,

    @field:SerializedName("jml_308_lahan_lainnya")
    val jml308LahanLainnya: Int? = null,

    @field:SerializedName("daftar_komoditas")
    val daftarKomoditas: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("start_latitude")
    val startLatitude: Double? = null,

    @field:SerializedName("end_latitude")
    val endLatitude: Double? = null,

    @field:SerializedName("start_longitude")
    val startLongitude: Double? = null,

    @field:SerializedName("end_longitude")
    val endLongitude: Double? = null,

    @field:SerializedName("status_upload")
    val statusUpload: Int? = null,
) : Parcelable