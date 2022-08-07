package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class TransferTransferResponse(

	@field:SerializedName("TransferTransferResponse")
	val transferTransferResponse: ArrayList<TransferTransferResponseItem?>? = null
)

data class TransferTransferResponseItem(

	@field:SerializedName("documentId")
	val documentId: Int? = null,

	@field:SerializedName("parentTransferId")
	val parentTransferId: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("updated")
	val updated: Boolean? = null
)
