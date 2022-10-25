package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class DelegatorResponse(

	@field:SerializedName("fromDate")
	val fromDate: String? = null,

	@field:SerializedName("categoryNames")
	val categoryNames: Any? = null,

	@field:SerializedName("isStructureSender")
	val isStructureSender: Boolean? = null,

	@field:SerializedName("categoryIds")
	val categoryIds: List<Int?>? = null,

	@field:SerializedName("privacyLevel")
	val privacyLevel: Int? = null,

	@field:SerializedName("fromUserId")
	val fromUserId: Int? = null,

	@field:SerializedName("toDate")
	val toDate: String? = null,

	@field:SerializedName("groupIds")
	val groupIds: List<Any?>? = null,

	@field:SerializedName("structureIds")
	val structureIds: List<Int?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("isStructureReceiver")
	val isStructureReceiver: Boolean? = null,

	@field:SerializedName("toUserId")
	val toUserId: Int? = null
)
