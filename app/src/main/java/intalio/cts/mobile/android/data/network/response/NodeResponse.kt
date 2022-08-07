package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class NodeResponse(

	@field:SerializedName("NodeResponse")
	val nodeResponse: List<NodeResponseItem?>? = null
)

data class NodeResponseItem(

	@field:SerializedName("enableTotalCount")
	val enableTotalCount: Boolean? = null,

	@field:SerializedName("visible")
	val visible: Boolean? = null,

	@field:SerializedName("columns")
	val columns: String? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("parentNodeId")
	val parentNodeId: Any? = null,

	@field:SerializedName("filters")
	val filters: String? = null,

	@field:SerializedName("roleIds")
	val roleIds: List<Any?>? = null,

	@field:SerializedName("isSearch")
	val isSearch: Any? = null,

	@field:SerializedName("inherit")
	val inherit: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("enableTodayCount")
	val enableTodayCount: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("customFunctions")
	val customFunctions: Any? = null,
//
//	@field:SerializedName("conditions")
//	val conditions: Any? = null,

	@field:SerializedName("order")
	val order: Int? = null
)
