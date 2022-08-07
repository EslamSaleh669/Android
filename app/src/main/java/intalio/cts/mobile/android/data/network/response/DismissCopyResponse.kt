package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class DismissCopyResponse(

	@field:SerializedName("DismissCopyResponse")
	val dismissCopyResponse: ArrayList<DismissCopyResponseItem>? = null
)

data class DismissCopyResponseItem(

	@field:SerializedName("transferId")
	val transferId: Int? = null,

	@field:SerializedName("updated")
	val updated: Boolean? = null
)
