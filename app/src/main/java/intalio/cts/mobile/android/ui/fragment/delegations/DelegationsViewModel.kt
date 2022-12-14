package intalio.cts.mobile.android.ui.fragment.delegations

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class DelegationsViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    var start: Int = 0
    var limit = 0
    var Delegations: ReplaySubject<ArrayList<DelegationDataItem>> = ReplaySubject.create()

    var disposable: Disposable? = null





    fun checkForDelegationsLoading(lastPosition: Int) {
        var currentItemsCount = 0
        for (item in Delegations.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreDelegations()
        }
    }


    fun loadMoreDelegations() {
        disposable = adminRepo.getDelegations(start).subscribe({
            start = start + it.data!!.size
            limit += it.data.size
            Delegations.onNext(it.data)
        }, {
            Delegations.onError(it)
        })
    }

    fun readUsersStructureData (): ArrayList<UsersStructureItem>? = userRepo.readUsersStructureData()

    fun readCategoriesData (): ArrayList<CategoryResponseItem>? = userRepo.readCategoriesData()

    fun saveDelegation(toUserId:Int,fromDate:String,toDate:String,categories:ArrayList<Int>): Observable<SaveDelegationResponse> =
        adminRepo.saveDelegations(toUserId,fromDate,toDate,categories)

    fun saveEditedDelegation(toUserId:Int,fromDate:String,toDate:String,categories:ArrayList<Int>,delegationId:Int): Observable<SaveDelegationResponse> =
        adminRepo.saveEditedDelegations(toUserId,fromDate,toDate,categories,delegationId)


    fun deleteDelegation(ids:Array<Int>): Call<ResponseBody> = adminRepo.deleteDelegation(ids)

    fun readLanguage() : String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }


}