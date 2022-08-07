package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class TypesResponse(

	@field:SerializedName("TypesResponse")
	val typesResponse: ArrayList<TypesResponseItem?>? = null
)

data class TypesResponseItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
