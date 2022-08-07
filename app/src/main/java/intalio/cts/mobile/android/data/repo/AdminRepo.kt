package intalio.cts.mobile.android.data.repo

import android.util.Log
import com.squareup.okhttp.Response
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentDetailsModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentVersionModel
import intalio.cts.mobile.android.data.network.ApiClient
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.util.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import java.io.File
import javax.inject.Inject

class AdminRepo @Inject constructor(

    private val apiClient: ApiClient,
    private val userRepo: UserRepo

) {


    fun getUserFullData():Observable<UserFullDataResponseItem>{

        return apiClient.getUserFullData(
            "${Constants.BASE_URL2}/api/GetUser",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            userRepo.readUserBasicInfo()!!.id!!.toInt(),
            userRepo.currentLang()
            ).subscribeOn(Schedulers.io())
    }


    fun getUser(userId:Int):Observable<UserFullDataResponseItem>{

        return apiClient.getUserFullData(
            "${Constants.BASE_URL2}/api/GetUser",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            userId   ,
            userRepo.currentLang()
            ).subscribeOn(Schedulers.io())
    }


    fun geStructure(structureId: Int):Observable<AllStructuresItem>{

        return apiClient.geStructure(
            "${Constants.BASE_URL2}/api/GetStructure",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            structureId   ,
            userRepo.currentLang()
            ).subscribeOn(Schedulers.io())
    }



    fun getFullStructures(language: Int):Observable<FullStructuresResponse>{

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

    fun getNodesData(): Observable<List<NodeResponseItem>> {
        return apiClient.getNodesData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}"
        ).subscribeOn(Schedulers.io())
    }


    fun getInboxCount(nodeId:Int): Observable<InboxCountResponse> {
        return apiClient.getInboxCount(
             userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
             nodeId
        ).subscribeOn(Schedulers.io())
    }

    fun getSentCount(nodeId:Int): Observable<InboxCountResponse> {
        return apiClient.getSentCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId
        ).subscribeOn(Schedulers.io())
    }


    fun getCompletedCount(nodeId:Int): Observable<InboxCountResponse> {
        return apiClient.getCompletedCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId
        ).subscribeOn(Schedulers.io())
    }

    fun getRequestedCount(nodeId:Int): Observable<InboxCountResponse> {
        return apiClient.getRequestedCount(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nodeId
        ).subscribeOn(Schedulers.io())
    }



    fun getCategoriesData(): Observable<List<CategoryResponseItem>> {
        return apiClient.getCategoriesData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
        ).subscribeOn(Schedulers.io())
    }




    fun getStructureData(ids:Array<Int>): Observable<ArrayList<UsersStructureItem>> {
        return apiClient.getUserStructureData(
            "${Constants.BASE_URL2}/api/SearchUsersByStructureIds",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids
        ).subscribeOn(Schedulers.io())
    }

    fun listUserExistenceAttributeInStructure(ids:Array<Int>): Call<ResponseBody> {
        return apiClient.listUserExistenceAttributeInStructure(
            "${Constants.BASE_URL2}/api/ListUserExistenceAttributeInStructure",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids,"StructureReceiver",
            "true","Privacy"
        )
    }


    fun getAllStructures(text:String): Observable<AllStructuresResponse> {
        return apiClient.getAllStructures(
            "${Constants.BASE_URL2}/api/GetUsersAndStructuresWithSearchAttributes",
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",text).subscribeOn(Schedulers.io())
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

    fun getInboxes(start:Int): Observable<CorrespondenceResponse> {
        return apiClient.inboxData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }

    fun getSent(start:Int): Observable<CorrespondenceResponse> {
        return apiClient.sentData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }

    fun getCompleted(start:Int): Observable<CorrespondenceResponse> {
        return apiClient.completedData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }

    fun getRequested(start:Int): Observable<CorrespondenceResponse> {
        return apiClient.requestedData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }





    fun getDocumentBasicInfo(transferId:Int): Observable<DocumentBasicInfoResponse> {
        return apiClient.getDocumentBasicInfo(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,

        ).subscribeOn(Schedulers.io())
    }


    fun getMetaDataInfo(transferId:Int): Observable<MetaDataResponse> {
        return apiClient.getMetaDataInfo(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,

        ).subscribeOn(Schedulers.io())
    }

    fun getDocument(documentId: Int): Observable<MetaDataResponse> {
        return apiClient.getDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,

        ).subscribeOn(Schedulers.io())
    }



    fun getSearchDocument(documentId: Int): Observable<MetaDataResponse> {
        return apiClient.getSearchDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId
        ).subscribeOn(Schedulers.io())
    }



    fun getDelegations(start:Int): Observable<DelegationsResponse> {
        return apiClient.delegationsData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10
        ).subscribeOn(Schedulers.io())
    }





    fun saveDelegations(toUserId:Int,fromDate:String,toDate:String,categories:ArrayList<Int>): Observable<SaveDelegationResponse> {


        return apiClient.saveDelegation(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            toUserId,
            fromDate,
            toDate,
            categories
        ).subscribeOn(Schedulers.io())

    }

    fun deleteDelegation(ids:ArrayList<Int>): Call<Void> {

        for (item in ids){
            Log.d("delegationdata",item.toString())

        }
        return apiClient.deleteDelegation(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids
        )
    }

    fun saveEditedDelegations(toUserId:Int,fromDate:String,toDate:String,categories:ArrayList<Int>,delegationId:Int): Observable<SaveDelegationResponse> {

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


    fun notesData(start:Int,documentId:Int): Observable<NotesResponse> {
        return apiClient.notesData(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            start,
            10
        ).subscribeOn(Schedulers.io())
    }




    fun saveNotes(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean): Observable<SaveNotesResponse> {
        return apiClient.saveNote(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            note,
            IsPrivate
        ).subscribeOn(Schedulers.io())
    }

    fun saveEditedNotes(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean,nodeId: Int): Observable<SaveNotesResponse> {
        return apiClient.saveEditedNote(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            note,
            IsPrivate,
            nodeId
        ).subscribeOn(Schedulers.io())
    }

    fun deleteNote(noteID: Int, documentId:Int, transferId:Int): Observable<Boolean> {
        return apiClient.deleteNote(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            noteID,
            documentId,
            transferId

        ).subscribeOn(Schedulers.io())
    }


    fun transfersHistoryData(start:Int,documentId:Int): Observable<TransferHistoryResponse> {
        return apiClient.transfersHistoryData(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            start,
            10
        ).subscribeOn(Schedulers.io())
    }


    fun getMyTransfer(transferId:Int): Observable<MyTransferResponse> {
        return apiClient.getMyTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,

            ).subscribeOn(Schedulers.io())
    }


    fun getTransferDetails(transferId:Int): Observable<TransferDetailsResponse> {
        return apiClient.getTransferDetails(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId,
            ).subscribeOn(Schedulers.io())
    }


    fun nonArchData(start:Int,documentId:Int): Observable<NonArchAttachmentsResponse> {
        return apiClient.nonArchData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            start,
            10
        ).subscribeOn(Schedulers.io())
    }

    fun saveNonArch(documentId:Int,transferId:Int,typeId:Int,description:String,quantity:Int): Observable<SaveNotesResponse> {
        return apiClient.saveNonArch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            typeId,
            description,
            quantity
        ).subscribeOn(Schedulers.io())
    }

    fun saveEditedNonArch(documentId:Int,transferId:Int,typeId:Int,description:String,quantity:Int,nonarchID: Int): Observable<SaveNotesResponse> {
        return apiClient.saveEditedNonArch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            typeId,
            description,
            quantity,
            nonarchID
        ).subscribeOn(Schedulers.io())
    }


    fun deleteNonArch(nonarchID: Int, documentId:Int, transferId:Int): Observable<Boolean> {
        return apiClient.deleteNonArch(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            nonarchID,
            documentId,
            transferId

        ).subscribeOn(Schedulers.io())
    }

    fun addLinkedDocument(documentIds: Array<Int>, documentId:Int, transferId:Int): Observable<SaveNotesResponse> {
        return apiClient.addLinkedDocument(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
            transferId,
            documentIds,

            ).subscribeOn(Schedulers.io())
    }

    fun deleteLinkedC(linkId: Int, documentId:Int, transferId:Int): Observable<Boolean> {
        return apiClient.deleteLinkedC(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            linkId,
            documentId,
            transferId

        ).subscribeOn(Schedulers.io())
    }


    fun getLinkedC(documentId:Int): Observable<LinkedCorrespondenceResponse> {
        return apiClient.getLinkedC(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId
        ).subscribeOn(Schedulers.io())
    }


    fun completeTransfer(ids:Array<Int?>): Observable<ArrayList<CompleteTransferResponseItem>> {
        return apiClient.completeTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids
        ).subscribeOn(Schedulers.io())
    }


    fun getVisualTracking(documentId:Int): Observable<ArrayList<VisualTrackingResponseItem>> {
        return apiClient.getVisualTracking(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId,
        ).subscribeOn(Schedulers.io())
    }

    fun transferTransfer(list :List<TransferRequestModel>):Observable<ArrayList<TransferTransferResponseItem>> {
        return apiClient.transferTransfer(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            list
        ).subscribeOn(Schedulers.io())
    }

    fun replyToUser(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>): Call<ResponseBody> {
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
            structureReceivers

        )
    }

    fun replyToStructure(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>): Call<ResponseBody> {
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
            structureReceivers
        )
    }



    fun advancedSearch(start:Int,model:JSONObject):Observable<AdvancedSearchResponse> {

        return apiClient.advancedSearch(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            start,
            10,
            model

        ).subscribeOn(Schedulers.io())
    }


    fun attachmentsData(documentId:Int): Observable<ArrayList<AttachmentModel>> {
        return apiClient.attachmentsData(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            documentId
        ).subscribeOn(Schedulers.io())
    }

    fun uploadAttachment(body: MultipartBody
 ): Observable<UploadAttachmentResponse> {
        return apiClient.uploadAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }


    fun uploadOriginalAttachment(body: MultipartBody
 ): Observable<UploadAttachmentResponse> {
        return apiClient.uploadOriginalAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }

    fun replaceAttachment(body: MultipartBody
    ): Observable<UploadAttachmentResponse> {
        return apiClient.replaceAttachment(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            body

        ).subscribeOn(Schedulers.io())
    }

    fun viewTransfer(transferId:Int): Call<ResponseBody> {
        return apiClient.viewAction(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId

            )
    }

    fun lockTransfer(transferId:Int): Call<ResponseBody> {
        return apiClient.lockTransfer(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId

            )
    }
    fun unlockTransfer(transferId:Int): Call<ResponseBody> {
        return apiClient.unlockTransfer(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId

            )
    }

    fun recallTransfer(transferId:Int): Call<ResponseBody> {
        return apiClient.recallTransfer(
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            transferId

        )
    }
    fun transferDismissCopy(ids:Array<Int?>): Observable<ArrayList<DismissCopyResponseItem>> {
        return apiClient.transferDismissCopy(
            userRepo.currentLang(),
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ids
        ).subscribeOn(Schedulers.io())
    }


    fun getViewerDocumentDetails
                (ctsDocumentId : String ,
                 ctsTransferId : String ,
                 isDraft:Boolean ,
                 documentId: String ,
                 documentVersion:String
    ):Observable<ViewerDocumentDetailsModel>{

        return apiClient.getViewerDocumentDetails(
            "${Constants.VIEWER_URL}/api/document/${documentId}/version/${documentVersion}/details",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ctsDocumentId,
            ctsTransferId,
            isDraft

        ).subscribeOn(Schedulers.io())
    }

  //  @GET("api/document/{documentId}/versions")

    fun getViewerDocumentVersions
                (ctsDocumentId : String ,
                 ctsTransferId : String ,
                 isDraft:Boolean ,
                 documentId: String ,
    ):Observable<List<ViewerDocumentVersionModel>>{

        return apiClient.getViewerDocumentVersions(
            "${Constants.VIEWER_URL}/api/document/${documentId}/versions",
            "Bearer ${userRepo.readTokenData()!!.accessToken}",
            ctsDocumentId,
            ctsTransferId,
            isDraft

        ).subscribeOn(Schedulers.io())
    }



}