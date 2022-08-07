package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class FullCategoriesResponse(

	@field:SerializedName("FullCategoriesResponse")
	val fullCategoriesResponse: ArrayList<FullCategoriesResponseItem>? = null
)

data class FullCategoriesResponseItem(

	@field:SerializedName("nameAr")
	val nameAr: String? = null,

	@field:SerializedName("configuration")
	val configuration: Any? = null,

	@field:SerializedName("searchAttribute")
	val searchAttribute: String? = null,

	@field:SerializedName("searchAttributeTranslation")
	val searchAttributeTranslation: String? = null,

	@field:SerializedName("nameFr")
	val nameFr: String? = null,

	@field:SerializedName("basicAttribute")
	val basicAttribute: String? = null,

	@field:SerializedName("grouping")
	val grouping: Any? = null,

	@field:SerializedName("withoutFile")
	val withoutFile: Boolean? = null,

	@field:SerializedName("customAttributeTranslation")
	val customAttributeTranslation: String? = null,

	@field:SerializedName("customAttribute")
	val customAttribute: String? = null,

	@field:SerializedName("roleIds")
	val roleIds: List<Int?>? = null,

	@field:SerializedName("byFile")
	val byFile: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("allowedExtensionsByFile")
	val allowedExtensionsByFile: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("byTemplate")
	val byTemplate: Boolean? = null
)
