package intalio.cts.mobile.android.ui.fragment.note

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.NotesDataItem
import intalio.cts.mobile.android.ui.adapter.AllNotesAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.allnotes_fragment.*
import kotlinx.android.synthetic.main.allnotes_fragment.noDataFounded
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class AllNotesFragment : Fragment(), AllNotesAdapter.OnDeleteNoteClicked {
    private var Node_Inherit :String = ""
    private var delegationId: Int = 0

    private var DocumentId: Int = 0
    private var TransferId: Int = 0
    private var canDoAction: Boolean = false
    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>

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
        dialog = requireContext().launchLoadingDialog()

        return inflater.inflate(R.layout.allnotes_fragment, container, false)
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.Notes = ReplaySubject.create()


        translator = viewModel.readDictionary()!!.data!!
        setLabels()

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        requireArguments().getString(Constants.NODE_INHERIT).let {
            Node_Inherit = it!!
        }

        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it

        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        requireArguments().getBoolean(Constants.CANDOACTION).let {
            canDoAction = it
        }

        if (Node_Inherit == "Inbox" && canDoAction) {
            notesrel.visibility = View.VISIBLE

        }

        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        getNotes(DocumentId,delegationId)


        addnotes.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AddNotesFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.DOCUMENT_ID, DocumentId),
                            Pair(Constants.TRANSFER_ID, TransferId)
                        )


                    }
                )
                addToBackStack("")

            }
        }


    }


    private fun getNotes(DoctId: Int, delegationId: Int) {
        var noMoreData = ""

        when {
            viewModel.readLanguage() == "en" -> {

                noMoreData = "No more data"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                noMoreData = "لا يوجد المزيد"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {
                noMoreData = "Plus de données"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
            }
        }
        notesrecycler.adapter =
            AllNotesAdapter(arrayListOf(), requireActivity(), this, Node_Inherit, canDoAction)
        notesrecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



        autoDispose.add(viewModel.Notes.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()
            val lastPosition: Int =
                (notesrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

            if (it!!.isEmpty() && viewModel.limit == 0) {
                noDataFounded.visibility = View.VISIBLE
                notesrecycler.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (notesrecycler.adapter as AllNotesAdapter).addNotes(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        }, {
            noDataFounded.visibility = View.VISIBLE
            notesrecycler.visibility = View.GONE
            Timber.e(it)


        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notesrecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (notesrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForNotesLoading(lastPosition, DoctId,delegationId)

            }
        }

        viewModel.loadMoreNotes(DoctId,delegationId)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0
    }

    override fun onDeleteClicked(noteid: Int) {

        showDialog(noteid)


    }

    override fun onEditClicked(position: Int, model: NotesDataItem) {
        showNoteDialog(position, model)
    }

    private fun showNoteDialog(position: Int, model: NotesDataItem) {
        val customDialog = Dialog(requireContext(), R.style.FullScreenDialog)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.edit_note_layout)

        customDialog.findViewById<TextView>(R.id.centered_txt).setText(R.string.edit)
        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            customDialog.dismiss()
        }

        var requiredFields = ""

        when {
            viewModel.readLanguage() == "en" -> {

                customDialog.findViewById<TextView>(R.id.note_label).text =
                    translator.find { it.keyword == "Note" }!!.en
                customDialog.findViewById<TextView>(R.id.privacycheckbox).text =
                    translator.find { it.keyword == "Privacy" }!!.en
                customDialog.findViewById<TextView>(R.id.btnaddnote).text =
                    translator.find { it.keyword == "Edit" }!!.en
                customDialog.findViewById<TextView>(R.id.btnCancelnote).text =
                    translator.find { it.keyword == "Cancel" }!!.en
                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Edit" }!!.en
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {

                customDialog.findViewById<TextView>(R.id.note_label).text =
                    translator.find { it.keyword == "Note" }!!.ar
                customDialog.findViewById<TextView>(R.id.privacycheckbox).text =
                    translator.find { it.keyword == "Privacy" }!!.ar
                customDialog.findViewById<TextView>(R.id.btnaddnote).text =
                    translator.find { it.keyword == "Edit" }!!.ar
                customDialog.findViewById<TextView>(R.id.btnCancelnote).text =
                    translator.find { it.keyword == "Cancel" }!!.ar
                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Edit" }!!.ar
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {

                customDialog.findViewById<TextView>(R.id.note_label).text =
                    translator.find { it.keyword == "Note" }!!.fr
                customDialog.findViewById<TextView>(R.id.privacycheckbox).text =
                    translator.find { it.keyword == "Privacy" }!!.fr
                customDialog.findViewById<TextView>(R.id.btnaddnote).text =
                    translator.find { it.keyword == "Edit" }!!.fr
                customDialog.findViewById<TextView>(R.id.btnCancelnote).text =
                    translator.find { it.keyword == "Cancel" }!!.fr
                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Edit" }!!.fr
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.fr!!

            }
        }

        val editedNoteText = customDialog.findViewById(R.id.notetext) as EditText
        val editedNotePrivacy = customDialog.findViewById(R.id.privacycheckbox) as CheckBox

        val noteValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(model.notes.toString(), Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(model.notes.toString())
        }
        editedNoteText.setText(noteValue, TextView.BufferType.EDITABLE)
        editedNotePrivacy.isChecked = model.isPrivate!!

        val yesBtn = customDialog.findViewById(R.id.btnaddnote) as TextView
        val noBtn = customDialog.findViewById(R.id.btnCancelnote) as TextView
        yesBtn.setOnClickListener {
            val note = editedNoteText.text.toString().trim()

            if (note.isEmpty()) {
                requireActivity().makeToast(requiredFields)
            } else {
                model.notes = note
                model.isPrivate = editedNotePrivacy.isChecked

                dialog = requireContext().launchLoadingDialog()

                addEditedNote(DocumentId, TransferId, note, editedNotePrivacy.isChecked, model.id!!)
                (notesrecycler.adapter as AllNotesAdapter).modifyNote(position, model)

                customDialog.dismiss()
            }

        }
        noBtn.setOnClickListener {
            customDialog.dismiss()

        }
        customDialog.show()

    }

    private fun addEditedNote(
        DoctId: Int,
        transID: Int,
        note: String,
        ischecked: Boolean,
        noteid: Int
    ) {

        autoDispose.add(
            viewModel.saveEditedNote(DoctId, transID, note, ischecked, noteid,delegationId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        dialog!!.dismiss()

                        if (it.id!! > 0) {

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

    private fun showDialog(noteid: Int) {

        var deleteConfirmation = ""
        var yes = ""
        var no = ""


        when {
            viewModel.readLanguage() == "en" -> {


                deleteConfirmation =
                    translator.find { it.keyword == "DeleteNoteConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                deleteConfirmation =
                    translator.find { it.keyword == "DeleteNoteConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                deleteConfirmation =
                    translator.find { it.keyword == "DeleteNoteConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(deleteConfirmation)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialog, i ->
                    dialog.dismiss()

                    autoDispose.add(
                        viewModel.deleteNote(noteid, DocumentId, TransferId,delegationId)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                                {

                                    if (it.equals(true)) {
                                        (notesrecycler.adapter as AllNotesAdapter).removeNote(noteid)

                                    } else {
                                        requireActivity().makeToast(getString(R.string.network_error))
                                    }

                                },
                                {
                                    Timber.e(it)


                                })
                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()


                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {

                notes_label.text = translator.find { it.keyword == "Notes" }!!.en
                addnotes.text = translator.find { it.keyword == "Add" }!!.en
                centered_txt.text = translator.find { it.keyword == "Notes" }!!.en


            }
            viewModel.readLanguage() == "ar" -> {

                notes_label.text = translator.find { it.keyword == "Notes" }!!.ar
                addnotes.text = translator.find { it.keyword == "Add" }!!.ar
                centered_txt.text = translator.find { it.keyword == "Notes" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {

                notes_label.text = translator.find { it.keyword == "Notes" }!!.fr
                addnotes.text = translator.find { it.keyword == "Add" }!!.fr
                centered_txt.text = translator.find { it.keyword == "Notes" }!!.fr

            }
        }
    }

}



