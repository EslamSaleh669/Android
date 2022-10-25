package intalio.cts.mobile.android.ui.fragment.reply

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import okhttp3.ResponseBody
import retrofit2.Call

class ReplyViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!
    fun readPurposesData (): ArrayList<PurposesResponseItem>? = userRepo.readPurposes()



    fun replyToUser(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>,delegationId:Int): Call<ResponseBody>  =
        adminRepo.replyToUser(
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            structureReceivers,
            delegationId
        )

    fun replyToStructure(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>,delegationId:Int): Call<ResponseBody>  =
        adminRepo.replyToStructure(
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            structureReceivers,
            delegationId
        )



    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()

    fun readLanguage (): String = userRepo.currentLang()

    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()

    fun readCurrentNode():String = userRepo.readCurrentNode()!!

}