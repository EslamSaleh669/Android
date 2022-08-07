package intalio.cts.mobile.android.ui.fragment.nonarchattachments

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject

class NonArchAttachmentsViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    var start: Int = 0
    var limit = 0
    var NonArch: ReplaySubject<ArrayList<NonArchDataItem>> = ReplaySubject.create()

    var disposable: Disposable? = null





    fun checkForNonArchLoading(lastPosition: Int,documentId:Int) {
        var currentItemsCount = 0
        for (item in NonArch.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreNonArch(documentId)
        }
    }


    fun loadMoreNonArch(documentId:Int) {
        disposable = adminRepo.nonArchData(start,documentId).subscribe({
            start = start + it.data!!.size
            limit += it.data.size
            NonArch.onNext(it.data)
        }, {
            NonArch.onError(it)
        })
    }



    fun saveNonArch(documentId:Int,transferId:Int,typeId:Int,description:String,quantity:Int):
            Observable<SaveNotesResponse> =
        adminRepo.saveNonArch(documentId,transferId,typeId,description,quantity)


    fun saveEditedNonArch(documentId:Int,transferId:Int,typeId:Int,description:String,quantity:Int,nonarchId:Int):
            Observable<SaveNotesResponse> =
        adminRepo.saveEditedNonArch(documentId,transferId,typeId,description,quantity,nonarchId)


    fun deleteNonArch(noteID: Int, documentId:Int, transferId:Int): Observable<Boolean> =
        adminRepo.deleteNonArch(noteID,documentId,transferId)

    fun readTypesData (): ArrayList<TypesResponseItem>? = userRepo.readTypes()


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

 }