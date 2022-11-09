package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentDetailsModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentVersionModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call

class CorrespondenceDetailsViewModel(private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    var disposable: Disposable? = null

    var start: Int = 0
    var limit = 0
    var Transfers: ReplaySubject<ArrayList<TransferHistoryDataItem>> = ReplaySubject.create()




    fun readTokenData(): TokenResponse? = userRepo.readTokenData()

    fun checkForTransfersLoading(lastPosition: Int,documentId:Int,delegationId: Int) {
        var currentItemsCount = 0
        for (item in Transfers.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreTransfers(documentId,delegationId)
        }
    }


    fun loadMoreTransfers(documentId:Int,delegationId: Int) {
        disposable = adminRepo.transfersHistoryData(start,documentId,delegationId).subscribe({
            start += it.data!!.size
            limit += it.data.size
            Transfers.onNext(it.data)
        }, {
            Transfers.onError(it)
        })
    }


    fun currentLanguage(): String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()

    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!


    fun getDocumentBasicInfo(transferId:Int): Observable<DocumentBasicInfoResponse> = adminRepo.getDocumentBasicInfo(transferId)
    fun getTransferDetails(transferId: Int, delegationId: Int): Observable<TransferDetailsResponse> = adminRepo.getTransferDetails(transferId,delegationId)

    fun getDocument(documentId: Int): Observable<MetaDataResponse> = adminRepo.getDocument(documentId)



    fun getMyTransfer(transferId:Int,delegationId:Int): Observable<MyTransferResponse> = adminRepo.getMyTransfer(transferId,delegationId)


    fun completeTransfer(ids: Array<Int?>,delegationId: Int): Observable<ArrayList<CompleteTransferResponseItem>> = adminRepo.completeTransfer(ids,delegationId)

    fun transferDismissCopy(ids:Array<Int?>,delegationId:Int ) : Observable<ArrayList<DismissCopyResponseItem>> = adminRepo.transferDismissCopy(ids,delegationId)


    fun getVisualTracking(documentId:Int,delegationId: Int) : Observable<ArrayList<VisualTrackingResponseItem>> =
        adminRepo.getVisualTracking(documentId,delegationId)

    fun readCurrentNode():String = userRepo.readCurrentNode()!!


    fun savePath(path: String) = userRepo.savePath(path)


    fun viewTransfer(transferId: Int, delegationId: Int) : Call<ResponseBody> = adminRepo.viewTransfer(transferId,delegationId)

    fun getOriginalDocument(documentId:Int, delegationId:Int):
            Observable<ArrayList<AttachmentModel>> =
        adminRepo.attachmentsData(documentId,delegationId)

    fun getSearchDocumentInfo(documentId:Int,delegationId:Int): Observable<MetaDataResponse> = adminRepo.getSearchDocument(documentId,delegationId)


    fun replaceAttachment(body: MultipartBody):Observable<UploadAttachmentResponse>
            = adminRepo.replaceAttachment(body)


    fun readAllStructureData (): AllStructuresResponse = userRepo.readAllStructureData()!!
    fun readLanguage (): String = userRepo.currentLang()


    fun getViewerDocumentDetails(ctsDocumentId : String
                                 ,ctsTransferId : String ,isDraft:Boolean ,
                                 documentId: String ,
                                 documentVersion:String)
    :Observable<ViewerDocumentDetailsModel> =
        adminRepo.getViewerDocumentDetails(ctsDocumentId,ctsTransferId,isDraft,documentId,documentVersion)

    fun getViewerDocumentVersions(ctsDocumentId : String
                                 ,ctsTransferId : String ,isDraft:Boolean ,
                                 documentId: String)
    :Observable<List<ViewerDocumentVersionModel>> =
        adminRepo.getViewerDocumentVersions(ctsDocumentId,ctsTransferId,isDraft,documentId)


    fun getViewerPDF() : Call<ResponseBody> = adminRepo.getViewerPDF()

    fun unlockTransfer(transferId: Int, delegationId: Int) : Call<ResponseBody> = adminRepo.unlockTransfer(transferId,delegationId)
    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()
    fun readSettings (): ArrayList<ParamSettingsResponseItem> = userRepo.readSettings()!!

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }






}