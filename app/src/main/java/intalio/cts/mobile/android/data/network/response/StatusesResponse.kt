package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class StatusesResponse(

	@field:SerializedName("StatusesResponse")
	val statusesResponse: ArrayList<StatusesResponseItem?>? = null
)

data class StatusesResponseItem(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = "-"
)
