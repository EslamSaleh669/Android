package intalio.cts.mobile.android.data.model

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("ServiceUrl")
	var serviceUrl: String? = null,

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("Url")
	var url: String? = null,

	@field:SerializedName("ViewerUrl")
	var ViewerUrl: String? = null
)
