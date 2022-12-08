package intalio.cts.mobile.android.ui.fragment.advancedsearch

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject
import retrofit2.Call

class AdvanceSearchViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

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



    fun readAllStructureData (): AllStructuresResponse = userRepo.readAllStructureData()!!
    fun readPurposesData (): ArrayList<PurposesResponseItem>? = userRepo.readPurposes()
    fun readPrioritiesData (): ArrayList<PrioritiesResponseItem>? = userRepo.readPriorities()
    fun readStatuses (): ArrayList<StatusesResponseItem> = userRepo.readStatuses()!!

    fun readCategoriesData():ArrayList<CategoryResponseItem> = userRepo.readCategoriesData()!!

    fun readLanguage() : String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun delegationRequests(): Call<ArrayList<DelegationRequestsResponseItem>> = adminRepo.delegationRequests()

    fun readSettings (): ArrayList<ParamSettingsResponseItem> = userRepo.readSettings()!!
    fun getAllStructures(text: String,ids:ArrayList<Int>): Observable<AllStructuresResponse> = adminRepo.getAllStructures(text,ids)

}