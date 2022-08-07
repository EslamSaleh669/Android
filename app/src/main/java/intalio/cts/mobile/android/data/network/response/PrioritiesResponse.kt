package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class PrioritiesResponse(

	@field:SerializedName("PrioritiesResponse")
	val prioritiesResponse: ArrayList<PrioritiesResponseItem?>? = null
)

data class PrioritiesResponseItem(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = "-"
)
