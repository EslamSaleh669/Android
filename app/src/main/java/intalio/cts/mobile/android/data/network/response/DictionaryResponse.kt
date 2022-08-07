package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class DictionaryResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<DictionaryDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
)

data class DictionaryDataItem(

	@field:SerializedName("isSystem")
	val isSystem: Boolean? = null,

	@field:SerializedName("ar")
	var ar: String? = null,

	@field:SerializedName("en")
	var en: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("keyword")
	var keyword: String? = null,

	@field:SerializedName("fr")
	var fr: String? = null
)
