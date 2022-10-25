package intalio.cts.mobile.android.ui.fragment.correspondence

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
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


    var closedStart: Int = 0
    var closedLimit = 0
    var closedMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()


    var requestedStart: Int = 0
    var requestedLimit = 0
    var requestedMessages: ReplaySubject<ArrayList<CorrespondenceDataItem>> = ReplaySubject.create()


    fun readLanguage(): String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()


    fun checkForInboxLoading(lastPosition: Int,delegationId : Int) {
        var currentItemsCount = 0
        for (item in inboxMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreInboxes(delegationId)
        }
    }

    fun loadMoreInboxes(delegationId : Int) {
        disposable = adminRepo.getInboxes(inboxStart,delegationId).subscribe({
            inboxStart = inboxStart + it.data!!.size
            inboxLimit += it.data.size
            inboxMessages.onNext(it.data)
        }, {
            inboxMessages.onError(it)
        })
    }

    /////
    fun checkForSentLoading(lastPosition: Int,delegationId : Int) {
        var currentItemsCount = 0
        for (item in sentMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreSent(delegationId)
        }
    }

    fun loadMoreSent(delegationId : Int) {
        disposable = adminRepo.getSent(sentStart,delegationId).subscribe({
            sentStart += it.data!!.size
            sentLimit += it.data.size
            sentMessages.onNext(it.data)
        }, {
            sentMessages.onError(it)
        })
    }
    /////


    fun checkForCompletedLoading(lastPosition: Int,delegationId : Int) {
        var currentItemsCount = 0
        for (item in completedMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreCompleted(delegationId)
        }
    }

    fun loadMoreCompleted(delegationId : Int) {
        disposable = adminRepo.getCompleted(completedStart,delegationId).subscribe({
            completedStart = completedStart + it.data!!.size
            completedLimit += it.data.size
            completedMessages.onNext(it.data)
        }, {
            completedMessages.onError(it)
        })
    }

    /////
    fun checkForClosedLoading(lastPosition: Int,delegationId : Int) {
        var currentItemsCount = 0
        for (item in closedMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreClosed(delegationId)
        }
    }

    fun loadMoreClosed(delegationId : Int) {
        disposable = adminRepo.getClosed(closedStart,delegationId).subscribe({
            closedStart += it.data!!.size
            closedLimit += it.data.size
            closedMessages.onNext(it.data)
        }, {
            closedMessages.onError(it)
        })
    }


    /////
    fun checkForRequestedLoading(lastPosition: Int,delegationId : Int) {
        var currentItemsCount = 0
        for (item in requestedMessages.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreRequested(delegationId)
        }
    }

    fun loadMoreRequested(delegationId : Int) {
        disposable = adminRepo.getRequested(requestedStart,delegationId).subscribe({
            requestedStart = requestedStart + it.data!!.size
            requestedLimit += it.data.size
            requestedMessages.onNext(it.data)
        }, {
            requestedMessages.onError(it)
        })
    }


    fun saveCurrentNode(inherit: String) = userRepo.saveCurrentNode(inherit)
//    fun completeTransfer(ids: Array<Int?>): Observable<ArrayList<CompleteTransferResponseItem>> =
//        adminRepo.completeTransfer(ids)

    fun lockTransfer(transferId: Int,delegationId: Int): Call<ResponseBody> = adminRepo.lockTransfer(transferId,delegationId)
    fun recallTransfer(transferId: Int,delegationId: Int): Call<ResponseBody> = adminRepo.recallTransfer(transferId,delegationId)

//    fun transferDismissCopy(ids: Array<Int?>): Observable<ArrayList<DismissCopyResponseItem>> =
//        adminRepo.transferDismissCopy(ids)

    fun readUserinfo(): UserFullDataResponseItem = userRepo.readFullUserData()!!

    fun readFullCategories(): ArrayList<FullCategoriesResponseItem> =
        userRepo.readFullCategories()!!

    fun readNodes(): ArrayList<NodeResponseItem> =
        userRepo.readNodes()!!

    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }


}