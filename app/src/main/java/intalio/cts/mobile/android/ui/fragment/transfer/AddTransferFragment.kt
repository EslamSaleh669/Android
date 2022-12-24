package intalio.cts.mobile.android.ui.fragment.transfer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
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
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.model.selectedUserModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.*
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.fragment_addtransfer.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import okhttp3.ResponseBody
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
import org.json.JSONArray


class AddTransferFragment : Fragment(), TransferGrids_Adapter.OnTransferGridClicked,
    SelectedUserAdapter.OnDeleteSelectedUserClicked {

    private var purposeSelectedId = 0
    private var purposeSelectedName = ""
    private var isPurposeCCED = false
    private var delegationId = 0
    private var structureSelectedId = 0
    private var structureSelectedName = ""
    private var userSelectedId = 0
    private var userSelectedName = 0
    private var multiSelectionList = ArrayList<selectedUserModel>()


    private var itemType = ""
    private var userPrivacyLevel = 0

    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>
    private lateinit var settings: java.util.ArrayList<ParamSettingsResponseItem>

    private var datePickerDialog: DatePickerDialog? = null
    private var dateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null
    private lateinit var model: CorrespondenceDataItem

    private var selectedDate: String? = null
    private var fromDateSelectedInDays = 0

    private val Transfers = ArrayList<TransferRequestModel>()
    private var offlineId = 0
    private var mutliSelectionOfflineId = 0
    private var structurePrivacyLevel = 0
    private var lowPrivacyUsers = ""
    private var lowPrivacyStructuresWithoutOptions = ""
    private var lowPrivacyStructuresWithOptions = ""
    private var emptyStructureReceiversWithoutOptions = ""
    private var emptyStructureReceiversWithOptions = ""


    var dialog: Dialog? = null


    @Inject
    @field:Named("addtransfer")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TransfersViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(TransfersViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()


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
        settings = viewModel.readSettings()




        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        setLabels()

        val result = arguments?.getSerializable(Constants.Correspondence_Model)
        if (result.toString() != "null") {
            model = result as CorrespondenceDataItem

        }

        transfersgrids_recycler.adapter =
            TransferGrids_Adapter(arrayListOf(), requireActivity(), this, viewModel, translator)
        transfersgrids_recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        initDates()
        initPurposeComplete()

        val enableSendingRules = settings.find { it.keyword == "EnableSendingRules" }!!.content

        val structureSender =
            viewModel.readUserinfo().attributes!!.find { it!!.text == "StructureSender" }!!.value

        if (structureSender == "false") {
            val structureIds = ArrayList<Int>()

            for (item in viewModel.readUserinfo().structureIds!!) {
                structureIds.add(item)
            }
            getAvailableStructures(structureIds)

        } else {
            if (enableSendingRules == "true") {
                geStructureSendingRules(model.toStructureId!!)


            } else {

                initStructuresAutoComplete()


            }
        }


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
        btnsaveTransfer.setOnClickListener {

            if (purposeSelectedId == 0 || multiSelectionList.size == 0) {

                requireActivity().makeToast(requiredFields)

            } else {
                for (selectedItem in multiSelectionList) {
                    if (selectedItem.itemType == "user") {

                        val docPrivacyLevel =
                            viewModel.readprivacies().find { it.id == model.privacyId }!!.level!!
                        addMultiUsersTransfer(
                            selectedItem.userPrivacyLevel!!,
                            docPrivacyLevel,
                            selectedItem
                        )

                    } else {

                        checkMultiStructures(
                            selectedItem.fromStructureId!!,
                            model.privacyId,
                            selectedItem,
                            purposeSelectedId,
                            purposeSelectedName,
                            etDateFrom!!.text.toString().trim(),
                            etInstructions.text.toString().trim()
                        )

                    }
                }



                purposeSelectedId = 0
                purposeSelectedName = ""
                structureSelectedId = 0
                structureSelectedName = ""
                isPurposeCCED = false
                userSelectedId = 0
                itemType = ""
                multiSelectionList.clear()

                actvTransferautocomplete.setText("")
                actvPurposesautocomplete.setText("")
                etInstructions.setText("")
                etTransferDueDate.setText("")




                if (structureSender == "false") {
                    val structureIds = ArrayList<Int>()

                    for (item in viewModel.readUserinfo().structureIds!!) {
                        structureIds.add(item)
                    }
                    getAvailableStructures(structureIds)

                } else {
                    if (enableSendingRules == "true") {
                        geStructureSendingRules(model.fromStructureId!!)

                    } else {

                        initStructuresAutoComplete()

                    }
                }


            }
        }

        var emptyTransfers = ""

        when {
            viewModel.readLanguage() == "en" -> {

                emptyTransfers = "please save at least one transfer"

            }
            viewModel.readLanguage() == "ar" -> {
                emptyTransfers = "الرجاء حفظ إحالة واحدة علي الأقل"

            }
            viewModel.readLanguage() == "fr" -> {
                emptyTransfers = "veuillez enregistrer au moins un transfert"

            }
        }

        btnTransferTransfer.setOnClickListener {

            if (Transfers.size == 0) {
                requireActivity().makeToast(emptyTransfers)
            } else {

                sendTransfer(model)


            }

        }
//        val bundle = Bundle()
//        bundle.putSerializable(Constants.TRANSFER_MODEL, Transfers)
//        (activity as AppCompatActivity).supportFragmentManager.commit {
//            replace(R.id.fragmentContainer,
//                TransferListFragment().apply {
//                    arguments = bundleOf(
//                        Pair(Constants.TRANSFER_MODEL, Transfers)
//                    )
//
//
//                }
//            )
//            addToBackStack("")
//
//        }
    }


    private fun setLabels() {

        when {
            viewModel.readLanguage() == "en" -> {

                textViewTransfer.text = translator.find { it.keyword == "To" }!!.en
                textViewPurpose.text = translator.find { it.keyword == "Purposes" }!!.en
                textViewTransferDueDate.text = translator.find { it.keyword == "DueDate" }!!.en
                textViewInstructionsToReceiver.text =
                    translator.find { it.keyword == "Instruction" }!!.en
                toreq.text = "(required)"
                purposereq.text = "(required)"
                btnsaveTransfer.text = translator.find { it.keyword == "Add" }!!.en
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.en
                transfergrid_reciever_label.text = translator.find { it.keyword == "To" }!!.en
                transfergrid_purpose_label.text = translator.find { it.keyword == "Purpose" }!!.en
                transfergrid_duedate_label.text = translator.find { it.keyword == "DueDate" }!!.en

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
                textViewInstructionsToReceiver.text =
                    translator.find { it.keyword == "Instruction" }!!.ar
                toreq.text = "(الزامي)"
                purposereq.text = "(الزامي)"
                btnsaveTransfer.text = translator.find { it.keyword == "Add" }!!.ar
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.ar
                transfergrid_reciever_label.text = translator.find { it.keyword == "To" }!!.ar
                transfergrid_purpose_label.text = translator.find { it.keyword == "Purpose" }!!.ar
                transfergrid_duedate_label.text = translator.find { it.keyword == "DueDate" }!!.ar

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
                textViewInstructionsToReceiver.text =
                    translator.find { it.keyword == "Instruction" }!!.fr
                toreq.text = "(requis)"
                purposereq.text = "(requis)"
                btnsaveTransfer.text = translator.find { it.keyword == "Add" }!!.fr
                btnTransferTransfer.text = translator.find { it.keyword == "Transfer" }!!.fr
                transfergrid_reciever_label.text = translator.find { it.keyword == "To" }!!.fr
                transfergrid_purpose_label.text = translator.find { it.keyword == "Purpose" }!!.fr
                transfergrid_duedate_label.text = translator.find { it.keyword == "DueDate" }!!.fr

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

    private fun initStructuresAutoComplete() {

        multiSelecteduser.adapter = SelectedUserAdapter(
            requireActivity(),
            arrayListOf(), this,
            viewModel
        )
        multiSelecteduser.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        actvTransferautocomplete.threshold = 0
        val structuresArray = viewModel.readAllStructureData().structures
        val userArray = viewModel.readAllStructureData().users

        val fullStructures = viewModel.readFullStructures()


        val transferToUser = settings.find { it.keyword == "EnableTransferToUsers" }!!.content

        if (transferToUser == "true") {
            for (item in userArray!!) {

                if (delegationId == 0) {
                    if (item.id != viewModel.readUserinfo().id) {
                        val structureItem = AllStructuresItem()
                        structureItem.id = item.id

                        fullStructures!!.find { it.id == item.structureIds?.get(0) }?.name.let {

                            if (it != null) {

                                when {
                                    viewModel.readLanguage() == "en" -> {
                                        structureItem.name =
                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                    }
                                    viewModel.readLanguage() == "ar" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameAr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }

                                    }
                                    viewModel.readLanguage() == "fr" -> {


                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameFr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }


                                    }
                                }


                            }
                        }

                        structureItem.attributes = item.attributes
                        structureItem.structureIds = item.structureIds
                        structureItem.itemType = "user"


                        structuresArray!!.add(structureItem)
                    }

                } else {
                    val delegator = viewModel.readSavedDelegator()
                    if (item.id != delegator!!.fromUserId && item.id != viewModel.readUserinfo().id) {
                        val structureItem = AllStructuresItem()
                        structureItem.id = item.id

                        fullStructures!!.find { it.id == item.structureIds?.get(0) }?.name.let {

                            if (it != null) {

                                when {
                                    viewModel.readLanguage() == "en" -> {
                                        structureItem.name =
                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                    }
                                    viewModel.readLanguage() == "ar" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameAr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }

                                    }
                                    viewModel.readLanguage() == "fr" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameFr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }
                                    }
                                }


                            }
                        }

                        structureItem.attributes = item.attributes
                        structureItem.structureIds = item.structureIds
                        structureItem.itemType = "user"


                        structuresArray!!.add(structureItem)
                    }

                }

            }

        }


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()

            )

        actvTransferautocomplete.setAdapter(arrayAdapter)

        actvTransferautocomplete.setOnClickListener {

            if (actvTransferautocomplete.text.toString().isNotEmpty()) {
                actvTransferautocomplete.showDropDown()

            } else {
                val arrayAdapterr =
                    StructuresAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        structuresArray,
                        viewModel.readLanguage()

                    )
                actvTransferautocomplete.setAdapter(arrayAdapterr)
                actvTransferautocomplete.showDropDown()

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

                val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem


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

                    val userObject = selectedUserModel()
                    userObject.fromUserId = selectedObject.id!!
                    userObject.fromStructureId = selectedObject.structureIds!![0]
                    userObject.text = selectedObject.name
                    userObject.offlineId = mutliSelectionOfflineId
                    userObject.itemType = "user"
                    userObject.userPrivacyLevel = userPrivacyLevel

                    actvTransferautocomplete.setText("")
                    (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(userObject)
                    mutliSelectionOfflineId += 1

                    multiSelectionList.find { it.text == userObject.text }.let {
                        if (it == null) {
                            multiSelectionList.add(userObject)

                        }
                    }

                } else {
                    val userObject = selectedUserModel()
                    userObject.fromUserId = 0
                    userObject.fromStructureId = selectedObject.id!!
                    userObject.text = selectedObject.name
                    userObject.offlineId = mutliSelectionOfflineId
                    userObject.itemType = "structure"
                    userObject.userPrivacyLevel = userPrivacyLevel


                    actvTransferautocomplete.setText("")
                    (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(userObject)
                    mutliSelectionOfflineId += 1
                    structureSelectedId = selectedObject.id!!
                    structureSelectedName = selectedObject.name!!

                    multiSelectionList.find { it.text == userObject.text }.let {
                        if (it == null) {
                            multiSelectionList.add(userObject)

                        }
                    }

                }


                requireActivity().hideKeyboard(requireActivity())
                actvTransferautocomplete.clearFocus()
                actvTransferautocomplete.dismissDropDown()
            }

        actvTransferautocomplete.doOnTextChanged { text, start, before, count ->

            val structureIds = ArrayList<Int>()

            autoDispose.add(
                viewModel.getAllStructures(text.toString(), structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            //manageResponse(it,structuresArray)

                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }

                            val enableTransferToUser =
                                settings.find { it.keyword == "EnableTransferToUsers" }!!.content

                            if (enableTransferToUser == "true") {
                                if (it.users!!.size > 0) {

                                    for (item in it.users) {
                                        if (delegationId == 0) {
                                            if (item.id != viewModel.readUserinfo().id) {
                                                val structureItem = AllStructuresItem()
                                                structureItem.id = item.id
                                                fullStructures!!.find {
                                                    it.id == item.structureIds?.get(
                                                        0
                                                    )
                                                }?.name.let {
                                                    if (it != null) {

                                                        when {
                                                            viewModel.readLanguage() == "en" -> {
                                                                structureItem.name =
                                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                                            }
                                                            viewModel.readLanguage() == "ar" -> {

                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameAr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                            viewModel.readLanguage() == "fr" -> {

                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameFr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                        }


                                                    }
                                                }
                                                structureItem.attributes = item.attributes
                                                structureItem.structureIds = item.structureIds
                                                structureItem.itemType = "user"


                                                allUsersAndStructures.add(structureItem)
                                            }

                                        } else {
                                            val delegator = viewModel.readSavedDelegator()

                                            if (item.id != delegator!!.fromUserId && item.id != viewModel.readUserinfo().id) {
                                                val structureItem = AllStructuresItem()
                                                structureItem.id = item.id
                                                fullStructures!!.find {
                                                    it.id == item.structureIds?.get(
                                                        0
                                                    )
                                                }?.name.let {
                                                    if (it != null) {

                                                        when {
                                                            viewModel.readLanguage() == "en" -> {
                                                                structureItem.name =
                                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                                            }
                                                            viewModel.readLanguage() == "ar" -> {

                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameAr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                            viewModel.readLanguage() == "fr" -> {
                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameFr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                        }


                                                    }
                                                }
                                                structureItem.attributes = item.attributes
                                                structureItem.structureIds = item.structureIds
                                                structureItem.itemType = "user"


                                                allUsersAndStructures.add(structureItem)
                                            }

                                        }

                                    }
                                }
                            }



                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            actvTransferautocomplete.setAdapter(arrayAdapter)
                            if (actvTransferautocomplete.hasFocus()) {
                                actvTransferautocomplete.showDropDown()

                            } else {
                                actvTransferautocomplete.dismissDropDown()

                            }


                        }, {

                            Timber.e(it)

                        })
            )

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

                    val selectedObject = parent!!.getItemAtPosition(position) as AllStructuresItem
                    actvTransferautocomplete.setText(selectedObject.name)
                    if (selectedObject.itemType == "user") {
                        itemType = selectedObject.itemType!!
                        val privacy =
                            selectedObject.attributes!!.find { it!!.text == "Privacy" }!!.value
                        if (privacy != null) {
                            userPrivacyLevel =
                                viewModel.readprivacies()
                                    .find { it.id == privacy.toInt() }!!.level!!
                        }
                        userSelectedId = selectedObject.id!!

                        structureSelectedId = selectedObject.structureIds!![0]

                        structureSelectedName = selectedObject.name!!

                        val userObject = selectedUserModel()
                        userObject.fromUserId = selectedObject.id!!
                        userObject.fromStructureId = selectedObject.structureIds!![0]
                        userObject.text = selectedObject.name
                        userObject.offlineId = mutliSelectionOfflineId
                        userObject.itemType = "user"
                        userObject.userPrivacyLevel = userPrivacyLevel

                        actvTransferautocomplete.setText("")
                        (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(
                            userObject
                        )
                        mutliSelectionOfflineId += 1

                        multiSelectionList.find { it.text == userObject.text }.let {
                            if (it == null) {
                                multiSelectionList.add(userObject)

                            }
                        }

                    } else {

                        structureSelectedId = selectedObject.id!!
                        structureSelectedName = selectedObject.name!!

                        val userObject = selectedUserModel()
                        userObject.fromUserId = 0
                        userObject.fromStructureId = selectedObject.id!!
                        userObject.text = selectedObject.name
                        userObject.offlineId = mutliSelectionOfflineId
                        userObject.itemType = "structure"
                        userObject.userPrivacyLevel = userPrivacyLevel


                        actvTransferautocomplete.setText("")
                        (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(
                            userObject
                        )
                        mutliSelectionOfflineId += 1

                        multiSelectionList.find { it.text == userObject.text }.let {
                            if (it == null) {
                                multiSelectionList.add(userObject)

                            }
                        }
                    }

                    requireActivity().hideKeyboard(requireActivity())
                    actvTransferautocomplete.clearFocus()
                    actvTransferautocomplete.dismissDropDown()
                }

        }

    }

    private fun initAvailableStructuresAutoComplete(
        allStructuresResponse: AllStructuresResponse,
        structureIds: ArrayList<Int>
    ) {

        multiSelecteduser.adapter = SelectedUserAdapter(
            requireActivity(),
            arrayListOf(), this,
            viewModel
        )
        multiSelecteduser.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        actvTransferautocomplete.threshold = 0
        val structuresArray = allStructuresResponse.structures
        val userArray = allStructuresResponse.users

        val fullStructures = viewModel.readFullStructures()


        val transferToUser = settings.find { it.keyword == "EnableTransferToUsers" }!!.content


        if (transferToUser == "true") {
            for (item in userArray!!) {

                if (delegationId == 0) {

                    if (item.id != viewModel.readUserinfo().id) {
                        val structureItem = AllStructuresItem()
                        structureItem.id = item.id
                        fullStructures!!.find { it.id == item.structureIds?.get(0) }?.name.let {
                            if (it != null) {

                                when {
                                    viewModel.readLanguage() == "en" -> {
                                        structureItem.name =
                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                    }
                                    viewModel.readLanguage() == "ar" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameAr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }
                                    }
                                    viewModel.readLanguage() == "fr" -> {
                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameFr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }
                                    }
                                }


                            }
                        }
                        structureItem.attributes = item.attributes
                        structureItem.structureIds = item.structureIds
                        structureItem.itemType = "user"


                        structuresArray!!.add(structureItem)
                    }

                } else {


                    val delegator = viewModel.readSavedDelegator()

                    if (item.id != delegator!!.fromUserId && item.id != viewModel.readUserinfo().id) {
                        val structureItem = AllStructuresItem()
                        structureItem.id = item.id
                        fullStructures!!.find { it.id == item.structureIds?.get(0) }?.name.let {
                            if (it != null) {

                                when {
                                    viewModel.readLanguage() == "en" -> {
                                        structureItem.name =
                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                    }
                                    viewModel.readLanguage() == "ar" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameAr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }
                                    }
                                    viewModel.readLanguage() == "fr" -> {

                                        val size =
                                            fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                        if (size > 0) {
                                            val structureName =
                                                fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                    it!!.text == "NameFr"
                                                }!!.value
                                            if (structureName.isNullOrEmpty()) {
                                                structureItem.name =
                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                            } else {
                                                structureItem.name =
                                                    "$structureName / ${item.fullName}"
                                            }
                                        } else {
                                            structureItem.name =
                                                "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                        }
                                    }
                                }


                            }
                        }
                        structureItem.attributes = item.attributes
                        structureItem.structureIds = item.structureIds
                        structureItem.itemType = "user"


                        structuresArray!!.add(structureItem)
                    }


                }


            }

        }


        var arrayAdapter =
            StructuresAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                structuresArray,
                viewModel.readLanguage()

            )

        actvTransferautocomplete.setAdapter(arrayAdapter)

        actvTransferautocomplete.setOnClickListener {

            if (actvTransferautocomplete.text.toString().isNotEmpty()) {
                actvTransferautocomplete.showDropDown()

            } else {
                val arrayAdapterr =
                    StructuresAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        structuresArray,
                        viewModel.readLanguage()

                    )
                actvTransferautocomplete.setAdapter(arrayAdapterr)
                actvTransferautocomplete.showDropDown()

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

                    val userObject = selectedUserModel()
                    userObject.fromUserId = selectedObject.id!!
                    userObject.fromStructureId = selectedObject.structureIds!![0]
                    userObject.text = selectedObject.name
                    userObject.offlineId = mutliSelectionOfflineId
                    userObject.itemType = "user"
                    userObject.userPrivacyLevel = userPrivacyLevel

                    actvTransferautocomplete.setText("")
                    (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(userObject)
                    mutliSelectionOfflineId += 1

                    multiSelectionList.find { it.text == userObject.text }.let {
                        if (it == null) {
                            multiSelectionList.add(userObject)

                        }
                    }

                } else {
                    val userObject = selectedUserModel()
                    userObject.fromUserId = 0
                    userObject.fromStructureId = selectedObject.id!!
                    userObject.text = selectedObject.name
                    userObject.offlineId = mutliSelectionOfflineId
                    userObject.itemType = "structure"
                    userObject.userPrivacyLevel = userPrivacyLevel


                    actvTransferautocomplete.setText("")
                    (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(userObject)
                    mutliSelectionOfflineId += 1
                    structureSelectedId = selectedObject.id!!
                    structureSelectedName = selectedObject.name!!

                    multiSelectionList.find { it.text == userObject.text }.let {
                        if (it == null) {
                            multiSelectionList.add(userObject)

                        }
                    }

                }


            }


        actvTransferautocomplete.doOnTextChanged { text, start, before, count ->


            autoDispose.add(
                viewModel.getAvailableStructures(text.toString(), structureIds)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {


                            //manageResponse(it,structuresArray)

                            val allUsersAndStructures = ArrayList<AllStructuresItem>()
                            if (it.structures != null) {
                                allUsersAndStructures.addAll(it.structures)
                            }


                            val enableTransferToUser =
                                settings.find { it.keyword == "EnableTransferToUsers" }!!.content

                            if (enableTransferToUser == "true") {
                                if (it.users!!.size > 0) {

                                    for (item in it.users) {
                                        if (delegationId == 0) {
                                            if (item.id != viewModel.readUserinfo().id) {
                                                val structureItem = AllStructuresItem()
                                                structureItem.id = item.id
                                                fullStructures!!.find {
                                                    it.id == item.structureIds?.get(
                                                        0
                                                    )
                                                }?.name.let {
                                                    if (it != null) {

                                                        when {
                                                            viewModel.readLanguage() == "en" -> {
                                                                structureItem.name =
                                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                                            }
                                                            viewModel.readLanguage() == "ar" -> {
                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameAr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                            viewModel.readLanguage() == "fr" -> {
                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameFr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                        }


                                                    }
                                                }
                                                structureItem.attributes = item.attributes
                                                structureItem.structureIds = item.structureIds
                                                structureItem.itemType = "user"


                                                allUsersAndStructures.add(structureItem)
                                            }

                                        } else {
                                            val delegator = viewModel.readSavedDelegator()

                                            if (item.id != delegator!!.fromUserId && item.id != viewModel.readUserinfo().id) {
                                                val structureItem = AllStructuresItem()
                                                structureItem.id = item.id
                                                fullStructures!!.find {
                                                    it.id == item.structureIds?.get(
                                                        0
                                                    )
                                                }?.name.let {
                                                    if (it != null) {

                                                        when {
                                                            viewModel.readLanguage() == "en" -> {
                                                                structureItem.name =
                                                                    "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"
                                                            }
                                                            viewModel.readLanguage() == "ar" -> {

                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameAr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                            viewModel.readLanguage() == "fr" -> {
                                                                val size =
                                                                    fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.size
                                                                if (size > 0) {
                                                                    val structureName =
                                                                        fullStructures.find { it.id == item.structureIds!![0] }!!.attributes!!.find {
                                                                            it!!.text == "NameFr"
                                                                        }!!.value
                                                                    if (structureName.isNullOrEmpty()) {
                                                                        structureItem.name =
                                                                            "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                    } else {
                                                                        structureItem.name =
                                                                            "$structureName / ${item.fullName}"
                                                                    }
                                                                } else {
                                                                    structureItem.name =
                                                                        "${fullStructures.find { it.id == item.structureIds!![0] }!!.name} / ${item.fullName}"

                                                                }
                                                            }
                                                        }


                                                    }
                                                }
                                                structureItem.attributes = item.attributes
                                                structureItem.structureIds = item.structureIds
                                                structureItem.itemType = "user"


                                                allUsersAndStructures.add(structureItem)
                                            }

                                        }


                                    }
                                }
                            }

                            arrayAdapter =
                                StructuresAdapter(
                                    requireContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    allUsersAndStructures,
                                    viewModel.readLanguage()

                                )
                            actvTransferautocomplete.setAdapter(arrayAdapter)
                            if (actvTransferautocomplete.hasFocus()) {
                                actvTransferautocomplete.showDropDown()
                            } else {
                                actvTransferautocomplete.dismissDropDown()
                            }


                        }, {

                            Timber.e(it)

                        })
            )

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
                    if (selectedObject.itemType == "user") {
                        itemType = selectedObject.itemType!!
                        val privacy =
                            selectedObject.attributes!!.find { it!!.text == "Privacy" }!!.value
                        if (privacy != null) {
                            userPrivacyLevel =
                                viewModel.readprivacies()
                                    .find { it.id == privacy.toInt() }!!.level!!
                        }
                        userSelectedId = selectedObject.id!!
                        structureSelectedId = selectedObject.structureIds!![0]
                        structureSelectedName = selectedObject.name!!

                        val userObject = selectedUserModel()
                        userObject.fromUserId = selectedObject.id!!
                        userObject.fromStructureId = selectedObject.structureIds!![0]
                        userObject.text = selectedObject.name
                        userObject.offlineId = mutliSelectionOfflineId
                        userObject.itemType = "user"
                        userObject.userPrivacyLevel = userPrivacyLevel

                        actvTransferautocomplete.setText("")
                        (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(
                            userObject
                        )
                        mutliSelectionOfflineId += 1

                        multiSelectionList.find { it.text == userObject.text }.let {
                            if (it == null) {
                                multiSelectionList.add(userObject)

                            }
                        }

                    } else {
                        val userObject = selectedUserModel()
                        userObject.fromUserId = 0
                        userObject.fromStructureId = selectedObject.id!!
                        userObject.text = selectedObject.name
                        userObject.offlineId = mutliSelectionOfflineId
                        userObject.itemType = "structure"
                        userObject.userPrivacyLevel = userPrivacyLevel


                        actvTransferautocomplete.setText("")
                        (multiSelecteduser.adapter as SelectedUserAdapter).addSelectedUser(
                            userObject
                        )
                        mutliSelectionOfflineId += 1
                        structureSelectedId = selectedObject.id!!
                        structureSelectedName = selectedObject.name!!

                        multiSelectionList.find { it.text == userObject.text }.let {
                            if (it == null) {
                                multiSelectionList.add(userObject)

                            }
                        }

                    }


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


                        structureName = it.name!!


                    }, {

                        Timber.e(it)

                    })
        )
        Log.d("stresponsename", structureName)

        return structureName
    }


    private fun geStructureSendingRules(fromStructureId: Int) {
        val structureIds = ArrayList<Int>()


        viewModel.geStructureSendingRules(
            fromStructureId

        ).enqueue(object : Callback<ResponseBody> {


            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    var responseRecieved: Any? = null
                    responseRecieved = response.body()!!.string()

                    val jsonArray = JSONArray(responseRecieved)
                    val strArr = arrayOfNulls<String>(jsonArray.length())

                    for (i in 0 until jsonArray.length()) {
                        strArr[i] = jsonArray.getString(i)
                        structureIds.add(strArr[i]!!.toInt())
                    }

                    getAvailableStructures(structureIds)


                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                requireActivity().makeToast(requireActivity().getString(R.string.network_error))
            }

        }

        )


    }

    private fun getAvailableStructures(structureIds: ArrayList<Int>) {
        autoDispose.add(
            viewModel.getAvailableStructures("", structureIds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {


                        initAvailableStructuresAutoComplete(it, structureIds)
                    }, {
                        Timber.e(it)


                    })
        )


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

                                etDateFrom!!.setText(drString)
                                etDateFrom!!.clearFocus()

                            }
                            cmp < 0 -> {
                                etDateFrom!!.setText("")
                                requireActivity().hideKeyboard(requireActivity())
                                requireActivity().makeToast(getString(R.string.lessthan_duedate))
                            }
                            else -> {

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


    private fun addUserTransfer(
        userPrivacyLevel: Int,
        docPrivacyLevel: Int,
        selectedItem: selectedUserModel
    ) {
        var NoUserWithSelectedPrivacy = ""
        var ContinueConfirmation = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.en!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.ar!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.fr!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }
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


            (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(addedModel)


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
                    "${NoUserWithSelectedPrivacy}\n$structureSelectedName\n${
                        ContinueConfirmation
                    }"
                )
                .setPositiveButton(
                    yes,
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
                        (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                            addedModel
                        )


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
                    no,
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


    private fun addMultiUsersTransfer(
        userPrivacyLevel: Int,
        docPrivacyLevel: Int,
        selectedItem: selectedUserModel
    ) {

        val addedModel = TransferRequestModel()
        addedModel.DocumentId = model.documentId?.toString()
        addedModel.DocumentPrivacyId = model.privacyId?.toString()
        addedModel.PrivacyId = model.privacyId.toString()
        addedModel.purposeId = purposeSelectedId.toString()
        addedModel.FromStructureId = model.fromStructureId!!
        addedModel.toStructureId = selectedItem.fromStructureId!!
        addedModel.name = selectedItem.text
        addedModel.ParentTransferId = model.id
        addedModel.cced = isPurposeCCED == true
        addedModel.instruction = etInstructions.text.toString().trim()
        addedModel.dueDate = etDateFrom!!.text.toString().trim()
        addedModel.IsStructure = false
        addedModel.toUserId = selectedItem.fromUserId
        addedModel.purposeName = purposeSelectedName
        addedModel.transferOfflineId = offlineId

        Transfers.add(addedModel)
        offlineId += 1

        (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(addedModel)
        if (userPrivacyLevel < docPrivacyLevel) {
            lowPrivacyUsers = "$lowPrivacyUsers \n ${selectedItem.text}"
        }
    }

    private fun checkStructure(
        structureSelectedId: Int,
        documentPrivacyId: Int?,
        selectedItem: selectedUserModel
    ) {

        var NoStructureReceivers = ""


        when {
            viewModel.readLanguage() == "en" -> {

                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.fr!!

            }
        }
        val SendWithoutStructureReceiverOrPrivacyLevel =
            settings.find { it.keyword == "SendWithoutStructureReceiverOrPrivacyLevel" }!!.content


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

                            }

                        }

                        if (privacyList.size > 0) {
                            getStructurePrivacyLevel(
                                privacyList.maxOrNull() ?: 0,
                                documentPrivacyId
                            )

                        } else {
                            if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {
                                requireActivity().makeToast("$NoStructureReceivers${structureSelectedName}")
                                structurePrivacyLevel = 0


                            } else {
                                emptyStructureContinueConfirmDialogWithOptions(
                                    NoStructureReceivers,
                                    model
                                )
                            }
                        }


                    } else {

                        if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {
                            requireActivity().makeToast("$NoStructureReceivers${structureSelectedName}")
                            structurePrivacyLevel = 0


                        } else {
                            emptyStructureContinueConfirmDialogWithOptions(
                                NoStructureReceivers,
                                model
                            )
                        }


                    }


                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                requireActivity().makeToast(requireActivity().getString(R.string.network_error))
            }

        }

        )

    }


    private fun getStructurePrivacyLevel(structurePrivacyId: Int, documentPrivacyId: Int?) {

        var NoStructureReceiversWithSelectedPrivacy = ""


        when {
            viewModel.readLanguage() == "en" -> {

                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.fr!!

            }
        }
        val SendWithoutStructureReceiverOrPrivacyLevel =
            settings.find { it.keyword == "SendWithoutStructureReceiverOrPrivacyLevel" }!!.content

        structurePrivacyLevel =
            viewModel.readprivacies().find { it.id == structurePrivacyId }!!.level!!
        val docLevel = viewModel.readprivacies().find { it.id == documentPrivacyId }!!.level!!

        if (structurePrivacyLevel >= docLevel) {
            addStructureTransfer()
        } else {

            if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {
                requireActivity().makeToast("$NoStructureReceiversWithSelectedPrivacy${structureSelectedName}")
                structurePrivacyLevel = 0


            } else {
                emptyStructureContinueConfirmDialogWithOptions(
                    NoStructureReceiversWithSelectedPrivacy,
                    model
                )
            }
        }

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
        (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(addedModel)

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


    private fun checkMultiStructures(
        structureSelectedId: Int,
        documentPrivacyId: Int?,
        selectedItem: selectedUserModel,
        purposeSelectedId: Int,
        purposeSelectedName: String,
        dueDate: String,
        instruction: String
    ) {


        val SendWithoutStructureReceiverOrPrivacyLevel =
            settings.find { it.keyword == "SendWithoutStructureReceiverOrPrivacyLevel" }!!.content


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

                    Log.d("responseRecieved", response.body()!!.string())


                    val structuresItem = JSONObject(responseRecieved)
                    val userExistenceArray =
                        structuresItem.getJSONArray(structureSelectedId.toString())
                    if (userExistenceArray.length() > 0) {


                        (0 until userExistenceArray.length()).forEach { item ->

                            if (userExistenceArray[item] as String != "") {
                                privacyList.add((userExistenceArray[item] as String).toInt())

                            }

                        }

                        if (privacyList.size > 0) {
                            getMultiStructuresPrivacyLevel(
                                privacyList.maxOrNull() ?: 0,
                                documentPrivacyId,
                                selectedItem,
                                purposeSelectedId,
                                purposeSelectedName,
                                dueDate,
                                instruction
                            )

                        } else {
                            if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {

                                emptyStructureReceiversWithoutOptions =
                                    "$emptyStructureReceiversWithoutOptions \n ${selectedItem.text}"
                                structurePrivacyLevel = 0
                                val addedModel = TransferRequestModel()
                                addedModel.DocumentId = model.documentId?.toString()
                                addedModel.DocumentPrivacyId = model.privacyId?.toString()
                                addedModel.PrivacyId = model.privacyId.toString()
                                addedModel.purposeId = purposeSelectedId.toString()
                                addedModel.FromStructureId = model.fromStructureId!!
                                addedModel.toStructureId = selectedItem.fromStructureId!!
                                addedModel.name = selectedItem.text
                                addedModel.ParentTransferId = model.id
                                addedModel.cced = isPurposeCCED == true
                                addedModel.instruction = instruction
                                addedModel.dueDate = dueDate
                                addedModel.IsStructure = true
                                addedModel.purposeName = purposeSelectedName.toString()
                                addedModel.transferOfflineId = offlineId

                                Transfers.add(addedModel)
                                offlineId += 1
                                (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                                    addedModel
                                )

                            } else {

                                emptyStructureReceiversWithOptions =
                                    "$emptyStructureReceiversWithOptions \n ${selectedItem.text}"

                                structurePrivacyLevel = 0
                                val addedModel = TransferRequestModel()
                                addedModel.DocumentId = model.documentId?.toString()
                                addedModel.DocumentPrivacyId = model.privacyId?.toString()
                                addedModel.PrivacyId = model.privacyId.toString()
                                addedModel.purposeId = purposeSelectedId.toString()
                                addedModel.FromStructureId = model.fromStructureId!!
                                addedModel.toStructureId = selectedItem.fromStructureId!!
                                addedModel.name = selectedItem.text
                                addedModel.ParentTransferId = model.id
                                addedModel.cced = isPurposeCCED == true
                                addedModel.instruction = instruction
                                addedModel.dueDate = dueDate
                                addedModel.IsStructure = true
                                addedModel.purposeName = purposeSelectedName.toString()
                                addedModel.transferOfflineId = offlineId

                                Transfers.add(addedModel)
                                offlineId += 1
                                (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                                    addedModel
                                )

                            }
                        }


                    } else {

                        if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {
                            emptyStructureReceiversWithoutOptions =
                                "$emptyStructureReceiversWithoutOptions \n ${selectedItem.text}"
                            structurePrivacyLevel = 0
                            val addedModel = TransferRequestModel()
                            addedModel.DocumentId = model.documentId?.toString()
                            addedModel.DocumentPrivacyId = model.privacyId?.toString()
                            addedModel.PrivacyId = model.privacyId.toString()
                            addedModel.purposeId = purposeSelectedId.toString()
                            addedModel.FromStructureId = model.fromStructureId!!
                            addedModel.toStructureId = selectedItem.fromStructureId!!
                            addedModel.name = selectedItem.text
                            addedModel.ParentTransferId = model.id
                            addedModel.cced = isPurposeCCED == true
                            addedModel.instruction = instruction
                            addedModel.dueDate = dueDate
                            addedModel.IsStructure = true
                            addedModel.purposeName = purposeSelectedName
                            addedModel.transferOfflineId = offlineId

                            Transfers.add(addedModel)
                            offlineId += 1
                            (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                                addedModel
                            )

                        } else {

                            emptyStructureReceiversWithOptions =
                                "$emptyStructureReceiversWithOptions \n ${selectedItem.text}"

                            structurePrivacyLevel = 0
                            val addedModel = TransferRequestModel()
                            addedModel.DocumentId = model.documentId?.toString()
                            addedModel.DocumentPrivacyId = model.privacyId?.toString()
                            addedModel.PrivacyId = model.privacyId.toString()
                            addedModel.purposeId = purposeSelectedId.toString()
                            addedModel.FromStructureId = model.fromStructureId!!
                            addedModel.toStructureId = selectedItem.fromStructureId!!
                            addedModel.name = selectedItem.text
                            addedModel.ParentTransferId = model.id
                            addedModel.cced = isPurposeCCED == true
                            addedModel.instruction = instruction
                            addedModel.dueDate = dueDate
                            addedModel.IsStructure = true
                            addedModel.purposeName = purposeSelectedName
                            addedModel.transferOfflineId = offlineId

                            Transfers.add(addedModel)
                            offlineId += 1
                            (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                                addedModel
                            )
                        }


                    }


                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                requireActivity().makeToast(requireActivity().getString(R.string.network_error))
            }

        }

        )

    }


    private fun getMultiStructuresPrivacyLevel(
        structurePrivacyId: Int,
        documentPrivacyId: Int?,
        selectedItem: selectedUserModel,
        purposeSelectedId: Int,
        purposeSelectedName: String,
        dueDate: String,
        instruction: String
    ) {

        var NoStructureReceiversWithSelectedPrivacy = ""


        when {
            viewModel.readLanguage() == "en" -> {

                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.fr!!

            }
        }
        val SendWithoutStructureReceiverOrPrivacyLevel =
            settings.find { it.keyword == "SendWithoutStructureReceiverOrPrivacyLevel" }!!.content

        structurePrivacyLevel =
            viewModel.readprivacies().find { it.id == structurePrivacyId }!!.level!!
        val docLevel = viewModel.readprivacies().find { it.id == documentPrivacyId }!!.level!!


        if (structurePrivacyLevel >= docLevel) {
            //  addStructureTransfer()


            val addedModel = TransferRequestModel()
            addedModel.DocumentId = model.documentId?.toString()
            addedModel.DocumentPrivacyId = model.privacyId?.toString()
            addedModel.PrivacyId = model.privacyId.toString()
            addedModel.purposeId = purposeSelectedId.toString()
            addedModel.FromStructureId = model.fromStructureId!!
            addedModel.toStructureId = selectedItem.fromStructureId!!
            addedModel.name = selectedItem.text
            addedModel.ParentTransferId = model.id
            addedModel.cced = isPurposeCCED == true
            addedModel.instruction = instruction
            addedModel.dueDate = dueDate
            addedModel.IsStructure = true
            addedModel.purposeName = purposeSelectedName
            addedModel.transferOfflineId = offlineId

            Transfers.add(addedModel)
            offlineId += 1
            (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(addedModel)

        } else {

            if (SendWithoutStructureReceiverOrPrivacyLevel == "false") {
                requireActivity().makeToast("$NoStructureReceiversWithSelectedPrivacy${structureSelectedName}")
                structurePrivacyLevel = 0

                lowPrivacyStructuresWithoutOptions =
                    "$lowPrivacyStructuresWithoutOptions \n ${selectedItem.text}"

                val addedModel = TransferRequestModel()
                addedModel.DocumentId = model.documentId?.toString()
                addedModel.DocumentPrivacyId = model.privacyId?.toString()
                addedModel.PrivacyId = model.privacyId.toString()
                addedModel.purposeId = purposeSelectedId.toString()
                addedModel.FromStructureId = model.fromStructureId!!
                addedModel.toStructureId = selectedItem.fromStructureId!!
                addedModel.name = selectedItem.text
                addedModel.ParentTransferId = model.id
                addedModel.cced = isPurposeCCED == true
                addedModel.instruction = instruction
                addedModel.dueDate = dueDate
                addedModel.IsStructure = true
                addedModel.purposeName = purposeSelectedName
                addedModel.transferOfflineId = offlineId

                Transfers.add(addedModel)
                offlineId += 1
                (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(addedModel)


            } else {
                lowPrivacyStructuresWithOptions =
                    "$lowPrivacyStructuresWithOptions \n ${selectedItem.text}"

                val addedModel = TransferRequestModel()
                addedModel.DocumentId = model.documentId?.toString()
                addedModel.DocumentPrivacyId = model.privacyId?.toString()
                addedModel.PrivacyId = model.privacyId.toString()
                addedModel.purposeId = purposeSelectedId.toString()
                addedModel.FromStructureId = model.fromStructureId!!
                addedModel.toStructureId = selectedItem.fromStructureId!!
                addedModel.name = selectedItem.text
                addedModel.ParentTransferId = model.id
                addedModel.cced = isPurposeCCED == true
                addedModel.instruction = instruction
                addedModel.dueDate = dueDate
                addedModel.IsStructure = true
                addedModel.purposeName = purposeSelectedName
                addedModel.transferOfflineId = offlineId

                Transfers.add(addedModel)
                offlineId += 1
                (transfersgrids_recycler.adapter as TransferGrids_Adapter).addTransferGrid(
                    addedModel
                )

                //   emptyStructureContinueConfirmDialog(NoStructureReceiversWithSelectedPrivacy)
            }
        }

    }


    private fun emptyStructureContinueConfirmDialogWithOptions(
        emptySelectedStructures: String,
        model: CorrespondenceDataItem
    ) {
        var ContinueConfirmation = ""
        var NoStructureReceivers = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.en!!

                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }

        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
        var alertDialog = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(
                "$NoStructureReceivers\n$emptySelectedStructures\n${
                    ContinueConfirmation
                }"
            )
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    emptyStructureReceiversWithOptions = ""
                    if (lowPrivacyUsers == "" && lowPrivacyStructuresWithOptions == "") {
                        callTransferApi()
                    } else if (lowPrivacyUsers == "" && lowPrivacyStructuresWithOptions != "") {
                        lowPrivacyStructuresDialogWithOptions(
                            lowPrivacyStructuresWithOptions.trim(),
                            this.model
                        )

                    } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions == "") {
                        lowPrivacyUsersDialog(lowPrivacyUsers.trim(), this.model)
                    }

                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    refreshPage(model)
                    dialogInterface.dismiss()

                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

    }

    private fun emptyStructureContinueConfirmDialogWithoutOptions(
        emptySelectedStructures: String,
        model: CorrespondenceDataItem
    ) {


        var ContinueConfirmation = ""
        var NoStructureReceivers = ""
        var yes = ""
        var ok = ""
        var no = ""


        when {
            viewModel.readLanguage() == "en" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.en!!

                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!
                ok = translator.find { it.keyword == "OK" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!
                ok = translator.find { it.keyword == "OK" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                NoStructureReceivers =
                    translator.find { it.keyword == "NoStructureReceivers" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!
                ok = translator.find { it.keyword == "OK" }!!.fr!!


            }
        }

        val customDialog = Dialog(requireActivity(), R.style.ConfirmationStyle)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.empty_or_lowlevel_structure)

        customDialog.findViewById<TextView>(R.id.structure_msg).text =
            "$NoStructureReceivers\n$emptySelectedStructures"

        customDialog.findViewById<TextView>(R.id.strucutre_confirm_msg).text = ok

        customDialog.findViewById<TextView>(R.id.strucutre_confirm_msg).setOnClickListener {
            refreshPage(model)
            customDialog.dismiss()

        }
        customDialog.show()


    }

    private fun lowPrivacyStructuresDialogWithOptions(
        lowPrivacySelectedStructures: String,
        model: CorrespondenceDataItem
    ) {

        var ContinueConfirmation = ""
        var NoStructureReceiversWithSelectedPrivacy = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.en!!

                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }

        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
        var alertDialog = AlertDialog.Builder(requireActivity())

            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(
                "$NoStructureReceiversWithSelectedPrivacy\n $lowPrivacySelectedStructures\n${
                    ContinueConfirmation
                }"
            )
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
//                    lowPrivacyStructuresWithoutOptions = ""

                    if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions == "") {
                        callTransferApi()
                    } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions != "") {
                        emptyStructureContinueConfirmDialogWithOptions(
                            emptyStructureReceiversWithOptions.trim(),
                            model
                        )

                    } else if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions != "") {

                        emptyStructureContinueConfirmDialogWithOptions(
                            emptyStructureReceiversWithOptions.trim(),
                            model
                        )

                    } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions == "") {
                        lowPrivacyUsersDialog(lowPrivacyUsers.trim(), model)
                    }
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    refreshPage(model)
                    dialogInterface.dismiss()
                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

    }

    private fun lowPrivacyStructuresDialogWithoutOptions(
        lowPrivacySelectedStructures: String,
        model: CorrespondenceDataItem
    ) {

        var ContinueConfirmation = ""
        var NoStructureReceiversWithSelectedPrivacy = ""
        var yes = ""
        var no = ""
        var ok = ""

        when {
            viewModel.readLanguage() == "en" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.en!!

                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!
                ok = translator.find { it.keyword == "OK" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!
                ok = translator.find { it.keyword == "OK" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {

                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                NoStructureReceiversWithSelectedPrivacy =
                    translator.find { it.keyword == "NoStructureReceiversWithSelectedPrivacy" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!
                ok = translator.find { it.keyword == "OK" }!!.fr!!

            }
        }

        val customDialog = Dialog(requireActivity(), R.style.ConfirmationStyle)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.empty_or_lowlevel_structure)

        customDialog.findViewById<TextView>(R.id.structure_msg).text =
            "$NoStructureReceiversWithSelectedPrivacy\n $lowPrivacySelectedStructures"

        customDialog.findViewById<TextView>(R.id.strucutre_confirm_msg).text = ok


        customDialog.findViewById<TextView>(R.id.strucutre_confirm_msg).setOnClickListener {
            refreshPage(model)
            customDialog.dismiss()

        }
        customDialog.show()
    }

    private fun lowPrivacyUsersDialog(
        lowPrivacySelectedUsers: String,
        model: CorrespondenceDataItem
    ) {

        var NoUserWithSelectedPrivacy = ""
        var ContinueConfirmation = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.en!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.ar!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                NoUserWithSelectedPrivacy =
                    translator.find { it.keyword == "NoUserWithSelectedPrivacy" }!!.fr!!
                ContinueConfirmation =
                    translator.find { it.keyword == "ContinueConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }


        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()

        var alertDialog = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(
                "$NoUserWithSelectedPrivacy\n$lowPrivacySelectedUsers\n${ContinueConfirmation}"
            )
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    lowPrivacyUsers = ""

                    if (emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions == "") {
                        callTransferApi()
                    } else if (emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions != "") {
                        lowPrivacyStructuresDialogWithOptions(
                            lowPrivacyStructuresWithOptions.trim(),
                            this.model
                        )

                    } else if (emptyStructureReceiversWithOptions != "" && emptyStructureReceiversWithOptions == "") {
                        emptyStructureContinueConfirmDialogWithOptions(
                            emptyStructureReceiversWithOptions.trim(),
                            model
                        )
                    }

                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    refreshPage(model)
                    dialogInterface.dismiss()

                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private fun sendTransfer(model: CorrespondenceDataItem) {

        val SendWithoutStructureReceiverOrPrivacyLevel =
            settings.find { it.keyword == "SendWithoutStructureReceiverOrPrivacyLevel" }!!.content

        if (SendWithoutStructureReceiverOrPrivacyLevel == "true") {

            if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions == "") {
                callTransferApi()
            } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions != "" && lowPrivacyStructuresWithOptions != "") {

                lowPrivacyStructuresDialogWithOptions(lowPrivacyStructuresWithOptions.trim(), model)

            } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions == "") {

                lowPrivacyUsersDialog(lowPrivacyUsers.trim(), model)

            } else if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions != "" && lowPrivacyStructuresWithOptions == "") {

                emptyStructureContinueConfirmDialogWithOptions(
                    emptyStructureReceiversWithOptions.trim(),
                    model
                )

            } else if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions != "") {

                lowPrivacyStructuresDialogWithOptions(lowPrivacyStructuresWithOptions.trim(), model)

            } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions != "" && lowPrivacyStructuresWithOptions == "") {

                emptyStructureContinueConfirmDialogWithOptions(
                    emptyStructureReceiversWithOptions.trim(),
                    model
                )

            } else if (lowPrivacyUsers != "" && emptyStructureReceiversWithOptions == "" && lowPrivacyStructuresWithOptions != "") {

                lowPrivacyStructuresDialogWithOptions(lowPrivacyStructuresWithOptions.trim(), model)

            } else if (lowPrivacyUsers == "" && emptyStructureReceiversWithOptions != "" && lowPrivacyStructuresWithOptions != "") {

                lowPrivacyStructuresDialogWithOptions(lowPrivacyStructuresWithOptions.trim(), model)
            }


        } else {
            when {
                lowPrivacyStructuresWithoutOptions != "" -> {
                    lowPrivacyStructuresDialogWithoutOptions(
                        lowPrivacyStructuresWithoutOptions.trim(),
                        model
                    )
                }
                emptyStructureReceiversWithoutOptions != "" -> {

                    emptyStructureContinueConfirmDialogWithoutOptions(
                        emptyStructureReceiversWithoutOptions.trim(), model
                    )
                }
                lowPrivacyUsers != "" -> {

                    lowPrivacyUsersDialog(lowPrivacyUsers.trim(), model)

                }
                else -> {
                    callTransferApi()
                }
            }
        }


    }

    private fun callTransferApi() {
        dialog = requireActivity().launchLoadingDialog()

        var fileInUSe = ""
        var originalDocumentInUse = ""

        when {
            viewModel.readLanguage() == "en" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.en!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.ar!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.fr!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.fr!!

            }
        }



        autoDispose.add(
            viewModel.transferTransfer(Transfers, delegationId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        if (it[0].updated == true) {
                            Transfers.clear()

                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceFragment().apply {
                                        arguments = bundleOf(
                                            Pair(
                                                Constants.NODE_INHERIT,
                                                viewModel.readCurrentNode()
                                            )
                                        )
                                    }
                                )
                            }
                        } else if (it[0].updated == false && it[0].message == "OriginalFileInUse") {
                            requireActivity().makeToast(originalDocumentInUse)

                        } else if (it[0].updated == false && it[0].message == "FileInUse") {
                            requireActivity().makeToast(fileInUSe)
                        }

                        dialog!!.dismiss()
                    }, {
                        dialog!!.dismiss()

                        Timber.e(it)

                    })
        )
    }

    private fun refreshPage(model: CorrespondenceDataItem) {
        (activity as AppCompatActivity).supportFragmentManager.commit {
            replace(R.id.fragmentContainer,
                AddTransferFragment().apply {
                    arguments = bundleOf(
                        Pair(
                            Constants.Correspondence_Model, model
                        )
                    )


                }
            )

        }
    }

    override fun onDeleteClicked(transferId: Int) {
        (transfersgrids_recycler.adapter as TransferGrids_Adapter).removeTransferGrid(transferId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Transfers.removeIf {
                transferId == it.transferOfflineId
            }
        }

    }

    override fun onDeleteSelectedUSerClick(offlineId: Int) {


        (multiSelecteduser.adapter as SelectedUserAdapter).removeSelectedUser(offlineId)

        multiSelectionList.find { it.offlineId == offlineId }.let {
            multiSelectionList.remove(it)
        }
    }

}

