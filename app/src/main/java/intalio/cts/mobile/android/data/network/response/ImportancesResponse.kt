package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class ImportancesResponse(

	@field:SerializedName("ImportancesResponse")
	val importancesResponse: ArrayList<ImportancesResponseItem?>? = null
)

data class ImportancesResponseItem(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
