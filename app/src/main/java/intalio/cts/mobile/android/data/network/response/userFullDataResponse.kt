package intalio.cts.mobile.android.data.network.response

import com.google.gson.annotations.SerializedName

data class Role(

	@field:SerializedName("privileges")
	val privileges: List<String?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class StructuresItem(

	@field:SerializedName("parent")
	val parent: Parent? = null,

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("userAttributes")
	val userAttributes: List<UserAttributesItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<UserAttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("managerId")
	val managerId: Int? = null,

	@field:SerializedName("parentId")
	val parentId: Int? = null
)

data class UserAttributesItem(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class UserFullDataResponseItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("applicationRole")
	val applicationRole: String? = null,

	@field:SerializedName("role")
	val role: Role? = null,

	@field:SerializedName("structureIds")
	val structureIds: List<Int>? = null,

	@field:SerializedName("structures")
	val structures: List<StructuresItem?>? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("groups")
	val groups: List<GroupsItem?>? = null,

	@field:SerializedName("managerId")
	val managerId: Int? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("middleName")
	val middleName: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<UserAttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("applicationRoleId")
	val applicationRoleId: Int? = null,

	@field:SerializedName("defaultStructureId")
	val defaultStructureId: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class UserFullDataResponse(

	@field:SerializedName("userFullDataResponse")
	val userFullDataResponse: List<UserFullDataResponseItem?>? = null
)

data class Parent(

	@field:SerializedName("external")
	val external: Boolean? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attributes")
	val attributes: List<AttrAttributesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AttrAttributesItem(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class GroupsItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
