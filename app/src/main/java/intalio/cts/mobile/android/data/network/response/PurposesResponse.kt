package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class PurposesResponse(

	@field:SerializedName("PurposesResponse")
	val purposesResponse: ArrayList<PurposesResponseItem?>? = null
)

data class PurposesResponseItem(

	@field:SerializedName("cCed")
	val cCed: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
