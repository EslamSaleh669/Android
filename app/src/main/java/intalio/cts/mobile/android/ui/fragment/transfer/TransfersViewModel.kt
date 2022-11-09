package intalio.cts.mobile.android.ui.fragment.transfer

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call

class TransfersViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readAllStructureData (): AllStructuresResponse = userRepo.readAllStructureData()!!
    fun readPurposesData (): ArrayList<PurposesResponseItem>? = userRepo.readPurposes()
    fun transferTransfer (list: List<TransferRequestModel>, delegationId: Int):Observable<ArrayList<TransferTransferResponseItem>> =
        adminRepo.transferTransfer(list,delegationId)


    fun getAllStructures(text: String,ids: ArrayList<Int>): Observable<AllStructuresResponse> = adminRepo.getAllStructures(text,ids)
    fun getAvailableStructures(text:String, ids: ArrayList<Int>): Observable<AllStructuresResponse> = adminRepo.getAvailableStructures(text,ids)



    fun readprivacies (): ArrayList<PrivaciesResponseItem> = userRepo.readPrivacies()!!
    fun readFullStructures (): ArrayList<FullStructuresResponseItem> = userRepo.readFullStructures()!!
    fun readSettings (): ArrayList<ParamSettingsResponseItem> = userRepo.readSettings()!!

    fun listUserExistenceAttributeInStructure(ids:Array<Int>): Call<ResponseBody> = adminRepo.listUserExistenceAttributeInStructure(ids)

    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!

    fun getUser(userId:Int) : Observable<UserFullDataResponseItem> = adminRepo.getUser(userId)
    fun geStructure(userId:Int) : Observable<AllStructuresItem> = adminRepo.geStructure(userId)
    fun geStructureSendingRules(fromStructureId:Int) : Call<ResponseBody> = adminRepo.geStructureSendingRules(fromStructureId)



    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun readLanguage (): String = userRepo.currentLang()
    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()

    fun readCurrentNode():String = userRepo.readCurrentNode()!!

}