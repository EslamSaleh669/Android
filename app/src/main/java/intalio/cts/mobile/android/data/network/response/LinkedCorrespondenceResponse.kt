package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class LinkedCorrespondenceResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<LinkedCorDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
)

data class LinkedCorDataItem(

	@field:SerializedName("allowDelete")
	val allowDelete: Boolean? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("isDirectLink")
	val isDirectLink: Boolean? = null,

	@field:SerializedName("statusId")
	val statusId: Int? = null,

	@field:SerializedName("linkedBy")
	val linkedBy: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: Any? = null,

	@field:SerializedName("linkedDocumentId")
	val linkedDocumentId: Int? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null,

	@field:SerializedName("linkedDocumentReferenceNumber")
	val linkedDocumentReferenceNumber: String? = null,

	@field:SerializedName("status")
	val status: Any? = null
)
