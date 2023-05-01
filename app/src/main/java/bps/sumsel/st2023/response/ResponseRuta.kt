package bps.sumsel.st2023.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ResponseRuta(

	@field:SerializedName("kode_kab")
	val kodeKab: String? = null,

	@field:SerializedName("kepala_ruta")
	val kepalaRuta: String? = null,

	@field:SerializedName("kode_desa")
	val kodeDesa: String? = null,

	@field:SerializedName("end_longitude")
	val endLongitude: String? = null,

	@field:SerializedName("end_time")
	val endTime: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_sls")
	val idSls: String? = null,

	@field:SerializedName("end_latitude")
	val endLatitude: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("start_longitude")
	val startLongitude: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("start_latitude")
	val startLatitude: String? = null,

	@field:SerializedName("kode_prov")
	val kodeProv: String? = null,

	@field:SerializedName("encId")
	val encId: String? = null,

	@field:SerializedName("id_sub_sls")
	val idSubSls: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("nurt")
	val nurt: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("kode_kec")
	val kodeKec: String? = null,

	@field:SerializedName("sektor")
	val sektor: Int? = null,
) : Parcelable
