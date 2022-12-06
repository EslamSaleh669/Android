package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class LoginResponseError(
//
//	@field:SerializedName("error_description")
//	val errorDescription: String? = null,

	@field:SerializedName("error")
	val error: String? = null
)
