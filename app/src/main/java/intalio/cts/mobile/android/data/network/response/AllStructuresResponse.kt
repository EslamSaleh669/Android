package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllStructuresResponse(

	@field:SerializedName("structures")
	val structures: ArrayList<AllStructuresItem>? = null,

	@field:SerializedName("users")
	val users: ArrayList<AllStructuresUsersItem>? = null
) : Serializable

data class AllStructuresAttributesItem(

	@field:SerializedName("text")
	var text: String? = null,

	@field:SerializedName("type")
	val type: Any? = null,

	@field:SerializedName("value")
	val value: String? = null
): Serializable

data class AllStructuresUsersItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("applicationRole")
	val applicationRole: Any? = null,

	@field:SerializedName("structureIds")
	val structureIds: List<Int>? = null,

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
	val attributes: List<AllStructuresAttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("applicationRoleId")
	val applicationRoleId: Any? = null,

	@field:SerializedName("defaultStructureId")
	val defaultStructureId: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
): Serializable

data class AllStructuresItem(

	@field:SerializedName("parent")
	val parent: Any? = null,

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: Any? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("attributes")
	var attributes: List<AllStructuresAttributesItem?>? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("managerId")
	val managerId: Int? = null,

	@field:SerializedName("parentId")
	val parentId: Int? = null,

	var itemType: String? = "",

	var structureIds: List<Int>? = null,
): Serializable
