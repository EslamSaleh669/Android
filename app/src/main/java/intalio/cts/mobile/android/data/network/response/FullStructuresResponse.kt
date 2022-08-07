package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class FullStructuresResponse(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("items")
	val items: ArrayList<FullStructuresResponseItem>? = null
)

data class FullStructuresResponseItem(

	@field:SerializedName("parent")
	val parent: Any? = null,

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<Any?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("managerId")
	val managerId: Int? = null,

	@field:SerializedName("parentId")
	val parentId: Int? = null

)
