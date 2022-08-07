package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class ParamSettingsResponse(

	@field:SerializedName("ParamSettingsResponse")
	val paramSettingsResponse: ArrayList<ParamSettingsResponseItem>? = null
)

data class ParamSettingsResponseItem(

	@field:SerializedName("isSystem")
	val isSystem: Boolean? = null,

	@field:SerializedName("description")
	val description: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("keyword")
	val keyword: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
