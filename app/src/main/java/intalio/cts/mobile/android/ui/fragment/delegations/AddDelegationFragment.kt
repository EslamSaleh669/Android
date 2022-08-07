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

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        btnCancelDelegate.setOnClickListener {
            activity?.onBackPressed()
        }



        val result = arguments?.getSerializable(Constants.Delegation_Model)
        if (result.toString() == "null"){

            centered_txt.text = requireActivity().getText(R.string.delegation)

            initDates()
            initUserAutoComplete("add")
            initCategoriesAutoComplete("add")

        }else{
            editedModel = (result as? DelegationDataItem)!!
            centered_txt.text = requireActivity().getText(R.string.edit)
            initEditedDates()
            initUserAutoComplete("edit")
            initCategoriesAutoComplete("edit")
        }





        btnSaveDelegate.setOnClickListener {





            if (userSelectedId == 0 || etDateFrom!!.text.toString() == ""
                || etDateTo!!.text.toString() == "" || addedCategoriesIds.size == 0){

                requireActivity().makeToast(getString(R.string.requiredField))

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

                            Log.d("dateeeef",fromDateSelectedInDays.toString())
                            Log.d("dateeeet",getToday().toString())
                            etDateFrom!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(getString(R.string.wrong_date_less))
                        } else {
                            val toField = etDateTo!!.text.toString()
                            if (toField != "" && fromDateSelectedInDays <= toDateSelectedInDays || toField == "") {
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()
                            } else {
                                etDateFrom!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.wrong_date_greater))
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
                                requireActivity().makeToast(getString(R.string.wrong_date_less))

                            } else {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            }
                        } else if (toDateSelectedInDays < getToday()) {
                            etDateTo!!.setText("")
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(getString(R.string.wrong_date_less))

                        } else {
                            if (fromfield != "" && fromDateSelectedInDays <= toDateSelectedInDays || fromfield == "") {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            } else {
                                etDateTo!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.wrong_date_less))

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


        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                isAboveDateClicked = true
                datePickerDialog!!.show()
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
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.delegateFrom) as EditText?
        etDateTo = requireActivity().findViewById<View>(R.id.delegateTo) as EditText?

        etDateFrom!!.setText(editedModel.fromDate.toString(), TextView.BufferType.EDITABLE)
        etDateTo!!.setText(editedModel.toDate.toString(), TextView.BufferType.EDITABLE)

        val cashedFromString:String =
            changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", etDateFrom!!.text.toString())!!
//        val cashedFromDate = SimpleDateFormat("dd/mm/yyyy").parse(cashedFromString)
//        val cashedcalenar = Calendar.getInstance()
//        cashedcalenar.time = cashedFromDate
//
//
//        datePickerDialog!!.datePicker.minDate = cashedcalenar.timeInMillis
//



        dateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    selectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", selectedDate)!!


                    if (isAboveDateClicked) {
                        fromDateSelectedInDays =
                            (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31

                        if (!isDateValid(selectedDate!!,cashedFromString)) {
                            requireActivity().hideKeyboard(requireActivity())
                            requireActivity().makeToast(getString(R.string.wrong_cashed_date_less))
                        } else {
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()
                        }
                    } else {
                        toDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                        val fromfield = etDateFrom!!.text.toString()
                        if (fromfield != "") {
                            if (toDateSelectedInDays < fromDateSelectedInDays) {
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.wrong_cashed_date_less))

                            } else {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            }

                        } else {
                            if (fromfield != "" && fromDateSelectedInDays <= toDateSelectedInDays ) {
                                etDateTo!!.setText(drString)
                                etDateTo!!.clearFocus()
                            } else {
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.wrong_cashed_date_less))

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


        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                isAboveDateClicked = true
                datePickerDialog!!.show()
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


                    if (it.message.toString().equals("null")){
                        requireActivity().makeToast(getString(R.string.done))
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
                        requireActivity().makeToast(getString(R.string.done))
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

    fun isDateValid(selectedDate: String , cashedDate: String) : Boolean {
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
}