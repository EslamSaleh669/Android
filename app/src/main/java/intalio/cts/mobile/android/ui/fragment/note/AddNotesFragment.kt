package intalio.cts.mobile.android.ui.fragment.note

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_addnote.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AddNotesFragment : Fragment() {
    private var DocumentId: Int = 0
    private var TransferId: Int = 0
    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>
    private var delegationId: Int = 0

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

        translator = viewModel.readDictionary()!!.data!!
        setLabels()


        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it

        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        var requiredFields = ""
        var validationMessage = ""

        when {
            viewModel.readLanguage() == "en" -> {

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.en!!
                validationMessage = "This value is too short. It should have 10 characters or more."

            }
            viewModel.readLanguage() == "ar" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.ar!!
                validationMessage = "القيمة المدخلة قصيرة جداً . تأكد من إدخال 10 حرف أو أكثر"


            }
            viewModel.readLanguage() == "fr" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.fr!!
                validationMessage = "Cette chaîne est trop courte. Elle doit avoir au minimum 10 caractères."

            }
        }

        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        btnaddnote.setOnClickListener {
            val note = notetext.text.toString().trim()

            if (note.isEmpty()) {
                requireActivity().makeToast(requiredFields)
            }else if (note.toCharArray().size < 10){
                requireActivity().makeToast(validationMessage)

            }
            else {
                dialog = requireContext().launchLoadingDialog()

                addNote(DocumentId, TransferId, note,delegationId)

            }
        }
        btnCancelnote.setOnClickListener {
            activity?.onBackPressed()
        }


    }


    private fun addNote(DoctId: Int, transID: Int, note: String, delegationId: Int) {

        autoDispose.add(
            viewModel.saveNote(DoctId, transID, note, privacycheckbox.isChecked,delegationId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {

                    dialog!!.dismiss()

                    if (it.id!! > 0) {
                        activity?.onBackPressed()
                    } else {
                        requireActivity().makeToast(it.message.toString())
                    }

                },
                {
                    Timber.e(it)
                    dialog!!.dismiss()


                })
        )

    }


    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {

                note_label.text = translator.find { it.keyword == "Note" }!!.en
                privacycheckbox.text = translator.find { it.keyword == "Private" }!!.en

                btnaddnote.text = translator.find { it.keyword == "Add" }!!.en
                btnCancelnote.text = translator.find { it.keyword == "Cancel" }!!.en

                centered_txt.text = translator.find { it.keyword == "Add" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {

                note_label.text = translator.find { it.keyword == "Note" }!!.ar
                privacycheckbox.text = translator.find { it.keyword == "Private" }!!.ar

                btnaddnote.text = translator.find { it.keyword == "Add" }!!.ar
                btnCancelnote.text = translator.find { it.keyword == "Cancel" }!!.ar

                centered_txt.text = translator.find { it.keyword == "Add" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {

                note_label.text = translator.find { it.keyword == "Note" }!!.fr
                privacycheckbox.text = translator.find { it.keyword == "Private" }!!.fr

                btnaddnote.text = translator.find { it.keyword == "Add" }!!.fr
                btnCancelnote.text = translator.find { it.keyword == "Cancel" }!!.fr

                centered_txt.text = translator.find { it.keyword == "Add" }!!.fr
            }
        }
    }
}