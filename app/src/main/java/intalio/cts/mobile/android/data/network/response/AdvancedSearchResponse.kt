package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdvancedSearchResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<AdvancedSearchResponseDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
): Serializable

data class AdvancedSearchResponseDataItem(

	@field:SerializedName("sendingEntity")
	val sendingEntity: String? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("importanceId")
	val importanceId: Int? = null,

	@field:SerializedName("priorityId")
	val priorityId: Int? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("statusId")
	val statusId: Int? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("receivingEntity")
	val receivingEntity: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null
) : Serializable
