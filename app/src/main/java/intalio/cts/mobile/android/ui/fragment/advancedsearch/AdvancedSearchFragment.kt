package intalio.cts.mobile.android.ui.fragment.advancedsearch

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.*
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceResultFragment
import intalio.cts.mobile.android.util.*

import kotlinx.android.synthetic.main.fragment_advancedsearch.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class AdvancedSearchFragment : Fragment() {

    private var DocumentId:Int = 0
    private var TransferId:Int = 0

    private var purposeSelectedId = 0
    private var categorySelectedId = 0
    private var fromStructureSelectedId = 0
    private var toStructureSelectedId = 0

    private var typeOfSearch = 0

    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null
    private var fromDateListener: OnDateSetListener? = null
    private var toDateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null
    private var etDateTo: EditText? = null

    private var fromSelectedDate: String? = null
    private var toSelectedDate: String? = null
    private var fromDateSelectedInDays = 0
    private var toDateSelectedInDays = 0





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

        centered_txt.text = requireActivity().getText(R.string.advanced_search)

        arguments?.getInt(Constants.SEARCH_TYPE).let {
            typeOfSearch = it!!
            if (it == 2){
                requireArguments().getInt(Constants.DOCUMENT_ID).let {
                    DocumentId = it
                }


                requireArguments().getInt(Constants.TRANSFER_ID).let {
                    TransferId = it
                }
            }
        }
//        val result = arguments?.getSerializable(Constants.Correspondence_Model)
//        if (result.toString() != "null"){
//            model = result as CorrespondenceDataItem
//
//        }


        initPurposeComplete()
        initCategoryComplete()
        initDateFrom()
        initDateTo()
        initFromStructuresAutoComplete()
        initToStructuresAutoComplete()


        btnSearch.setOnClickListener {
            if (searchSubject.text.toString().trim() == "" &&etRefNumber.text.toString().trim() == "" &&
                categoryAutoComplete.text.toString().trim() == ""&&TransferFromAutoComplete.text.toString().trim() == ""&&
                TransferToAutoComplete.text.toString().trim() == ""&&PurposeAutoComplete.text.toString().trim() == ""&&
                searchDateFrom.text.toString().trim() == ""&&searchDateTo.text.toString().trim() == "")
            {

                requireActivity().makeToast(getString(R.string.fill_search_criteria))
            }else{
                prepareModel()
            }



        }

}


    private fun initPurposeComplete() {
         PurposeAutoComplete.threshold = 0
        val purposesAutoCompleteArray = viewModel.readPurposesData()!!

        val arrayAdapter =
            PurposesStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                purposesAutoCompleteArray
            )
         PurposeAutoComplete.setAdapter(arrayAdapter)




         PurposeAutoComplete.setOnClickListener {
             PurposeAutoComplete.showDropDown()

        }


         PurposeAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                PurposeAutoComplete.showDropDown()

            } else {
                PurposeAutoComplete.setText("")
            }

        }



         PurposeAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                PurposeAutoComplete.clearFocus()
                PurposeAutoComplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as PurposesResponseItem
                PurposeAutoComplete.setText(selectedObject.text.toString())
                purposeSelectedId = selectedObject.id!!

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
                val selectedObject= parent!!.getItemAtPosition(position) as CategoryResponseItem
                categoryAutoComplete.setText(selectedObject.text.toString())
                categorySelectedId = selectedObject.id!!

            }

    }

    private fun initFromStructuresAutoComplete() {
        TransferFromAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures




        val arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray
            )

        TransferFromAutoComplete.setAdapter(arrayAdapter)




        TransferFromAutoComplete.setOnClickListener {
            TransferFromAutoComplete.showDropDown()

        }


        TransferFromAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                TransferFromAutoComplete.showDropDown()

            } else {
                TransferFromAutoComplete.setText("")
            }

        }



        TransferFromAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                TransferFromAutoComplete.clearFocus()
                TransferFromAutoComplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as AllStructuresItem
                TransferFromAutoComplete.setText(selectedObject.name.toString())
                fromStructureSelectedId = selectedObject.id!!

            }

    }

    private fun initToStructuresAutoComplete() {
        TransferToAutoComplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures




        val arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray
            )

        TransferToAutoComplete.setAdapter(arrayAdapter)




        TransferToAutoComplete.setOnClickListener {
            TransferToAutoComplete.showDropDown()

        }


        TransferToAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                TransferToAutoComplete.showDropDown()

            } else {
                TransferToAutoComplete.setText("")
            }

        }



        TransferToAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                TransferToAutoComplete.clearFocus()
                TransferToAutoComplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as AllStructuresItem
                TransferToAutoComplete.setText(selectedObject.name.toString())
                toStructureSelectedId = selectedObject.id!!

            }

    }



    private fun initDateFrom() {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.searchDateFrom) as EditText?

        fromDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    fromSelectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String = changeDateFormat("mm/dd/yyyy", "yyyy_mm_dd", fromSelectedDate)!!
                    fromDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                    etDateFrom!!.setText(drString)
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

        fromDatePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
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
    private fun initDateTo() {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateTo = requireActivity().findViewById<View>(R.id.searchDateTo) as EditText?

        toDateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    toSelectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String = changeDateFormat("mm/dd/yyyy", "yyyy_mm_dd", toSelectedDate)!!
                    toDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
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
        val inputDate: DateFormat = SimpleDateFormat(input,Locale.US)
        val outputDate: DateFormat = SimpleDateFormat(output,Locale.US)
        val date = inputDate.parse(dateString!!)
        return outputDate.format(date!!)
    }


    private fun emptyForm() {

        searchSubject.setText("")
        etRefNumber.setText("")
        categoryAutoComplete.setText("")
        TransferFromAutoComplete.setText("")
        TransferToAutoComplete.setText("")
        PurposeAutoComplete.setText("")
        searchDateFrom.setText("")
        searchDateTo.setText("")




    }

    private fun prepareModel(){

        val addedModel = AdvancedSearchRequest()
        if (searchSubject.text.toString().isNotEmpty()){
            addedModel.subject = searchSubject.text.toString().trim()
        }
        if (etRefNumber.text.toString().isNotEmpty()){
            addedModel.referenceNumber = etRefNumber.text.toString()
        }
        if (fromStructureSelectedId != 0){
            addedModel.fromStructure = fromStructureSelectedId.toString()
        }
        if (toStructureSelectedId != 0){
            addedModel.toStructure = toStructureSelectedId.toString()
        }

        if (categorySelectedId != 0){
            addedModel.category = categorySelectedId.toString()
        }
//        if (purposeSelectedId != 0){
//            addedModel.purposeId = purposeSelectedId.toString()
//        }
        if (etDateFrom!!.text.toString().isNotEmpty()){
            addedModel.fromDate = etDateFrom!!.text.toString().trim()
        }
        if (etDateTo!!.text.toString().isNotEmpty()){
            addedModel.toDate = etDateTo!!.text.toString().trim()
        }

        val bundle = Bundle()
        bundle.putSerializable(Constants.SEARCH_MODEL,addedModel)
        if (typeOfSearch == 1){
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
        }else{
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    LinkedCorrespondenceResultFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.SEARCH_MODEL, addedModel),
                            Pair(Constants.SEARCH_TYPE, typeOfSearch),
                            Pair(Constants.DOCUMENT_ID,DocumentId),
                            Pair(Constants.TRANSFER_ID,TransferId),

                        )

                    },"advancedSearch"
                )
               addToBackStack("advancedSearch")

            }
        }

        emptyForm()

    }


}