package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(

	@field:SerializedName("CategoryResponse")
	val categoryResponse: ArrayList<CategoryResponseItem?>? = null
)

data class CategoryResponseItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
