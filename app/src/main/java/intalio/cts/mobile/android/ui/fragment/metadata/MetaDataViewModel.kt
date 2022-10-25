package intalio.cts.mobile.android.ui.fragment.metadata

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable

class MetaDataViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readStatuses (): ArrayList<StatusesResponseItem> = userRepo.readStatuses()!!
    fun readPriorities (): ArrayList<PrioritiesResponseItem> = userRepo.readPriorities()!!
    fun readprivacies (): ArrayList<PrivaciesResponseItem> = userRepo.readPrivacies()!!

    fun readCategoriesData (): ArrayList<CategoryResponseItem>? = userRepo.readCategoriesData()



    fun getMetaDataInfo(transferId: Int, delegationId: Int): Observable<MetaDataResponse> = adminRepo.getMetaDataInfo(transferId,delegationId)

    fun getSearchDocumentInfo(documentId:Int,delegationId:Int): Observable<MetaDataResponse> = adminRepo.getSearchDocument(documentId,delegationId)


    fun getDocument(documentId: Int): Observable<MetaDataResponse> = adminRepo.getDocument(documentId)


    fun readPath():String = userRepo.readPath()!!
    fun readLanguage (): String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()

    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()


}