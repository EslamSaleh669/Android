package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class NotesResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<NotesDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
)

data class NotesDataItem(

	@field:SerializedName("notes")
	var notes: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("isEditable")
	val isEditable: Boolean? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("isCreatedByDelegator")
	val isCreatedByDelegator: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("isPrivate")
    var isPrivate: Boolean? = null,

	@field:SerializedName("createdByDelegatedUser")
	val createdByDelegatedUser: String? = null
)
