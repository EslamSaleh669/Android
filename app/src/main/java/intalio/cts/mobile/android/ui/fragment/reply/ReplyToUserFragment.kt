package intalio.cts.mobile.android.ui.fragment.reply

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
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.util.*

import kotlinx.android.synthetic.main.fragment_addtransfer.*
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

    private var selectedDate: String? = null
    private lateinit var userInfo: UserFullDataResponseItem
    private var fromDateSelectedInDays = 0

    private var addedCategoriesIds = ArrayList<Int>()


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



        centered_txt.text = requireActivity().getText(R.string.reply_to_user)
        userInfo = viewModel.readUserinfo()
        val result = arguments?.getSerializable(Constants.Correspondence_Model)
        if (result.toString() != "null") {
            model = result as CorrespondenceDataItem
            tvCommentReplyTitle.text = model.createdByUser

        }
        initPurposeComplete()

        initDates()

        btnSendReply.setOnClickListener {

            if (purposeSelectedId == 0 || etCommentReply.text.toString().trim() == "") {
                requireActivity().makeToast(getString(R.string.requiredField))

            } else {
                dialog = requireContext().launchLoadingDialog()
                replyToUser()
            }

        }


    }

    private fun replyToUser() {

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
            recievers.toTypedArray()
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                dialog!!.dismiss()

                if (response.code() != 200) {

                    requireActivity().makeToast(getString(R.string.network_error))
                    activity!!.onBackPressed()

                } else {
                    requireActivity().makeToast(getString(R.string.done))
                    (activity as AppCompatActivity).supportFragmentManager.commit {
                        replace(R.id.fragmentContainer,
                            CorrespondenceFragment().apply {
                                arguments = bundleOf(
                                    Pair(Constants.NODE_ID, 2)
                                )


                            }
                        )
                        addToBackStack("")

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

        datePickerDialog!!.datePicker.minDate = cal.timeInMillis
        val dueDate = model.dueDate

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        if (model.dueDate != ""){
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





}