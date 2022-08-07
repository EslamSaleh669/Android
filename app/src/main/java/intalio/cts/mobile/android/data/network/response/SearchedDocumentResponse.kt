package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class SearchedDocumentResponse(

	@field:SerializedName("sendingEntity")
	val sendingEntity: SearchedSendingEntity? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("documentType")
	val documentType: SearchedDocumentType? = null,

	@field:SerializedName("createdByUser")
	val createdByUser: String? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("documentTypeId")
	val documentTypeId: Int? = null,

	@field:SerializedName("importanceId")
	val importanceId: Int? = null,

	@field:SerializedName("templateId")
	val templateId: Int? = null,

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

	@field:SerializedName("receivers")
	val receivers: List<SearchedReceiversItem?>? = null,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String? = null,

	@field:SerializedName("carbonCopies")
	val carbonCopies: List<CarbonCopiesItem?>? = null,

	@field:SerializedName("carbonCopy")
	val carbonCopy: List<Int?>? = null,

	@field:SerializedName("isLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("attachmentId")
	val attachmentId: Int? = null,

	@field:SerializedName("keyword")
	val keyword: String? = null,

	@field:SerializedName("receivingEntities")
	val receivingEntities: List<SearchedReceivingEntitiesItem?>? = null,

	@field:SerializedName("createdByUserId")
	val createdByUserId: Int? = null,

	@field:SerializedName("classification")
	val classification: SearchedClassification? = null,

	@field:SerializedName("classificationId")
	val classificationId: Int? = null,

	@field:SerializedName("sendingEntityId")
	val sendingEntityId: Int? = null,

	@field:SerializedName("fromStructureId")
	val fromStructureId: Int? = null,

	@field:SerializedName("customAttributesTranslation")
	val customAttributesTranslation: String? = null,

	@field:SerializedName("scannedFile")
	val scannedFile: ScannedFile? = null,

	@field:SerializedName("formData")
	val formData: String? = null,

	@field:SerializedName("externalReferenceNumber")
	val externalReferenceNumber: String? = null,

	@field:SerializedName("enableEdit")
	val enableEdit: Boolean? = null,

	@field:SerializedName("categoryId")
	val categoryId: Int? = null,

	@field:SerializedName("customAttributes")
	val customAttributes: String? = null,

	@field:SerializedName("register")
	val register: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SearchedDocumentType(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class ScannedFile(

	@field:SerializedName("fileName")
	val fileName: String? = null,

	@field:SerializedName("extension")
	val extension: String? = null,

	@field:SerializedName("mD5Checksum")
	val mD5Checksum: String? = null,

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("fileSize")
	val fileSize: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("contentType")
	val contentType: String? = null
)

data class SearchedClassification(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class SearchedReceivingEntitiesItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class CarbonCopiesItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class SearchedSendingEntity(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)

data class SearchedReceiversItem(

	@field:SerializedName("isEntityGroup")
	val isEntityGroup: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("text")
	val text: String? = null
)
