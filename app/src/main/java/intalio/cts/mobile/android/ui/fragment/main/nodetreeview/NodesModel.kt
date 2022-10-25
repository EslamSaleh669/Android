package intalio.cts.mobile.android.ui.fragment.main.nodetreeview

import com.google.gson.annotations.SerializedName
import intalio.cts.mobile.android.data.model.AttachmentModel
import java.io.Serializable

data class NodesModel(


    @field:SerializedName("icon")
    val icon: String? = null,

    @field:SerializedName("parentNodeId")
    val parentNodeId: Any? = null,

    @field:SerializedName("filters")
    val filters: String? = null,

    @field:SerializedName("roleIds")
    val roleIds: List<Any?>? = null,

    @field:SerializedName("isSearch")
    val isSearch: Any? = null,

    @field:SerializedName("inherit")
    var inherit: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("enableTodayCount")
    val enableTodayCount: Boolean? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("customFunctions")
    val customFunctions: Any? = null,
//
//	@field:SerializedName("conditions")
//	val conditions: Any? = null,

    @field:SerializedName("order")
    val order: Int? = null,

    var children: ArrayList<NodesModel?>? = null
) : Serializable