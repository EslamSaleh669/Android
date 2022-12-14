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
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import intalio.cts.mobile.android.data.network.response.AllStructuresResponse
import intalio.cts.mobile.android.data.network.response.DictionaryResponse
import intalio.cts.mobile.android.data.network.response.VisualTrackingResponseItem
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Graph
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Node
import intalio.cts.mobile.android.ui.fragment.visualtracking.layouts.AbstractGraphAdapter

import java.util.*

abstract class GraphActivity : AppCompatActivity() {
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

        val graph = createGraph()
        val structure = getStructuresAndUsers()
//        val tranlator = getDictionary()
        val language = readLanguage()

        recyclerView = findViewById(R.id.recycler)
        setLayoutManager()
        setEdgeDecoration()
        setupGraphView(graph,structure,language)

      //  setupFab(graph)
        setupToolbar()
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




                if (position == 0){

                    holder.colorView.setBackgroundColor(resources.getColor(R.color.appcolor))
                    holder.Category.text = Objects.requireNonNull(getNodeCategory(position)).toString()
                    holder.RefNumber.text = Objects.requireNonNull(getNodeRefNumber(position)).toString()
                    holder.CreatedDateTitle.text = getString(R.string.created_date)
                    holder.CreatedDate.text = Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                    holder.CreatedByTitle.text = getString(R.string.created_by)
                    holder.CreatedBy.text = Objects.requireNonNull(getNodeCreatedBy(position)).toString()

                }else{


                    if ((position) == 0){
                         for (item in structure.structures!!){
                            if (getNodeStructureID(position).toInt() == item!!.id){
                                holder.RefNumber.text = item.name
                            }
                        }

                        holder.colorView.setBackgroundColor(resources.getColor(R.color.blue))
                        holder.Category.text = Objects.requireNonNull(getNodeCategory(position)).toString()
                        holder.CreatedDateTitle.text = getString(R.string.transfer_date)
                        holder.CreatedDate.text = Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                        holder.CreatedByTitle.text = getString(R.string.created_by)
                        holder.CreatedBy.text = Objects.requireNonNull(getNodeCreatedBy(position)).toString()
                    }else{
                        var structures = ""
                        var user = ""

                        for (item in structure.structures!!){
                            if (getNodeStructureID(position).toInt() == item!!.id){
                                structures = item.name!!
                            }
                        }

                        for (item in structure.users!!){
                            if (getNodeUserID(position).toInt() == item!!.id){
                                user = item.fullName!!
                            }
                        }
                        holder.colorView.setBackgroundColor(resources.getColor(R.color.orange))
                        holder.Category.text = Objects.requireNonNull(getNodeCategory(position)).toString()
                        holder.RefNumber.text = "${structures}/${user}"
                        holder.CreatedDateTitle.text = getString(R.string.transfer_date)
                        holder.CreatedDate.text = Objects.requireNonNull(getNodeCreatedDate(position)).toString()
                        holder.CreatedByTitle.text = getString(R.string.created_by)
                        holder.CreatedBy.text = Objects.requireNonNull(getNodeCreatedBy(position)).toString()

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

    protected inner class NodeViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Category: TextView = itemView.findViewById(R.id.category)
        var RefNumber: TextView = itemView.findViewById(R.id.refnum)
        var CreatedDate: TextView = itemView.findViewById(R.id.createddate)
        var CreatedDateTitle: TextView = itemView.findViewById(R.id.createddatetitle)

        var CreatedBy: TextView = itemView.findViewById(R.id.createdby)
        var CreatedByTitle: TextView = itemView.findViewById(R.id.createdbytitle)
        var colorView: View = itemView.findViewById(R.id.colorview)
        var nodeItem : LinearLayout = itemView.findViewById(R.id.nodeItem)


        init {

            nodeItem.setOnClickListener {

              //  currentNode = adapter.getNode(bindingAdapterPosition)
//                Snackbar.make(itemView, "Clicked on " + adapter.getSelectedNode(bindingAdapterPosition)?.toString(),
//                    Snackbar.LENGTH_SHORT).show()
                val selectedNode = adapter.getSelectedNode(bindingAdapterPosition)
                Log.d("selectedNode",selectedNode.toString())
              //  showNoteDialog(bindingAdapterPosition,selectedNode)
            }

        }
    }
//
//    private fun showNoteDialog(
//        position: Int,
//        model: VisualTrackingResponseItem,
//    ) {
//        val customDialog = Dialog(this, R.style.FullScreenDialog)
//        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        customDialog.setCancelable(true)
//        customDialog.setContentView(R.layout.visual_tracking_details_dialog)
//
////        customDialog.findViewById<TextView>(R.id.centered_txt).setText(R.string.edit)
////        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
////            customDialog.dismiss()
////        }
//
//        var requiredFields = ""
//        val language = readLanguage()
//        val translator = getDictionary()
//
//        when (language) {
//            "en" -> {
//
//                customDialog.findViewById<TextView>(R.id.vt_title).text =
//                    translator.data!!.find { it.keyword == "Transfer" }!!.en
//
//                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
//                    translator.data.find { it.keyword == "CreatedBy" }!!.en
//
//                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
//                    translator.data.find { it.keyword == "DueDate" }!!.en
//
//                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
//                    translator.data.find { it.keyword == "OpenedDate" }!!.en
//
//                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
//                    translator.data.find { it.keyword == "CreatedDate" }!!.en
//
//                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
//                    translator.data.find { it.keyword == "Instruction" }!!.en
//
//
//            }
//            "ar" -> {
//
//                customDialog.findViewById<TextView>(R.id.vt_title).text =
//                    translator.data!!.find { it.keyword == "Transfer" }!!.ar
//
//                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
//                    translator.data.find { it.keyword == "CreatedBy" }!!.ar
//
//                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
//                    translator.data.find { it.keyword == "DueDate" }!!.ar
//
//                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
//                    translator.data.find { it.keyword == "OpenedDate" }!!.ar
//
//                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
//                    translator.data.find { it.keyword == "CreatedDate" }!!.ar
//
//                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
//                    translator.data.find { it.keyword == "Instruction" }!!.ar
//
//
//            }
//            "fr" -> {
//                customDialog.findViewById<TextView>(R.id.vt_title).text =
//                    translator.data!!.find { it.keyword == "Transfer" }!!.fr
//
//                customDialog.findViewById<TextView>(R.id.vt_createdby_label).text =
//                    translator.data.find { it.keyword == "CreatedBy" }!!.fr
//
//                customDialog.findViewById<TextView>(R.id.vt_duedate_label).text =
//                    translator.data.find { it.keyword == "DueDate" }!!.fr
//
//                customDialog.findViewById<TextView>(R.id.vt_opendate_label).text =
//                    translator.data.find { it.keyword == "OpenedDate" }!!.fr
//
//                customDialog.findViewById<TextView>(R.id.vt_createddate_label).text =
//                    translator.data.find { it.keyword == "CreatedDate" }!!.fr
//
//                customDialog.findViewById<TextView>(R.id.vt_instructions_label).text =
//                    translator.data.find { it.keyword == "Instruction" }!!.fr
//
//
//            }
//        }
//
//        val vt_createdby_value = customDialog.findViewById(R.id.vt_createdby_value) as TextView
//        val vt_duedate_value = customDialog.findViewById(R.id.vt_duedate_value) as TextView
//        val vt_opendate_value = customDialog.findViewById(R.id.vt_opendate_label) as TextView
//        val vt_createddate_value = customDialog.findViewById(R.id.vt_createddate_value) as TextView
//        val vt_instruction_value = customDialog.findViewById(R.id.vt_instructions_value) as TextView
//
//        if (model.createdBy != null){
//            vt_createdby_value.text = model.createdBy
//        }else{
//            vt_createdby_value.text = "---"
//        }
//
//        if (model.dueDate != null){
//            vt_duedate_value.text = model.dueDate.toString()
//        }else{
//            vt_duedate_value.text = "---"
//        }
//
//        if (model.openedDate != null){
//            vt_opendate_value.text = model.openedDate.toString()
//        }else{
//            vt_opendate_value.text = "---"
//        }
//
//        if (model.createdDate != null){
//            vt_createddate_value.text = model.createdDate
//        }else{
//            vt_createddate_value.text = "---"
//        }
//
//
//        if (model.instruction != null){
//            vt_instruction_value.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(model.instruction.toString(), Html.FROM_HTML_MODE_COMPACT)
//            } else {
//                Html.fromHtml(model.instruction.toString())
//            }
//
//
//        }else{
//            vt_instruction_value.text = "---"
//        }
//
//
//        customDialog.show()
//
//    }


    protected val nodeText: String
        get() = "Incoming " + nodeCount++
}