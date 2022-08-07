package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class PrivaciesResponse(

	@field:SerializedName("PrivaciesResponse")
	val privaciesResponse: ArrayList<PrivaciesResponseItem?>? = null
)

data class PrivaciesResponseItem(

	@field:SerializedName("level")
	val level: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = "-"
)
