package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_mytransfers.*
import kotlinx.android.synthetic.main.fragment_mytransfers.receiving_entity
import kotlinx.android.synthetic.main.fragment_mytransfers.sending_entity
import kotlinx.android.synthetic.main.fragment_mytransfers.subject
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class MyTransferFragment : Fragment() {
    private var TransferId: Int = 0
    private var lockedBy: String = ""
    private var lockedByDelegator: String = ""
    private var lockedDate: String = ""
    private lateinit var translator: ArrayList<DictionaryDataItem>
    private var delegationId = 0

    @Inject
    @field:Named("correspondenceDetails")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: CorrespondenceDetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CorrespondenceDetailsViewModel::class.java)
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

        return inflater.inflate(R.layout.fragment_mytransfers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (ContextCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) ActivityCompat.requestPermissions(
//            requireActivity(), arrayOf(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//            ), 1
//        )
        // showPdf()
        // getViewerPDF()

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        arguments?.getString(Constants.LOCKED_BY).let {
            lockedBy = it!!

        }

        arguments?.getString(Constants.LOCKED_BY).let {
            lockedByDelegator = it!!

        }

        arguments?.getString(Constants.LOCKED_DATE).let {
            lockedDate = it!!

        }



        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }


        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        setLabels()


        getMyTransfers(TransferId, delegationId)


    }


    private fun getMyTransfers(TransferId: Int, delegationId: Int) {

        autoDispose.add(viewModel.getMyTransfer(TransferId, delegationId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    dialog!!.dismiss()
                    if (it.toString().isNotEmpty()) {

                        if (it.subject.isNullOrEmpty()) {
                            subject.text = "-"
                        } else {
                            subject.text = it.subject
                        }

                        if (it.sendingEntity.isNullOrEmpty()) {
                            sending_entity.text = "-"
                        } else {
                            sending_entity.text = it.sendingEntity

                        }

                        if (it.receivingEntity.isNullOrEmpty()) {
                            receiving_entity.text = "-"
                        } else {
                            receiving_entity.text = it.receivingEntity
                        }

                        if (it.fromStructure.isNullOrEmpty()) {
                            from_structure.text = "-"
                        } else {
                            from_structure.text = it.fromStructure

                        }

                        if (it.fromUser.isNullOrEmpty()) {
                            from_user.text = "-"
                        } else {
                            from_user.text = it.fromUser

                        }
                        if (it.purpose.isNullOrEmpty()) {
                            purpose.text = "-"
                        } else {
                            purpose.text = it.purpose

                        }


                        if (it.createdDate.isNullOrEmpty()) {
                            created_date.text = "-"
                        } else {
                            created_date.text = it.createdDate

                        }

                        if (it.openedDate.isNullOrEmpty()) {
                            opened_date.text = "-"
                        } else {
                            opened_date.text = it.openedDate

                        }
                        if (it.dueDate.isNullOrEmpty()) {
                            due_date.text = "-"
                        } else {
                            due_date.text = it.dueDate

                        }

                        if (it.instruction.isNullOrEmpty()) {
                            instructions.text = "-"
                        } else {

                            instructions.text =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(
                                        it.instruction,
                                        Html.FROM_HTML_MODE_COMPACT
                                    )
                                } else {
                                    Html.fromHtml(it.instruction)
                                }

                        }

                        viewModel.readSavedDelegator().let {
                            if (it != null) {

                                if (it.fromUserId == 0) {
                                    if ((lockedBy == viewModel.readUserinfo().fullName || lockedBy == "you")) {
                                        when {
                                            viewModel.readLanguage() == "en" -> {
                                                lockedby.text =
                                                    translator.find { it.keyword == "You" }!!.en

                                            }
                                            viewModel.readLanguage() == "ar" -> {
                                                lockedby.text =
                                                    translator.find { it.keyword == "You" }!!.ar

                                            }
                                            viewModel.readLanguage() == "fr" -> {
                                                lockedby.text =
                                                    translator.find { it.keyword == "You" }!!.fr

                                            }
                                        }
                                    } else {
                                        lockedby.text = lockedBy

                                    }
                                } else {
                                    if ((lockedBy == it.fromUser || lockedBy == "delegator") && lockedByDelegator == "true") {
                                        when {
                                            viewModel.readLanguage() == "en" -> {

                                                lockedby.text =
                                                    "${translator.find { it.keyword == "You" }!!.en} ${translator.find { it.keyword == "OnBehalfOf" }!!.en} ${it.fromUser}"
                                            }
                                            viewModel.readLanguage() == "ar" -> {

                                                lockedby.text =
                                                    "${translator.find { it.keyword == "You" }!!.ar} ${translator.find { it.keyword == "OnBehalfOf" }!!.ar} ${it.fromUser}"

                                            }
                                            viewModel.readLanguage() == "fr" -> {

                                                lockedby.text =
                                                    "${translator.find { it.keyword == "You" }!!.fr} ${translator.find { it.keyword == "OnBehalfOf" }!!.fr} ${it.fromUser}"
                                            }
                                        }
                                    }  else {
                                        lockedby.text = lockedBy

                                    }
                                }

                            } else {
                                if ((lockedBy == viewModel.readUserinfo().fullName || lockedBy == "you")) {
                                    when {
                                        viewModel.readLanguage() == "en" -> {
                                            lockedby.text =
                                                translator.find { it.keyword == "You" }!!.en

                                        }
                                        viewModel.readLanguage() == "ar" -> {
                                            lockedby.text =
                                                translator.find { it.keyword == "You" }!!.ar

                                        }
                                        viewModel.readLanguage() == "fr" -> {
                                            lockedby.text =
                                                translator.find { it.keyword == "You" }!!.fr

                                        }
                                    }
                                } else {
                                    lockedby.text = lockedBy

                                }
                            }
                        }


                        lockeddate.text = lockedDate


                    }


                }, {
                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }

    private fun setLabels() {

        translator = viewModel.readDictionary()!!.data!!

        when {
            viewModel.readLanguage() == "en" -> {

                fromstructurelabel.text = translator.find { it.keyword == "FromStructure" }!!.en
                fromuserlabel.text = translator.find { it.keyword == "FromUser" }!!.en
                purposelabel.text = translator.find { it.keyword == "Purposes" }!!.en
                createddatelabel.text = translator.find { it.keyword == "CreatedDate" }!!.en
                opendatelabel.text = translator.find { it.keyword == "OpenedDate" }!!.en
                duedatelabel.text = translator.find { it.keyword == "DueDate" }!!.en
                instructionslabel.text = translator.find { it.keyword == "Instruction" }!!.en
                subjectlabel.text = translator.find { it.keyword == "Subject" }!!.en
                sending_entity_label.text = translator.find { it.keyword == "SendingEntity" }!!.en
                receiving_entity_label.text =
                    translator.find { it.keyword == "ReceivingEntity" }!!.en
                lockedbylabel.text = translator.find { it.keyword == "LockedBy" }!!.en
                lockeddatelabel.text = translator.find { it.keyword == "LockedDate" }!!.en
                centered_txt.text = translator.find { it.keyword == "MyTransfer" }!!.en


            }
            viewModel.readLanguage() == "ar" -> {

                fromstructurelabel.text = translator.find { it.keyword == "FromStructure" }!!.ar
                fromuserlabel.text = translator.find { it.keyword == "FromUser" }!!.ar
                purposelabel.text = translator.find { it.keyword == "Purposes" }!!.ar
                createddatelabel.text = translator.find { it.keyword == "CreatedDate" }!!.ar
                opendatelabel.text = translator.find { it.keyword == "OpenedDate" }!!.ar
                duedatelabel.text = translator.find { it.keyword == "DueDate" }!!.ar
                instructionslabel.text = translator.find { it.keyword == "Instruction" }!!.ar
                subjectlabel.text = translator.find { it.keyword == "Subject" }!!.ar
                sending_entity_label.text = translator.find { it.keyword == "SendingEntity" }!!.ar
                receiving_entity_label.text =
                    translator.find { it.keyword == "ReceivingEntity" }!!.ar
                lockedbylabel.text = translator.find { it.keyword == "LockedBy" }!!.ar
                lockeddatelabel.text = translator.find { it.keyword == "LockedDate" }!!.ar
                centered_txt.text = translator.find { it.keyword == "MyTransfer" }!!.ar


            }
            viewModel.readLanguage() == "fr" -> {

                fromstructurelabel.text = translator.find { it.keyword == "FromStructure" }!!.fr
                fromuserlabel.text = translator.find { it.keyword == "FromUser" }!!.fr
                purposelabel.text = translator.find { it.keyword == "Purposes" }!!.fr
                createddatelabel.text = translator.find { it.keyword == "CreatedDate" }!!.fr
                opendatelabel.text = translator.find { it.keyword == "OpenedDate" }!!.fr
                duedatelabel.text = translator.find { it.keyword == "DueDate" }!!.fr
                instructionslabel.text = translator.find { it.keyword == "Instruction" }!!.fr
                subjectlabel.text = translator.find { it.keyword == "Subject" }!!.fr
                sending_entity_label.text = translator.find { it.keyword == "SendingEntity" }!!.fr
                receiving_entity_label.text =
                    translator.find { it.keyword == "ReceivingEntity" }!!.fr
                lockedbylabel.text = translator.find { it.keyword == "LockedBy" }!!.fr
                lockeddatelabel.text = translator.find { it.keyword == "LockedDate" }!!.fr
                centered_txt.text = translator.find { it.keyword == "MyTransfer" }!!.fr


            }
        }
    }

    private fun getViewerPDF(): Long {


        val downloadReference: Long
        val dm: DownloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse("http://192.168.1.11:8080/viewer/api/document/34079/version/1/view")

        val request = DownloadManager.Request(uri)
        request.addRequestHeader(
            "Authorization",
            "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjAzMkQzMkYyMUFGRjc5OTZDOTc2NzBBNDcyNTdFRjU3IiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2NjQxODU0MjAsImV4cCI6MTY2NDIyMTQyMCwiaXNzIjoiaHR0cDovLzE5Mi4xNjguMS4xMTo5OTQ5IiwiYXVkIjpbIklkZW50aXR5U2VydmVyQXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiY2xpZW50X2lkIjoiNWM3ZDM2OGMtMjA1My00MGE1LWIwMDUtY2FhNjY0N2EwMDcwIiwic3ViIjoiMjAzMjEiLCJhdXRoX3RpbWUiOjE2NjQxODU0MTksImlkcCI6ImxvY2FsIiwiRGlzcGxheU5hbWUiOiJlc2xhbSBzYWxlaCIsIkxvZ2luUHJvdmlkZXJUeXBlIjoxLCJFbWFpbCI6ImVAcy5jb20iLCJJZCI6MjAzMjEsIkZpcnN0TmFtZSI6ImVzbGFtIiwiTGFzdE5hbWUiOiJzYWxlaCIsIk1pZGRsZU5hbWUiOiJzYWxlaCIsIlN0cnVjdHVyZUlkIjoiMSIsIk1hbmFnZXJJZCI6IiIsIlN0cnVjdHVyZUlkcyI6IjEiLCJHcm91cElkcyI6IiIsIlN0cnVjdHVyZVNlbmRlciI6InRydWUiLCJTdHJ1Y3R1cmVSZWNlaXZlciI6InRydWUiLCJQcml2YWN5IjoiMiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IkNvbnRyaWJ1dGUiLCJBcHBsaWNhdGlvblJvbGVJZCI6IjMiLCJDbGllbnRzIjpbIntcIlJvbGVJZFwiOjMsXCJSb2xlXCI6XCJDb250cmlidXRlXCIsXCJDbGllbnRJZFwiOlwiNWM3ZDM2OGMtMjA1My00MGE1LWIwMDUtY2FhNjY0N2EwMDcwXCJ9Iiwie1wiUm9sZUlkXCI6MSxcIlJvbGVcIjpcIkFkbWluaXN0cmF0b3JcIixcIkNsaWVudElkXCI6XCJmY2Q2ZDZjMC0xOWMwLTQ4YjctYjk5Mi0xNWFiMGI0MmM3MzNcIn0iLCJ7XCJSb2xlSWRcIjoxLFwiUm9sZVwiOlwiQWRtaW5pc3RyYXRvclwiLFwiQ2xpZW50SWRcIjpcIjI3ZmZkOTAxLWFhOGEtNGQyNi1hOTgzLTg2MDJjMGI1ZDBlZlwifSJdLCJqdGkiOiJCMjE0M0FBNzZEREVCOTNEQjc3OEREMjRFOUNFRDlFQiIsInNpZCI6IjEzM0Q3NEU3NjZDOEI5QTgwN0Q0MkEwOEVEMDdFOTBEIiwiaWF0IjoxNjY0MTg1NDIwLCJzY29wZSI6WyJvcGVuaWQiLCJJZGVudGl0eVNlcnZlckFwaSIsIm9mZmxpbmVfYWNjZXNzIl0sImFtciI6WyJwd2QiXX0.IhqjY8VF7iv73xjbs5ytKVau5cpWg4lvFvLqNv25mo1B3lDBFfZqKV7t1UpI3vhAlD9WCt3y3QY__v8rDow_byr9tXJmzTPOs0s73hgxb3ug9IIlMicc9MWtFmN4-HtZZuPHwwy2mruNvV484EUuT9qxJ7mwLW3dY8qDGI-jT9cbaePMv2J8nMBSG5vT4D2r9vEKNNP34RKmEZRySnWpB6YIfKG5dvfOTM8-LqK3dojIrJyddG1iwZU5fzC5axLsGDL3qahN26r4CJW7HGS7eicyI5iKEjP30nI8x0FEleuecsnj0pN2Am15o37cIuba_BzwSC0TNpKqrGJidgyNmw"
        )

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "myPdf.pdf"
        )

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle("myPdf")

        requireActivity().makeToast("start_download")

        downloadReference = dm.enqueue(request) ?: 0


        return downloadReference


    }

//    fun showPdf() {
//        try {
//            val file = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    .toString() + "/" + "Eslam Salih" + ".pdf"
//            )
////            pdfView.fromFile(file).load()
////            Log.d("reqdestination", file.path.toString())
//
////            Picasso.get().load(file).into(testpdfimg)
//
//            Glide.with(requireContext()).load(file).into(testpdfimg)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("reqdestination", e.toString())
//
//        }
//    }
}

