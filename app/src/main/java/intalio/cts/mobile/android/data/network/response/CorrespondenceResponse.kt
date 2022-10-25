package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CorrespondenceResponse(

	@field:SerializedName("recordsFiltered")
	val recordsFiltered: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<CorrespondenceDataItem>? = null,

	@field:SerializedName("draw")
	val draw: Int? = null,

	@field:SerializedName("recordsTotal")
	val recordsTotal: Int? = null
): Serializable

data class correspondenceReceivingEntityIdItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
): Serializable

data class CorrespondenceDataItem(

	var messageLock : String,
	var isexternalbroadcast : Boolean,

	@field:SerializedName("receivingEntityId")
	val receivingEntityId: ArrayList<correspondenceReceivingEntityIdItem>? = null,

	@field:SerializedName("sendingEntity")
	val sendingEntity: String? = null,

	@field:SerializedName("fromUser")
	val fromUser: String? = null,

	@field:SerializedName("purpose")
	val purpose: Any? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("isRead")
	val isRead: Boolean? = null,

	@field:SerializedName("purposeId")
	val purposeId: Int? = null,

	@field:SerializedName("importanceId")
	val importanceId: Int? = null,

	@field:SerializedName("transferDate")
	val transferDate: String? = null,

	@field:SerializedName("toUserId")
	val toUserId: Int? = null,

	@field:SerializedName("sentToStructure")
	val sentToStructure: Boolean? = null,

	@field:SerializedName("priorityId")
	val priorityId: Int? = null,

	@field:SerializedName("toUser")
	val toUser: String? = null,

	@field:SerializedName("cced")
	val cced: Boolean? = null,

	@field:SerializedName("isOverDue")
	val isOverDue: Boolean? = null,

	@field:SerializedName("createdByStructureId")
	val createdByStructureId: Int? = null,

	@field:SerializedName("lockedBy")
    var lockedBy: String? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("lockedByDelegatedUser")
    var lockedByDelegatedUser: String? = null,

	@field:SerializedName("isLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("createdByUserId")
	val createdByUserId: Int? = null,

	@field:SerializedName("toStructureId")
	val toStructureId: Int? = null,

	@field:SerializedName("fromUserId")
	val fromUserId: Int? = null,

	@field:SerializedName("fromStructure")
	val fromStructure: String? = null,

	@field:SerializedName("lockedDate")
    var lockedDate: String? = null,

	@field:SerializedName("viewMode")
	val viewMode: Boolean? = null,

	@field:SerializedName("ownerDelegatedUserId")
	val ownerDelegatedUserId: Any? = null,

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
	val documentId: Int? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
) : Serializable
