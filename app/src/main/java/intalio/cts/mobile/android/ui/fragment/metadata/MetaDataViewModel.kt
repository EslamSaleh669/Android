package intalio.cts.mobile.android.ui.fragment.metadata

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import okhttp3.ResponseBody
import retrofit2.Response

class MetaDataViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readStatuses (): ArrayList<StatusesResponseItem> = userRepo.readStatuses()!!
    fun readPriorities (): ArrayList<PrioritiesResponseItem> = userRepo.readPriorities()!!
    fun readprivacies (): ArrayList<PrivaciesResponseItem> = userRepo.readPrivacies()!!

    fun readCategoriesData (): ArrayList<CategoryResponseItem>? = userRepo.readCategoriesData()



    fun getMetaDataInfo(transferId:Int): Observable<MetaDataResponse> = adminRepo.getMetaDataInfo(transferId)

    fun getSearchDocumentInfo(documentId:Int): Observable<MetaDataResponse> = adminRepo.getSearchDocument(documentId)


    fun getDocument(documentId: Int): Observable<MetaDataResponse> = adminRepo.getDocument(documentId)


    fun readPath():String = userRepo.readPath()!!
    fun readLanguage (): String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()



}