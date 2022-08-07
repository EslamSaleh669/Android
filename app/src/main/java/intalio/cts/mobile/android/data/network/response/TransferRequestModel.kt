package intalio.cts.mobile.android.data.network.response

import java.io.Serializable

class TransferRequestModel : Serializable {

    var toStructureId = 0
    var toUserId: Int? = null
    var name: String? = null
    var dueDate: String? = null
    var purposeId: String? = null
    var instruction: String? = null
    var cced: Boolean? = null
    var FromStructureId = 0
    var ParentTransferId: Int? = null
    var IsStructure: Boolean? = null
    var DocumentId: String? = null
    var DocumentPrivacyId: String? = null
    var PrivacyId: String? = null
    var purposeName: String? = null
    var transferOfflineId: Int? = null
}