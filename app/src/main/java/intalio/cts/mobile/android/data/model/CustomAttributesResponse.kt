package intalio.cts.mobile.android.data.model

import com.google.gson.annotations.SerializedName

data class CustomAttributesResponse(

	@field:SerializedName("components")
	val components: ArrayList<ComponentsItem?>? = null
)

data class Widget(

	@field:SerializedName("type")
	val type: String? = null
)

data class Properties(
	val any: Any? = null
)

data class Attributes(
	val any: Any? = null
)

data class Conditional(

	@field:SerializedName("eq ")
	val eq: String? = null,

	@field:SerializedName("show ")
	val show: Any? = null,

	@field:SerializedName("json")
	val json: String? = null,

	@field:SerializedName("when ")
	val wwhen: Any? = null
)

data class Overlay(

	@field:SerializedName("top")
	val top: String? = null,

	@field:SerializedName("left")
	val left: String? = null,

	@field:SerializedName("width")
	val width: String? = null,

	@field:SerializedName("style")
	val style: String? = null,

	@field:SerializedName("height")
	val height: String? = null
)

data class ComponentsItem(

	@field:SerializedName("allowMultipleMasks")
	val allowMultipleMasks: Boolean? = null,

	@field:SerializedName("redrawOn")
	val redrawOn: String? = null,

	@field:SerializedName("conditional")
	val conditional: Conditional? = null,

	@field:SerializedName("defaultValue")
	val defaultValue: String? = null,

	@field:SerializedName("tooltip")
	val tooltip: String? = null,

	@field:SerializedName("suffix")
	val suffix: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("calculateValue")
	val calculateValue: String? = null,

	@field:SerializedName("protected")
	val jsonMemberProtected: Boolean? = null,

	@field:SerializedName("dbIndex")
	val dbIndex: Boolean? = null,

	@field:SerializedName("modalEdit")
	val modalEdit: Boolean? = null,

	@field:SerializedName("inputType")
	val inputType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("persistent")
	val persistent: Boolean? = null,

	@field:SerializedName("case")
	val jsonMemberCase: String? = null,

	@field:SerializedName("hideLabel")
	val hideLabel: Boolean? = null,

	@field:SerializedName("customConditional")
	val customConditional: String? = null,

	@field:SerializedName("overlay")
	val overlay: Overlay? = null,

	@field:SerializedName("tabindex")
	val tabindex: String? = null,

	@field:SerializedName("properties ")
	val properties: Properties? = null,

	@field:SerializedName("inputMask ")
	val inputMask: String? = null,

	@field:SerializedName("clearOnHide")
	val clearOnHide: Boolean? = null,

	@field:SerializedName("inputFormat")
	val inputFormat: String? = null,

	@field:SerializedName("input")
	val input: Boolean? = null,

	@field:SerializedName("customDefaultValue")
	val customDefaultValue: String? = null,

	@field:SerializedName("spellcheck")
	val spellcheck: Boolean? = null,

	@field:SerializedName("tags ")
	val tags: List<Any?>? = null,

	@field:SerializedName("unique")
	val unique: Boolean? = null,

	@field:SerializedName("showCharCount")
	val showCharCount: Boolean? = null,

	@field:SerializedName("errorLabel")
	val errorLabel: String? = null,

	@field:SerializedName("widget")
	val widget: Widget? = null,

	@field:SerializedName("refreshOn")
	val refreshOn: String? = null,

	@field:SerializedName("hidden")
	val hidden: Boolean? = null,

	@field:SerializedName("prefix ")
	val prefix: String? = null,

	@field:SerializedName("labelPosition")
	val labelPosition: String? = null,

	@field:SerializedName("calculateServer")
	val calculateServer: Boolean? = null,

	@field:SerializedName("disabled")
	val disabled: Boolean? = null,

	@field:SerializedName("placeholder")
	val placeholder: String? = null,

	@field:SerializedName("description ")
	val description: String? = null,

	@field:SerializedName("key")
	val key: String? = null,

	@field:SerializedName("mask")
	val mask: Boolean? = null,

	@field:SerializedName("validate")
	val validate: Validate? = null,

	@field:SerializedName("autocomplete")
	val autocomplete: String? = null,

	@field:SerializedName("dataGridLabel")
	val dataGridLabel: Boolean? = null,

	@field:SerializedName("allowCalculateOverride")
	val allowCalculateOverride: Boolean? = null,

	@field:SerializedName("multiple")
	val multiple: Boolean? = null,

	@field:SerializedName("tableView")
	val tableView: Boolean? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("autofocus")
	val autofocus: Boolean? = null,

	@field:SerializedName("showWordCount")
	val showWordCount: Boolean? = null,

	@field:SerializedName("validateOn")
	val validateOn: String? = null,

	@field:SerializedName("encrypted")
	val encrypted: Boolean? = null,

	@field:SerializedName("customClass")
	val customClass: String? = null,

	@field:SerializedName("attributes")
	val attributes: Attributes? = null,

	@field:SerializedName("logic")
	val logic: List<Any?>? = null
)

data class Validate(

	@field:SerializedName("strictDateValidation")
	val strictDateValidation: Boolean? = null,

	@field:SerializedName("pattern ")
	val pattern: String? = null,

	@field:SerializedName("required ")
	val required: Boolean? = null,

	@field:SerializedName("custom")
	val custom: String? = null,

	@field:SerializedName("customPrivate")
	val customPrivate: Boolean? = null,

	@field:SerializedName("minLength")
	val minLength: String? = null,

	@field:SerializedName("unique")
	val unique: Boolean? = null,

	@field:SerializedName("multiple")
	val multiple: Boolean? = null,

	@field:SerializedName("customMessage")
	val customMessage: String? = null,

	@field:SerializedName("json")
	val json: String? = null,

	@field:SerializedName("maxLength")
	val maxLength: String? = null
)
