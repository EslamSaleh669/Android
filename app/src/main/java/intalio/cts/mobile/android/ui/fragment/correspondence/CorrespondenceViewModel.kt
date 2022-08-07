package intalio.cts.mobile.android.ui.fragment.correspondence

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import okhttp3.ResponseBody
import retrofit2.Call

class CorrespondenceViewModel(private val userRepo: UserRepo, private val adminRepo: AdminRepo) :
    ViewModel() {
    var disposable: Disposable? = null

    var inboxStart: Int = 0
    var inboxLimit = 0
    var inboxMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()

    var sentStart: Int = 0
    var sentLimit = 0
    var sentMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()

    var completedStart: Int = 0
    var completedLimit = 0
    var completedMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()

    var requestedStart: Int = 0
    var requestedLimit = 0
    var requestedMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()



    fun readLanguage() : String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()


    fun checkForInboxLoading(lastPosition: Int) {
        var currentItemsCount = 0
        for (item in inboxMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreInboxes()
        }
    }

    fun loadMoreInboxes() {
        disposable = adminRepo.getInboxes(inboxStart).subscribe({
            inboxStart = inboxStart + it.data!!.size
            inboxLimit += it.data.size
            inboxMessages.onNext(it.data)
        }, {
            inboxMessages.onError(it)
        })
    }

    /////
    fun checkForSentLoading(lastPosition: Int) {
        var currentItemsCount = 0
        for (item in sentMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreSent()
        }
    }

    fun loadMoreSent() {
        disposable = adminRepo.getSent(sentStart).subscribe({
            sentStart += it.data!!.size
            sentLimit += it.data.size
            sentMessages.onNext(it.data)
        }, {
            sentMessages.onError(it)
        })
    }
/////

    fun checkForCompletedLoading(lastPosition: Int) {
        var currentItemsCount = 0
        for (item in completedMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreCompleted()
        }
    }

    fun loadMoreCompleted() {
        disposable = adminRepo.getCompleted(completedStart).subscribe({
            completedStart = completedStart + it.data!!.size
            completedLimit += it.data.size
            completedMessages.onNext(it.data)
        }, {
            completedMessages.onError(it)
        })
    }


    fun checkForRequestedLoading(lastPosition: Int) {
        var currentItemsCount = 0
        for (item in requestedMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreRequested()
        }
    }

    fun loadMoreRequested() {
        disposable = adminRepo.getRequested(requestedStart).subscribe({
            requestedStart = requestedStart + it.data!!.size
            requestedLimit += it.data.size
            requestedMessages.onNext(it.data)
        }, {
            requestedMessages.onError(it)
        })
    }


    fun saveNodeId(nodeId: Int) = userRepo.saveNodeID(nodeId)
    fun completeTransfer(ids: Array<Int?>): Observable<ArrayList<CompleteTransferResponseItem>> =
        adminRepo.completeTransfer(ids)
    fun lockTransfer(transferId: Int) : Call<ResponseBody> = adminRepo.lockTransfer(transferId)
    fun recallTransfer(transferId: Int) : Call<ResponseBody> = adminRepo.recallTransfer(transferId)

    fun transferDismissCopy(ids:Array<Int?>) : Observable<ArrayList<DismissCopyResponseItem>> = adminRepo.transferDismissCopy(ids)
    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!

    fun readFullCategories (): ArrayList<FullCategoriesResponseItem> = userRepo.readFullCategories()!!
    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }


}