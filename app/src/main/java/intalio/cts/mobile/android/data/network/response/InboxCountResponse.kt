package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class InboxCountResponse(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("today")
	val today: Int? = null
)
