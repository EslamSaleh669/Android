package intalio.cts.mobile.android.ui.fragment.attachments

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject

class AttachmentsViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


    fun attachmentsData(documentId:Int):
            Observable<ArrayList<AttachmentModel>> =
        adminRepo.attachmentsData(documentId)



    fun readPath() : String = userRepo.readPath()!!

    fun savePath(path: String) = userRepo.savePath(path)


    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun readLanguage (): String = userRepo.currentLang()

}