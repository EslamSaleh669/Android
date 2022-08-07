package intalio.cts.mobile.android.ui.fragment.uploadattachment

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File

class UploadAttachmentViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!



   // fun uploadAttachment(model: JSONObject): Observable<UploadAttachmentResponse>  = adminRepo.uploadAttachment(model)


    fun uploadAttachment(body: MultipartBody):Observable<UploadAttachmentResponse>
            = adminRepo.uploadAttachment(body)



    fun uploadOriginalAttachment(body: MultipartBody):Observable<UploadAttachmentResponse>
            = adminRepo.uploadOriginalAttachment(body)









}