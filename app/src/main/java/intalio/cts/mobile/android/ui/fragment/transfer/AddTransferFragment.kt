package intalio.cts.mobile.android.ui.fragment.transfer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.*
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.fragment_addtransfer.*
import kotlinx.android.synthetic.main.fragment_mytransfers.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import okhttp3.ResponseBody
import okhttp3.internal.notifyAll
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList


class AddTransferFragment : Fragment(), AddedStructuresAdapter.OnDeleteClicked {

    private var purposeSelectedId = 0
    private var purposeSelectedName = ""
    private var isPurposeCCED = false

    private var structureSelectedId = 0
    private var structureSelectedName = ""
    private var userSelectedId = 0
    private var userSelectedName = 0

    private var itemType = ""
    private var userPrivacyLevel = 0

    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>

    private var datePickerDialog: DatePickerDialog? = null
    private var dateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null
    private lateinit var model: CorrespondenceDataItem

    private var selectedDate: String? = null
    private var fromDateSelectedInDays = 0

    private val Transfers = ArrayList<TransferRequestModel>()
    private var offlineId = 0
    private var structurePrivacyLevel = 0


    @Inject
    @field:Named("addtransfer")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TransfersViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(TransfersViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_addtransfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        translator = viewModel.readDictionary()!!.data!!

         setLabels()

        val result = arguments?.getSerializable(Constants.Correspondence_Model)
        if (result.toString() != "null") {
            model = result as CorrespondenceDataItem

        }

        initDates()
        initPurposeComplete()
        initStructuresAutoComplete()




        btnsaveTransfer.setOnClickListener {
            initStructuresAutoComplete()
            if (purposeSelectedId == 0 || structureSelectedId == 0) {
              requireActivity().makeToast(context?.getString(R.string.requiredField)!!)

            } else {

                if (itemType == "user") {

                    val docPrivacyLevel =
                        viewModel.readprivacies().find { it.id == model.privacyId }!!.level!!
                    addUserTransfer(userPrivacyLevel, docPrivacyLevel)


                } else {

                    checkStructure(structureSelectedId, model.privacyId)

                }


            }
        }


        btnTransferTransfer.setOnClickListener {

            if (Transfers.size == 0) {
                requireActivity().makeToast(getString(R.string.please_save_transfer))
            } else {
                //showTransfersDialog()


                val bundle = Bundle()
                bundle.putSerializable(Constants.TRANSFER_MODEL, Transfers)
                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        TransferListFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.TRANSFER_MODEL,Transfers)
                            )


                        }
                    )
                    addToBackStack("")

                }
            }

        }

    }



    private fun setLabels(){


        actvTransferautocomplete
        actvPurposesautocomplete
        etTransferDueDate
        etInstructions
        when {
            viewModel.readLanguage() == "en" -> {

                textViewTransfer.text = translator.find { it.keyword == "To" }!!.en
                textViewPurpose.text = translator.find { it.keyword == "Purposes" }!!.en
                textViewTransferDueDate.text = translator.find { it.keyword == "DueDate" }!!.en
                textViewInstructionsToReceiver.text = translator.find { it.keyword == "Instruction" }!!.en
                toreq.text = "(required)"
                purposereq.text = "(required)"
                btnsaveTransfer.text = translator.find { it.keyword == "Save" }!!.en
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.en

                actvTransferautocomplete.hint = translator.find { it.keyword == "To" }!!.en
                actvPurposesautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.en
                etTransferDueDate.hint = translator.find { it.keyword == "DueDate" }!!.en
                etInstructions.hint = translator.find { it.keyword == "Instruction" }!!.en


                centered_txt.text = translator.find { it.keyword == "Transfer" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {

                textViewTransfer.text = translator.find { it.keyword == "To" }!!.ar
                textViewPurpose.text = translator.find { it.keyword == "Purposes" }!!.ar
                textViewTransferDueDate.text = translator.find { it.keyword == "DueDate" }!!.ar
                textViewInstructionsToReceiver.text = translator.find { it.keyword == "Instruction" }!!.ar
                toreq.text = "(مطلوب)"
                purposereq.text = "(مطلوب)"
                btnsaveTransfer.text = translator.find { it.keyword == "Save" }!!.ar
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.ar

                actvTransferautocomplete.hint = translator.find { it.keyword == "To" }!!.ar
                actvPurposesautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.ar
                etTransferDueDate.hint = translator.find { it.keyword == "DueDate" }!!.ar
                etInstructions.hint = translator.find { it.keyword == "Instruction" }!!.ar
                centered_txt.text = translator.find { it.keyword == "Transfer" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {

                textViewTransfer.text = translator.find { it.keyword == "To" }!!.fr
                textViewPurpose.text = translator.find { it.keyword == "Purposes" }!!.fr
                textViewTransferDueDate.text = translator.find { it.keyword == "DueDate" }!!.fr
                textViewInstructionsToReceiver.text = translator.find { it.keyword == "Instruction" }!!.fr
                toreq.text = "(requis)"
                purposereq.text = "(requis)"
                btnsaveTransfer.text = translator.find { it.keyword == "Save" }!!.fr
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.fr


                actvTransferautocomplete.hint = translator.find { it.keyword == "To" }!!.fr
                actvPurposesautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.fr
                etTransferDueDate.hint = translator.find { it.keyword == "DueDate" }!!.fr
                etInstructions.hint = translator.find { it.keyword == "Instruction" }!!.fr
                centered_txt.text = translator.find { it.keyword == "Transfer" }!!.fr

            }
        }
    }



    private fun initPurposeComplete() {
        actvPurposesautocomplete.threshold = 0
        val purposesAutoCompleteArray = viewModel.readPurposesData()!!

        val arrayAdapter =
            PurposesStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                purposesAutoCompleteArray
            )
        actvPurposesautocomplete.setAdapter(arrayAdapter)


        actvPurposesautocomplete.setOnClickListener {
            actvPurposesautocomplete.showDropDown()

        }

        actvPurposesautocomplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                actvPurposesautocomplete.showDropDown()

            } else {
                actvPurposesautocomplete.setText("")
            }

        }

        actvPurposesautocomplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                actvPurposesautocomplete.clearFocus()
                actvPurposesautocomplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as PurposesResponseItem
                actvPurposesautocomplete.setText(selectedObject.text.toString())
                purposeSelectedId = selectedObject.id!!
                purposeSelectedName = selectedObject.text!!

                isPurposeCCED = selectedObject.cCed == true

                Log.d("purposeid", itemType)


            }

    }

    private fun manageResponse(
        allStructuresResponse: AllStructuresResponse,
        structuresArray: ArrayList<AllStructuresItem>?
    ) {
        val allUsersAndStructures = ArrayList<AllStructuresItem>()
        if (allStructuresResponse.structures != null) {
            allUsersAndStructures.addAll(allStructuresResponse.structures)
        }

        if (allStructuresResponse.users!!.size > 0) {

            for (item in allStructuresResponse.users) {

                val structureItem = AllStructuresItem()
                structureItem.id = item!!.id
                structureItem.name =
                    "${structuresArray!!.find { it.id == item.structureIds!![0]!! }!!.name} / ${item.fullName}"
                structureItem.attributes = item.attributes
                structureItem.structureIds = item.structureIds
                structureItem.itemType = "user"


                allUsersAndStructures.add(structureItem)
            }

        }


        val arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                allUsersAndStructures
            )
        actvTransferautocomplete.setAdapter(arrayAdapter)


    }

    private fun initStructuresAutoComplete() {



        actvTransferautocomplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures
        val userArray = viewModel.readAllStructureData().users

        val fullStructures = viewModel.readFullStructures()


        for (item in userArray!!) {

             if (item!!.id != viewModel.readUserinfo().id) {
                val structureItem = AllStructuresItem()
                structureItem.id = item.id
                structureItem.name =
                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                structureItem.attributes = item.attributes
                structureItem.structureIds = item.structureIds
                structureItem.itemType = "user"


                structuresArray!!.add(structureItem)
            }

        }


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray
            )

        actvTransferautocomplete.setAdapter(arrayAdapter)

        actvTransferautocomplete.setOnClickListener {

            if (actvTransferautocomplete.text.toString().isNotEmpty()){
                actvTransferautocomplete.showDropDown()

            }else{
                val arrayAdapterr =
                    StructuresAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        structuresArray
                    )
                actvTransferautocomplete.setAdapter(arrayAdapterr)
                actvTransferautocomplete.showDropDown()

            }

        }

        actvTransferautocomplete.doOnTextChanged { text, start, before, count ->

            if (count >= 2) {
                autoDispose.add(
                    viewModel.getAllStructures(text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {

                                Log.d("userresponse", it.toString())

                                //manageResponse(it,structuresArray)

                                val allUsersAndStructures = ArrayList<AllStructuresItem>()
                                if (it.structures != null) {
                                    allUsersAndStructures.addAll(it.structures)
                                }

                                if (it.users!!.size > 0) {

                                    for (item in it.users) {
                                        if (item!!.id != viewModel.readUserinfo().id) {
                                            val structureItem = AllStructuresItem()
                                            structureItem.id = item.id
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            structureItem.attributes = item.attributes
                                            structureItem.structureIds = item.structureIds
                                            structureItem.itemType = "user"


                                            allUsersAndStructures.add(structureItem)
                                        }

                                    }
                                }


                                arrayAdapter =
                                    StructuresAdapter(
                                        requireContext(),
                                        R.layout.support_simple_spinner_dropdown_item,
                                        allUsersAndStructures
                                    )
                                actvTransferautocomplete.setAdapter(arrayAdapter)
                                actvTransferautocomplete.showDropDown()


                            }, {

                                Timber.e(it)

                            })
                )
            }


        }


        actvTransferautocomplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                actvTransferautocomplete.showDropDown()

            } else {
                actvTransferautocomplete.setText("")
            }

        }



        actvTransferautocomplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                actvTransferautocomplete.clearFocus()
                actvTransferautocomplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                actvTransferautocomplete.setText(selectedObject.name)
                if (selectedObject.itemType == "user") {
                    itemType = selectedObject.itemType!!
                    val privacy =
                        selectedObject.attributes!!.find { it!!.text == "Privacy" }!!.value
                    if (privacy != null) {
                        userPrivacyLevel =
                            viewModel.readprivacies().find { it.id == privacy.toInt() }!!.level!!
                    }
                    userSelectedId = selectedObject.id!!


                    structureSelectedId = selectedObject.structureIds!![0]

                    structureSelectedName = selectedObject.name!!

                } else {

                    structureSelectedId = selectedObject.id!!
                    structureSelectedName = selectedObject.name!!

                }


            }
    }


    private fun getStructure(structureId: Int): String {

        var structureName = ""
        autoDispose.add(
            viewModel.geStructure(structureId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        Log.d("stresponse", it.toString())

                        structureName = it.name!!


                    }, {
                        Log.d("stresponse", it.toString())

                        Timber.e(it)

                    })
        )
        Log.d("stresponsename", structureName)

        return structureName
    }

    private fun initDates() {
        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.etTransferDueDate) as EditText?
        val dueDate = model.dueDate

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        dateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {


                    selectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", selectedDate)!!

                    fromDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31

                    if (fromDateSelectedInDays < getToday()) {

                        etDateFrom!!.setText("")
                        requireActivity().hideKeyboard(requireActivity())
                        requireActivity().makeToast(getString(R.string.wrong_date_less))

                    } else if (model.dueDate != "") {


                        val firstDate: Date = sdf.parse(dueDate)
                        val secondDate: Date = sdf.parse(drString)


                        val cmp = firstDate.compareTo(secondDate)
                        when {
                            cmp > 0 -> {
                                requireActivity().makeToast(getString(R.string.done))
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()

                            }
                            cmp < 0 -> {
                                etDateFrom!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.lessthan_duedate))
                            }
                            else -> {
                                requireActivity().makeToast(getString(R.string.done))
                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()
                            }
                        }


                    } else {
                        etDateFrom!!.setText(drString)
                        etDateFrom!!.clearFocus()

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



        datePickerDialog!!.datePicker.minDate = cal.timeInMillis

        if (model.dueDate != "") {
            val maxDate: Date = sdf.parse(dueDate)
            val maxCalendar = Calendar.getInstance()
            maxCalendar.time = maxDate
            datePickerDialog!!.datePicker.maxDate = maxCalendar.timeInMillis

        }

        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateFrom!!.setText("")

            })


        etDateFrom!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            requireActivity().hideKeyboard(requireActivity())
            if (hasFocus) {
                datePickerDialog!!.show()
            }
        }

        etDateFrom!!.setOnClickListener { v ->
            requireActivity().hideKeyboard(requireActivity())
            datePickerDialog!!.show()
        }
    }

    @Throws(Exception::class)
    fun changeDateFormat(input: String?, output: String?, dateString: String?): String? {
        val inputDate: DateFormat = SimpleDateFormat(input, Locale.US)
        val outputDate: DateFormat = SimpleDateFormat(output, Locale.US)
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

    override fun onDeleteClicked(catid: Int, catname: String) {
//        (multiSelectedTransfer.adapter as AddedStructuresAdapter).removeCategory(catid)
//
//        addedCategoriesIds.remove(catid)
//        addedCategoriesNames.remove(catname)

    }

    private fun addStructureTransfer() {

        val addedModel = TransferRequestModel()
        addedModel.DocumentId = model.documentId?.toString()
        addedModel.DocumentPrivacyId = model.privacyId?.toString()
        addedModel.PrivacyId = model.privacyId.toString()
        addedModel.purposeId = purposeSelectedId.toString()
        addedModel.FromStructureId = model.fromStructureId!!
        addedModel.toStructureId = structureSelectedId
        addedModel.name = structureSelectedName
        addedModel.ParentTransferId = model.id
        addedModel.cced = isPurposeCCED == true
        addedModel.instruction = etInstructions.text.toString().trim()
        addedModel.dueDate = etDateFrom!!.text.toString().trim()
        addedModel.IsStructure = true
        addedModel.purposeName = purposeSelectedName
        addedModel.transferOfflineId = offlineId




        Transfers.add(addedModel)
        offlineId += 1
        Log.d("purposeid", itemType)

        Log.d("selecteddatatype", itemType)
        Log.d("selecteddataname", structureSelectedName)
        Log.d("selecteddatastruc", structureSelectedId.toString())

        purposeSelectedId = 0
        purposeSelectedName = ""
        structureSelectedId = 0
        structureSelectedName = ""
        isPurposeCCED = false

        actvTransferautocomplete.setText("")
        actvPurposesautocomplete.setText("")
        etInstructions.setText("")
        etTransferDueDate.setText("")


    }


    private fun addUserTransfer(userPrivacyLevel: Int, docPrivacyLevel: Int) {
        if (userPrivacyLevel >= docPrivacyLevel) {

            val addedModel = TransferRequestModel()
            addedModel.DocumentId = model.documentId?.toString()
            addedModel.DocumentPrivacyId = model.privacyId?.toString()
            addedModel.PrivacyId = model.privacyId.toString()
            addedModel.purposeId = purposeSelectedId.toString()
            addedModel.FromStructureId = model.fromStructureId!!
            addedModel.toStructureId = structureSelectedId
            addedModel.name = structureSelectedName
            addedModel.ParentTransferId = model.id
            addedModel.cced = isPurposeCCED == true
            addedModel.instruction = etInstructions.text.toString().trim()
            addedModel.dueDate = etDateFrom!!.text.toString().trim()
            addedModel.IsStructure = false
            addedModel.toUserId = userSelectedId
            addedModel.purposeName = purposeSelectedName
            addedModel.transferOfflineId = offlineId


            Transfers.add(addedModel)
            offlineId += 1
            Log.d("selecteddatatype", itemType)
            Log.d("selecteddataid", userSelectedId.toString())
            Log.d("selecteddataname", structureSelectedName)
            Log.d("selecteddatastruc", structureSelectedId.toString())

            Log.d("selecteddadoclevel", userPrivacyLevel.toString())
            Log.d("selecteddastruclevel", userPrivacyLevel.toString())

            purposeSelectedId = 0
            purposeSelectedName = ""
            structureSelectedId = 0
            structureSelectedName = ""
            isPurposeCCED = false
            userSelectedId = 0
            itemType = ""

            actvTransferautocomplete.setText("")
            actvPurposesautocomplete.setText("")
            etInstructions.setText("")
            etTransferDueDate.setText("")


        } else {

            val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
            val alertDialog = AlertDialog.Builder(requireActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(
                    "${getString(R.string.users_donthave_privacy)} $structureSelectedName\n${
                        getString(
                            R.string.continue_confirm
                        )
                    }"
                )
                .setPositiveButton(
                    requireActivity().getString(R.string.yes),
                    DialogInterface.OnClickListener { dialogg, i ->
                        dialogg.dismiss()

                        val addedModel = TransferRequestModel()
                        addedModel.DocumentId = model.documentId?.toString()
                        addedModel.DocumentPrivacyId = model.privacyId?.toString()
                        addedModel.PrivacyId = model.privacyId.toString()
                        addedModel.purposeId = purposeSelectedId.toString()
                        addedModel.FromStructureId = model.fromStructureId!!
                        addedModel.toStructureId = structureSelectedId
                        addedModel.name = structureSelectedName
                        addedModel.ParentTransferId = model.id
                        addedModel.cced = isPurposeCCED == true
                        addedModel.instruction = etInstructions.text.toString().trim()
                        addedModel.dueDate = etDateFrom!!.text.toString().trim()
                        addedModel.IsStructure = false
                        addedModel.toUserId = userSelectedId
                        addedModel.purposeName = purposeSelectedName
                        addedModel.transferOfflineId = offlineId


                        Transfers.add(addedModel)
                        offlineId += 1


                        Log.d("selecteddatatype", itemType)
                        Log.d("selecteddataid", userSelectedId.toString())
                        Log.d("selecteddataname", structureSelectedName)
                        Log.d("selecteddatastruc", structureSelectedId.toString())

                        Log.d("selecteddadoclevel", userPrivacyLevel.toString())
                        Log.d("selecteddastruclevel", userPrivacyLevel.toString())

                        purposeSelectedId = 0
                        purposeSelectedName = ""
                        structureSelectedId = 0
                        structureSelectedName = ""
                        isPurposeCCED = false
                        userSelectedId = 0
                        itemType = ""

                        actvTransferautocomplete.setText("")
                        actvPurposesautocomplete.setText("")
                        etInstructions.setText("")
                        etTransferDueDate.setText("")


                    })
                .setNegativeButton(
                    requireActivity().getString(R.string.no),
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        purposeSelectedId = 0
                        purposeSelectedName = ""
                        structureSelectedId = 0
                        structureSelectedName = ""
                        isPurposeCCED = false
                        userSelectedId = 0
                        itemType = ""

                        actvTransferautocomplete.setText("")
                        actvPurposesautocomplete.setText("")
                        etInstructions.setText("")
                        etTransferDueDate.setText("")

                    }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        }


    }


    private fun checkStructure(structureSelectedId: Int, documentPrivacyId: Int?) {
        val structureIds = arrayOf(structureSelectedId)


        viewModel.listUserExistenceAttributeInStructure(
            structureIds

        ).enqueue(object : Callback<ResponseBody> {


            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val privacyList = ArrayList<Int>()
                    var responseRecieved: Any? = null
                    responseRecieved = response.body()!!.string()
                    val structuresItem = JSONObject(responseRecieved)
                    val userExistenceArray =
                        structuresItem.getJSONArray(structureSelectedId.toString())
                    if (userExistenceArray.length() > 0) {


                        (0 until userExistenceArray.length()).forEach { item ->

                            if (userExistenceArray[item] as String != "") {
                                privacyList.add((userExistenceArray[item] as String).toInt())

                            } else {
                                requireActivity().makeToast("${getString(R.string.structure_recievers_error)}${structureSelectedName}")

                            }


                        }


                        getStructurePrivacyLevel(privacyList.maxOrNull() ?: 0, documentPrivacyId)


                    } else {
                        requireActivity().makeToast("${getString(R.string.structure_recievers_error)}${structureSelectedName}")

                        structurePrivacyLevel = 0
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("structureresponse", e.toString())

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("structureresponse", t.toString())

                requireActivity().makeToast(requireActivity().getString(R.string.network_error))
            }

        }

        )
    }


    private fun getStructurePrivacyLevel(structurePrivacyId: Int, documentPrivacyId: Int?) {
        structurePrivacyLevel =
            viewModel.readprivacies().find { it.id == structurePrivacyId }!!.level!!
        val docLevel = viewModel.readprivacies().find { it.id == documentPrivacyId }!!.level!!

        if (structurePrivacyLevel >= docLevel) {
            addStructureTransfer()
        } else {
            requireActivity().makeToast("${getString(R.string.structure_recievers_error)}${structureSelectedName}")
        }

    }

//    private fun showTransfersDialog() {
//        val customDialog = Dialog(requireContext(), R.style.FullScreenDialog)
//        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        customDialog.setCancelable(false)
//        customDialog.setContentView(R.layout.transferlist_dialog)
//
//        customDialog.findViewById<TextView>(R.id.centered_txt).setText(R.string.my_transfers)
//        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
//            customDialog.dismiss()
//        }
//
//        val transferRecycler = customDialog.findViewById(R.id.transfers_recycler) as RecyclerView
//        val transferButton = customDialog.findViewById(R.id.send_transfers) as Button
//
//        transferRecycler.adapter =
//            TransferList_Adapter(Transfers, requireActivity(),)
//        transferRecycler.layoutManager =
//            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//
//        transferButton.setOnClickListener {
//
//
//            dialog = requireActivity().launchLoadingDialog()
//
//            autoDispose.add(
//                viewModel.transferTransfer(Transfers).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        {
//
//                            Log.d("transfererresponse", it.toString())
//                            Log.d("transfererresponse", it[0].message.toString())
//                            if (it[0].updated == true) {
//                                Transfers.clear()
//                                customDialog.dismiss()
//                                requireActivity().makeToast(getString(R.string.done))
//                                (activity as AppCompatActivity).supportFragmentManager.commit {
//                                    replace(R.id.fragmentContainer,
//                                        CorrespondenceFragment().apply {
//                                            arguments = bundleOf(
//                                                Pair(Constants.NODE_ID, 2)
//                                            )
//                                        }
//                                    )
//                                }
//                            } else if (it[0].updated == false && it[0].message == "OriginalFileInUse") {
//                                requireActivity().makeToast(getString(R.string.original_doc_checkedout))
//
//                            } else if (it[0].updated == false && it[0].message == "FileInUse") {
//                                requireActivity().makeToast(getString(R.string.there_is_file_checkedout))
//                            }
//
//                            dialog!!.dismiss()
//                        }, {
//                            dialog!!.dismiss()
//
//                            Timber.e(it)
//
//                        })
//            )
//        }
//
//        customDialog.show()
//
//    }


}