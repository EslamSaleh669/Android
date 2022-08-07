package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class CompleteTransferResponse(

	@field:SerializedName("completeTransferResponse")
	val completeTransferResponse: ArrayList<CompleteTransferResponseItem?>? = null
)

data class CompleteTransferResponseItem(

	@field:SerializedName("documentAttachmentIdHasValue")
	val documentAttachmentIdHasValue: Boolean? = null,

	@field:SerializedName("uncompletedDocumentReferenceNumber")
	val uncompletedDocumentReferenceNumber: String? = null,

	@field:SerializedName("documentId")
	val documentId: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("transferId")
	val transferId: Int? = null,

	@field:SerializedName("updated")
	val updated: Boolean? = null
)
