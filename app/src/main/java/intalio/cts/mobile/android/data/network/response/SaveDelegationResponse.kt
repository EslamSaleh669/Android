package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class SaveDelegationResponse(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("message")
	val message: Any? = null
)
