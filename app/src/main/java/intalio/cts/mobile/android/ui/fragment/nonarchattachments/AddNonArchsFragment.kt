package intalio.cts.mobile.android.ui.fragment.nonarchattachments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.TypesResponseItem
import intalio.cts.mobile.android.ui.adapter.NonArchTypesAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_addnonarch.*
import kotlinx.android.synthetic.main.fragment_addnonarch.attchdesc
import kotlinx.android.synthetic.main.fragment_addnonarch.attchquantity
import kotlinx.android.synthetic.main.fragment_addnonarch.attchtypeautocomplete
import kotlinx.android.synthetic.main.fragment_addnonarch.btnCancelattch
import kotlinx.android.synthetic.main.fragment_addnonarch.btnSaveattch
import kotlinx.android.synthetic.main.fragment_addnonarch.description_label
import kotlinx.android.synthetic.main.fragment_addnonarch.quantity_label
import kotlinx.android.synthetic.main.fragment_addnonarch.type_label
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AddNonArchsFragment : Fragment() {
     private var DocumentId:Int = 0
     private var TransferId:Int = 0
     private var typeSelectedId = 0
    private var delegationId = 0

    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>

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

        return inflater.inflate(R.layout.fragment_addnonarch, container, false)
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

        initTypesAutoComplete()


        var requiredFields = ""

        when {
            viewModel.readLanguage() == "en" -> {

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.fr!!

            }
        }
        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        btnSaveattch.setOnClickListener {
            val desctription = attchdesc.text.toString().trim()
            val quantity = attchquantity.text.toString().trim()

            if (typeSelectedId == 0 || attchquantity.text.toString() == "")
            {
                requireActivity().makeToast(requiredFields)
            }else{
                dialog = requireContext().launchLoadingDialog()

                addNonArch(DocumentId,TransferId,desctription,quantity.toInt(),delegationId)

            }
        }
        btnCancelattch.setOnClickListener {
            activity?.onBackPressed()
        }


    }


    private fun initTypesAutoComplete() {
        attchtypeautocomplete.threshold = 0
        val attachmentAutoCompleteArray = viewModel.readTypesData()!!

        val arrayAdapter =
            NonArchTypesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                attachmentAutoCompleteArray
            )
        attchtypeautocomplete.setAdapter(arrayAdapter)




        attchtypeautocomplete.setOnClickListener {
            attchtypeautocomplete.showDropDown()

        }


        attchtypeautocomplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                attchtypeautocomplete.showDropDown()

            } else {
                attchtypeautocomplete.setText("")
            }

        }



        attchtypeautocomplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                attchtypeautocomplete.clearFocus()
                attchtypeautocomplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as TypesResponseItem
                attchtypeautocomplete.setText(selectedObject.text.toString())
                typeSelectedId = selectedObject.id!!

            }

    }

    private fun addNonArch(
        DoctId: Int,
        transID: Int,
        desc: String,
        quantity: Int,
        delegationId: Int
    ) {

        autoDispose.add(viewModel.saveNonArch(DoctId,transID,typeSelectedId,desc,quantity,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe(
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

    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {

                type_label.text = translator.find { it.keyword == "Type" }!!.en
                attchtypeautocomplete.hint = translator.find { it.keyword == "Type" }!!.en

                quantity_label.text = translator.find { it.keyword == "Quantity" }!!.en
                attchquantity.hint = translator.find { it.keyword == "Quantity" }!!.en

                description_label.text = translator.find { it.keyword == "Description" }!!.en

                btnSaveattch.text = translator.find { it.keyword == "Save" }!!.en
                btnCancelattch.text = translator.find { it.keyword == "Cancel" }!!.en

                centered_txt.text = translator.find { it.keyword == "Add" }!!.en

                requiredtype_label.findViewById<TextView>(R.id.requiredtype_label).text = "(required)"
                requiredquantity_label.findViewById<TextView>(R.id.requiredquantity_label).text = "(required)"

            }
            viewModel.readLanguage() == "ar" -> {

                type_label.text = translator.find { it.keyword == "Type" }!!.ar
                attchtypeautocomplete.hint = translator.find { it.keyword == "Type" }!!.ar

                quantity_label.text = translator.find { it.keyword == "Quantity" }!!.ar
                attchquantity.hint = translator.find { it.keyword == "Quantity" }!!.ar


                description_label.text = translator.find { it.keyword == "Description" }!!.ar

                btnSaveattch.text = translator.find { it.keyword == "Save" }!!.ar
                btnCancelattch.text = translator.find { it.keyword == "Cancel" }!!.ar

                centered_txt.text = translator.find { it.keyword == "Add" }!!.ar
                requiredtype_label.findViewById<TextView>(R.id.requiredtype_label).text = "(الزامي)"
                requiredquantity_label.findViewById<TextView>(R.id.requiredquantity_label).text = "(الزامي)"

            }
            viewModel.readLanguage() == "fr" -> {

                type_label.text = translator.find { it.keyword == "Type" }!!.fr
                attchtypeautocomplete.hint = translator.find { it.keyword == "Type" }!!.fr

                quantity_label.text = translator.find { it.keyword == "Quantity" }!!.fr
                attchquantity.hint = translator.find { it.keyword == "Quantity" }!!.fr


                description_label.text = translator.find { it.keyword == "Description" }!!.fr

                btnSaveattch.text = translator.find { it.keyword == "Save" }!!.fr
                btnCancelattch.text = translator.find { it.keyword == "Cancel" }!!.fr

                centered_txt.text = translator.find { it.keyword == "Add" }!!.fr
                requiredtype_label.findViewById<TextView>(R.id.requiredtype_label).text = "(requis)"
                requiredquantity_label.findViewById<TextView>(R.id.requiredquantity_label).text = "(requis)"
            }
        }
    }

 }