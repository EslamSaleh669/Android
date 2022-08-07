package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import androidx.lifecycle.ViewModel
import com.squareup.okhttp.Response
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentDetailsModel
import intalio.cts.mobile.android.data.model.viewer.ViewerDocumentVersionModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    fun checkForTransfersLoading(lastPosition: Int,documentId:Int) {
        var currentItemsCount = 0
        for (item in Transfers.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreTransfers(documentId)
        }
    }


    fun loadMoreTransfers(documentId:Int) {
        disposable = adminRepo.transfersHistoryData(start,documentId).subscribe({
            start = start + it.data!!.size
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
    fun getTransferDetails(transferId:Int): Observable<TransferDetailsResponse> = adminRepo.getTransferDetails(transferId)

    fun getDocument(documentId: Int): Observable<MetaDataResponse> = adminRepo.getDocument(documentId)



    fun getMyTransfer(transferId:Int): Observable<MyTransferResponse> = adminRepo.getMyTransfer(transferId)


    fun completeTransfer(ids: Array<Int?>): Observable<ArrayList<CompleteTransferResponseItem>> = adminRepo.completeTransfer(ids)

    fun transferDismissCopy(ids:Array<Int?>) : Observable<ArrayList<DismissCopyResponseItem>> = adminRepo.transferDismissCopy(ids)


    fun getVisualTracking(documentId:Int) : Observable<ArrayList<VisualTrackingResponseItem>> =
        adminRepo.getVisualTracking(documentId)

    fun readNodeID():Int = userRepo.readNodeID()!!


    fun savePath(path: String) = userRepo.savePath(path)


    fun viewTransfer(transferId: Int) : Call<ResponseBody> = adminRepo.viewTransfer(transferId)

    fun getOriginalDocument(documentId:Int):
            Observable<ArrayList<AttachmentModel>> =
        adminRepo.attachmentsData(documentId)


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


    fun unlockTransfer(transferId: Int) : Call<ResponseBody> = adminRepo.unlockTransfer(transferId)

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }






}