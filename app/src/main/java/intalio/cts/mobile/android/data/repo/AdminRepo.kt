package intalio.cts.mobile.android.data.repo

import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentDetailsModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentVersionModel
import intalio.cts.mobile.android.data.network.ApiClient
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.util.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import javax.inject.Inject

class AdminRepo @Inject constructor(

    private val apiClient: ApiClient,
    private val userRepo: UserRepo

) {


    fun getUserFullData(): Observable<UserFullDataResponseItem> {

        return apiClient.getUserFullData(
            "${Constants.BASE_URL2}/api/GetUser",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            userRepo.readUserBasicInfo()!!.id!!.toInt(),
            userRepo.currentLang()
        ).subscribeOn(Schedulers.io())
    }


    fun getUser(userId: Int): Observable<UserFullDataResponseItem> {

        return apiClient.getUserFullData(
            "${Constants.BASE_URL2}/api/GetUser",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            userId,
            userRepo.currentLang()
        ).subscribeOn(Schedulers.io())
    }


    fun geStructure(structureId: Int): Observable<AllStructuresItem> {

        return apiClient.geStructure(
            "${Constants.BASE_URL2}/api/GetStructure",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            structureId,
            userRepo.currentLang()
        ).subscribeOn(Schedulers.io())
    }


    fun geStructureSendingRules(fromStructureId: Int): Call<ResponseBody> {

        return apiClient.geStructureSendingRules(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            fromStructureId
        )
    }

    fun getFullStructures(language: Int): Observable<FullStructuresResponseItem> {

        return apiClient.getFullStructures(
            "${Constants.BASE_URL2}/api/ListStructures",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            0,
            400,
            language
        ).subscribeOn(Schedulers.io())
    }




    fun getSettings(): Observable<ArrayList<ParamSettingsResponseItem>> {
        return apiClient.getSettings(
            "Bearer ${userRepo.readTokenData()!!.accessToken}"
        ).subscribeOn(Schedulers.io())
    }

    fun getNodesData(): Observable<ArrayList<NodeResponseItem>> {
        return apiClient.getNodesData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}"
        ).subscribeOn(Schedulers.io())
    }


    fun getInboxCount(nodeId: Int, delegationId: Int): Observable<InboxCountResponse> {
        return apiClient.getInboxCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getSentCount(nodeId: Int, delegationId: Int): Observable<InboxCountResponse> {
        return apiClient.getSentCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getCompletedCount(nodeId: Int, delegationId: Int): Observable<InboxCountResponse> {
        return apiClient.getCompletedCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getClosedCount(nodeId: Int, delegationId: Int): Observable<InboxCountResponse> {
        return apiClient.getClosedCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getRequestedCount(nodeId: Int, delegationId: Int): Observable<InboxCountResponse> {
        return apiClient.getRequestedCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getCategoriesData(): Observable<List<CategoryResponseItem>> {
        return apiClient.getCategoriesData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }


    fun getStructureData(ids: Array<Int>): Observable<ArrayList<UsersStructureItem>> {
        return apiClient.getUserStructureData(
            "${Constants.BASE_URL2}/api/SearchUsersByStructureIds",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids
        ).subscribeOn(Schedulers.io())
    }

    fun listUserExistenceAttributeInStructure(ids: Array<Int>): Call<ResponseBody> {
        return apiClient.listUserExistenceAttributeInStructure(
            "${Constants.BASE_URL2}/api/ListUserExistenceAttributeInStructure",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids, "StructureReceiver",
            "true", "Privacy"
        )
    }


    fun getAllStructures(text: String,ids: ArrayList<Int>
    ): Observable<AllStructuresResponse> {
        return apiClient.getAllStructures(
            "${Constants.BASE_URL2}/api/GetUsersAndStructuresWithSearchAttributes",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}", text,ids, userRepo.currentLang()
        ).subscribeOn(Schedulers.io())
    }


    fun getAvailableStructures(
        text: String,
        ids: ArrayList<Int>
    ): Observable<AllStructuresResponse> {
        return apiClient.getAvailableStructures(
            "${Constants.BASE_URL2}/api/GetUsersAndStructuresWithSearchAttributes",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}", text,
            ids,
            userRepo.currentLang()

        ).subscribeOn(Schedulers.io())
    }

    fun getStatuses(): Observable<ArrayList<StatusesResponseItem>> {
        return apiClient.getStatuses(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }

    fun getPurposes(): Observable<ArrayList<PurposesResponseItem>> {
        return apiClient.getPurposes(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }

    fun getPriorities(): Observable<ArrayList<PrioritiesResponseItem>> {
        return apiClient.getPriorities(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }

    fun getPrivacies(): Observable<ArrayList<PrivaciesResponseItem>> {
        return apiClient.getPrivacies(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }


    fun getImportances(): Observable<ArrayList<ImportancesResponseItem>> {
        return apiClient.getImportances(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }


    fun getFullCategories(): Observable<ArrayList<FullCategoriesResponseItem>> {
        return apiClient.getFullCategories(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }


    fun getTypes(): Observable<ArrayList<TypesResponseItem>> {
        return apiClient.getTypes(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }

    fun getInboxes(start: Int, delegationId: Int, nodeId: Int): Observable<CorrespondenceResponse> {
        return apiClient.inboxData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getSent(start: Int, delegationId: Int, nodeID: Int): Observable<CorrespondenceResponse> {
        return apiClient.sentData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            nodeID,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getCompleted(start: Int, delegationId: Int, nodeID: Int): Observable<CorrespondenceResponse> {
        return apiClient.completedData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            nodeID,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getClosed(start: Int, delegationId: Int, nodeID: Int): Observable<CorrespondenceResponse> {
        return apiClient.closedData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            nodeID,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getRequested(start: Int, delegationId: Int, nodeID: Int): Observable<CorrespondenceResponse> {
        return apiClient.requestedData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            nodeID,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getDocumentBasicInfo(transferId: Int): Observable<DocumentBasicInfoResponse> {
        return apiClient.getDocumentBasicInfo(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,

            ).subscribeOn(Schedulers.io())
    }


    fun getMetaDataInfo(transferId: Int, delegationId: Int): Observable<MetaDataResponse> {
        return apiClient.getMetaDataInfo(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun getDocument(documentId: Int): Observable<MetaDataResponse> {
        return apiClient.getDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,

            ).subscribeOn(Schedulers.io())
    }


    fun getSearchDocument(documentId: Int, delegationId: Int): Observable<MetaDataResponse> {
        return apiClient.getSearchDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getDelegations(start: Int): Observable<DelegationsResponse> {
        return apiClient.delegationsData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }


    fun saveDelegations(
        toUserId: Int,
        fromDate: String,
        toDate: String,
        categories: ArrayList<Int>
    ): Observable<SaveDelegationResponse> {


        return apiClient.saveDelegation(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            toUserId,
            fromDate,
            toDate,
            categories
        ).subscribeOn(Schedulers.io())

    }

    fun deleteDelegation(ids: Array<Int>): Call<ResponseBody> {

//        for (item in ids){
//            Log.d("delegationdata",item.toString())
//
//        }
        return apiClient.deleteDelegation(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            " ${Constants.BASE_URL}/Delegation/Delete",
            ids
        )
    }

    fun saveEditedDelegations(
        toUserId: Int,
        fromDate: String,
        toDate: String,
        categories: ArrayList<Int>,
        delegationId: Int
    ):
            Observable<SaveDelegationResponse> {

        return apiClient.saveEditedDelegation(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            toUserId,
            fromDate,
            toDate,
            categories,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun delegationRequests(): Call<ArrayList<DelegationRequestsResponseItem>> =
        apiClient.delegationRequests(
            "Bearer ${userRepo.readTokenData()!!.accessToken}"
        )

    fun getDelegationById(delegationId: Int): Observable<DelegatorResponse> =
        apiClient.getDelegationById(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            delegationId
        )


    fun notesData(
        start: Int,
        documentId: Int,
        transferId: Int,
        delegationId: Int
    ): Observable<NotesResponse> {
        if (transferId == 0  ) {
            return apiClient.notesData(
                "Bearer ${userRepo.readTokenData()!!.accessToken}",
                documentId = documentId,
                start = start,
                length = 10,
                delegationId = delegationId
            ).subscribeOn(Schedulers.io())
        }else {
            return apiClient.notesData(
                "Bearer ${userRepo.readTokenData()!!.accessToken}",
                documentId,
                transferId,
                start,
                10,
                delegationId
            ).subscribeOn(Schedulers.io())
        }

    }


    fun saveNotes(
        documentId: Int,
        transferId: Int,
        note: String,
        IsPrivate: Boolean,
        delegationId: Int
    ): Observable<SaveNotesResponse> {
        return apiClient.saveNote(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            note,
            IsPrivate,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun saveEditedNotes(
        documentId: Int,
        transferId: Int,
        note: String,
        IsPrivate: Boolean,
        nodeId: Int,
        delegationId: Int
    ): Observable<SaveNotesResponse> {
        return apiClient.saveEditedNote(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            note,
            IsPrivate,
            nodeId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun deleteNote(
        noteID: Int,
        documentId: Int,
        transferId: Int,
        delegationId: Int
    ): Observable<Boolean> {
        return apiClient.deleteNote(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            noteID,
            documentId,
            transferId,
            delegationId

        ).subscribeOn(Schedulers.io())
    }


    fun transfersHistoryData(
        start: Int,
        documentId: Int,
        delegationId: Int
    ): Observable<TransferHistoryResponse> {
        return apiClient.transfersHistoryData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            start,
            10,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getMyTransfer(transferId: Int, delegationId: Int): Observable<MyTransferResponse> {
        return apiClient.getMyTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getTransferDetails(
        transferId: Int,
        delegationId: Int
    ): Observable<TransferDetailsResponse> {
        return apiClient.getTransferDetails(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun nonArchData(
        start: Int,
        documentId: Int,
        delegationId: Int
    ): Observable<NonArchAttachmentsResponse> {
        return apiClient.nonArchData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            start,
            10,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun saveNonArch(
        documentId: Int,
        transferId: Int,
        typeId: Int,
        description: String,
        quantity: Int,
        delegationId: Int
    ): Observable<SaveNotesResponse> {
        return apiClient.saveNonArch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            typeId,
            description,
            quantity,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun saveEditedNonArch(
        documentId: Int,
        transferId: Int,
        typeId: Int,
        description: String,
        quantity: Int,
        nonarchID: Int,
        delegationId: Int
    ): Observable<SaveNotesResponse> {
        return apiClient.saveEditedNonArch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            typeId,
            description,
            quantity,
            nonarchID,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun deleteNonArch(
        nonarchID: Int,
        documentId: Int,
        transferId: Int,
        delegationId: Int
    ): Observable<Boolean> {
        return apiClient.deleteNonArch(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nonarchID,
            documentId,
            transferId,
            delegationId

        ).subscribeOn(Schedulers.io())
    }

    fun addLinkedDocument(
        documentIds: Array<Int>,
        documentId: Int,
        transferId: Int,
        delegationId: Int
    ): Observable<SaveNotesResponse> {
        return apiClient.addLinkedDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            documentIds,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun deleteLinkedC(
        linkId: Int,
        documentId: Int,
        transferId: Int,
        delegationId: Int
    ): Observable<Boolean> {
        return apiClient.deleteLinkedC(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            linkId,
            documentId,
            transferId,
            delegationId

        ).subscribeOn(Schedulers.io())
    }


    fun getLinkedC(documentId: Int, delegationId: Int): Observable<LinkedCorrespondenceResponse> {
        return apiClient.getLinkedC(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun completeTransfer(
        ids: Array<Int?>,
        delegationId: Int
    ): Observable<ArrayList<CompleteTransferResponseItem>> {
        return apiClient.completeTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getVisualTracking(
        documentId: Int,
        delegationId: Int
    ): Observable<ArrayList<VisualTrackingResponseItem>> {
        return apiClient.getVisualTracking(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun transferTransfer(
        list: List<TransferRequestModel>,
        delegationId: Int
    ): Observable<ArrayList<TransferTransferResponseItem>> {
        return apiClient.transferTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            list,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun replyToUser(
        documentId: Int, transferId: Int, purposeId: Int, dueDate: String,
        instruction: String, structureId: Int, structureReceivers: Array<Int>, delegationId: Int
    ): Call<ResponseBody> {
        return apiClient.replyToUser(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            2,
            structureReceivers,
            delegationId

        )
    }

    fun replyToStructure(
        documentId: Int, transferId: Int, purposeId: Int, dueDate: String,
        instruction: String, structureId: Int, structureReceivers: Array<Int>, delegationId: Int
    ): Call<ResponseBody> {
        return apiClient.replyToStructure(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            3,
            structureReceivers,
            delegationId
        )
    }


    fun advancedSearch(start: Int, model: JSONObject): Observable<AdvancedSearchResponse> {

        return apiClient.advancedSearch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            model

        ).subscribeOn(Schedulers.io())
    }


    fun attachmentsData(
        documentId: Int,
        delegationId: Int
    ): Observable<ArrayList<AttachmentModel>> {
        return apiClient.attachmentsData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            delegationId
        ).subscribeOn(Schedulers.io())
    }

    fun uploadAttachment(
        body: MultipartBody
    ): Observable<UploadAttachmentResponse> {
        return apiClient.uploadAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }


    fun uploadOriginalAttachment(
        body: MultipartBody
    ): Observable<UploadAttachmentResponse> {
        return apiClient.uploadOriginalAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }

    fun replaceAttachment(
        body: MultipartBody
    ): Observable<UploadAttachmentResponse> {
        return apiClient.replaceAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }

    fun viewTransfer(transferId: Int, delegationId: Int): Call<ResponseBody> {
        return apiClient.viewAction(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId

        )
    }

    fun lockTransfer(transferId: Int, delegationId: Int): Call<ResponseBody> {
        return if (delegationId == 0) {
            apiClient.lockTransfer(
                "Bearer ${userRepo.readTokenData()!!.accessToken}",
                transferId = transferId
            )
        } else {
            apiClient.lockTransfer(
                "Bearer ${userRepo.readTokenData()!!.accessToken}",
                transferId = transferId,
                delegationId = delegationId
            )
        }

    }

    fun unlockTransfer(transferId: Int, delegationId: Int): Call<ResponseBody> {
        return apiClient.unlockTransfer(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId

        )
    }

    fun recallTransfer(transferId: Int, delegationId: Int): Call<ResponseBody> {
        return apiClient.recallTransfer(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            delegationId

        )
    }

    fun transferDismissCopy(
        ids: Array<Int?>,
        delegationId: Int
    ): Observable<ArrayList<DismissCopyResponseItem>> {
        return apiClient.transferDismissCopy(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids,
            delegationId
        ).subscribeOn(Schedulers.io())
    }


    fun getViewerDocumentDetails(
        ctsDocumentId: String,
        ctsTransferId: String,
        isDraft: Boolean,
        documentId: String,
        documentVersion: String
    ): Observable<ViewerDocumentDetailsModel> {

        return apiClient.getViewerDocumentDetails(
            "${Constants.VIEWER_URL}/api/document/${documentId}/version/${documentVersion}/details",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ctsDocumentId,
            ctsTransferId,
            isDraft

        ).subscribeOn(Schedulers.io())
    }

    //  @GET("api/document/{documentId}/versions")

    fun getViewerDocumentVersions(
        ctsDocumentId: String,
        ctsTransferId: String,
        isDraft: Boolean,
        documentId: String,
    ): Observable<List<ViewerDocumentVersionModel>> {

        return apiClient.getViewerDocumentVersions(
            "${Constants.VIEWER_URL}/api/document/${documentId}/versions",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ctsDocumentId,
            ctsTransferId,
            isDraft

        ).subscribeOn(Schedulers.io())
    }


    fun getViewerPDF(): Call<ResponseBody> {
        return apiClient.getViewerPDF(
            "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjAzMkQzMkYyMUFGRjc5OTZDOTc2NzBBNDcyNTdFRjU3IiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2NjQxODU0MjAsImV4cCI6MTY2NDIyMTQyMCwiaXNzIjoiaHR0cDovLzE5Mi4xNjguMS4xMTo5OTQ5IiwiYXVkIjpbIklkZW50aXR5U2VydmVyQXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiY2xpZW50X2lkIjoiNWM3ZDM2OGMtMjA1My00MGE1LWIwMDUtY2FhNjY0N2EwMDcwIiwic3ViIjoiMjAzMjEiLCJhdXRoX3RpbWUiOjE2NjQxODU0MTksImlkcCI6ImxvY2FsIiwiRGlzcGxheU5hbWUiOiJlc2xhbSBzYWxlaCIsIkxvZ2luUHJvdmlkZXJUeXBlIjoxLCJFbWFpbCI6ImVAcy5jb20iLCJJZCI6MjAzMjEsIkZpcnN0TmFtZSI6ImVzbGFtIiwiTGFzdE5hbWUiOiJzYWxlaCIsIk1pZGRsZU5hbWUiOiJzYWxlaCIsIlN0cnVjdHVyZUlkIjoiMSIsIk1hbmFnZXJJZCI6IiIsIlN0cnVjdHVyZUlkcyI6IjEiLCJHcm91cElkcyI6IiIsIlN0cnVjdHVyZVNlbmRlciI6InRydWUiLCJTdHJ1Y3R1cmVSZWNlaXZlciI6InRydWUiLCJQcml2YWN5IjoiMiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IkNvbnRyaWJ1dGUiLCJBcHBsaWNhdGlvblJvbGVJZCI6IjMiLCJDbGllbnRzIjpbIntcIlJvbGVJZFwiOjMsXCJSb2xlXCI6XCJDb250cmlidXRlXCIsXCJDbGllbnRJZFwiOlwiNWM3ZDM2OGMtMjA1My00MGE1LWIwMDUtY2FhNjY0N2EwMDcwXCJ9Iiwie1wiUm9sZUlkXCI6MSxcIlJvbGVcIjpcIkFkbWluaXN0cmF0b3JcIixcIkNsaWVudElkXCI6XCJmY2Q2ZDZjMC0xOWMwLTQ4YjctYjk5Mi0xNWFiMGI0MmM3MzNcIn0iLCJ7XCJSb2xlSWRcIjoxLFwiUm9sZVwiOlwiQWRtaW5pc3RyYXRvclwiLFwiQ2xpZW50SWRcIjpcIjI3ZmZkOTAxLWFhOGEtNGQyNi1hOTgzLTg2MDJjMGI1ZDBlZlwifSJdLCJqdGkiOiJCMjE0M0FBNzZEREVCOTNEQjc3OEREMjRFOUNFRDlFQiIsInNpZCI6IjEzM0Q3NEU3NjZDOEI5QTgwN0Q0MkEwOEVEMDdFOTBEIiwiaWF0IjoxNjY0MTg1NDIwLCJzY29wZSI6WyJvcGVuaWQiLCJJZGVudGl0eVNlcnZlckFwaSIsIm9mZmxpbmVfYWNjZXNzIl0sImFtciI6WyJwd2QiXX0.IhqjY8VF7iv73xjbs5ytKVau5cpWg4lvFvLqNv25mo1B3lDBFfZqKV7t1UpI3vhAlD9WCt3y3QY__v8rDow_byr9tXJmzTPOs0s73hgxb3ug9IIlMicc9MWtFmN4-HtZZuPHwwy2mruNvV484EUuT9qxJ7mwLW3dY8qDGI-jT9cbaePMv2J8nMBSG5vT4D2r9vEKNNP34RKmEZRySnWpB6YIfKG5dvfOTM8-LqK3dojIrJyddG1iwZU5fzC5axLsGDL3qahN26r4CJW7HGS7eicyI5iKEjP30nI8x0FEleuecsnj0pN2Am15o37cIuba_BzwSC0TNpKqrGJidgyNmw",
            "application/pdf",
            "http://192.168.1.11:8080/viewer/api/document/34079/version/1/view"
        )
    }


}