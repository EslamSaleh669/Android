package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class AttachmentsResponse(

	@field:SerializedName("AttachmentsResponse")
	val attachmentsResponse: List<AttachmentsResponseItem?>? = null
)

data class Data(

	@field:SerializedName("hasEditAccess")
	val hasEditAccess: Boolean? = null,

	@field:SerializedName("version")
	val version: Int? = null,

	@field:SerializedName("isWord")
	val isWord: Boolean? = null
)

data class ChildrenItem(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("children")
	val children: List<AttachmentsResponseItem>? = null,

	@field:SerializedName("isLocked")
	val isLocked: Any? = null,

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

data class AttachmentsResponseItem(

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("children")
	val children: ArrayList<AttachmentsResponseItem>? = null,

	@field:SerializedName("isLocked")
	val isLocked: Any? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("state")
	val state: AttachmentState? = null,

	@field:SerializedName("title")
	val title: Any? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("parentId")
	val parentId: Any? = null
)

data class AttachmentState(

	@field:SerializedName("opened")
	val opened: Boolean? = null,

	@field:SerializedName("disabled")
	val disabled: Boolean? = null,

	@field:SerializedName("selected")
	val selected: Boolean? = null
)
