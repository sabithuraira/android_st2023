package bps.sumsel.st2023.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ResponseSls(
	@field:SerializedName("datas")
	val datas: Datas? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class ResponseSingleSls(
	@field:SerializedName("data")
	val data: SlsItem? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class ResponseSlsPetugas(
	@field:SerializedName("datas")
	val datas: List<SlsItem>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Datas(
	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("data")
	val data: List<SlsItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String? = null,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("links")
	val links: List<LinksItem?>? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
) : Parcelable

@Parcelize
data class SlsItem(

	@field:SerializedName("nama_sls")
	val namaSls: String? = null,

	@field:SerializedName("kode_desa")
	val kodeDesa: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_sls")
	val idSls: String? = null,

	@field:SerializedName("jml_dok_ke_pml")
	val jmlDokKePml: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("status_selesai_pcl")
	val statusSelesaiPcl: Int? = null,

	@field:SerializedName("encId")
	val encId: String? = null,

	@field:SerializedName("id_sub_sls")
	val idSubSls: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jml_keluarga_tani_st2023")
	val jmlKeluargaTaniSt2023: Int? = null,

	@field:SerializedName("kode_kab")
	val kodeKab: String? = null,

	@field:SerializedName("jml_keluarga_tani")
	val jmlKeluargaTani: Int? = null,

	@field:SerializedName("status_sls")
	val statusSls: Int? = null,

	@field:SerializedName("sls_op")
	val slsOp: Int? = null,

	@field:SerializedName("kode_pml")
	val kodePml: String? = null,

	@field:SerializedName("kode_pcl")
	val kodePcl: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("jml_nr")
	val jmlNr: Int? = null,

	@field:SerializedName("jenis_sls")
	val jenisSls: Int? = null,

	@field:SerializedName("jml_art_tani")
	val jmlArtTani: Int? = null,

	@field:SerializedName("sektor1")
	val sektor1: Int? = null,

	@field:SerializedName("sektor2")
	val sektor2: Int? = null,

	@field:SerializedName("sektor3")
	val sektor3: Int? = null,

	@field:SerializedName("sektor4")
	val sektor4: Int? = null,

	@field:SerializedName("kode_prov")
	val kodeProv: String? = null,

	@field:SerializedName("sektor5")
	val sektor5: Int? = null,

	@field:SerializedName("sektor6")
	val sektor6: Int? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("jml_dok_ke_koseka")
	val jmlDokKeKoseka: Int? = null,

	@field:SerializedName("kode_kec")
	val kodeKec: String? = null,

	@field:SerializedName("jml_dok_ke_bps")
	val jmlDokKeBps: Int? = null,

	@field:SerializedName("kode_koseka")
	val kodeKoseka: String? = null,

	@field:SerializedName("nama_desa")
	val nama_desa: String? = null,

	@field:SerializedName("nama_kec")
	val nama_kec: String? = null,

	@field:SerializedName("daftar_ruta")
	val daftar_ruta: List<ResponseRuta>? = null,
) : Parcelable

@Parcelize
data class LinksItem(

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable
