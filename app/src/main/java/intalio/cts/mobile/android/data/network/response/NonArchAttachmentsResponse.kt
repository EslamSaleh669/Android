package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class NonArchAttachmentsResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<NonArchDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
)

data class NonArchDataItem(

	@field:SerializedName("quantity")
	var quantity: Int? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("isEditable")
	val isEditable: Boolean? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("description")
    var description: String? = null,

	@field:SerializedName("isCreatedByDelegator")
	val isCreatedByDelegator: Boolean? = null,

	@field:SerializedName("typeId")
	val typeId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
    var type: String? = null,

	@field:SerializedName("createdByDelegatedUser")
	val createdByDelegatedUser: String? = null
)
