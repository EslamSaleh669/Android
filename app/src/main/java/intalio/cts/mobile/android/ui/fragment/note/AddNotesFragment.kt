package intalio.cts.mobile.android.ui.fragment.note

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_addnote.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AddNotesFragment : Fragment() {
     private var DocumentId:Int = 0
     private var TransferId:Int = 0

    @Inject
    @field:Named("notes")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NotesViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)
        }

    private val autoDispose: AutoDispose = AutoDispose()
    var dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            autoDispose.bindTo(this.lifecycle)


            (activity?.application as MyApplication).appComponent?.inject(this)

        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_addnote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        centered_txt.text = requireActivity().getText(R.string.add)
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it

        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        btnaddnote.setOnClickListener {
            val note = notetext.text.toString().trim()

            if (note.isEmpty()){
                requireActivity().makeToast(getString(R.string.please_add_note))
            }else{
                dialog = requireContext().launchLoadingDialog()

                addNote(DocumentId,TransferId,note)

            }
        }
        btnCancelnote.setOnClickListener {
            activity?.onBackPressed()
        }


    }


    private fun addNote(DoctId: Int , transID:Int , note:String) {

        autoDispose.add(viewModel.saveNote(DoctId,transID,note,privacycheckbox.isChecked).observeOn(AndroidSchedulers.mainThread()).subscribe(
            {

                dialog!!.dismiss()

                if (it.id!! > 0){
                    activity?.onBackPressed()
                }else{
                    requireActivity().makeToast(it.message.toString())
                }

            },
            {
                Timber.e(it)
                dialog!!.dismiss()


            }))

    }


 }