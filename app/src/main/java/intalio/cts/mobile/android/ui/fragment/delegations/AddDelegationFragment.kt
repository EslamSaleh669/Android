package intalio.cts.mobile.android.ui.fragment.delegations

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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.CategoryResponseItem
import intalio.cts.mobile.android.data.network.response.DelegationDataItem
import intalio.cts.mobile.android.data.network.response.UsersStructureItem
import intalio.cts.mobile.android.ui.adapter.AddedCategoriesAdapter
import intalio.cts.mobile.android.ui.adapter.CategoriesAdapter
import intalio.cts.mobile.android.ui.adapter.UsresStructureAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.add_delegation.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import kotlinx.android.synthetic.main.fragment_addnote.*
import kotlinx.android.synthetic.main.fragment_addnote.btnCancelnote
import kotlinx.android.synthetic.main.fragment_addnote.btnaddnote
import kotlinx.android.synthetic.main.fragment_addnote.note_label
import kotlinx.android.synthetic.main.fragment_addnote.privacycheckbox
import kotlinx.android.synthetic.main.fragment_addtransfer.*
import kotlinx.android.synthetic.main.toolbar_layout.centered_txt


class AddDelegationFragment : Fragment(),AddedCategoriesAdapter.OnDeleteClicked {

    private var userSelectedId = 0
    private lateinit var editedModel: DelegationDataItem

    private var datePickerDialog: DatePickerDialog? = null
    private var dateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null
    private var etDateTo: EditText? = null

    private var isAboveDateClicked = false
    private var selectedDate: String? = null
    private var fromDateSelectedInDays = 0
    private var toDateSelectedInDays = 0

    private var fromEditedDateSelectedInDays = 0
    private var toEditedDateSelectedInDays = 0
    private var addedCategoriesIds = ArrayList<Int>()
    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>





    @Inject
    @field:Named("delegations")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: DelegationsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DelegationsViewModel::class.java)
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
        return inflater.inflate(R.layout.add_delegation, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        translator = viewModel.readDictionary()!!.data!!
        setLabels()

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        btnCancelDelegate.setOnClickListener {
            activity?.onBackPressed()
        }

        var requiredFields = ""
        var edit = ""

        when {
            viewModel.readLanguage() == "en" -> {

                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.en!!
                edit = translator.find { it.keyword == "Edit" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.ar!!
                edit = translator.find { it.keyword == "Edit" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {
                requiredFields = translator.find { it.keyword == "RequiredFields" }!!.fr!!
                edit = translator.find { it.keyword == "Edit" }!!.fr!!

            }
        }



        val result = arguments?.getSerializable(Constants.Delegation_Model)
        if (result.toString() == "null"){


            initDates()
            initUserAutoComplete("add")
            initCategoriesAutoComplete("add")

        }else{
            editedModel = (result as? DelegationDataItem)!!
            centered_txt.text = edit
            initEditedDates()
            initUserAutoComplete("edit")
            initCategoriesAutoComplete("edit")
        }






        btnSaveDelegate.setOnClickListener {

            if (userSelectedId == 0 || etDateFrom!!.text.toString() == ""
                || etDateTo!!.text.toString() == "" || addedCategoriesIds.size == 0){

                requireActivity().makeToast(requiredFields)

            }else{
                dialog = requireContext().launchLoadingDialog()

                if (result.toString() == "null"){
                    saveDelegation("add")

                }else{
                    saveDelegation("edit")

                }
            }
        }

}


    private fun initUserAutoComplete(state: String) {
        userautocomopletetextview.threshold = 0
        val userAutoCompleteArray = viewModel.readUsersStructureData()!!

        val arrayAdapter =
            UsresStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                userAutoCompleteArray
            )
        userautocomopletetextview.setAdapter(arrayAdapter)

        if (state == "edit"){
            userautocomopletetextview!!.setText(editedModel.toUser.toString(), TextView.BufferType.EDITABLE)
            userSelectedId = editedModel.toUserValueText!!.id!!

        }


        userautocomopletetextview.setOnClickListener {
            userautocomopletetextview.showDropDown()

        }


        userautocomopletetextview.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                userautocomopletetextview.showDropDown()

            } else {
                userautocomopletetextview.setText("")
            }

        }



        userautocomopletetextview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                userautocomopletetextview.clearFocus()
                userautocomopletetextview.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as UsersStructureItem
                userautocomopletetextview.setText(selectedObject.fullName.toString())
                userSelectedId = selectedObject.id!!

            }

    }

    private fun initCategoriesAutoComplete(state: String) {

        autoCompletecategories.threshold = 0
        val categoriesArray = viewModel.readCategoriesData()!!
        val editedCategories = ArrayList<CategoryResponseItem>()



        val arrayAdapter =
            CategoriesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                categoriesArray
            )
        autoCompletecategories.setAdapter(arrayAdapter)



        multiSelectedcategory.adapter =  AddedCategoriesAdapter(requireActivity(),
            arrayListOf(),this)
        multiSelectedcategory.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        if (state == "edit"){

            editedModel.categoryIds.let {

                for (id in it!!){
                    for (obj in categoriesArray){
                        if (id == obj.id){


                                (multiSelectedcategory.adapter as AddedCategoriesAdapter).addCategories(obj)
                                 addedCategoriesIds.add(obj.id!!)

                        }
                    }
                }

            }


        }




        autoCompletecategories.setOnClickListener {
            autoCompletecategories.showDropDown()

        }


        autoCompletecategories.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                autoCompletecategories.showDropDown()

            } else {
                autoCompletecategories.setText("")
            }

        }

        autoCompletecategories.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                userautocomopletetextview.clearFocus()
                userautocomopletetextview.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as CategoryResponseItem

                 autoCompletecategories.setText("")
                (multiSelectedcategory.adapter as AddedCategoriesAdapter).addCategories(selectedObject)

                if (!addedCategoriesIds.contains(selectedObject.id)){
                    addedCategoriesIds.add(selectedObject.id!!)

                }
            }
    }

    private fun initDates() {

        var wrongDateLess = ""
        var wrongDateGreater= ""

        when {
            viewModel.readLanguage() == "en" -> {

                wrongDateLess = "Cannot select start date less than today"
                wrongDateGreater = "Cannot select start date greater than end date"
            }
            viewModel.readLanguage() == "ar" -> {
                wrongDateLess = "لا يمكن إختيار تاريخ أصغر من تاريخ اليوم"
                wrongDateGreater = "لا يمكن إختيار تاريخ أكبر من تاريخ الإنتهاء"

            }
            viewModel.readLanguage() == "fr" -> {
                wrongDateLess = "Impossible de sélectionner une date de début antérieure à aujourd'hui"
                wrongDateGreater = "Impossible de sélectionner une date de début supérieure à la date de fin"
            }
        }

        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.delegateFrom) as EditText?
        etDateTo = requireActivity().findViewById<View>(R.id.delegateTo) as EditText?

        dateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    selectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", selectedDate)!!

                    if (isAboveDateClicked) {
                        fromDateSelectedInDays =
                            (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                        if (fromDateSelectedInDays < getToday()) {

                            etDateFrom!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(wrongDateLess)
                        } else {
                            val toField = etDateTo!!.text.toString()
                            if (toField != "" && fromDateSelectedInDays <= toDateSelectedInDays || toField == "") {
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()
                            } else {
                                etDateFrom!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongDateGreater)
                            }
                        }
                    } else {
                        toDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                        val fromfield = etDateFrom!!.text.toString()
                        if (fromfield != "") {
                            if (toDateSelectedInDays < fromDateSelectedInDays) {
                                etDateTo!!.setText("")
                               // Utility.dateForDialog = fromfield
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongDateLess)

                            } else {

                                etDateTo!!.setText(drString)

                                etDateTo!!.clearFocus()
                            }
                        } else if (toDateSelectedInDays < getToday()) {
                            etDateTo!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(wrongDateLess)

                        } else {
                            if (fromfield != "" && fromDateSelectedInDays <= toDateSelectedInDays || fromfield == "") {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            } else {
                                etDateTo!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongDateLess)

                            }
                        }
                    }
                    datePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        datePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), dateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), dateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }



        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())

                if (isAboveDateClicked) {
                    etDateFrom!!.setText("")
                } else {
                    etDateTo!!.setText("")
                }
            })

        datePickerDialog!!.datePicker.minDate = cal.timeInMillis


        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateFrom!!.setText("")

            })




        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                isAboveDateClicked = true
                datePickerDialog!!.show()

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

                val minDate: Date = sdf.parse(etDateFrom!!.text.toString())
                val minCalendar = Calendar.getInstance()
                minCalendar.time = minDate
                datePickerDialog!!.datePicker.minDate = minCalendar.timeInMillis


            }
        }

        etDateFrom!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            isAboveDateClicked = true
            datePickerDialog!!.show()
        }


        etDateTo!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                isAboveDateClicked = false
                datePickerDialog!!.show()
            }
        }

        etDateTo!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            isAboveDateClicked = false
            datePickerDialog!!.show()
        }


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEditedDates() {


        var wrongCashedDateLess = ""
        var wrongCashedDateGreater= ""

        when {
            viewModel.readLanguage() == "en" -> {

                wrongCashedDateLess = "Cannot select end date less than the old selected one"
                wrongCashedDateGreater = "Cannot select start date greater than end date"
            }
            viewModel.readLanguage() == "ar" -> {
                wrongCashedDateLess = "لا يمكن إختيار تاريخ أصغر من تاريخ اليوم"
                wrongCashedDateGreater = "لا يمكن تحديد تاريخ انتهاء أقل من تاريخ البدء"

            }
            viewModel.readLanguage() == "fr" -> {
                wrongCashedDateLess = "Impossible de sélectionner une date de début antérieure à l'ancienne sélectionnée"
                wrongCashedDateGreater = "Impossible de sélectionner une date de début supérieure à la date de fin"
            }
        }
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.delegateFrom) as EditText?
        etDateTo = requireActivity().findViewById<View>(R.id.delegateTo) as EditText?

        etDateFrom!!.setText(editedModel.fromDate.toString(), TextView.BufferType.EDITABLE)
        etDateTo!!.setText(editedModel.toDate.toString(), TextView.BufferType.EDITABLE)

        val cashedFromString:String =
            changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", etDateFrom!!.text.toString())!!



        dateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    selectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", selectedDate)!!

                    if (isAboveDateClicked) {
                        fromDateSelectedInDays =
                            (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                        if (fromDateSelectedInDays < getToday()) {

                            etDateFrom!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(wrongCashedDateLess)
                        } else {
                            val toField = etDateTo!!.text.toString()
                            if (toField != "" && fromDateSelectedInDays <= toDateSelectedInDays || toField == "") {
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()
                            } else {
                                etDateFrom!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongCashedDateGreater)
                            }
                        }
                    } else {
                        toDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                        val fromfield = etDateFrom!!.text.toString()
                        if (fromfield != "") {
                            if (toDateSelectedInDays < fromDateSelectedInDays) {
                                etDateTo!!.setText("")
                                // Utility.dateForDialog = fromfield
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongCashedDateLess)

                            } else {

                                etDateTo!!.setText(drString)

                                etDateTo!!.clearFocus()
                            }
                        } else if (toDateSelectedInDays < getToday()) {
                            etDateTo!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(wrongCashedDateLess)

                        } else {
                            if (fromfield != "" && fromDateSelectedInDays <= toDateSelectedInDays || fromfield == "") {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            } else {
                                etDateTo!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(wrongCashedDateLess)

                            }
                        }
                    }
                    datePickerDialog!!.updateDate(
                        cal[Calendar.YEAR],
                        cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                    )
                } catch (e: Exception) {
                }
            }

        datePickerDialog = if (density >= 3.0) {
            DatePickerDialog(
                requireContext(), dateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        } else {
            DatePickerDialog(
                requireContext(), dateListener,
                cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
            )
        }



        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())

                if (isAboveDateClicked) {
                    etDateFrom!!.setText("")
                } else {
                    etDateTo!!.setText("")
                }
            })

      //  datePickerDialog!!.datePicker.minDate = cal.timeInMillis


        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateFrom!!.setText("")

            })




        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {


                isAboveDateClicked = true
                datePickerDialog!!.show()


            }
        }

        etDateFrom!!.setOnClickListener { v ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            val minDate: Date = sdf.parse(editedModel.fromDate.toString())
            val minCalendar = Calendar.getInstance()
            minCalendar.time = minDate
            datePickerDialog!!.datePicker.minDate = minCalendar.timeInMillis

            requireActivity().hideKeyboard(requireActivity())
            isAboveDateClicked = true

            datePickerDialog!!.show()
        }


        etDateTo!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                isAboveDateClicked = false
                datePickerDialog!!.show()
            }
        }

        etDateTo!!.setOnClickListener { v ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            val minDate: Date = sdf.parse(editedModel.toDate.toString())
            val minCalendar = Calendar.getInstance()
            minCalendar.time = minDate
            datePickerDialog!!.datePicker.minDate = minCalendar.timeInMillis

            requireActivity().hideKeyboard(requireActivity())
            isAboveDateClicked = false
            datePickerDialog!!.show()
        }


    }

    @Throws(Exception::class)
    fun changeDateFormat(input: String?, output: String?, dateString: String?): String? {
        val inputDate: DateFormat = SimpleDateFormat(input,Locale.US)
        val outputDate: DateFormat = SimpleDateFormat(output,Locale.US)
        val date = inputDate.parse(dateString!!)
        return outputDate.format(date!!)
    }

    private fun getToday(): Int {
        val calendar = Calendar.getInstance()
        val thisYear = calendar[Calendar.YEAR]
        val thisMonth = calendar[Calendar.MONTH]
        val thisDay = calendar[Calendar.DAY_OF_MONTH]
        return thisDay + (thisMonth + 1) * 31 + thisYear * 12 * 31
    }

    private fun saveDelegation(state:String){

        if (state == "add"){

            autoDispose.add(viewModel.saveDelegation(userSelectedId,etDateFrom!!.text.toString()
                ,etDateTo!!.text.toString(),addedCategoriesIds).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {


                    if (it.message.toString() == "null"){
                        activity?.onBackPressed()

                    }else{
                        requireActivity().makeToast(it.message.toString())

                    }

                    dialog!!.dismiss()
                },{
                    dialog!!.dismiss()

                    Timber.e(it)

                }))
        }else{


            autoDispose.add(viewModel.saveEditedDelegation(userSelectedId,etDateFrom!!.text.toString()
                ,etDateTo!!.text.toString(),addedCategoriesIds, editedModel.id!!
            ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {


                    if (it.message.toString().equals("null")){

                        activity?.onBackPressed()

                    }else{
                        requireActivity().makeToast(it.message.toString())

                    }

                    dialog!!.dismiss()
                },{
                    dialog!!.dismiss()

                    Timber.e(it)

                }))
        }
    }

    private fun isDateValid(selectedDate: String, cashedDate: String) : Boolean {
        try {
            val selectDate = SimpleDateFormat("dd/mm/yyyy").parse(selectedDate)
            val cashDate = SimpleDateFormat("dd/mm/yyyy").parse(cashedDate)
            return cashDate.before(selectDate)
        } catch(ignored: java.text.ParseException) {
            return false
        }
    }

    override fun onDeleteClicked(catid: Int) {

        (multiSelectedcategory.adapter as AddedCategoriesAdapter).removeCategory(catid)
        addedCategoriesIds.remove(catid)


    }


    private fun setLabels() {

        when {
            viewModel.readLanguage() == "en" -> {

                fullname_label.text = translator.find { it.keyword == "FullName" }!!.en
                delgationfromdatedate_label.text = translator.find { it.keyword == "FromDate" }!!.en
                delegationtodate_label.text = translator.find { it.keyword == "ToDate" }!!.en
                categories_label.text = translator.find { it.keyword == "Categories" }!!.en

                requiredfullname_label.text = "(required)"
                requredfromdate_label.text = "(required)"
                requiredtodate_label.text = "(required)"
                requiredcategories_label.text = "(required)"

                btnSaveDelegate.text = translator.find { it.keyword == "Save" }!!.en
                btnCancelDelegate.text = translator.find { it.keyword == "Cancel" }!!.en

                userautocomopletetextview.hint = translator.find { it.keyword == "FullName" }!!.en
                delegateFrom.hint = translator.find { it.keyword == "FromDate" }!!.en
                delegateTo.hint = translator.find { it.keyword == "ToDate" }!!.en
                autoCompletecategories.hint = translator.find { it.keyword == "Categories" }!!.en


                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {

                fullname_label.text = translator.find { it.keyword == "FullName" }!!.ar
                delgationfromdatedate_label.text = translator.find { it.keyword == "FromDate" }!!.ar
                delegationtodate_label.text = translator.find { it.keyword == "ToDate" }!!.ar
                categories_label.text = translator.find { it.keyword == "Categories" }!!.ar

                requiredfullname_label.text = "(الزامي)"
                requredfromdate_label.text = "(الزامي)"
                requiredtodate_label.text = "(الزامي)"
                requiredcategories_label.text = "(الزامي)"

                btnSaveDelegate.text = translator.find { it.keyword == "Save" }!!.ar
                btnCancelDelegate.text = translator.find { it.keyword == "Cancel" }!!.ar

                userautocomopletetextview.hint = translator.find { it.keyword == "FullName" }!!.ar
                delegateFrom.hint = translator.find { it.keyword == "FromDate" }!!.ar
                delegateTo.hint = translator.find { it.keyword == "ToDate" }!!.ar
                autoCompletecategories.hint = translator.find { it.keyword == "Categories" }!!.ar


                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {


                fullname_label.text = translator.find { it.keyword == "FullName" }!!.fr
                delgationfromdatedate_label.text = translator.find { it.keyword == "FromDate" }!!.fr
                delegationtodate_label.text = translator.find { it.keyword == "ToDate" }!!.fr
                categories_label.text = translator.find { it.keyword == "Categories" }!!.fr

                requiredfullname_label.text = "(requis)"
                requredfromdate_label.text = "(requis)"
                requiredtodate_label.text = "(requis)"
                requiredcategories_label.text = "(requis)"

                btnSaveDelegate.text = translator.find { it.keyword == "Save" }!!.fr
                btnCancelDelegate.text = translator.find { it.keyword == "Cancel" }!!.fr

                userautocomopletetextview.hint = translator.find { it.keyword == "FullName" }!!.fr
                delegateFrom.hint = translator.find { it.keyword == "FromDate" }!!.fr
                delegateTo.hint = translator.find { it.keyword == "ToDate" }!!.fr
                autoCompletecategories.hint = translator.find { it.keyword == "Categories" }!!.fr


                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.fr



            }
        }
    }
}