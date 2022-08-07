package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class DocumentBasicInfoResponse(

	@field:SerializedName("sendingEntity")
	val sendingEntity: Any? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("privacyId")
	val privacyId: Any? = null,

	@field:SerializedName("documentType")
	val documentType: Any? = null,

	@field:SerializedName("subject")
	val subject: Any? = null,

	@field:SerializedName("dueDate")
	val dueDate: Any? = null,

	@field:SerializedName("documentTypeId")
	val documentTypeId: Any? = null,

	@field:SerializedName("importanceId")
	val importanceId: Any? = null,

	@field:SerializedName("templateId")
	val templateId: Any? = null,

	@field:SerializedName("body")
	val body: Any? = null,

	@field:SerializedName("categoryName")
	val categoryName: String? = null,

	@field:SerializedName("priorityId")
	val priorityId: Any? = null,

	@field:SerializedName("createdByStructureId")
	val createdByStructureId: Int? = null,

	@field:SerializedName("basicAttributes")
	val basicAttributes: Any? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("receivers")
	val receivers: List<Any?>? = null,

	@field:SerializedName("carbonCopies")
	val carbonCopies: Any? = null,

	@field:SerializedName("carbonCopy")
	val carbonCopy: Any? = null,

	@field:SerializedName("isLocked")
	val isLocked: Any? = null,

	@field:SerializedName("attachmentId")
	val attachmentId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("keyword")
	val keyword: Any? = null,

	@field:SerializedName("receivingEntities")
	val receivingEntities: List<Any?>? = null,

	@field:SerializedName("createdByUserId")
	val createdByUserId: Int? = null,

	@field:SerializedName("classification")
	val classification: Any? = null,

	@field:SerializedName("classificationId")
	val classificationId: Any? = null,

	@field:SerializedName("sendingEntityId")
	val sendingEntityId: Any? = null,

	@field:SerializedName("fromStructureId")
	val fromStructureId: Int? = null,

	@field:SerializedName("customAttributesTranslation")
	val customAttributesTranslation: Any? = null,

	@field:SerializedName("scannedFile")
	val scannedFile: Any? = null,

	@field:SerializedName("formData")
	val formData: Any? = null,

	@field:SerializedName("externalReferenceNumber")
	val externalReferenceNumber: Any? = null,

	@field:SerializedName("enableEdit")
	val enableEdit: Boolean? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("customAttributes")
	val customAttributes: Any? = null,

	@field:SerializedName("register")
	val register: Boolean? = null
)
