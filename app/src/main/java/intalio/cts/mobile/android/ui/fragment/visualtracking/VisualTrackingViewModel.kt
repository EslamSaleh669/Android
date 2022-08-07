package intalio.cts.mobile.android.ui.fragment.visualtracking

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject

class VisualTrackingViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {



    fun getVisualTracking(documentId:Int) : Observable<ArrayList<VisualTrackingResponseItem>> =
        adminRepo.getVisualTracking(documentId)


 }