package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DelegationsResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<DelegationDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
)

data class DelegationDataItem(

	@field:SerializedName("toUser")
	val toUser: String? = null,

	@field:SerializedName("fromDate")
	val fromDate: String? = null,

	@field:SerializedName("categoryIds")
	val categoryIds: List<Int?>? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("toDate")
	val toDate: String? = null,

	@field:SerializedName("modifiedDate")
	val modifiedDate: String? = null,

	@field:SerializedName("toUserValueText")
	val toUserValueText: ToUserValueText? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Serializable

data class ToUserValueText(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
