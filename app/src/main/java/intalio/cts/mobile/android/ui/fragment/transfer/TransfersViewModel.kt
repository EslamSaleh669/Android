package intalio.cts.mobile.android.ui.fragment.transfer

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

class TransfersViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readAllStructureData (): AllStructuresResponse = userRepo.readAllStructureData()!!
    fun readPurposesData (): ArrayList<PurposesResponseItem>? = userRepo.readPurposes()
    fun transferTransfer (list :List<TransferRequestModel>):Observable<ArrayList<TransferTransferResponseItem>> =
        adminRepo.transferTransfer(list)


    fun getAllStructures(text: String): Observable<AllStructuresResponse> = adminRepo.getAllStructures(text)



    fun readprivacies (): ArrayList<PrivaciesResponseItem> = userRepo.readPrivacies()!!
    fun readFullStructures (): ArrayList<FullStructuresResponseItem> = userRepo.readFullStructures()!!

    fun listUserExistenceAttributeInStructure(ids:Array<Int>): Call<ResponseBody> = adminRepo.listUserExistenceAttributeInStructure(ids)

    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!

    fun getUser(userId:Int) : Observable<UserFullDataResponseItem> = adminRepo.getUser(userId)
    fun geStructure(userId:Int) : Observable<AllStructuresItem> = adminRepo.geStructure(userId)



    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun readLanguage (): String = userRepo.currentLang()


}