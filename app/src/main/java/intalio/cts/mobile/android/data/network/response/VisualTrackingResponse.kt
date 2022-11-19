package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VisualTrackingResponse(

	@field:SerializedName("VisualTrackingResponse")
	var visualTrackingResponse: ArrayList<VisualTrackingResponseItem>? = null
):Serializable

data class VisualTrackingResponseItem(

	@field:SerializedName("sendingEntity")
	val sendingEntity: String? = null,

	@field:SerializedName("isRoot")
	val isRoot: Boolean? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: Any? = null,

	@field:SerializedName("isRead")
	val isRead: Boolean? = null,

	@field:SerializedName("description")
	val description: Any? = null,

	@field:SerializedName("privacy")
	val privacy: String? = null,

	@field:SerializedName("transferDate")
	val transferDate: Any? = null,

	@field:SerializedName("isOverDue")
	val isOverDue: Boolean? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("isLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("isCced")
	val isCced: Boolean? = null,

	@field:SerializedName("structureId")
	val structureId: Int? = null,

	@field:SerializedName("priority")
	val priority: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("parentId")
	val parentId: Any? = null,

	@field:SerializedName("ownerDelegatedUserId")
	val ownerDelegatedUserId: Int? = null,

	@field:SerializedName("openedDate")
	val openedDate: Any? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("isClosed")
	val isClosed: Boolean? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("receivingEntity")
	val receivingEntity: String? = null,

	@field:SerializedName("instruction")
	val instruction: Any? = null,

	@field:SerializedName("ownerUserId")
	val ownerUserId: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
) :Serializable
