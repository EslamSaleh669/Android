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





    fun checkForNotesLoading(lastPosition: Int,documentId:Int,delegationId: Int) {
        var currentItemsCount = 0
        for (item in Notes.values) {
            currentItemsCount += (item as ArrayList<*>).size
        }
        if (currentItemsCount - 1 == lastPosition) {
            loadMoreNotes(documentId,delegationId)
        }
    }


    fun loadMoreNotes(documentId:Int,delegationId: Int) {
        disposable = adminRepo.notesData(start,documentId,delegationId).subscribe({
            start = start + it.data!!.size
            limit += it.data.size
            Notes.onNext(it.data)
        }, {
            Notes.onError(it)
        })
    }



    fun saveNote(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean,delegationId: Int):
            Observable<SaveNotesResponse> =
        adminRepo.saveNotes(documentId,transferId,note,IsPrivate,delegationId)


    fun saveEditedNote(documentId:Int,transferId:Int,note:String,IsPrivate:Boolean,noteID: Int,delegationId: Int):
            Observable<SaveNotesResponse> =
        adminRepo.saveEditedNotes(documentId,transferId,note,IsPrivate,noteID,delegationId)

    fun deleteNote(noteID: Int, documentId:Int, transferId:Int,delegationId: Int): Observable<Boolean> =
        adminRepo.deleteNote(noteID,documentId,transferId,delegationId)

    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()

    fun readLanguage (): String = userRepo.currentLang()

    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

 }