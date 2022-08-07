package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class UsersStructure(

	@field:SerializedName("UsersStructure")
	val usersStructure: ArrayList<UsersStructureItem?>? = null
)

data class UsersStructureItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("applicationRole")
	val applicationRole: Any? = null,

	@field:SerializedName("structureIds")
	val structureIds: List<Int?>? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("groups")
	val groups: Any? = null,

	@field:SerializedName("managerId")
	val managerId: Any? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("middleName")
	val middleName: Any? = null,

	@field:SerializedName("attributes")
	val attributes: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("applicationRoleId")
	val applicationRoleId: Any? = null,

	@field:SerializedName("defaultStructureId")
	val defaultStructureId: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
