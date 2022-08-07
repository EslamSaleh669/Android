package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class UploadAttachmentResponse(

	@field:SerializedName("isLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("state")
	val state: State? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("parentId")
	val parentId: String? = null
)

data class State(

	@field:SerializedName("opened")
	val opened: Boolean? = null,

	@field:SerializedName("disabled")
	val disabled: Boolean? = null,

	@field:SerializedName("selected")
	val selected: Boolean? = null
)
