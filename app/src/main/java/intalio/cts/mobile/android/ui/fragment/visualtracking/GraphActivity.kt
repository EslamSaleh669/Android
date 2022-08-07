package intalio.cts.mobile.android.ui.fragment.visualtracking.visualtrackingutil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import intalio.cts.mobile.android.data.network.response.AllStructuresResponse
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
    protected abstract fun readLanguage(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val graph = createGraph()
        val structure = getStructuresAndUsers()
        val language = readLanguage()
        recyclerView = findViewById(R.id.recycler)
        setLayoutManager()
        setEdgeDecoration()
        setupGraphView(graph,structure,language)

      //  setupFab(graph)
        setupToolbar()
    }

    private fun setupGraphView(graph: Graph, structure: AllStructuresResponse, language: String) {
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


                    if (getNodeUserID(position) == 0){
//                        val nodeUserId = getNodeUserID(position).toIn
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
                        Log.d("aaaaaaaaxxxxxxxxx",getNodeCreatedBy(position).toString())
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



        init {
            itemView.setOnClickListener {

                currentNode = adapter.getNode(bindingAdapterPosition)
                Snackbar.make(itemView, "Clicked on " + adapter.getNodeCategory(bindingAdapterPosition)?.toString(),
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    protected val nodeText: String
        get() = "Incoming " + nodeCount++
}