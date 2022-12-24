package intalio.cts.mobile.android.data.model

import com.google.gson.annotations.SerializedName


data class selectedUserModel(

    var fromUserId: Int? = null,
    var fromStructureId: Int? = null,
    var offlineId: Int? = null,
    var text: String? = null,
    var itemType: String? = null,
    var userPrivacyLevel: Int? = null
)
