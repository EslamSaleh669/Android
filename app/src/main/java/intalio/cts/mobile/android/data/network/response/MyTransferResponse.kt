package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class MyTransferResponse(

	@field:SerializedName("receivingEntityId")
	val receivingEntityId: List<MyTransferReceivingEntityIdItem?>? = null,

	@field:SerializedName("sendingEntity")
	val sendingEntity: String? = null,

	@field:SerializedName("fromUser")
	val fromUser: String? = null,

	@field:SerializedName("purpose")
	val purpose: String? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("purposeId")
	val purposeId: Int? = null,

	@field:SerializedName("importanceId")
	val importanceId: Int? = null,

	@field:SerializedName("priorityId")
	val priorityId: Int? = null,

	@field:SerializedName("toUser")
	val toUser: String? = null,

	@field:SerializedName("cced")
	val cced: Boolean? = null,

	@field:SerializedName("createdByStructureId")
	val createdByStructureId: Int? = null,

	@field:SerializedName("toStructureId")
	val toStructureId: Int? = null,

	@field:SerializedName("fromStructure")
	val fromStructure: String? = null,

	@field:SerializedName("viewMode")
	val viewMode: Boolean? = null,

	@field:SerializedName("fromStructureId")
	val fromStructureId: Int? = null,

	@field:SerializedName("toStructure")
	val toStructure: String? = null,

	@field:SerializedName("openedDate")
	val openedDate: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("sentToUser")
	val sentToUser: Boolean? = null,

	@field:SerializedName("receivingEntity")
	val receivingEntity: String? = null,

	@field:SerializedName("closedDate")
	val closedDate: String? = null,

	@field:SerializedName("instruction")
	val instruction: String? = null,

	@field:SerializedName("ownerUserId")
	val ownerUserId: Int? = null,

	@field:SerializedName("documentId")
	val documentId: Int? = null
)

data class MyTransferReceivingEntityIdItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
