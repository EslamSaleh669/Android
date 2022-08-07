package intalio.cts.mobile.android.ui.fragment.reply

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

class ReplyViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!
    fun readPurposesData (): ArrayList<PurposesResponseItem>? = userRepo.readPurposes()



    fun replyToUser(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>): Call<ResponseBody>  =
        adminRepo.replyToUser(
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            structureReceivers
        )

    fun replyToStructure(documentId:Int, transferId:Int,purposeId:Int,dueDate:String,
                    instruction:String,structureId:Int,structureReceivers:Array<Int>): Call<ResponseBody>  =
        adminRepo.replyToStructure(
            documentId,
            transferId,
            purposeId,
            dueDate,
            instruction,
            structureId,
            structureReceivers
        )







}