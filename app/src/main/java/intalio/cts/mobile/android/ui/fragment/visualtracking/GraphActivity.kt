package intalio.cts.mobile.android.ui.fragment.visualtracking.visualtrackingutil

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import intalio.cts.mobile.android.data.network.response.AllStructuresResponse
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.VisualTrackingResponseItem
import intalio.cts.mobile.android.ui.activity.auth.login.LoginViewModel
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Graph
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Node
import intalio.cts.mobile.android.ui.fragment.visualtracking.layouts.AbstractGraphAdapter
import intalio.cts.mobile.android.util.AutoDispose
import intalio.cts.mobile.android.util.MyApplication
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mytransfers.*
import timber.log.Timber

import java.util.*
import javax.inject.Inject
import javax.inject.Named

abstract class GraphActivity : AppCompatActivity() {

    private lateinit var translator: ArrayList<DictionaryDataItem>
    private lateinit var ownerFullName: String

    @Inject
    @field:Named("login")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }
    private val autoDispose: AutoDispose = AutoDispose()

    protected lateinit var recyclerView: RecyclerView
    protected lateinit var adapter: AbstractGraphAdapter<NodeViewHolder>
    private lateinit var fab: FloatingActionButton
    private var currentNode: Node? = null
    private var nodeCount = 1


    protected abstract fun createGraph(): Graph
    protected abstract fun setLayoutManager()
    protected abstract fun setEdgeDecoration()
    protected abstract fun getStructuresAndUsers(): AllStructuresResponse

    //    protected abstract fun getDictionary(): DictionaryResponse
    protected abstract fun readLanguage(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        (application as MyApplication).appComponent?.inject(this)
        autoDispose.bindTo(this.lifecycle)

        val graph = createGraph()
        val structure = getStructuresAndUsers()
//        val tranlator = getDictionary()
        val language = readLanguage()

        recyclerView = findViewById(R.id.recycler)
        setLayoutManager()
        setEdgeDecoration()
        setupGraphView(graph, structure, language)

        //  setupFab(graph)
        setupToolbar()
        //  translator = viewModel.readDictionary()!!.data!!

    }


    fun fullUserName(
        userId: Int,
        created_By: String,
        loggedUser: String?,
        holder: NodeViewHolder,
        you: String,
        onBehalfOf: String
    ) {


        viewModel.readSavedDelegator().let {


            if (it != null) {


                if (it.fromUserId == 0) {
                    if (userId == -1) {
                        if (loggedUser == created_By) {
                            holder.CreatedBy.text = you
                        } else {
                            holder.CreatedBy.text = created_By
                        }


                    } else {

                        autoDispose.add(
                            viewModel.fullUserName(userId).observeOn(Schedulers.io()).subscribe({


                                val delegatorFullName = it.fullName


                                if (loggedUser == created_By) {

                                    holder.CreatedBy.text =
                                        "${delegatorFullName} $onBehalfOf $you"
                                } else {

                                    holder.CreatedBy.text =
                                        "${delegatorFullName} $onBehalfOf ${created_By}"
                                }


                            }, {
                                Timber.e(it)

                            })
                        )

                    }
                } else {

                    if (userId == -1) {

                        holder.CreatedBy.text = created_By


                    } else {

                        autoDispose.add(
                            viewModel.fullUserName(userId).observeOn(Schedulers.io()).subscribe({
                                val delegatorFullName = it.fullName

                                if (loggedUser == delegatorFullName) {
                                    holder.CreatedBy.text =
                                        "$you $onBehalfOf ${created_By}"

                                } else {
                                    holder.CreatedBy.text =
                                        "${delegatorFullName} $onBehalfOf ${created_By}"

                                }


                            }, {
                                Timber.e(it)

                            })
                        )


                    }


                }

            } else {


                if (userId == -1) {
                    if (loggedUser == created_By) {
                        holder.CreatedBy.text = you
                    } else {
                        holder.CreatedBy.text = created_By
                    }


                } else {


                    autoDispose.add(
                        viewModel.fullUserName(userId).observeOn(Schedulers.io()).subscribe({
                            val delegatorFullName = it.fullName


                            if (loggedUser == created_By) {

                                holder.CreatedBy.text =
                                    "${delegatorFullName} $onBehalfOf $you"
                            } else {

                                holder.CreatedBy.text =
                                    "${delegatorFullName} $onBehalfOf ${created_By}"
                            }

                        }, {
                            Timber.e(it)

                        })
                    )
                }

            }
        }


//
//        autoDispose.add(
//            viewModel.fullUserName(userId).observeOn(Schedulers.io()).subscribe({
//
//
//
//            }, {
//                Timber.e(it)
//
//            })
//        )
//


    }

    fun getTransferDetails(

        transferId: Int,
        holder: NodeViewHolder,
        you: String,
        onBehalfOf: String,
        loggedUser: String?

    ) {

        var delegationId = 0


        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }


        autoDispose.add(
            viewModel.getTransferDetails(transferId, delegationId).observeOn(Schedulers.io())
                .subscribe(
                    { transferDetails ->


                    Log.d("transferdee", transferDetails.toString())

//                    if (it.lockedByDelegatedUser.isNullOrEmpty()) {
//                        if (loggedUser == it.lockedBy) {
//                            holder.CreatedBy.text = you
//                        } else {
//                            holder.CreatedBy.text = it.lockedBy
//                        }
//
//                    } else {
//                        if (loggedUser == it.lockedByDelegatedUser) {
//                            holder.CreatedBy.text = "$you $onBehalfOf ${it.lockedBy}"
//
//                        }
////                        else if (loggedUser == it.lockedBy) {
////                            holder.CreatedBy.text = "${it.lockedByDelegatedUser} $onBehalfOf $you"
////
////                        }
//                        else {
//                            holder.CreatedBy.text =
//                                "${it.lockedByDelegatedUser} $onBehalfOf ${it.lockedBy}"
//
//                        }
//
//                    }

                    viewModel.readSavedDelegator().let {

                        if (it != null) {

                            if (it.fromUserId == 0) {
                                if (transferDetails.ownerDelegatedUserId!!.equals(null)) {
                                    if (loggedUser == transferDetails.lockedBy){
                                        holder.CreatedBy.text = you
                                    }else{
                                        holder.CreatedBy.text = transferDetails.lockedBy
                                    }


                                } else {

                                    if (loggedUser == transferDetails.lockedBy){

                                        holder.CreatedBy.text =
                                            "${transferDetails.lockedByDelegatedUser} $onBehalfOf $you"
                                    }else{

                                        holder.CreatedBy.text =
                                            "${transferDetails.lockedByDelegatedUser} $onBehalfOf ${transferDetails.lockedBy}"
                                    }

                                }
                            }else {

                                if (transferDetails.lockedByDelegatedUser.isNullOrEmpty()) {

                                    holder.CreatedBy.text = transferDetails.lockedBy


                                } else {

                                    if (loggedUser == transferDetails.lockedByDelegatedUser) {
                                        holder.CreatedBy.text =
                                            "$you $onBehalfOf ${transferDetails.lockedBy}"

                                    } else {
                                        holder.CreatedBy.text =
                                            "${transferDetails.lockedByDelegatedUser} $onBehalfOf ${transferDetails.lockedBy}"

                                    }

                                }


                            }

                        } else {

                            if (transferDetails.lockedByDelegatedUser.isNullOrEmpty()) {
                                if (loggedUser == transferDetails.lockedBy){
                                    holder.CreatedBy.text = you
                                }else{
                                    holder.CreatedBy.text = transferDetails.lockedBy
                                }


                            } else {

                                if (loggedUser == transferDetails.lockedBy){

                                    holder.CreatedBy.text =
                                        "${transferDetails.lockedByDelegatedUser} $onBehalfOf $you"
                                }else{

                                    holder.CreatedBy.text =
                                        "${transferDetails.lockedByDelegatedUser} $onBehalfOf ${transferDetails.lockedBy}"
                                }

                            }

                        }
                    }


                }, {
                    Timber.e(it)

                })
        )


    }


    private fun setupGraphView(
        graph: Graph,
        structure: AllStructuresResponse,
        language: String
    ) {

        adapter = object : AbstractGraphAdapter<NodeViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.node, parent, false)
                return NodeViewHolder(view)
            }

            override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {

                val translator = viewModel.readDictionary()!!.data!!
                var onBehalfOf = ""
                var you = ""
                var CreatedBy = ""
                var TransferDate = ""
                var CreatedDate = ""
                when {
                    viewModel.readLanguage() == "en" -> {
                        you = translator.find { it.keyword == "You" }!!.en!!
                        onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.en!!
                        TransferDate = translator.find { it.keyword == "TransferDate" }!!.en!!
                        CreatedBy = translator.find { it.keyword == "CreatedBy" }!!.en!!
                        CreatedDate = translator.find { it.keyword == "CreatedDate" }!!.en!!

                    }
                    viewModel.readLanguage() == "ar" -> {
                        you = translator.find { it.keyword == "You" }!!.ar!!

                        onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.ar!!
                        TransferDate = translator.find { it.keyword == "TransferDate" }!!.ar!!
                        CreatedBy = translator.find { it.keyword == "CreatedBy" }!!.ar!!
                        CreatedDate = translator.find { it.keyword == "CreatedDate" }!!.ar!!
                    }
                    viewModel.readLanguage() == "fr" -> {
                        you = translator.find { it.keyword == "You" }!!.fr!!
                        onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.fr!!
                        TransferDate = translator.find { it.keyword == "TransferDate" }!!.fr!!
                        CreatedBy = translator.find { it.keyword == "CreatedBy" }!!.fr!!
                        CreatedDate = translator.find { it.keyword == "CreatedDate" }!!.fr!!
                    }
                }


                if (position == 0) {

                    holder.colorView.setBackgroundColor(resources.getColor(R.color.appcolor))
                    holder.Category.text =
                        Objects.requireNonNull(getNodeCategory(position)).toString()
                    holder.RefNumber.text =
                        Objects.requireNonNull(getNodeRefNumber(position)).toString()
                    holder.CreatedDateTitle.text = CreatedDate
                    holder.CreatedDate.text =
                        Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                    holder.CreatedByTitle.text = CreatedBy
                    holder.CreatedBy.text =
                        Objects.requireNonNull(getNodeCreatedBy(position)).toString()

                } else {

                    if ((position) == 0) {
                        for (item in structure.structures!!) {
                            if (getNodeStructureID(position).toInt() == item!!.id) {
                                holder.RefNumber.text = item.name
                            }
                        }

                        holder.Category.text =
                            Objects.requireNonNull(getNodeCategory(position)).toString()
                        holder.CreatedDateTitle.text = TransferDate
                        holder.CreatedDate.text =
                            Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                        holder.CreatedByTitle.text = CreatedBy
                        holder.CreatedBy.text =
                            Objects.requireNonNull(getNodeCreatedBy(position)).toString()
                    } else {
                        holder.CreatedByTitle.visibility = View.GONE
                        var structures = ""
                        var user = ""

                        for (item in structure.structures!!) {
                            if (getNodeStructureID(position).toInt() == item!!.id) {
                                structures = item.name!!
                            }
                        }

                        for (item in structure.users!!) {
                            if (getNodeUserID(position).toInt() == item!!.id) {
                                user = item.fullName!!
                            }
                        }
                        holder.Category.text =
                            Objects.requireNonNull(getNodeCategory(position)).toString()
                        if (user.isEmpty()) {
                            holder.colorView.setBackgroundColor(resources.getColor(R.color.blue))
                            holder.RefNumber.text = structures
                        } else {
                            holder.colorView.setBackgroundColor(resources.getColor(R.color.orange))

                            holder.RefNumber.text = "${structures}/${user}"

                        }
                        holder.CreatedDateTitle.text = TransferDate
                        holder.CreatedDate.text =
                            Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                        holder.CreatedByTitle.text = CreatedBy

                        val created_By =
                            Objects.requireNonNull(getNodeCreatedBy(position)).toString()
                        val ownerDelegatedID =
                            Objects.requireNonNull(getCreatedByDelegatorID(position))

                        val loggedUser = viewModel.readUserinfo().fullName
                        val loggedUserid = viewModel.readUserinfo().id

                        val transferId = Objects.requireNonNull(getTransferId(position))
//                        fullUserName(
//                            ownerDelegatedID as Int,
//                            created_By,
//                            loggedUser,
//                            holder,
//                            you,
//                            onBehalfOf
//                        )


                        val transferAsInt = transferId!!.substringAfter("_").toInt()
                        getTransferDetails(transferAsInt, holder, you, onBehalfOf, loggedUser)


                        //  holder.CreatedBy.text = Objects.requireNonNull(getNodeCreatedBy(position)).toString()


                    }

                }


            }
        }.apply {
            this.submitGraph(graph)
            recyclerView.adapter = this

        }


    }


    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""

        setSupportActionBar(toolbar)
        val ab = supportActionBar
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    inner class NodeViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var Category: TextView = itemView.findViewById(R.id.category)
        var RefNumber: TextView = itemView.findViewById(R.id.refnum)
        var CreatedDate: TextView = itemView.findViewById(R.id.createddate)
        var CreatedDateTitle: TextView = itemView.findViewById(R.id.createddatetitle)

        var CreatedBy: TextView = itemView.findViewById(R.id.createdby)
        var CreatedByTitle: TextView = itemView.findViewById(R.id.createdbytitle)
        var colorView: View = itemView.findViewById(R.id.colorview)
        var nodeItem: LinearLayout = itemView.findViewById(R.id.nodeItem)


        init {

            nodeItem.setOnClickListener {

                val selectedNode = adapter.getSelectedNode(bindingAdapterPosition)

                if (bindingAdapterPosition == 0){
                    showDocumentDetailsDialog(selectedNode)
                }else{
                    showTransferDetailsDialog(selectedNode)

                }
            }
        }
    }

    private fun showTransferDetailsDialog(
        model: VisualTrackingResponseItem,
    ) {

        val customDialog = Dialog(this, R.style.FullScreenDialog)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(true)
        customDialog.setContentView(R.layout.visual_tracking_details_dialog)

        val language = viewModel.readLanguage()
        val translator = viewModel.readDictionary()!!.data!!

        when (language) {
            "en" -> {

                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Transfer" }!!.en

                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
                    "${translator.find { it.keyword == "CreatedBy" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
                "${translator.find { it.keyword == "DueDate" }!!.en}: "
                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
                "${translator.find { it.keyword == "OpenedDate" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
                "${translator.find { it.keyword == "CreatedDate" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
                "${translator.find { it.keyword == "Instruction" }!!.en}: "


            }
            "ar" -> {

                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Transfer" }!!.ar

                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
                    "${translator.find { it.keyword == "CreatedBy" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
                    "${translator.find { it.keyword == "DueDate" }!!.ar}: "
                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
                    "${translator.find { it.keyword == "OpenedDate" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
                    "${translator.find { it.keyword == "CreatedDate" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
                    "${translator.find { it.keyword == "Instruction" }!!.ar}: "


            }
            "fr" -> {
                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Transfer" }!!.fr

                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
                    "${translator.find { it.keyword == "CreatedBy" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
                    "${translator.find { it.keyword == "DueDate" }!!.fr}: "
                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
                    "${translator.find { it.keyword == "OpenedDate" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
                    "${translator.find { it.keyword == "CreatedDate" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
                    "${translator.find { it.keyword == "Instruction" }!!.fr}: "

            }
        }

        val vt_createdby_value = customDialog.findViewById(R.id.vt_createdby_value) as TextView
        val vt_duedate_value = customDialog.findViewById(R.id.vt_duedate_value) as TextView
        val vt_opendate_value = customDialog.findViewById(R.id.vt_opendate_value) as TextView
        val vt_createddate_value = customDialog.findViewById(R.id.vt_createddate_value) as TextView
        val vt_instruction_value = customDialog.findViewById(R.id.vt_instructions_value) as TextView

        if (model.createdBy != null) {
            vt_createdby_value.text = model.createdBy
        } else {
            vt_createdby_value.text = "---"
        }

        if (model.dueDate != null) {
            vt_duedate_value.text = model.dueDate.toString()
        } else {
            vt_duedate_value.text = "---"
        }

        if (model.openedDate != null) {
            vt_opendate_value.text = model.openedDate.toString()
        } else {
            vt_opendate_value.text = "---"
        }

        if (model.transferDate != null) {
            vt_createddate_value.text = model.transferDate.toString()
        } else {
            vt_createddate_value.text = "---"
        }

        if (model.instruction != null) {
            vt_instruction_value.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(model.instruction.toString(), Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(model.instruction.toString())
            }


        } else {
            vt_instruction_value.text = "---"
        }


        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()

    }


    private fun showDocumentDetailsDialog(
        model: VisualTrackingResponseItem,
    ) {

        val customDialog = Dialog(this, R.style.FullScreenDialog)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(true)
        customDialog.setContentView(R.layout.visual_tracking_document_details)

        val language = viewModel.readLanguage()
        val translator = viewModel.readDictionary()!!.data!!

        when (language) {
            "en" -> {

                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Document" }!!.en

                customDialog.findViewById<TextView>(R.id.vt_sending_entity_label).text =
                    "${translator.find { it.keyword == "SendingEntity" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_receiving_entity_label).text =
                    "${translator.find { it.keyword == "ReceivingEntity" }!!.en}: "
                customDialog.findViewById<TextView>(R.id.vt_subject_label).text =
                    "${translator.find { it.keyword == "Subject" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_priority_label).text =
                    "${translator.find { it.keyword == "Priority" }!!.en}: "

                customDialog.findViewById<TextView>(R.id.vt_privacy_label).text =
                    "${translator.find { it.keyword == "Privacy" }!!.en}: "


            }
            "ar" -> {


                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Document" }!!.ar

                customDialog.findViewById<TextView>(R.id.vt_sending_entity_label).text =
                    "${translator.find { it.keyword == "SendingEntity" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_receiving_entity_label).text =
                    "${translator.find { it.keyword == "ReceivingEntity" }!!.ar}: "
                customDialog.findViewById<TextView>(R.id.vt_subject_label).text =
                    "${translator.find { it.keyword == "Subject" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_priority_label).text =
                    "${translator.find { it.keyword == "Priority" }!!.ar}: "

                customDialog.findViewById<TextView>(R.id.vt_privacy_label).text =
                    "${translator.find { it.keyword == "Privacy" }!!.ar}: "


            }
            "fr" -> {

                customDialog.findViewById<TextView>(R.id.centered_txt).text =
                    translator.find { it.keyword == "Document" }!!.fr

                customDialog.findViewById<TextView>(R.id.vt_sending_entity_label).text =
                    "${translator.find { it.keyword == "SendingEntity" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_receiving_entity_label).text =
                    "${translator.find { it.keyword == "ReceivingEntity" }!!.fr}: "
                customDialog.findViewById<TextView>(R.id.vt_subject_label).text =
                    "${translator.find { it.keyword == "Subject" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_priority_label).text =
                    "${translator.find { it.keyword == "Priority" }!!.fr}: "

                customDialog.findViewById<TextView>(R.id.vt_privacy_label).text =
                    "${translator.find { it.keyword == "Privacy" }!!.fr}: "


            }
        }

        val vt_sendingentity_value = customDialog.findViewById(R.id.vt_sending_entity_value) as TextView
        val vt_recievingentity_value = customDialog.findViewById(R.id.vt_receiving_entity_value) as TextView
        val vt_subject_value = customDialog.findViewById(R.id.vt_subject_value) as TextView
        val vt_priority_value = customDialog.findViewById(R.id.vt_priority_value) as TextView
        val vt_privacy_value = customDialog.findViewById(R.id.vt_privacy_value) as TextView

        if (model.sendingEntity != null) {
            vt_sendingentity_value.text = model.sendingEntity
        } else {
            vt_sendingentity_value.text = "---"
        }

        if (model.receivingEntity != null) {
            vt_recievingentity_value.text = model.receivingEntity.toString()
        } else {
            vt_recievingentity_value.text = "---"
        }

        if (model.subject != null) {
            vt_subject_value.text = model.subject.toString()
        } else {
            vt_subject_value.text = "---"
        }

        if (model.priority != null) {
            vt_priority_value.text = model.priority.toString()
        } else {
            vt_priority_value.text = "---"
        }

        if (model.privacy != null) {
            vt_privacy_value.text = model.privacy.toString()
        } else {
            vt_privacy_value.text = "---"
        }




        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()

    }

    protected val nodeText: String
        get() = "Incoming " + nodeCount++
}