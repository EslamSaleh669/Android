package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class DelegationRequestsResponse(

	@field:SerializedName("DelegationRequestsResponse")
	val delegationRequestsResponse: ArrayList<DelegationRequestsResponseItem>? = null
)

data class DelegationRequestsResponseItem(

	@field:SerializedName("fromUserRoleId")
	var fromUserRoleId: Int? = 0,

	@field:SerializedName("fromUser")
	var fromUser: String? = "",

	@field:SerializedName("fromUserId")
	var fromUserId: Int? = 0,

	@field:SerializedName("id")
	var id: Int? = 0
)
