package intalio.cts.mobile.android.ui.fragment.note

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import org.json.JSONObject

class NotesViewModel (private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    var start: Int = 0
    var limit = 0
    var Notes: ReplaySubject<ArrayList<NotesDataItem>> = ReplaySubject.create()

    var disposable: Disposable? = null





    fun checkForNotesLoading(lastPosition: Int,documentId:Int) {
        var currentItemsCount = 0
        for (item in Notes.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreNotes(documentId)
        }
    }


    fun loadMoreNotes(documentId:Int) {
        disposable = adminRepo.notesData(start,documentId).subscribe({
            start = start + it.data!!.size
            limit += it.data.size
            Notes.onNext(it.data)
        }, {
            Notes.onError(it)
        })
    }



    fun saveNote(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean):
            Observable<SaveNotesResponse> =
        adminRepo.saveNotes(documentId,transferId,note,IsPrivate)


    fun saveEditedNote(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean,noteID: Int):
            Observable<SaveNotesResponse> =
        adminRepo.saveEditedNotes(documentId,transferId,note,IsPrivate,noteID)

    fun deleteNote(noteID: Int, documentId:Int, transferId:Int): Observable<Boolean> =
        adminRepo.deleteNote(noteID,documentId,transferId)


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

 }