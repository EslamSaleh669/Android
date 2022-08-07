package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MetaDataResponse(

	@field:SerializedName("sendingEntity")
	val sendingEntity: SendingEntity? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("documentType")
	val documentType: DocumentType? = null,

	@field:SerializedName("subject")
	val subject: String? = "-",

	@field:SerializedName("dueDate")
	val dueDate: String? = "-",

	@field:SerializedName("documentTypeId")
	val documentTypeId: Any? = null,

	@field:SerializedName("importanceId")
	val importanceId: Any? = null,

	@field:SerializedName("templateId")
	val templateId: Any? = null,

	@field:SerializedName("body")
	val body: String? = null,

	@field:SerializedName("categoryName")
	val categoryName: String? = null,

	@field:SerializedName("priorityId")
	val priorityId: Int? = null,

	@field:SerializedName("createdByStructureId")
	val createdByStructureId: Int? = null,

	@field:SerializedName("basicAttributes")
	val basicAttributes: String? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = "-",

	@field:SerializedName("receivers")
	val receivers: List<ReceiversItem?>? = null,

	@field:SerializedName("carbonCopies")
	val carbonCopies: List<Any?>? = null,

	@field:SerializedName("carbonCopy")
	val carbonCopy: List<Any?>? = null,

	@field:SerializedName("isLocked")
	val isLocked: Any? = null,

	@field:SerializedName("attachmentId")
	val attachmentId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("keyword")
	val keyword: Any? = null,

	@field:SerializedName("receivingEntities")
	val receivingEntities: List<MetaDataReceivingEntitiesItem?>? = null,

	@field:SerializedName("createdByUserId")
	val createdByUserId: Int? = null,

	@field:SerializedName("classification")
	val classification: Classification? = null,

	@field:SerializedName("classificationId")
	val classificationId: Any? = null,

	@field:SerializedName("sendingEntityId")
	val sendingEntityId: Int? = null,

	@field:SerializedName("fromStructureId")
	val fromStructureId: Int? = null,

	@field:SerializedName("customAttributesTranslation")
	val customAttributesTranslation: String? = null,

	@field:SerializedName("scannedFile")
	val scannedFile: Any? = null,

	@field:SerializedName("formData")
	val formData: String? = null,

	@field:SerializedName("externalReferenceNumber")
	val externalReferenceNumber: String? = null,

	@field:SerializedName("enableEdit")
	val enableEdit: Boolean? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("customAttributes")
	val customAttributes: String? = null,

	@field:SerializedName("register")
	val register: Boolean? = null
): Serializable

data class SendingEntity(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = "-"
): Serializable

data class Classification(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: Any? = null
): Serializable

data class ReceiversItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: Any? = null
): Serializable

data class DocumentType(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: Any? = null
): Serializable

data class MetaDataReceivingEntitiesItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = "-"
): Serializable
