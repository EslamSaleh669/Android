package intalio.cts.mobile.android.data.network


import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentDetailsModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentVersionModel
import intalio.cts.mobile.android.data.network.response.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST


interface ApiClient {
    //http://192.168.1.4:9448/connect/token

    @FormUrlEncoded
    @POST()
    fun userLogin(
        @Url url: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") GrantType: String,
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("scope") scope: String
    ): Observable<TokenResponse>

    // https://iamp.intalio.com/connect/token
    @GET()
    fun getUserBasicInfo(
        @Header("Authorization") token: String,
        @Url url: String
    ): Observable<UserInfoResponse>

//    @GET("TranslatorDictionary/List")
//    fun getDictionary(
//        @Header("Authorization") token: String,
//        @Query("draw") draw: Int,
//        @Query("start") start: Int,
//        @Query("length") length: Int
//
//    ): Observable<DictionaryResponse>
//

    @GET()
    fun getDictionary(
        @Header("Authorization") token: String,
        @Url url: String,
        @Query("draw") draw: Int,
        @Query("start") start: Int,
        @Query("length") length: Int

    ): Observable<DictionaryResponse>

    @GET()
    fun getUserFullData(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("language") lang: String
    ): Observable<UserFullDataResponseItem>
    // https://iamp.intalio.com/api/GetUser

    @GET()
    fun geStructure(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("language") lang: String
    ): Observable<AllStructuresItem>
    // https://iamp.intalio.com/api/GetUser

    @GET("SendingRule/GetStructureIdsSendingRules")
    fun geStructureSendingRules(
        @Header("Authorization") token: String,
        @Query("structureId") id: Int,
    ): Call<ResponseBody>


    @GET()
    fun getFullStructures(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("startIndex") start: Int ,
        @Query("pageSize") pageSize: Int,
        @Query("language") language: Int

    ): Observable<FullStructuresResponseItem>


    @GET("Parameter/List")
    fun getSettings(
        @Header("Authorization") token: String
    ): Observable<ArrayList<ParamSettingsResponseItem>>

    @GET("Node/ListTreeNodes")
    fun getNodesData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,

    ): Observable<ArrayList<NodeResponseItem>>

    @GET("Transfer/GetInboxCounts")
    fun getInboxCount(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("nodeId") nodeId: Int,
        @Query("delegationId")delegationId: Int

    ): Observable<InboxCountResponse>


    @GET("Transfer/GetSentCounts")
    fun getSentCount(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("nodeId") nodeId: Int,
        @Query("delegationId")delegationId: Int

    ): Observable<InboxCountResponse>

    @GET("Transfer/GetCompletedCounts")
    fun getCompletedCount(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("nodeId") nodeId: Int,
        @Query("delegationId")delegationId: Int
    ): Observable<InboxCountResponse>


    @GET("Document/GetClosedCounts")
    fun getClosedCount(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("nodeId") nodeId: Int,
        @Query("delegationId")delegationId: Int
    ): Observable<InboxCountResponse>

    @GET("Document/GetMyRequestsCounts")
    fun getRequestedCount(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("nodeId") nodeId: Int,
        @Query("delegationId")delegationId: Int
    ): Observable<InboxCountResponse>


    @GET("Category/ListCategories")
    fun getCategoriesData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<List<CategoryResponseItem>>

    @FormUrlEncoded
    @POST()
    fun getUserStructureData(
        @Url url: String,
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ids[]") ids: Array<Int>


    ): Observable<ArrayList<UsersStructureItem>>
//https://iamp.intalio.com/api/SearchUsersByStructureIds


    @FormUrlEncoded
    @POST()
    fun  listUserExistenceAttributeInStructure(
        @Url url: String,
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ids[]") ids: Array<Int>,
        @Field("attributeName") attributeName: String,
        @Field("attributeValue") attributeValue: String,
        @Field("returnedAttribute") returnedAttribute: String

    ): Call<ResponseBody>


    @GET("Status/ListStatuses")
    fun getStatuses(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<StatusesResponseItem>>


    @GET("Purpose/ListPurposes")
    fun getPurposes(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<PurposesResponseItem>>


    @GET("Priority/ListPriorities")
    fun getPriorities(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<PrioritiesResponseItem>>


    @GET("Privacy/ListPrivacies")
    fun getPrivacies(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<PrivaciesResponseItem>>

    @GET("Importance/ListImportances")
    fun getImportances(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<ImportancesResponseItem>>


    @GET("Category/ListFullCategories")
    fun getFullCategories(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<FullCategoriesResponseItem>>


    @GET("NonArchivedAttachmentsTypes/ListTypes")
    fun getTypes(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String

    ): Observable<ArrayList<TypesResponseItem>>

    @POST()
    fun getAllStructures(
        @Url url: String,
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("text") text: String,
        @Query("ids[]") ids: ArrayList<Int>,
        @Query("language") language: String


        ): Observable<AllStructuresResponse>

    @FormUrlEncoded
    @POST()
    fun getAvailableStructures(
        @Url url: String,
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("text") text: String,
        @Field("ids[]") ids: ArrayList<Int>,
        @Field("language") language: String

    ): Observable<AllStructuresResponse>

    // "https://iamp.intalio.com/api/GetUsersAndStructuresWithSearchAttributes"
    ////
    @FormUrlEncoded
    @POST("Transfer/ListInbox")
    fun inboxData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("DelegationId") delegationId: Int

    ): Observable<CorrespondenceResponse>


    @FormUrlEncoded
    @POST("Transfer/ListSent")
    fun sentData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("DelegationId") delegationId: Int

    ): Observable<CorrespondenceResponse>


    @FormUrlEncoded
    @POST("Transfer/ListCompleted")
    fun completedData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("DelegationId") delegationId: Int

    ): Observable<CorrespondenceResponse>


    @FormUrlEncoded
    @POST("Document/ListClosed")
    fun closedData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("DelegationId") delegationId: Int

    ): Observable<CorrespondenceResponse>

    @FormUrlEncoded
    @POST("Document/ListMyRequests")
    fun requestedData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("DelegationId") delegationId: Int

    ): Observable<CorrespondenceResponse>


    @POST("Transfer/View")
    fun viewAction(
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int


    ): Call<ResponseBody>


    @POST("Transfer/Lock")
    fun lockTransfer(
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int? = null


    ): Call<ResponseBody>

    @POST("Transfer/Recall")
    fun recallTransfer(
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int

    ):Call<ResponseBody>

    @POST("Transfer/UnLock")
    fun unlockTransfer(
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int

    ):Call<ResponseBody>



    @FormUrlEncoded
    @POST("Transfer/DismissCarbonCopy")
    fun transferDismissCopy(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ids[]") ids: Array<Int?>,
        @Field("delegationId") delegationId:Int

    ): Observable<ArrayList<DismissCopyResponseItem>>


    @GET("Document/GetDocumentBasicInfoByTransferId")
    fun getDocumentBasicInfo(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") transferId: Int

    ): Observable<DocumentBasicInfoResponse>


    @GET("Document/GetDocument")
    fun getDocument(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") documentId: Int

    ): Observable<MetaDataResponse>


    @GET("Document/GetDocumentByTransferId")
    fun getMetaDataInfo(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId:Int

    ): Observable<MetaDataResponse>


    @GET("Document/GetSearchDocument")
    fun getSearchDocument(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") documentId: Int,
        @Query("delegationId") delegationId:Int


    ): Observable<MetaDataResponse>


    @FormUrlEncoded
    @POST("Delegation/List")
    fun delegationsData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int

    ): Observable<DelegationsResponse>

    @FormUrlEncoded
    @POST("Delegation/Save")
    fun saveDelegation(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ToUserId") toUserId: Int,
        @Field("FromDate") fromDate: String,
        @Field("ToDate") toDate: String,
        @Field("CategoryIds[]") ids: ArrayList<Int>,
    ): Observable<SaveDelegationResponse>

    @FormUrlEncoded
    @POST("Delegation/Save")
    fun saveEditedDelegation(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ToUserId") toUserId: Int,
        @Field("FromDate") fromDate: String,
        @Field("ToDate") toDate: String,
        @Field("CategoryIds[]") ids: ArrayList<Int>,
        @Field("Id") delegationId: Int
    ): Observable<SaveDelegationResponse>

    @DELETE()
    fun deleteDelegation(
        @Header("Authorization") token: String,
        @Url url: String,
        @Query("ids") ids: Array<Int>
    ): Call<ResponseBody>


    @GET("Delegation/ListDelegationToUser")
    fun delegationRequests(
        @Header("Authorization") token: String
    ): Call<ArrayList<DelegationRequestsResponseItem>>

    @GET("Delegation/GetByDelegationId")
    fun getDelegationById(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("delegationId") delegationId: Int
    ): Observable<DelegatorResponse>


    @GET("Note/List")
    fun notesData(
        @Header("Authorization") token: String,
        @Query("documentId") documentId: Int,
        @Query("TransferId") transferId: Int? = null,
        @Query("start") start: Int,
        @Query("length") length: Int,
        @Query("delegationId") delegationId:  Int? = null

    ): Observable<NotesResponse>


    @FormUrlEncoded
    @POST("Note/Index")
    fun saveNote(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("DocumentId") DocumentId: Int,
        @Field("TransferId") TransferId: Int,
        @Field("Notes") Notes: String,
        @Field("IsPrivate") IsPrivate: Boolean,
        @Field("delegationId") delegationId: Int
    ): Observable<SaveNotesResponse>


    @FormUrlEncoded
    @POST("Note/Index")
    fun saveEditedNote(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("DocumentId") DocumentId: Int,
        @Field("TransferId") TransferId: Int,
        @Field("Notes") Notes: String,
        @Field("IsPrivate") IsPrivate: Boolean,
        @Field("Id") nodeId: Int,
        @Field("delegationId") delegationId: Int
    ): Observable<SaveNotesResponse>

    @DELETE("Note/Delete")
    fun deleteNote(
        @Header("Authorization") token: String,
        @Query("id") noteID: Int,
        @Query("documentId") DocumentId: Int,
        @Query("transferId") TransferId: Int,
        @Query("delegationId") delegationId: Int


    ): Observable<Boolean>

    @FormUrlEncoded
    @POST("Transfer/ListTransferHistory")
    fun transfersHistoryData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("documentId") documentId: Int,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Query("delegationId") delegationId: Int
    ): Observable<TransferHistoryResponse>


    @GET("Transfer/GetTransferDetailsById")
    fun getTransferDetails(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int

    ): Observable<TransferDetailsResponse>


    @GET("Transfer/GetTransferInfoById")
    fun getMyTransfer(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") transferId: Int,
        @Query("delegationId") delegationId: Int


    ): Observable<MyTransferResponse>


    @GET("NonArchivedAttachments/List")
    fun nonArchData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("documentId") documentId: Int,
        @Query("start") start: Int,
        @Query("length") length: Int,
        @Query("delegationId") delegationId: Int


    ): Observable<NonArchAttachmentsResponse>


    @POST("/NonArchivedAttachments/Index")
    fun saveNonArch(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("DocumentId") DocumentId: Int,
        @Query("TransferId") TransferId: Int,
        @Query("TypeId") TypeId: Int,
        @Query("Description") Description: String,
        @Query("Quantity") Quantity: Int,
        @Query("delegationId") delegationId: Int

    ): Observable<SaveNotesResponse>


    @POST("/NonArchivedAttachments/Index")
    fun saveEditedNonArch(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("DocumentId") DocumentId: Int,
        @Query("TransferId") TransferId: Int,
        @Query("TypeId") TypeId: Int,
        @Query("Description") Description: String,
        @Query("Quantity") Quantity: Int,
        @Query("Id") nonarchID: Int,
        @Query("delegationId") delegationId: Int

    ): Observable<SaveNotesResponse>


    @DELETE("NonArchivedAttachments/Delete")
    fun deleteNonArch(
        @Header("Authorization") token: String,
        @Query("id") nonarchID: Int,
        @Query("documentId") DocumentId: Int,
        @Query("transferId") TransferId: Int,
        @Query("delegationId") delegationId: Int


    ): Observable<Boolean>


    @GET("LinkedDocument/List")
    fun getLinkedC(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("documentId") documentId: Int,
        @Query("delegationId") delegationId: Int
    ): Observable<LinkedCorrespondenceResponse>

    @DELETE("LinkedDocument/Delete")
    fun deleteLinkedC(
        @Header("Authorization") token: String,
        @Query("id") noteID: Int,
        @Query("documentId") DocumentId: Int,
        @Query("transferId") TransferId: Int,
        @Query("delegationId") delegationId: Int


    ): Observable<Boolean>


    @FormUrlEncoded
    @POST("Transfer/Complete")
    fun completeTransfer(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("ids[]") ids: Array<Int?>,
        @Field("delegationId") delegationId: Int

    ): Observable<ArrayList<CompleteTransferResponseItem>>


    @GET("Document/GetTrackingData")
    fun getVisualTracking(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("id") DocumentId: Int,
        @Query("delegationId") delegationId: Int

    ): Observable<ArrayList<VisualTrackingResponseItem>>


    @POST("Transfer/Transfer")
    fun transferTransfer(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Body model: List<TransferRequestModel>,
        @Query("delegationId") delegationId: Int

    ): Observable<ArrayList<TransferTransferResponseItem>>

    @POST("Transfer/Reply")
    @FormUrlEncoded
    fun replyToUser(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("id") id: Int,
        @Field("transferId") transferId: Int,
        @Field("purposeId") purposeId: Int,
        @Field("dueDate") dueDate: String,
        @Field("instruction") instruction: String,
        @Field("structureId") structureId: Int,
        @Field("transferToType") transferToType: Int,
        @Field("structureReceivers[]") structureReceivers: Array<Int>,
        @Field("delegationId") delegationId: Int

    ): Call<ResponseBody>


    @POST("Transfer/Reply")
    @FormUrlEncoded
    fun replyToStructure(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("id") id: Int,
        @Field("transferId") transferId: Int,
        @Field("purposeId") purposeId: Int,
        @Field("dueDate") dueDate: String,
        @Field("instruction") instruction: String,
        @Field("structureId") structureId: Int,
        @Field("transferToType") transferToType: Int,
        @Field("structureReceivers[]") structureReceivers: Array<Int>,
        @Field("delegationId") delegationId: Int


    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("Search/List")
    fun advancedSearch(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("start") start: Int,
        @Field("length") length: Int,
        @Field("Model") FormData: JSONObject

    ): Observable<AdvancedSearchResponse>

    @GET("Attachment/List")
    fun attachmentsData(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Query("documentId") documentId: Int,
        @Query("delegationId") delegationId: Int

    ): Observable<ArrayList<AttachmentModel>>


//    @POST("Attachment/Upload")
//    @FormUrlEncoded
//    fun uploadAttachment(
//        @Header("Accept-Language") lang: String,
//        @Header("Authorization") token: String,
//        @Field("documentId") documentId: Int,
//        @Field("transferId") transferId: Int,
//        @Field("parentId") parentId: Int,
//        @Field("categoryId") categoryId: Int,
//        @Field("Name") Name: String,
//        @Field("Extension") Extension: String,
//        @Field("Data") Data: String,
//    ): Observable<UploadAttachmentResponse>

    @POST("Attachment/Upload")
    fun uploadAttachment(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Body model: MultipartBody

        ): Observable<UploadAttachmentResponse>

    @POST("Attachment/UploadOriginalMail")

    fun uploadOriginalAttachment(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Body model: MultipartBody

        ): Observable<UploadAttachmentResponse>


    @POST("Attachment/Replace")
    fun replaceAttachment(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Body model: MultipartBody

        ): Observable<UploadAttachmentResponse>

    @POST("LinkedDocument/Index")
    @FormUrlEncoded
    fun addLinkedDocument(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Field("documentId") DocumentId: Int,
        @Field("transferId") TransferId: Int,
        @Field("linkDocumentIds[]") documentIds: Array<Int>,
        @Field("delegationId") delegationId: Int


        ): Observable<SaveNotesResponse>


    @GET()
    fun getViewerDocumentDetails(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("ctsDocumentId") ctsDocumentId: String?,
        @Query("ctsTransferId") ctsTransferId: String?,
        @Query("isDraft") isDraft: Boolean
    ): Observable<ViewerDocumentDetailsModel>


    @GET()
    fun getViewerDocumentVersions(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("ctsDocumentId") ctsDocumentId: String?,
        @Query("ctsTransferId") ctsTransferId: String?,
        @Query("isDraft") isDraft: Boolean
    ): Observable<List<ViewerDocumentVersionModel>>


    @GET()
    fun getViewerPDF(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Url url: String,
    ): Call<ResponseBody>


}