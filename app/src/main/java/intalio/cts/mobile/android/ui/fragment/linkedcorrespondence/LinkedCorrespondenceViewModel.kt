package intalio.cts.mobile.android.ui.fragment.linkedcorrespondence

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject

class LinkedCorrespondenceViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    var start: Int = 0
    var limit = 0
    var Items: ReplaySubject<ArrayList<AdvancedSearchResponseDataItem>> = ReplaySubject.create()

    var disposable: Disposable? = null




    fun checkForItemsLoading(lastPosition:Int, model: JSONObject) {
        var currentItemsCount = 0
        for (item in Items.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreItems(model)
        }
    }


    fun loadMoreItems(model: JSONObject) {
        disposable = adminRepo.advancedSearch(start,model).subscribe({
            start += start+it.data!!.size
            limit += it.data.size
            Items.onNext(it.data)
        }, {
            Items.onError(it)
        })
    }


    fun getLinkedC(documentId: Int, delegationId: Int): Observable<LinkedCorrespondenceResponse> =
        adminRepo.getLinkedC(documentId,delegationId)

    fun deleteLinkedC(linkedId: Int, documentId:Int, transferId:Int,delegationId:Int): Observable<Boolean> =
        adminRepo.deleteLinkedC(linkedId,documentId,transferId,delegationId)

    fun addLinkedDocument(documentIds: Array<Int>, documentId:Int, transferId:Int,delegationId:Int): Observable<SaveNotesResponse> =
        adminRepo.addLinkedDocument(documentIds,documentId,transferId,delegationId)



    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()

    fun readLanguage (): String = userRepo.currentLang()
    fun readStatuses (): ArrayList<StatusesResponseItem> = userRepo.readStatuses()!!
    fun readCategoriesData():ArrayList<CategoryResponseItem> = userRepo.readCategoriesData()!!
    fun readSettings (): ArrayList<ParamSettingsResponseItem> = userRepo.readSettings()!!
    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!


    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }


}