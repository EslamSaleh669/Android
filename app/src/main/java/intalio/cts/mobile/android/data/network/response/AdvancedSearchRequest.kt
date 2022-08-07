package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdvancedSearchRequest(

	@field:SerializedName("fromUser")
	val fromUser: String? = null,

	@field:SerializedName("subject")
	var subject: String? = "",

	@field:SerializedName("toDate")
	var toDate: String? = "",

	@field:SerializedName("fromStructure")
	var fromStructure: String? = null,

	@field:SerializedName("fromTransferDate")
	val fromTransferDate: String? = "",

	@field:SerializedName("priority")
	val priority: String? = null,

	@field:SerializedName("fromDate")
	var fromDate: String? = "",

	@field:SerializedName("toUser")
	val toUser: String? = null,

	@field:SerializedName("toStructure")
	var toStructure: String? = null,

	@field:SerializedName("documentReceiver")
	val documentReceiver: String? = null,

	@field:SerializedName("isOverdue")
	val isOverdue: Boolean? = false,

	@field:SerializedName("referenceNumber")
	var referenceNumber: String? = "",

	@field:SerializedName("documentSender")
	val documentSender: String? = null,

	@field:SerializedName("documentId")
	val documentId: Any? = null,

	@field:SerializedName("toTransferDate")
	val toTransferDate: String? = "",

	@field:SerializedName("category")
	var category: String? = "",

	@field:SerializedName("keyword")
	val keyword: String? = "",

	@field:SerializedName("status")
	val status: String? = null
) : Serializable
