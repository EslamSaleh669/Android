package intalio.cts.mobile.android.ui.fragment.nonarchattachments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.NonArchDataItem
import intalio.cts.mobile.android.data.network.response.TypesResponseItem
import intalio.cts.mobile.android.ui.adapter.NonArchAdapter
import intalio.cts.mobile.android.ui.adapter.NonArchTypesAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_nonarch.*
import kotlinx.android.synthetic.main.fragment_nonarch.noDataFounded
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class NonArchAttachmentsFragment : Fragment() , NonArchAdapter.OnDeleteNoteClicked{
    private var Node_Inherit :String = ""
    private var DocumentId:Int = 0
    private var TransferId:Int = 0
    private var canDoAction:Boolean = false
    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>

    private var delegationId = 0

    @Inject
    @field:Named("nonarchattachments")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NonArchAttachmentsViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(NonArchAttachmentsViewModel::class.java)
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

        return inflater.inflate(R.layout.fragment_nonarch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.NonArch = ReplaySubject.create()

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
            nonrel.visibility = View.VISIBLE }
        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        getNonArch(DocumentId,delegationId)


        addnonarch.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AddNonArchsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.DOCUMENT_ID,DocumentId),
                            Pair(Constants.TRANSFER_ID,TransferId)
                        )


                    }
                )
                addToBackStack("")

            }
        }




    }


    private fun getNonArch(DoctId: Int, delegationId: Int) {
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

        nonarchrecycler.adapter =
            NonArchAdapter(arrayListOf(), requireActivity(),this,Node_Inherit,canDoAction,viewModel)
        nonarchrecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



        autoDispose.add(viewModel.NonArch.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()
            val lastPosition: Int =
                (nonarchrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()


             if (it!!.isEmpty() && viewModel.limit == 0) {
                 noDataFounded.visibility = View.VISIBLE
                 nonarchrecycler.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (nonarchrecycler.adapter as NonArchAdapter).addNonArch(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        },{
            dialog!!.dismiss()
            noDataFounded.visibility = View.VISIBLE
            nonarchrecycler.visibility = View.GONE
            Timber.e(it)

        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nonarchrecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (nonarchrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForNonArchLoading(lastPosition, DoctId,delegationId)

            }
        }

        viewModel.loadMoreNonArch(DoctId,delegationId)

    }



    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0
    }

    override fun onDeleteClicked(nonarchid: Int) {
        showDialog(nonarchid)

    }

    private fun showDialog(nonarchid: Int) {

        var deleteConfirmation = ""
        var yes = ""
        var no = ""


        when {
            viewModel.readLanguage() == "en" -> {


                deleteConfirmation = translator.find { it.keyword == "DeleteRowConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                deleteConfirmation = translator.find { it.keyword == "DeleteRowConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {



                deleteConfirmation = translator.find { it.keyword == "DeleteRowConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(deleteConfirmation)
            .setPositiveButton(yes, DialogInterface.OnClickListener { dialog, i ->
                dialog.dismiss()

                autoDispose.add(viewModel.deleteNonArch(nonarchid,DocumentId,TransferId,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        if (it.equals(true)){
                             (nonarchrecycler.adapter as NonArchAdapter).removeNoneArch(nonarchid)

                        }



                    },
                    {
                        Timber.e(it)


                    }))
            })
            .setNegativeButton(no, DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()


            }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }


    override fun onEditClicked(position: Int, model: NonArchDataItem) {
        showNonArchDialog(position,model)
    }

    private fun showNonArchDialog(position: Int , model: NonArchDataItem) {


        var typeSelectedId = model.typeId

        val customDialog = Dialog(requireContext(),R.style.FullScreenDialog)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(true)
        customDialog.setContentView(R.layout.edit_nonarch_layout)

         customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            customDialog.dismiss()
        }

        var requiredFields = ""
        when {
            viewModel.readLanguage() == "en" -> {


                customDialog.findViewById<TextView>(R.id.type_label).text = translator.find { it.keyword == "Type" }!!.en
                customDialog.findViewById<TextView>(R.id.attchtypeautocomplete).hint = translator.find { it.keyword == "Type" }!!.en
                customDialog.findViewById<TextView>(R.id.quantity_label).text = translator.find { it.keyword == "Quantity" }!!.en
                customDialog.findViewById<TextView>(R.id.attchquantity).hint = translator.find { it.keyword == "Quantity" }!!.en
                customDialog.findViewById<TextView>(R.id.description_label).text = translator.find { it.keyword == "Description" }!!.en
                customDialog.findViewById<TextView>(R.id.btnSaveattch).text = translator.find { it.keyword == "Save" }!!.en
                customDialog.findViewById<TextView>(R.id.btnCancelattch).text = translator.find { it.keyword == "Cancel" }!!.en
                customDialog.findViewById<TextView>(R.id.centered_txt).text = translator.find { it.keyword == "Edit" }!!.en

                customDialog.findViewById<TextView>(R.id.requiredtype_label).text = "(required)"
                customDialog.findViewById<TextView>(R.id.requiredquantity_label).text = "(required)"

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                customDialog.findViewById<TextView>(R.id.type_label).text = translator.find { it.keyword == "Type" }!!.ar
                customDialog.findViewById<TextView>(R.id.attchtypeautocomplete).hint = translator.find { it.keyword == "Type" }!!.ar
                customDialog.findViewById<TextView>(R.id.quantity_label).text = translator.find { it.keyword == "Quantity" }!!.ar
                customDialog.findViewById<TextView>(R.id.attchquantity).hint = translator.find { it.keyword == "Quantity" }!!.ar
                customDialog.findViewById<TextView>(R.id.description_label).text = translator.find { it.keyword == "Description" }!!.ar
                customDialog.findViewById<TextView>(R.id.btnSaveattch).text = translator.find { it.keyword == "Save" }!!.ar
                customDialog.findViewById<TextView>(R.id.btnCancelattch).text = translator.find { it.keyword == "Cancel" }!!.ar
                customDialog.findViewById<TextView>(R.id.centered_txt).text = translator.find { it.keyword == "Edit" }!!.ar

                customDialog.findViewById<TextView>(R.id.requiredtype_label).text = "(الزامي)"
                customDialog.findViewById<TextView>(R.id.requiredquantity_label).text = "(الزامي)"

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {

                customDialog.findViewById<TextView>(R.id.type_label).text = translator.find { it.keyword == "Type" }!!.fr
                customDialog.findViewById<TextView>(R.id.attchtypeautocomplete).hint = translator.find { it.keyword == "Type" }!!.fr
                customDialog.findViewById<TextView>(R.id.quantity_label).text = translator.find { it.keyword == "Quantity" }!!.fr
                customDialog.findViewById<TextView>(R.id.attchquantity).hint = translator.find { it.keyword == "Quantity" }!!.fr
                customDialog.findViewById<TextView>(R.id.description_label).text = translator.find { it.keyword == "Description" }!!.fr
                customDialog.findViewById<TextView>(R.id.btnSaveattch).text = translator.find { it.keyword == "Save" }!!.fr
                customDialog.findViewById<TextView>(R.id.btnCancelattch).text = translator.find { it.keyword == "Cancel" }!!.fr
                customDialog.findViewById<TextView>(R.id.centered_txt).text = translator.find { it.keyword == "Edit" }!!.fr

                customDialog.findViewById<TextView>(R.id.requiredtype_label).text = "(requis)"
                customDialog.findViewById<TextView>(R.id.requiredquantity_label).text = "(requis)"

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.fr!!
            }
        }


        val attachTypeAutoComplete = customDialog.findViewById<AutoCompleteTextView>(R.id.attchtypeautocomplete)
        val attachmentCount = customDialog.findViewById<EditText>(R.id.attchquantity)
        val attachmentDescription= customDialog.findViewById<EditText>(R.id.attchdesc)

        attachmentCount.setText(model.quantity.toString(), TextView.BufferType.EDITABLE)
        if (model.description.toString() != "null"){
            attachmentDescription.setText(model.description.toString(), TextView.BufferType.EDITABLE)
        }
        attachTypeAutoComplete.setText(model.type.toString(), TextView.BufferType.EDITABLE)

        attachTypeAutoComplete.threshold = 0
        val attachmentAutoCompleteArray = viewModel.readTypesData()!!

        val arrayAdapter =
            NonArchTypesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                attachmentAutoCompleteArray
            )
        attachTypeAutoComplete.setAdapter(arrayAdapter)




        attachTypeAutoComplete.setOnClickListener {
            attachTypeAutoComplete.showDropDown()

        }


        attachTypeAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                attachTypeAutoComplete.showDropDown()

            } else {
                attachTypeAutoComplete.setText("")
            }

        }



        attachTypeAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                attachTypeAutoComplete.clearFocus()
                attachTypeAutoComplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as TypesResponseItem
                attachTypeAutoComplete.setText(selectedObject.text.toString())
                typeSelectedId = selectedObject.id!!

            }





    val yesBtn = customDialog.findViewById(R.id.btnSaveattch) as TextView
        val noBtn = customDialog.findViewById(R.id.btnCancelattch) as TextView
        yesBtn.setOnClickListener {



            val desctription = attachmentDescription.text.toString().trim()
            val quantity = attachmentCount.text.toString().trim()

            if (quantity == "")
            {
                requireActivity().makeToast(requiredFields)
            }else{

                model.description = desctription
                model.quantity = quantity.toInt()
                model.type = attachTypeAutoComplete.text.toString().trim()
                dialog = requireContext().launchLoadingDialog()

                addEditedNonArch(DocumentId,TransferId,desctription.trim(),quantity.toInt(),
                    typeSelectedId!!, model.id!!
                )
                (nonarchrecycler.adapter as NonArchAdapter).modifyNonArch(position,model)

                customDialog.dismiss()
            }


        }
        noBtn.setOnClickListener {
            customDialog.dismiss()

        }


        customDialog.show()

    }

    private fun addEditedNonArch(DoctId: Int , transID:Int , desc:String , quantity : Int,selectedType:Int ,nonarchid: Int) {


        autoDispose.add(viewModel.saveEditedNonArch(DoctId,transID,selectedType,desc,quantity,nonarchid,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe(
            {

                dialog!!.dismiss()

                if (it.id!! > 0){

                }else{
                    requireActivity().makeToast(it.message.toString())
                }

            },
            {
                Timber.e(it)
                dialog!!.dismiss()


            }))

    }

    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {

                nonarch_label.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.en
                addnonarch.text = translator.find { it.keyword == "Add" }!!.en
                centered_txt.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.en



            }
            viewModel.readLanguage() == "ar" -> {

                nonarch_label.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.ar
                addnonarch.text = translator.find { it.keyword == "Add" }!!.ar
                centered_txt.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.ar




            }
            viewModel.readLanguage() == "fr" -> {


                nonarch_label.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.fr
                addnonarch.text = translator.find { it.keyword == "Add" }!!.fr
                centered_txt.text = translator.find { it.keyword == "NonArchivedAttachments" }!!.fr


            }
        }
    }





}