package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdvancedSearchRequest(

	@field:SerializedName("category")
	var category: String? = "",

	@field:SerializedName("subject")
	var subject: String? = "",

	@field:SerializedName("referenceNumber")
	var referenceNumber: String? = "",

	@field:SerializedName("status")
	var status: String? = null,

	@field:SerializedName("fromDate")
	var fromDate: String? = "",

	@field:SerializedName("toDate")
	var toDate: String? = "",

	@field:SerializedName("priority")
	var priority: String? = null,

	@field:SerializedName("documentSender")
	var documentSender: String? = null,

	@field:SerializedName("documentReceiver")
	var documentReceiver: String? = null,

	@field:SerializedName("fromUser")
	var fromUser: String? = null,

	@field:SerializedName("toUser")
	var toUser: String? = null,

	@field:SerializedName("fromStructure")
	var fromStructure: String? = null,

	@field:SerializedName("toStructure")
	var toStructure: String? = null,

	@field:SerializedName("fromTransferDate")
	var fromTransferDate: String? = "",

	@field:SerializedName("toTransferDate")
	var toTransferDate: String? = "",

	@field:SerializedName("isOverdue")
	var isOverdue: Boolean? = false,

	@field:SerializedName("keyword")
	var keyword: String? = "",

	@field:SerializedName("ocrContent")
	var ocrContent: String? = "",

	@field:SerializedName("delegationId")
	var delegationId: String? = "",


	@field:SerializedName("documentId")
	val documentId: Any? = null

	) : Serializable
