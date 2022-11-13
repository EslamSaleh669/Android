package intalio.cts.mobile.android.ui.fragment.advancedsearch

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.*
import intalio.cts.mobile.android.ui.adapter.treeObjects.UserssAdapter
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceResultFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers


import kotlinx.android.synthetic.main.fragment_advancedsearch.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList


class AdvancedSearchFragment : Fragment() {

    private var DocumentId: Int = 0
    private var TransferId: Int = 0

    private var typeOfSearch = 0

    private var prioritySelectedId = 0
    private var statusSelectedId = 0
    private var categorySelectedId = 0
    private var transferFromStructureSelectedId = 0
    private var transferToStructureSelectedId = 0

    private var transferFromUsersSelectedId = 0
    private var transferToUsersSelectedId = 0

    private var sendingEntitySelectedId = 0
    private var receivingEntitySelectedId = 0


    private val addedModel = AdvancedSearchRequest()
    private var transferFromDatePickerDialog: DatePickerDialog? = null
    private var transferToDatePickerDialog: DatePickerDialog? = null
    private var transferFromDateListener: OnDateSetListener? = null
    private var transferToDateListener: OnDateSetListener? = null
    private var transferDateFrom: EditText? = null
    private var transferDateTo: EditText? = null

    private var transferFromSelectedDate: String? = null
    private var transferToSelectedDate: String? = null


    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null
    private var fromDateListener: OnDateSetListener? = null
    private var toDateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null
    private var etDateTo: EditText? = null

    private var fromSelectedDate: String? = null
    private var toSelectedDate: String? = null


    private lateinit var translator: ArrayList<DictionaryDataItem>


    @Inject
    @field:Named("advancedsearch")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AdvanceSearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AdvanceSearchViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_advancedsearch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        btnReset.setOnClickListener {
            emptyForm()

        }


        translator = viewModel.readDictionary()!!.data!!

        arguments?.getInt(Constants.SEARCH_TYPE).let {
            typeOfSearch = it!!
            if (it == 2) {
                requireArguments().getInt(Constants.DOCUMENT_ID).let {
                    DocumentId = it
                }


                requireArguments().getInt(Constants.TRANSFER_ID).let {
                    TransferId = it
                }
            }
        }


        setLabels()

        initCategoryComplete()

        initPrioritiesComplete()
        initStatuesComplete()

        initTransferFromUserAutoComplete()
        initTransferToUserAutoComplete()

        initTransferFromStructuresAutoComplete()
        initTransferToStructuresAutoComplete()

        initSendingEntityAutoComplete()
        initReceivingEntityAutoComplete()


        initTransferDateFrom()
        initDateFrom()

        var requiredFields = ""

        when {
            viewModel.readLanguage() == "en" -> {

                requiredFields = translator.find { it.keyword == "FillSearchCritirea" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                requiredFields = translator.find { it.keyword == "FillSearchCritirea" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {
                requiredFields = translator.find { it.keyword == "FillSearchCritirea" }!!.fr!!

            }
        }


        btnSearch.setOnClickListener {
            if (
                categoryAutoComplete.text.toString().trim() == "" &&
                etRefNumber.text.toString().trim() == "" &&
                searchSubject.text.toString().trim() == "" &&
                statusAutoComplete.text.toString().trim() == "" &&
                Fromdate.text.toString().trim() == "" &&
                Todate.text.toString().trim() == "" &&
                priorityAutoComplete.text.toString().trim() == "" &&
                sendingEntityAutoComplete.text.toString().trim() == "" &&
                receivingEntityAutoComplete.text.toString().trim() == "" &&
                transferFromUserAutoComplete.text.toString().trim() == "" &&
                transferToUserAutoComplete.text.toString().trim() == "" &&
                transferFromStructureAutoComplete.text.toString().trim() == "" &&
                transferToStructureAutoComplete.text.toString().trim() == "" &&
                searchDateFrom.text.toString().trim() == "" &&
                searchDateTo.text.toString().trim() == "" &&
                searchKeyword.text.toString().trim() == "" &&
                !OverdueCheckbox.isChecked
            ) {


                requireActivity().makeToast(requiredFields)
            } else {
                prepareModel()
            }


        }

    }

    private fun setLabels() {
        when {
            viewModel.readLanguage() == "en" -> {


                category_label.text = translator.find { it.keyword == "Category" }!!.en
                refnumber_label.text = translator.find { it.keyword == "ReferenceNumber" }!!.en
                subject_label.text = translator.find { it.keyword == "Subject" }!!.en
                status_label.text = translator.find { it.keyword == "Status" }!!.en
                fromdate_label.text = translator.find { it.keyword == "FromDate" }!!.en
                todate_label.text = translator.find { it.keyword == "ToDate" }!!.en
                priority_label.text = translator.find { it.keyword == "Priority" }!!.en
                senEntity_label.text = translator.find { it.keyword == "SendingEntity" }!!.en
                recEntity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.en
                transfer_From_user_label.text =
                    translator.find { it.keyword == "TransferFromUser" }!!.en
                transfer_To_user_label.text =
                    translator.find { it.keyword == "TransferToUser" }!!.en
                transfer_From_structure_label.text =
                    translator.find { it.keyword == "TransferFromStructure" }!!.en
                transfer_To_Structure_label.text =
                    translator.find { it.keyword == "TransferToStructure" }!!.en
                transfer_From_date_label.text =
                    translator.find { it.keyword == "TransferFromDate" }!!.en
                transfer_to_date_label.text =
                    translator.find { it.keyword == "TransferToDate" }!!.en
                keyword_label.text = translator.find { it.keyword == "Keyword" }!!.en
                OverdueCheckbox.text = translator.find { it.keyword == "OverDue" }!!.en


                categoryAutoComplete.hint = translator.find { it.keyword == "Category" }!!.en
                etRefNumber.hint = translator.find { it.keyword == "ReferenceNumber" }!!.en
                searchSubject.hint = translator.find { it.keyword == "Subject" }!!.en
                statusAutoComplete.hint = translator.find { it.keyword == "Status" }!!.en
                Fromdate.hint = translator.find { it.keyword == "FromDate" }!!.en
                Todate.hint = translator.find { it.keyword == "ToDate" }!!.en
                priorityAutoComplete.hint = translator.find { it.keyword == "Priority" }!!.en
                sendingEntityAutoComplete.hint =
                    translator.find { it.keyword == "SendingEntity" }!!.en
                receivingEntityAutoComplete.hint =
                    translator.find { it.keyword == "ReceivingEntity" }!!.en
                transferFromUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromUser" }!!.en
                transferToUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferToUser" }!!.en
                transferFromStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromStructure" }!!.en
                transferToStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferToStructure" }!!.en
                searchDateFrom.hint = translator.find { it.keyword == "TransferFromDate" }!!.en
                searchDateTo.hint = translator.find { it.keyword == "TransferToDate" }!!.en
                searchKeyword.hint = translator.find { it.keyword == "Keyword" }!!.en


                btnReset
                centered_txt.text = translator.find { it.keyword == "Search" }!!.en
                btnReset.text = translator.find { it.keyword == "Reset" }!!.en
                btnSearch.text = translator.find { it.keyword == "Search" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {


                category_label.text = translator.find { it.keyword == "Category" }!!.ar
                refnumber_label.text = translator.find { it.keyword == "ReferenceNumber" }!!.ar
                subject_label.text = translator.find { it.keyword == "Subject" }!!.ar
                status_label.text = translator.find { it.keyword == "Status" }!!.ar
                fromdate_label.text = translator.find { it.keyword == "FromDate" }!!.ar
                todate_label.text = translator.find { it.keyword == "ToDate" }!!.ar
                priority_label.text = translator.find { it.keyword == "Priority" }!!.ar
                senEntity_label.text = translator.find { it.keyword == "SendingEntity" }!!.ar
                recEntity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.ar
                transfer_From_user_label.text =
                    translator.find { it.keyword == "TransferFromUser" }!!.ar
                transfer_To_user_label.text =
                    translator.find { it.keyword == "TransferToUser" }!!.ar
                transfer_From_structure_label.text =
                    translator.find { it.keyword == "TransferFromStructure" }!!.ar
                transfer_To_Structure_label.text =
                    translator.find { it.keyword == "TransferToStructure" }!!.ar
                transfer_From_date_label.text =
                    translator.find { it.keyword == "TransferFromDate" }!!.ar
                transfer_to_date_label.text =
                    translator.find { it.keyword == "TransferToDate" }!!.ar
                keyword_label.text = translator.find { it.keyword == "Keyword" }!!.ar
                OverdueCheckbox.text = translator.find { it.keyword == "OverDue" }!!.ar


                categoryAutoComplete.hint = translator.find { it.keyword == "Category" }!!.ar
                etRefNumber.hint = translator.find { it.keyword == "ReferenceNumber" }!!.ar
                searchSubject.hint = translator.find { it.keyword == "Subject" }!!.ar
                statusAutoComplete.hint = translator.find { it.keyword == "Status" }!!.ar
                Fromdate.hint = translator.find { it.keyword == "FromDate" }!!.ar
                Todate.hint = translator.find { it.keyword == "ToDate" }!!.ar
                priorityAutoComplete.hint = translator.find { it.keyword == "Priority" }!!.ar
                sendingEntityAutoComplete.hint =
                    translator.find { it.keyword == "SendingEntity" }!!.ar
                receivingEntityAutoComplete.hint =
                    translator.find { it.keyword == "ReceivingEntity" }!!.ar
                transferFromUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromUser" }!!.ar
                transferToUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferToUser" }!!.ar
                transferFromStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromStructure" }!!.ar
                transferToStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferToStructure" }!!.ar
                searchDateFrom.hint = translator.find { it.keyword == "TransferFromDate" }!!.ar
                searchDateTo.hint = translator.find { it.keyword == "TransferToDate" }!!.ar
                searchKeyword.hint = translator.find { it.keyword == "Keyword" }!!.ar


                btnReset
                centered_txt.text = translator.find { it.keyword == "Search" }!!.ar
                btnReset.text = translator.find { it.keyword == "Reset" }!!.ar
                btnSearch.text = translator.find { it.keyword == "Search" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {

                category_label.text = translator.find { it.keyword == "Category" }!!.fr
                refnumber_label.text = translator.find { it.keyword == "ReferenceNumber" }!!.fr
                subject_label.text = translator.find { it.keyword == "Subject" }!!.fr
                status_label.text = translator.find { it.keyword == "Status" }!!.fr
                fromdate_label.text = translator.find { it.keyword == "FromDate" }!!.fr
                todate_label.text = translator.find { it.keyword == "ToDate" }!!.fr
                priority_label.text = translator.find { it.keyword == "Priority" }!!.fr
                senEntity_label.text = translator.find { it.keyword == "SendingEntity" }!!.fr
                recEntity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.fr
                transfer_From_user_label.text =
                    translator.find { it.keyword == "TransferFromUser" }!!.fr
                transfer_To_user_label.text =
                    translator.find { it.keyword == "TransferToUser" }!!.fr
                transfer_From_structure_label.text =
                    translator.find { it.keyword == "TransferFromStructure" }!!.fr
                transfer_To_Structure_label.text =
                    translator.find { it.keyword == "TransferToStructure" }!!.fr
                transfer_From_date_label.text =
                    translator.find { it.keyword == "TransferFromDate" }!!.fr
                transfer_to_date_label.text =
                    translator.find { it.keyword == "TransferToDate" }!!.fr
                keyword_label.text = translator.find { it.keyword == "Keyword" }!!.fr
                OverdueCheckbox.text = translator.find { it.keyword == "OverDue" }!!.fr


                categoryAutoComplete.hint = translator.find { it.keyword == "Category" }!!.fr
                etRefNumber.hint = translator.find { it.keyword == "ReferenceNumber" }!!.fr
                searchSubject.hint = translator.find { it.keyword == "Subject" }!!.fr
                statusAutoComplete.hint = translator.find { it.keyword == "Status" }!!.fr
                Fromdate.hint = translator.find { it.keyword == "FromDate" }!!.fr
                Todate.hint = translator.find { it.keyword == "ToDate" }!!.fr
                priorityAutoComplete.hint = translator.find { it.keyword == "Priority" }!!.fr
                sendingEntityAutoComplete.hint =
                    translator.find { it.keyword == "SendingEntity" }!!.fr
                receivingEntityAutoComplete.hint =
                    translator.find { it.keyword == "ReceivingEntity" }!!.fr
                transferFromUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromUser" }!!.fr
                transferToUserAutoComplete.hint =
                    translator.find { it.keyword == "TransferToUser" }!!.fr
                transferFromStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferFromStructure" }!!.fr
                transferToStructureAutoComplete.hint =
                    translator.find { it.keyword == "TransferToStructure" }!!.fr
                searchDateFrom.hint = translator.find { it.keyword == "TransferFromDate" }!!.fr
                searchDateTo.hint = translator.find { it.keyword == "TransferToDate" }!!.fr
                searchKeyword.hint = translator.find { it.keyword == "Keyword" }!!.fr


                btnReset
                centered_txt.text = translator.find { it.keyword == "Search" }!!.fr
                btnReset.text = translator.find { it.keyword == "Reset" }!!.fr
                btnSearch.text = translator.find { it.keyword == "Search" }!!.fr
            }
        }
    }

    private fun initCategoryComplete() {
        categoryAutoComplete.threshold = 0
        val categoryAutoCompleteArray = viewModel.readCategoriesData()

        val arrayAdapter =
            CategoriesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                categoryAutoCompleteArray
            )
        categoryAutoComplete.setAdapter(arrayAdapter)




        categoryAutoComplete.setOnClickListener {
            categoryAutoComplete.showDropDown()

        }


        categoryAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                categoryAutoComplete.showDropDown()

            } else {
                categoryAutoComplete.setText("")
            }

        }



        categoryAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                categoryAutoComplete.clearFocus()
                categoryAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as CategoryResponseItem
                categoryAutoComplete.setText(selectedObject.text.toString())
                categorySelectedId = selectedObject.id!!

            }

    }

    private fun initPrioritiesComplete() {
        priorityAutoComplete.threshold = 0
        val prioritiesAutoCompleteArray = viewModel.readPrioritiesData()!!

        val arrayAdapter =
            PrioritiesStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                prioritiesAutoCompleteArray
            )
        priorityAutoComplete.setAdapter(arrayAdapter)




        priorityAutoComplete.setOnClickListener {
            priorityAutoComplete.showDropDown()

        }


        priorityAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                priorityAutoComplete.showDropDown()

            } else {
                priorityAutoComplete.setText("")
            }

        }



        priorityAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                priorityAutoComplete.clearFocus()
                priorityAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as PrioritiesResponseItem
                priorityAutoComplete.setText(selectedObject.text.toString())
                prioritySelectedId = selectedObject.id!!

            }

    }

    private fun initStatuesComplete() {
        statusAutoComplete.threshold = 0
        val statusAutoCompleteArray = viewModel.readStatuses()
        statusAutoCompleteArray.removeAt(0)

        val arrayAdapter =
            statusStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                statusAutoCompleteArray
            )
        statusAutoComplete.setAdapter(arrayAdapter)




        statusAutoComplete.setOnClickListener {
            statusAutoComplete.showDropDown()

        }


        statusAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                statusAutoComplete.showDropDown()

            } else {
                statusAutoComplete.setText("")
            }

        }



        statusAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                statusAutoComplete.clearFocus()
                statusAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as StatusesResponseItem
                statusAutoComplete.setText(selectedObject.text.toString())
                statusSelectedId = selectedObject.id!!

            }

    }

    private fun initTransferFromUserAutoComplete() {
        transferFromUserAutoComplete.threshold = 0
        val filteredUserArray = ArrayList<AllStructuresUsersItem>()

        val usersArray = viewModel.readAllStructureData().users


        for (item in usersArray!!){

          val result = filteredUserArray.find { it.fullName == item.fullName }
          if (result.toString() == "null"){
              filteredUserArray.add(item)
          }

        }



        var arrayAdapter =
            UserssAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                filteredUserArray
            )

        transferFromUserAutoComplete.setAdapter(arrayAdapter)




        transferFromUserAutoComplete.setOnClickListener {
            transferFromUserAutoComplete.showDropDown()

        }




        transferFromUserAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                transferFromUserAutoComplete.showDropDown()

            } else {
                transferFromUserAutoComplete.setText("")
            }

        }



        transferFromUserAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                transferFromUserAutoComplete.clearFocus()
                transferFromUserAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresUsersItem
                transferFromUserAutoComplete.setText(selectedObject.fullName.toString())
                transferFromUsersSelectedId = selectedObject.id!!

            }


        transferFromUserAutoComplete.doOnTextChanged { text, start, before, count ->
            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(),structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            val allUsers = ArrayList<AllStructuresUsersItem>()
                            if (it.users != null) {
                                allUsers.addAll(it.users)
                            }
                            val allUsersFiltered = ArrayList<AllStructuresUsersItem>()

                            for (item in allUsers){

                                val result = allUsersFiltered.find { it.fullName == item.fullName }
                                if (result.toString() == "null"){
                                    allUsersFiltered.add(item)
                                }

                            }

                            arrayAdapter =
                                UserssAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersFiltered
                                )
                            transferFromUserAutoComplete.setAdapter(arrayAdapter)
                            if (transferFromUserAutoComplete.hasFocus()) {
                                transferFromUserAutoComplete.showDropDown()
                            } else {
                                transferFromUserAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )



            transferFromUserAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    transferFromUserAutoComplete.showDropDown()

                } else {
                    transferFromUserAutoComplete.setText("")
                }

            }



            transferFromUserAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    transferFromUserAutoComplete.clearFocus()
                    transferFromUserAutoComplete.dismissDropDown()
                    val selectedObject =
                        parent!!.getItemAtPosition(position) as AllStructuresUsersItem
                    transferFromUserAutoComplete.setText(selectedObject.fullName.toString())
                    transferFromUsersSelectedId = selectedObject.id!!

                }

        }


    }

    private fun initTransferToUserAutoComplete() {
        transferToUserAutoComplete.threshold = 0

        val usersArray = viewModel.readAllStructureData().users
        val filteredUserArray = ArrayList<AllStructuresUsersItem>()

        for (item in usersArray!!){

            val result = filteredUserArray.find { it.fullName == item.fullName }
            if (result.toString() == "null"){
                filteredUserArray.add(item)
            }

        }

        var arrayAdapter =
            UserssAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                filteredUserArray
            )


        transferToUserAutoComplete.setAdapter(arrayAdapter)




        transferToUserAutoComplete.setOnClickListener {
            transferToUserAutoComplete.showDropDown()

        }



        transferToUserAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                transferToUserAutoComplete.showDropDown()

            } else {
                transferToUserAutoComplete.setText("")
            }

        }



        transferToUserAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                transferToUserAutoComplete.clearFocus()
                transferToUserAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresUsersItem
                transferToUserAutoComplete.setText(selectedObject.fullName.toString())
                transferToUsersSelectedId = selectedObject.id!!

            }


        transferToUserAutoComplete.doOnTextChanged { text, start, before, count ->
            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(),structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            val allUsers = ArrayList<AllStructuresUsersItem>()
                            if (it.users != null) {
                                allUsers.addAll(it.users)
                            }

                            val allUsersFiltered = ArrayList<AllStructuresUsersItem>()

                            for (item in allUsers){

                                val result = allUsersFiltered.find { it.fullName == item.fullName }
                                if (result.toString() == "null"){
                                    allUsersFiltered.add(item)
                                }

                            }
                            arrayAdapter =
                                UserssAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersFiltered
                                )
                            transferToUserAutoComplete.setAdapter(arrayAdapter)
                            if (transferToUserAutoComplete.hasFocus()) {
                                transferToUserAutoComplete.showDropDown()
                            } else {
                                transferToUserAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )



            transferToUserAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    transferToUserAutoComplete.showDropDown()

                } else {
                    transferToUserAutoComplete.setText("")
                }

            }




            transferToUserAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    transferToUserAutoComplete.clearFocus()
                    transferToUserAutoComplete.dismissDropDown()
                    val selectedObject =
                        parent!!.getItemAtPosition(position) as AllStructuresUsersItem
                    transferToUserAutoComplete.setText(selectedObject.fullName.toString())
                    transferToUsersSelectedId = selectedObject.id!!

                }


        }
    }


    private fun initTransferFromStructuresAutoComplete() {
        transferFromStructureAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()
            )

        transferFromStructureAutoComplete.setAdapter(arrayAdapter)




        transferFromStructureAutoComplete.setOnClickListener {
            transferFromStructureAutoComplete.showDropDown()

        }


        transferFromStructureAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                transferFromStructureAutoComplete.showDropDown()

            } else {
                transferFromStructureAutoComplete.setText("")
            }

        }



        transferFromStructureAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                transferFromStructureAutoComplete.clearFocus()
                transferFromStructureAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                transferFromStructureAutoComplete.setText(selectedObject.name.toString())
                transferFromStructureSelectedId = selectedObject.id!!

            }


        transferFromStructureAutoComplete.doOnTextChanged { text, start, before, count ->
            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(), structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }



                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            transferFromStructureAutoComplete.setAdapter(arrayAdapter)
                            if (transferFromStructureAutoComplete.hasFocus()) {
                                transferFromStructureAutoComplete.showDropDown()
                            } else {
                                transferFromStructureAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )






            transferFromStructureAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    transferFromStructureAutoComplete.showDropDown()

                } else {
                    transferFromStructureAutoComplete.setText("")
                }

            }



            transferFromStructureAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    transferFromStructureAutoComplete.clearFocus()
                    transferFromStructureAutoComplete.dismissDropDown()
                    val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                    transferFromStructureAutoComplete.setText(selectedObject.name.toString())
                    sendingEntitySelectedId = selectedObject.id!!

                }


        }

    }

    private fun initTransferToStructuresAutoComplete() {
        transferToStructureAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()

            )

        transferToStructureAutoComplete.setAdapter(arrayAdapter)




        transferToStructureAutoComplete.setOnClickListener {
            transferToStructureAutoComplete.showDropDown()

        }



        transferToStructureAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                transferToStructureAutoComplete.showDropDown()

            } else {
                transferToStructureAutoComplete.setText("")
            }

        }



        transferToStructureAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                transferToStructureAutoComplete.clearFocus()
                transferToStructureAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                transferToStructureAutoComplete.setText(selectedObject.name.toString())
                transferToStructureSelectedId = selectedObject.id!!

            }

        transferToStructureAutoComplete.doOnTextChanged { text, start, before, count ->
            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(),structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }



                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            transferToStructureAutoComplete.setAdapter(arrayAdapter)
                            if (transferToStructureAutoComplete.hasFocus()) {
                                transferToStructureAutoComplete.showDropDown()
                            } else {
                                transferToStructureAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )






            transferToStructureAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    transferToStructureAutoComplete.showDropDown()

                } else {
                    transferToStructureAutoComplete.setText("")
                }

            }



            transferToStructureAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    transferToStructureAutoComplete.clearFocus()
                    transferToStructureAutoComplete.dismissDropDown()
                    val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                    transferToStructureAutoComplete.setText(selectedObject.name.toString())
                    sendingEntitySelectedId = selectedObject.id!!

                }


        }

    }


    private fun initSendingEntityAutoComplete() {
        sendingEntityAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()

            )

        sendingEntityAutoComplete.setAdapter(arrayAdapter)




        sendingEntityAutoComplete.setOnClickListener {
            sendingEntityAutoComplete.showDropDown()

        }




        sendingEntityAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                sendingEntityAutoComplete.showDropDown()

            } else {
                sendingEntityAutoComplete.setText("")
            }

        }



        sendingEntityAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                sendingEntityAutoComplete.clearFocus()
                sendingEntityAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                sendingEntityAutoComplete.setText(selectedObject.name.toString())
                sendingEntitySelectedId = selectedObject.id!!

            }






        sendingEntityAutoComplete.doOnTextChanged { text, start, before, count ->
            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(),structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }



                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            sendingEntityAutoComplete.setAdapter(arrayAdapter)
                            if (sendingEntityAutoComplete.hasFocus()) {
                                sendingEntityAutoComplete.showDropDown()
                            } else {
                                sendingEntityAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )




            sendingEntityAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    sendingEntityAutoComplete.showDropDown()

                } else {
                    sendingEntityAutoComplete.setText("")
                }

            }



            sendingEntityAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    sendingEntityAutoComplete.clearFocus()
                    sendingEntityAutoComplete.dismissDropDown()
                    val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                    sendingEntityAutoComplete.setText(selectedObject.name.toString())
                    sendingEntitySelectedId = selectedObject.id!!

                }


        }

    }

    private fun initReceivingEntityAutoComplete() {
        receivingEntityAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()

            )

        receivingEntityAutoComplete.setAdapter(arrayAdapter)




        receivingEntityAutoComplete.setOnClickListener {
            receivingEntityAutoComplete.showDropDown()

        }


        receivingEntityAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                receivingEntityAutoComplete.showDropDown()

            } else {
                receivingEntityAutoComplete.setText("")
            }

        }



        receivingEntityAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                receivingEntityAutoComplete.clearFocus()
                receivingEntityAutoComplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                receivingEntityAutoComplete.setText(selectedObject.name.toString())
                receivingEntitySelectedId = selectedObject.id!!

            }



        receivingEntityAutoComplete.doOnTextChanged { text, start, before, count ->

            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(),structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }



                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            receivingEntityAutoComplete.setAdapter(arrayAdapter)
                            if (receivingEntityAutoComplete.hasFocus()) {
                                receivingEntityAutoComplete.showDropDown()
                            } else {
                                receivingEntityAutoComplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )






            receivingEntityAutoComplete.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    requireActivity().hideKeyboard(requireActivity())
                    receivingEntityAutoComplete.showDropDown()

                } else {
                    receivingEntityAutoComplete.setText("")
                }

            }



            receivingEntityAutoComplete.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    requireActivity().hideKeyboard(requireActivity())
                    receivingEntityAutoComplete.clearFocus()
                    receivingEntityAutoComplete.dismissDropDown()
                    val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                    receivingEntityAutoComplete.setText(selectedObject.name.toString())
                    sendingEntitySelectedId = selectedObject.id!!

                }


        }

    }


    private fun initTransferDateFrom() {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        transferDateFrom = requireActivity().findViewById<View>(R.id.searchDateFrom) as EditText?
        transferDateTo = requireActivity().findViewById<View>(R.id.searchDateTo) as EditText?

        transferFromDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    transferFromSelectedDate =
                        (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", transferFromSelectedDate)!!
                    transferDateFrom!!.setText(drString)
                    initTransferDateTo(drString)
                    transferDateFrom!!.clearFocus()


                    transferFromDatePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        transferFromDatePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), transferFromDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), transferFromDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }

//        transferFromDatePickerDialog!!.datePicker.minDate = cal.timeInMillis


        transferFromDatePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                transferDateFrom!!.setText("")

            })


        transferDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                transferFromDatePickerDialog!!.show()
            }
        }

        transferDateFrom!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            transferFromDatePickerDialog!!.show()
        }
    }

    private fun initTransferDateTo(drStringg: String) {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)


        transferToDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    transferToSelectedDate =
                        (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", transferToSelectedDate)!!
                    transferDateTo!!.setText(drString)
                    transferDateTo!!.clearFocus()


                    transferToDatePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        transferToDatePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), transferToDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), transferToDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }

//
//        transferToDatePickerDialog!!.datePicker.maxDate = cal.timeInMillis


        val minDate: Date = sdf.parse(drStringg)
        val minCalendar = Calendar.getInstance()
        minCalendar.time = minDate
        transferToDatePickerDialog!!.datePicker.minDate = minCalendar.timeInMillis



        transferToDatePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                transferDateTo!!.setText("")

            })


        transferDateTo!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                transferToDatePickerDialog!!.show()
            }
        }

        transferDateTo!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            transferToDatePickerDialog!!.show()
        }
    }


    private fun initDateFrom() {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.Fromdate) as EditText?
        etDateTo = requireActivity().findViewById<View>(R.id.Todate) as EditText?

        fromDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    fromSelectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", fromSelectedDate)!!
                    etDateFrom!!.setText(drString)
                    initDateTo(drString)
                    etDateFrom!!.clearFocus()


                    fromDatePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        fromDatePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), fromDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), fromDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }


//        fromDatePickerDialog!!.datePicker.minDate = cal.timeInMillis


        fromDatePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateFrom!!.setText("")

            })


        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                fromDatePickerDialog!!.show()
            }
        }

        etDateFrom!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            fromDatePickerDialog!!.show()
        }


    }

    private fun initDateTo(drString: String) {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

//        etDateTo = requireActivity().findViewById<View>(R.id.Todate) as EditText?

        toDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    toSelectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", toSelectedDate)!!
                    etDateTo!!.setText(drString)
                    etDateTo!!.clearFocus()


                    toDatePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        toDatePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), toDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), toDateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }

//        toDatePickerDialog!!.datePicker.maxDate = cal.timeInMillis


        val minDate: Date = sdf.parse(drString)
        val minCalendar = Calendar.getInstance()
        minCalendar.time = minDate
        toDatePickerDialog!!.datePicker.minDate = minCalendar.timeInMillis



        toDatePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateTo!!.setText("")

            })


        etDateTo!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                toDatePickerDialog!!.show()
            }
        }

        etDateTo!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            toDatePickerDialog!!.show()
        }
    }


    @Throws(Exception::class)
    fun changeDateFormat(input: String?, output: String?, dateString: String?): String? {
        val inputDate: DateFormat = SimpleDateFormat(input, Locale.US)
        val outputDate: DateFormat = SimpleDateFormat(output, Locale.US)
        val date = inputDate.parse(dateString!!)
        return outputDate.format(date!!)
    }


    private fun emptyForm() {
//        typeOfSearch = 0

        prioritySelectedId = 0
        statusSelectedId = 0
        categorySelectedId = 0
        transferFromStructureSelectedId = 0
        transferToStructureSelectedId = 0

        transferFromUsersSelectedId = 0
        transferToUsersSelectedId = 0

        sendingEntitySelectedId = 0
        receivingEntitySelectedId = 0

        categoryAutoComplete.clearFocus()
        statusAutoComplete.clearFocus()
        priorityAutoComplete.clearFocus()
        sendingEntityAutoComplete.clearFocus()
        receivingEntityAutoComplete.clearFocus()
        transferFromUserAutoComplete.clearFocus()
        transferToUserAutoComplete.clearFocus()
        transferFromStructureAutoComplete.clearFocus()
        transferToStructureAutoComplete.clearFocus()


        categoryAutoComplete.setText("")
        etRefNumber.setText("")
        searchSubject.setText("")
        statusAutoComplete.setText("")
        Fromdate.setText("")
        Todate.setText("")
        priorityAutoComplete.setText("")
        sendingEntityAutoComplete.setText("")
        receivingEntityAutoComplete.setText("")
        transferFromUserAutoComplete.setText("")
        transferToUserAutoComplete.setText("")
        transferFromStructureAutoComplete.setText("")
        transferToStructureAutoComplete.setText("")
        searchDateFrom.setText("")
        searchDateTo.setText("")
        searchKeyword.setText("")
        OverdueCheckbox.isChecked = false


    }

    private fun prepareModel() {


        if (categorySelectedId != 0) {
            addedModel.category = categorySelectedId.toString()
        }
        if (etRefNumber.text.toString().isNotEmpty()) {
            addedModel.referenceNumber = etRefNumber.text.toString()
        }
        if (statusSelectedId != 0) {
            addedModel.status = statusSelectedId.toString()
        }
        if (searchSubject.text.toString().isNotEmpty()) {
            addedModel.subject = searchSubject.text.toString().trim()
        }

        if (etDateFrom!!.text.toString().isNotEmpty()) {
            addedModel.fromDate = etDateFrom!!.text.toString().trim()
        }
        if (etDateTo!!.text.toString().isNotEmpty()) {
            addedModel.toDate = etDateTo!!.text.toString().trim()
        }

        if (prioritySelectedId != 0) {
            addedModel.priority = prioritySelectedId.toString()
        }

        if (sendingEntitySelectedId != 0) {
            addedModel.documentSender = sendingEntitySelectedId.toString()
        }

        if (receivingEntitySelectedId != 0) {
            addedModel.documentReceiver = receivingEntitySelectedId.toString()
        }


        if (transferFromUsersSelectedId != 0) {
            addedModel.fromUser = transferFromUsersSelectedId.toString()
        }

        if (transferToUsersSelectedId != 0) {
            addedModel.toUser = transferToUsersSelectedId.toString()
        }


        if (transferFromStructureSelectedId != 0) {
            addedModel.fromStructure = transferFromStructureSelectedId.toString()
        }
        if (transferToStructureSelectedId != 0) {
            addedModel.toStructure = transferToStructureSelectedId.toString()
        }

        if (transferDateFrom!!.text.toString().isNotEmpty()) {
            addedModel.fromTransferDate = transferDateFrom!!.text.toString().trim()
        }
        if (transferDateTo!!.text.toString().isNotEmpty()) {
            addedModel.toTransferDate = transferDateTo!!.text.toString().trim()
        }

        if (searchKeyword.text.toString().isNotEmpty()) {
            addedModel.keyword = searchKeyword.text.toString().trim()
        }

        if (OverdueCheckbox.isChecked) {
            addedModel.isOverdue = true
        }


        val bundle = Bundle()
        bundle.putSerializable(Constants.SEARCH_MODEL, addedModel)
        if (typeOfSearch == 1) {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AdvancedSearchResultFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.SEARCH_MODEL, addedModel),
                            Pair(Constants.SEARCH_TYPE, typeOfSearch)

                        )
                    }
                )
                addToBackStack("")

            }
        } else {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(
                    R.id.fragmentContainer,
                    LinkedCorrespondenceResultFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.SEARCH_MODEL, addedModel),
                            Pair(Constants.SEARCH_TYPE, typeOfSearch),
                            Pair(Constants.DOCUMENT_ID, DocumentId),
                            Pair(Constants.TRANSFER_ID, TransferId),

                            )

                    }, "advancedSearch"
                )
                addToBackStack("advancedSearch")

            }
        }
         //   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        emptyForm()

    }


}