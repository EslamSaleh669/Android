package intalio.cts.mobile.android.ui.fragment.reply

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.*
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.util.*

import kotlinx.android.synthetic.main.fragment_reply.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList


class ReplyToUserFragment : Fragment() {

    private var purposeSelectedId = 0
    private var datePickerDialog: DatePickerDialog? = null
    private var dateListener: OnDateSetListener? = null
    private var etDateFrom: EditText? = null

    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>


    private var selectedDate: String? = null
    private lateinit var userInfo: UserFullDataResponseItem
    private var fromDateSelectedInDays = 0

    private var addedCategoriesIds = ArrayList<Int>()
    private var delegationId = 0

    private lateinit var model: CorrespondenceDataItem


    @Inject
    @field:Named("replytransfer")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ReplyViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReplyViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_reply, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        btnCancelreply.setOnClickListener {
            activity?.onBackPressed()
        }


        translator = viewModel.readDictionary()!!.data!!
        setLabels()

        userInfo = viewModel.readUserinfo()
        val result = arguments?.getSerializable(Constants.Correspondence_Model)
        if (result.toString() != "null") {
            model = result as CorrespondenceDataItem
            tvCommentReplyTitle.text = model.createdByUser

        }
        initPurposeComplete()

        initDates()

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

        btnSendReply.setOnClickListener {

            if (purposeSelectedId == 0) {
                requireActivity().makeToast(requiredFields)


            } else {
                dialog = requireContext().launchLoadingDialog()
                replyToUser(delegationId)
            }

        }


    }

    private fun replyToUser(delegationId: Int) {

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


        val recievers = ArrayList<Int>()
        if (model.receivingEntityId!!.size > 0) {
            for (item in model.receivingEntityId!!) {
                recievers.add(item.id!!)

            }
        }
        viewModel.replyToUser(
            model.documentId!!,
            model.id!!,
            purposeSelectedId,
            etDateFrom!!.text.toString().trim(),
            etCommentReply.text.toString().trim(),
            model.fromStructureId!!,
            recievers.toTypedArray(),
            delegationId
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                dialog!!.dismiss()

                    var responseRecieved: Any? = null
                    responseRecieved = response.body()!!.string()

                    if (responseRecieved.toString().isEmpty()) {

                        (activity as AppCompatActivity).supportFragmentManager.commit {
                            replace(R.id.fragmentContainer,
                                CorrespondenceFragment().apply {
                                    arguments = bundleOf(
                                        Pair(Constants.NODE_INHERIT, viewModel.readCurrentNode())
                                    )

                                }
                            )

                        }
                    } else {
                        if (responseRecieved.toString() == "FileInUse") {
                            requireActivity().makeToast(fileInUSe)
                        } else if (responseRecieved.toString() == "OriginalFileInUse") {
                            requireActivity().makeToast(originalDocumentInUse)

                        }
                    }



            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialog!!.dismiss()
                requireActivity().makeToast(t.toString())
            }
        })


    }

    private fun initPurposeComplete() {

        replypurposeautocomplete.threshold = 0
        val purposesAutoCompleteArray = viewModel.readPurposesData()!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            purposesAutoCompleteArray.removeIf { it.cCed == true }
        }
        val arrayAdapter =
            PurposesStructureAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                purposesAutoCompleteArray
            )
        replypurposeautocomplete.setAdapter(arrayAdapter)




        replypurposeautocomplete.setOnClickListener {
            replypurposeautocomplete.showDropDown()

        }


        replypurposeautocomplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                replypurposeautocomplete.showDropDown()

            } else {
                //  replypurposeautocomplete.setText("")
            }

        }



        replypurposeautocomplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                replypurposeautocomplete.clearFocus()
                replypurposeautocomplete.dismissDropDown()
                val selectedObject = parent!!.getItemAtPosition(position) as PurposesResponseItem
                replypurposeautocomplete.setText(selectedObject.text.toString())
                purposeSelectedId = selectedObject.id!!

            }


    }

    private fun initDates() {


        var cancel = ""
        var ok = ""
        when {
            viewModel.readLanguage() == "en" -> {


                cancel = translator.find { it.keyword == "Cancel" }!!.en.toString()
                ok = translator.find { it.keyword == "OK" }!!.en.toString()
            }
            viewModel.readLanguage() == "ar" -> {


                cancel = translator.find { it.keyword == "Cancel" }!!.ar.toString()
                ok = translator.find { it.keyword == "OK" }!!.ar.toString()

            }
            viewModel.readLanguage() == "fr" -> {


                cancel = translator.find { it.keyword == "Cancel" }!!.fr.toString()
                ok = translator.find { it.keyword == "OK" }!!.fr.toString()

            }
        }

        val cal = Calendar.getInstance()
        val density = resources.displayMetrics.densityDpi.toFloat()

        etDateFrom = requireActivity().findViewById<View>(R.id.etreplyDueDate) as EditText?

        dateListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {

                    selectedDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    val drString: String =
                        changeDateFormat("mm/dd/yyyy", "dd/mm/yyyy", selectedDate)!!
                    fromDateSelectedInDays = (monthOfYear + 1) * 31 + dayOfMonth + year * 12 * 31
                    etDateFrom!!.setText(drString)
                    etDateFrom!!.clearFocus()


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
//
//        datePickerDialog!!.datePicker.minDate = cal.timeInMillis
//        val dueDate = model.dueDate
//
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale(viewModel.readLanguage()))
//
//        if (model.dueDate != ""){
//            val maxDate: Date = sdf.parse(dueDate)
//            val maxCalendar = Calendar.getInstance()
//            maxCalendar.time = maxDate
//            datePickerDialog!!.datePicker.maxDate = maxCalendar.timeInMillis
//
//        }

        datePickerDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, cancel,
            DialogInterface.OnClickListener { dialog, which ->
                requireActivity().hideKeyboard(requireActivity())
                etDateFrom!!.setText("")

            })

//        datePickerDialog!!.setButton(DialogInterface.BUTTON_POSITIVE, ok,
//            DialogInterface.OnClickListener { dialog, which ->
//                requireActivity().hideKeyboard(requireActivity())
//                etDateFrom!!.setText("")
//
//            })


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
        val inputDate: DateFormat = SimpleDateFormat(input, Locale(viewModel.readLanguage()))
        val outputDate: DateFormat = SimpleDateFormat(output, Locale(viewModel.readLanguage()))
        val date = inputDate.parse(dateString!!)
        return outputDate.format(date!!)
    }

    private fun setLabels() {

        when {
            viewModel.readLanguage() == "en" -> {

                replyto_label.text = translator.find { it.keyword == "ToUser" }!!.en
                textViewPurpose_label.text = translator.find { it.keyword == "Purposes" }!!.en
                replypurposeautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.en
                textViewTransferDueDate_label.text =
                    translator.find { it.keyword == "DueDate" }!!.en
                etreplyDueDate.hint = translator.find { it.keyword == "FromTransferDate" }!!.en
                message_label.text = translator.find { it.keyword == "Instruction" }!!.en
                btnSendReply.text = translator.find { it.keyword == "Reply" }!!.en
                btnCancelreply.text = translator.find { it.keyword == "Close" }!!.en
                centered_txt.text = translator.find { it.keyword == "ReplyToUser" }!!.en

                requiredpurpose_label.text = "(required)"
            }
            viewModel.readLanguage() == "ar" -> {

                replyto_label.text = translator.find { it.keyword == "ToUser" }!!.ar
                textViewPurpose_label.text = translator.find { it.keyword == "Purposes" }!!.ar
                replypurposeautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.ar
                textViewTransferDueDate_label.text =
                    translator.find { it.keyword == "DueDate" }!!.ar
                etreplyDueDate.hint = translator.find { it.keyword == "FromTransferDate" }!!.ar
                message_label.text = translator.find { it.keyword == "Instruction" }!!.ar
                btnSendReply.text = translator.find { it.keyword == "Reply" }!!.ar
                btnCancelreply.text = translator.find { it.keyword == "Close" }!!.ar
                centered_txt.text = translator.find { it.keyword == "ReplyToUser" }!!.ar

                requiredpurpose_label.text = "(الزامي)"

            }
            viewModel.readLanguage() == "fr" -> {

                replyto_label.text = translator.find { it.keyword == "ToUser" }!!.fr
                textViewPurpose_label.text = translator.find { it.keyword == "Purposes" }!!.fr
                replypurposeautocomplete.hint = translator.find { it.keyword == "Purposes" }!!.fr
                textViewTransferDueDate_label.text =
                    translator.find { it.keyword == "DueDate" }!!.fr
                etreplyDueDate.hint = translator.find { it.keyword == "FromTransferDate" }!!.fr
                message_label.text = translator.find { it.keyword == "Instruction" }!!.fr
                btnSendReply.text = translator.find { it.keyword == "Reply" }!!.fr
                btnCancelreply.text = translator.find { it.keyword == "Close" }!!.fr
                centered_txt.text = translator.find { it.keyword == "ReplyToUser" }!!.fr

                requiredpurpose_label.text = "(requis)"
            }
        }
    }


}