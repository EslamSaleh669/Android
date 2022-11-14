package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class FullStructuresResponse(

	@field:SerializedName("FullStructuresResponse")
	val fullStructuresResponse: List<FullStructuresResponseItem>? = null
)

data class ItemsItem(

	@field:SerializedName("parent")
	val parent: FullStructureParent? = null,

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<AttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("managerId")
	val managerId: Int? = null,

	@field:SerializedName("parentId")
	val parentId: Int? = null
)

data class FullStructureParent(

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<AttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AttributesItem(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class FullStructuresResponseItem(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("items")
	val items: ArrayList<ItemsItem>? = null
)
