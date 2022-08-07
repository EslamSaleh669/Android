package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(

	@field:SerializedName("sub")
	val sub: String? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("StructureSender")
	val structureSender: String? = null,

	@field:SerializedName("FirstName")
	val firstName: String? = null,

	@field:SerializedName("amr")
	val amr: String? = null,

	@field:SerializedName("StructureReceiver")
	val structureReceiver: String? = null,

	@field:SerializedName("MiddleName")
	val middleName: String? = null,

//	@field:SerializedName("Clients")
//	val clients: List<String?>? = null,

	@field:SerializedName("GroupIds")
	val groupIds: String? = null,

	@field:SerializedName("Privacy")
	val privacy: String? = null,

	@field:SerializedName("idp")
	val idp: String? = null,

	@field:SerializedName("auth_time")
	val authTime: Int? = null,

	@field:SerializedName("DisplayName")
	val displayName: String? = null,

	@field:SerializedName("Id")
	val id: String? = null,

	@field:SerializedName("LastName")
	val lastName: String? = null,

	@field:SerializedName("http://schemas.microsoft.com/ws/2008/06/identity/claims/role")
	val httpSchemasMicrosoftComWs200806IdentityClaimsRole: String? = null,

	@field:SerializedName("StructureIds")
	val structureIds: String? = null,

	@field:SerializedName("LoginProviderType")
	val loginProviderType: Int? = null,

	@field:SerializedName("ManagerId")
	val managerId: String? = null,

	@field:SerializedName("ApplicationRoleId")
	val applicationRoleId: String? = null,

	@field:SerializedName("StructureId")
	val structureId: String? = null
)
