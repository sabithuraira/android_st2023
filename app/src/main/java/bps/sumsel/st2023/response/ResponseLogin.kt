package bps.sumsel.st2023.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ResponseLogin(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class User(
	@field:SerializedName("kode_kab")
	val kodeKab: String? = null,

	@field:SerializedName("pengawas")
	val pengawas: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("kode_desa")
	val kodeDesa: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jabatan")
	val jabatan: Int? = null,

	@field:SerializedName("kode_kec")
	val kodeKec: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
) : Parcelable

@Parcelize
data class Data(
	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("user")
	val user: User? = null
) : Parcelable
