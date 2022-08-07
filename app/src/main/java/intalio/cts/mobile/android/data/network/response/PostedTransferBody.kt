package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class PostedTransferBody(

	@field:SerializedName("PostedTransferBody")
	val postedTransferBody: List<PostedTransferBodyItem?>? = null
)

data class PostedTransferBodyItem(

	@field:SerializedName("ParentTransferId")
	val parentTransferId: Int? = null,

	@field:SerializedName("cced")
	val cced: Boolean? = null,

	@field:SerializedName("DocumentPrivacyId")
	val documentPrivacyId: Int? = null,

	@field:SerializedName("FromStructureId")
	val fromStructureId: Int? = null,

	@field:SerializedName("toStructureId")
	val toStructureId: Int? = null,

	@field:SerializedName("instruction")
	val instruction: String? = null,

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("purposeId")
	val purposeId: String? = null,

	@field:SerializedName("DocumentId")
	val documentId: Int? = null,

	@field:SerializedName("toUserId")
	val toUserId: Any? = null,

	@field:SerializedName("IsStructure")
	val isStructure: Boolean? = null
)
