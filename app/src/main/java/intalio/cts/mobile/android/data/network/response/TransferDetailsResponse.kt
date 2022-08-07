package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class TransferDetailsResponse(

	@field:SerializedName("fromUser")
	val fromUser: String? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("isRead")
	val isRead: Boolean? = null,

	@field:SerializedName("importanceId")
	val importanceId: Any? = null,

	@field:SerializedName("transferDate")
	val transferDate: String? = null,

	@field:SerializedName("sentToStructure")
	val sentToStructure: Boolean? = null,

	@field:SerializedName("cced")
	val cced: Boolean? = null,

	@field:SerializedName("isOverDue")
	val isOverDue: Boolean? = null,

	@field:SerializedName("lockedBy")
	val lockedBy: String? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("lockedByDelegatedUser")
	val lockedByDelegatedUser: String? = null,

	@field:SerializedName("isLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("receivingEntities")
	val receivingEntities: List<ReceivingEntitiesItem?>? = null,

	@field:SerializedName("createdByUserId")
	val createdByUserId: Int? = null,

	@field:SerializedName("toStructureId")
	val toStructureId: Int? = null,

	@field:SerializedName("fromUserId")
	val fromUserId: Int? = null,

	@field:SerializedName("fromStructure")
	val fromStructure: String? = null,

	@field:SerializedName("lockedDate")
	val lockedDate: String? = null,

	@field:SerializedName("ownerDelegatedUserId")
	val ownerDelegatedUserId: Any? = null,

	@field:SerializedName("openedDate")
	val openedDate: String? = null,

	@field:SerializedName("sentToUser")
	val sentToUser: Boolean? = null,

	@field:SerializedName("ownerUserId")
	val ownerUserId: Int? = null,

	@field:SerializedName("documentId")
	val documentId: Int? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null
)

data class ReceivingEntitiesItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
