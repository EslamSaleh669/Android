package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AllStructuresItem
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.ui.adapter.TransferHistory_Adapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.allnotes_fragment.*
import kotlinx.android.synthetic.main.allnotes_fragment.noDataFounded
import kotlinx.android.synthetic.main.fragment_metadata.*
import kotlinx.android.synthetic.main.fragment_mytransfers.*
import kotlinx.android.synthetic.main.fragment_mytransfers.receiving_entity
import kotlinx.android.synthetic.main.fragment_mytransfers.sending_entity
import kotlinx.android.synthetic.main.fragment_mytransfers.subject
import kotlinx.android.synthetic.main.fragment_transferhistory.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Named

class MyTransferFragment : Fragment() {
    private var TransferId: Int = 0
    private var lockedBy: String = ""
    private var lockedDate: String = ""
    private lateinit var translator: ArrayList<DictionaryDataItem>

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


        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        setLabels()

        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
            getMyTransfers(TransferId)
        }


        requireArguments().getString(Constants.LOCKED_BY).let {
            lockedBy = it!!
        }

        requireArguments().getString(Constants.LOCKED_DATE).let {
            lockedDate = it!!
        }






    }


    private fun getMyTransfers(TransferId: Int) {

        autoDispose.add(viewModel.getMyTransfer(TransferId)
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

                        instructions.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(
                                it.instruction,
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            Html.fromHtml(it.instruction)
                        }

                    }

                     if (lockedBy == viewModel.readUserinfo().fullName){
                        when {
                            viewModel.readLanguage() == "en" -> {
                                lockedby.text = "You"
                            }
                            viewModel.readLanguage() == "ar" -> {
                                lockedby.text ="من قبلك"

                            }
                            viewModel.readLanguage() == "fr" -> {
                                lockedby.text= "Tu"

                            }
                        }
                    }else{
                        lockedby.text = lockedBy

                    }
                    lockeddate.text = lockedDate


                }


            }, {
                dialog!!.dismiss()
                Timber.e(it)


            })
        )


    }

    private fun setLabels(){

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
                receiving_entity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.en
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
                receiving_entity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.ar
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
                receiving_entity_label.text = translator.find { it.keyword == "ReceivingEntity" }!!.fr
                lockedbylabel.text = translator.find { it.keyword == "LockedBy" }!!.fr
                lockeddatelabel.text = translator.find { it.keyword == "LockedDate" }!!.fr
                centered_txt.text = translator.find { it.keyword == "MyTransfer" }!!.fr


            }
        }
    }



}