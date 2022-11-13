package intalio.cts.mobile.android.ui.fragment.metadata

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.MetaDataResponse
import intalio.cts.mobile.android.util.*
import intalio.cts.mobile.android.viewer.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_metadata.*

import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import org.json.JSONArray
import java.util.*


class MetaDataFragment : Fragment() {
    private var TransferId: Int = 0
    private lateinit var translator: ArrayList<DictionaryDataItem>

    private var delegationId = 0

    @Inject
    @field:Named("metadata")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MetaDataViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MetaDataViewModel::class.java)
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

        return inflater.inflate(R.layout.fragment_metadata, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        translator = viewModel.readDictionary()!!.data!!
        setLabels()

        back_icon.setOnClickListener {

            activity?.onBackPressed()

        }


        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it

            Log.d("pattthy",viewModel.readPath())
            if (viewModel.readPath() == "node" ) {
                requireArguments().getString(Constants.NODE_INHERIT).let { nodeID ->
                    if (nodeID == "MyRequests") {
                        getRequestedDocumentInfo(TransferId)
                    } else if (nodeID == "Closed"){
                        getSearchDocumentInfo(TransferId,delegationId)

                    } else {
                        getMetaData(TransferId,delegationId)

                    }
                }

            }else if (viewModel.readPath() == "attachment"){
                requireArguments().getString(Constants.LATEST_PATH).let { latest_path ->

                    when (latest_path) {
                        "node" -> {
                            getMetaData(TransferId,delegationId)

                        }
                        "requested node" -> {
                            getRequestedDocumentInfo(TransferId)

                        }
                        "closed node" -> {
                            getSearchDocumentInfo(TransferId,delegationId)

                        }
                        else -> {
                            getSearchDocumentInfo(TransferId,delegationId)

                        }
                    }

                }

            }
            else {
                getSearchDocumentInfo(TransferId,delegationId)
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getMetaData(TransferId: Int,delegationId:Int) {
        val statuses = viewModel.readStatuses()
        val priorities = viewModel.readPriorities()
        val privacies = viewModel.readprivacies()

        autoDispose.add(viewModel.getMetaDataInfo(TransferId,delegationId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    dialog!!.dismiss()

                    if (it.toString().isNotEmpty()) {

                        sending_entity.text = it.sendingEntity!!.text

                        if (it.receivingEntities!!.size > 1) {
                            var fulltxt = ""
                            for (item in it.receivingEntities) {
                                if (fulltxt.isNotEmpty()) {
                                    fulltxt = "$fulltxt / ${item!!.text}"

                                } else {
                                    fulltxt = item!!.text.toString()
                                }
                            }
                            receiving_entity.text = fulltxt
                        } else {
                            receiving_entity.text = it.receivingEntities[0]!!.text
                        }

                        subject.text = it.subject
                        reference_number.text = it.referenceNumber
                        if (!it.dueDate.isNullOrEmpty()) {
                            transfer_due_date.text = it.dueDate
                        } else {
                            transfer_due_date.text = "-"
                        }


                        for (item in statuses) {
                            if (item.id == it.status) {
                                status.text = item.text
                            }
                        }

                        for (item in privacies) {
                            if (item.id == it.privacyId) {
                                privacy.text = item.text
                            }
                        }

                        for (item in priorities) {
                            if (item.id == it.priorityId) {
                                priority.text = item.text
                            }
                        }

                        if (it.customAttributes != null && it.customAttributesTranslation != null && it.formData != "[]") {
                            testFormData(it)

                        }

                    }


                }, {
                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getSearchDocumentInfo(transferId: Int,delegationId:Int) {
        val statuses = viewModel.readStatuses()
        val priorities = viewModel.readPriorities()
        val privacies = viewModel.readprivacies()

        autoDispose.add(viewModel.getSearchDocumentInfo(transferId,delegationId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    dialog!!.dismiss()
                    if (it.toString().isNotEmpty()) {
                        sending_entity.text = it.sendingEntity!!.text
                        receiving_entity.text = it.receivingEntities!![0]!!.text
                        subject.text = it.subject
                        reference_number.text = it.referenceNumber
                        if (!it.dueDate.isNullOrEmpty()) {
                            transfer_due_date.text = it.dueDate
                        } else {
                            transfer_due_date.text = "-"
                        }

                        for (item in statuses) {
                            if (item.id == it.status) {
                                status.text = item.text
                            }
                        }

                        for (item in privacies) {
                            if (item.id == it.privacyId) {
                                privacy.text = item.text
                            }
                        }

                        for (item in priorities) {
                            if (item.id == it.priorityId) {
                                priority.text = item.text
                            }
                        }



                        if (it.customAttributes != null && it.customAttributesTranslation != null && it.formData != "[]") {
                            testFormData(it)

                        }
                    }


                }, {
                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getRequestedDocumentInfo(transferId: Int) {
        val statuses = viewModel.readStatuses()
        val priorities = viewModel.readPriorities()
        val privacies = viewModel.readprivacies()


        autoDispose.add(viewModel.getDocument(transferId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    dialog!!.dismiss()
                    if (it.toString().isNotEmpty()) {
                        sending_entity.text = it.sendingEntity!!.text
                        if (it.receivingEntities!!.size > 1) {
                            var fulltxt = ""
                            for (item in it.receivingEntities) {
                                fulltxt = "$fulltxt - ${item!!.text}"
                            }
                            receiving_entity.text = fulltxt
                        } else {
                            receiving_entity.text = it.receivingEntities[0]!!.text
                        }

                        subject.text = it.subject
                        reference_number.text = it.referenceNumber


                        if (!it.dueDate.isNullOrEmpty()) {
                            transfer_due_date.text = it.dueDate
                        } else {
                            transfer_due_date.text = "-"
                        }


                        for (item in statuses) {
                            if (item.id == it.status) {
                                status.text = item.text
                            }
                        }

                        for (item in privacies) {
                            if (item.id == it.privacyId) {
                                privacy.text = item.text
                            }
                        }

                        for (item in priorities) {
                            if (item.id == it.priorityId) {
                                priority.text = item.text
                            }
                        }



                        if (it.customAttributes != null && it.customAttributesTranslation != null && it.formData != "[]") {
                            testFormData(it)

                        }
                    }


                }, {
                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun testFormData(metaDataResponse: MetaDataResponse) {

        val customAttributesObject = JSONObject(metaDataResponse.customAttributes)
        val customAttributesTranslation = JSONArray(metaDataResponse.customAttributesTranslation)


        val keyFont = ResourcesCompat.getFont(requireActivity(), R.font.myriadpro_bold)
        val valueFont = ResourcesCompat.getFont(requireActivity(), R.font.myriadpro_regular)
        val keyParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        keyParams.setMargins(0, 44, 0, 0)
        val valueParam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        valueParam.setMargins(0, 20, 0, 0)

        val viewParam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )
        viewParam.setMargins(0, 30, 0, 0)


        if (metaDataResponse.formData != "[]") {

            val jsonObject = JSONObject(metaDataResponse.formData!!)
            val iterator = jsonObject.keys()

            while (iterator.hasNext()) {
                val customAttributesArray = customAttributesObject.getJSONArray("components")
                val key = iterator.next()
                val label = getLabel(customAttributesArray, key, customAttributesTranslation)


                if (label.toCharArray().isNotEmpty()) {

                    val formattedKey: String = Utility.toFormattedCase(
                        label.replace("(.)(\\p{Upper})".toRegex(), "$1_$2").lowercase()
                    )
                    val keyTxt = TextView(activity)
                    keyTxt.setTextColor(requireActivity().resources.getColor(R.color.black2))
                    keyTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    keyTxt.typeface = keyFont
                    keyTxt.layoutParams = keyParams


                    val valueTxt = TextView(activity)
                    valueTxt.setTextColor(requireActivity().resources.getColor(R.color.black2))
                    valueTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    valueTxt.typeface = valueFont
                    valueTxt.layoutParams = valueParam


                    val view = View(requireActivity())
                    view.setBackgroundColor(requireActivity().resources.getColor(R.color.viewcolor))
                    view.layoutParams = viewParam


                    if (jsonObject.getString(key).toCharArray().isNotEmpty()) {

                        val valueText = jsonObject.getString(key)
                        val itemType = getItemType(customAttributesArray,key)
                        Log.d("valuekeeyvalue", itemType)

                        // address condition
                        if (itemType == "address") {

                            var finaStringResult = ""
                            val valueJsonObject = JSONObject(valueText)
                            val valueIterator = valueJsonObject.keys()

                            while (valueIterator.hasNext()) {


                                val valueKey = valueIterator.next()


                                if (valueKey == "place_id") {


                                    val addressJsonObject =
                                        JSONObject(valueJsonObject.getString("address"))
                                    val addressIterator = addressJsonObject.keys()



                                    while (addressIterator.hasNext()) {
                                        val addressKey = addressIterator.next()

                                        if (addressJsonObject.get(addressKey).toString()
                                                .isNotEmpty()
                                        ) {

                                            finaStringResult += "${addressJsonObject.get(addressKey)}, "

                                        }


                                    }

                                    break


                                } else {

                                    finaStringResult += "$valueKey : "
                                    finaStringResult += "${valueJsonObject.get(valueKey)} \n"

                                }

                            }
                            keyTxt.text = formattedKey
                            valueTxt.text = finaStringResult
                            metadatalin.addView(keyTxt)
                            metadatalin.addView(valueTxt)
                            metadatalin.addView(view)

                        }


                        // radio group conditions
                        else if (itemType == "selectboxes") {

                            Log.d("conditons", "I am in checkboxes")
                            val valueJsonObject = JSONObject(valueText)
                            val valueIterator = valueJsonObject.keys()
                            var finaStringResult = ""
                            keyTxt.text = formattedKey
                            metadatalin.addView(keyTxt)

                            while (valueIterator.hasNext()) {
                                val valueKey = valueIterator.next()
                                if (valueJsonObject.get(valueKey).toString().isNotEmpty()) {

                                    finaStringResult += "$valueKey is ${valueJsonObject.get(valueKey)},\n "
                                    Log.d("finaStringResult", finaStringResult)

                                    val newBox = CheckBox(activity)
                                    newBox.text = valueKey
                                    newBox.isChecked =
                                        valueJsonObject.get(valueKey).toString().toBoolean()

                                    newBox.isClickable = false
                                    metadatalin.addView(newBox)
                                }

                            }


                            metadatalin.addView(view)

                        }

                        // multi selection conditions
                        else if (itemType == "select" && valueText.startsWith("[")) {

                            Log.d("conditons", "I am in select")
                            val jsonArray = JSONArray(valueText)
                             var finaStringResult = ""
                            keyTxt.text = formattedKey


                            for (i in 0 until jsonArray.length()) {
                                finaStringResult += "${jsonArray.getString(i)}\n"
                             }


                            valueTxt.text = finaStringResult


                            metadatalin.addView(keyTxt)
                            metadatalin.addView(valueTxt)
                            metadatalin.addView(view)

                        }

                        // checkbox conditions
                        else if (itemType == "checkbox") {

                            val newBox = CheckBox(activity)
                            newBox.text = ""
                            newBox.isChecked =
                                valueText.toBoolean()
                            newBox.isClickable = false

                            keyTxt.text = formattedKey
                            valueTxt.text = valueText
                            metadatalin.addView(keyTxt)
                            metadatalin.addView(newBox)
                            metadatalin.addView(view)
                        }

                        // Radio conditions
                        else if (itemType == "radio") {

                            val newBox = CheckBox(activity)
                            newBox.text = ""
                            newBox.isChecked = true
                            newBox.isClickable = false

                            keyTxt.text = valueText.toString()
                            metadatalin.addView(keyTxt)
                            metadatalin.addView(newBox)
                            metadatalin.addView(view)
                        }
                        else {
                            Log.d("conditons", "I am in else")

                            keyTxt.text = formattedKey
                            valueTxt.text = valueText
                            metadatalin.addView(keyTxt)
                            metadatalin.addView(valueTxt)
                            metadatalin.addView(view)
                        }


                    }

                }

            }


        }


    }

    private fun getLabel(
        customAttributesArray: JSONArray,
        key: String,
        customAttributesTranslation: JSONArray
    ): String {
        (0 until customAttributesArray.length()).forEach {
            val component = customAttributesArray.getJSONObject(it)

            if (key == component.get("key")) {
                val myLabel = component.get("label").toString()

                return getTranslation(customAttributesTranslation, myLabel)


            }

        }

        return ""
    }

    private fun getTranslation(
        customAttributesTranslation: JSONArray,
        myTranslatedLabel: String
    ): String {

        val language = viewModel.readLanguage()


        val languageModified =
            language.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        (0 until customAttributesTranslation.length()).forEach {
            val translatedComponent = customAttributesTranslation.getJSONObject(it)

            if (myTranslatedLabel == translatedComponent.get("Keyword")) {


                return translatedComponent.get(languageModified).toString()


            }

        }

        return ""
    }


    private fun getItemType(
        customAttributesArray: JSONArray,
        key: String
     ): String {
        (0 until customAttributesArray.length()).forEach {
            val component = customAttributesArray.getJSONObject(it)

            if (key == component.get("key")) {

                return component.get("type").toString()

            }

        }

        return ""
    }


    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {
                subjecttxt.text = translator.find { it.keyword == "Subject" }!!.en
                refnumtxt.text = translator.find { it.keyword == "ReferenceNumber" }!!.en
                sendingentity_txt.text = translator.find { it.keyword == "SendingEntity" }!!.en
                receivingentity_txt.text = translator.find { it.keyword == "ReceivingEntity" }!!.en
                duetdate_txt.text = translator.find { it.keyword == "DueDate" }!!.en
                statustxt.text = translator.find { it.keyword == "Status" }!!.en
                privacytxt.text = translator.find { it.keyword == "Privacy" }!!.en
                prioritytxt.text = translator.find { it.keyword == "Priority" }!!.en
                centered_txt.text = translator.find { it.keyword == "Attributes" }!!.en


            }
            viewModel.readLanguage() == "ar" -> {
                subjecttxt.text = translator.find { it.keyword == "Subject" }!!.ar
                refnumtxt.text = translator.find { it.keyword == "ReferenceNumber" }!!.ar
                sendingentity_txt.text = translator.find { it.keyword == "SendingEntity" }!!.ar
                receivingentity_txt.text = translator.find { it.keyword == "ReceivingEntity" }!!.ar
                duetdate_txt.text = translator.find { it.keyword == "DueDate" }!!.ar
                statustxt.text = translator.find { it.keyword == "Status" }!!.ar
                privacytxt.text = translator.find { it.keyword == "Privacy" }!!.ar
                prioritytxt.text = translator.find { it.keyword == "Priority" }!!.ar
                centered_txt.text = translator.find { it.keyword == "Attributes" }!!.ar


            }
            viewModel.readLanguage() == "fr" -> {
                subjecttxt.text = translator.find { it.keyword == "Subject" }!!.fr
                refnumtxt.text = translator.find { it.keyword == "ReferenceNumber" }!!.fr
                sendingentity_txt.text = translator.find { it.keyword == "SendingEntity" }!!.fr
                receivingentity_txt.text = translator.find { it.keyword == "ReceivingEntity" }!!.fr
                duetdate_txt.text = translator.find { it.keyword == "DueDate" }!!.fr
                statustxt.text = translator.find { it.keyword == "Status" }!!.fr
                privacytxt.text = translator.find { it.keyword == "Privacy" }!!.fr
                prioritytxt.text = translator.find { it.keyword == "Priority" }!!.fr
                centered_txt.text = translator.find { it.keyword == "Attributes" }!!.fr
            }
        }
    }


}