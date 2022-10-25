package intalio.cts.mobile.android.ui.fragment.visualtracking.layouts

import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import intalio.cts.mobile.android.data.network.response.VisualTrackingResponseItem

import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Graph
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Node


abstract class AbstractGraphAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var graph: Graph? = null
    override fun getItemCount(): Int = graph?.nodeCount ?: 0

    open fun getNode(position: Int): Node? = graph?.getNodeAtPosition(position)
    open fun getNodeCategory(position: Int): Any? =
        graph?.getNodeAtPosition(position)?.data!!.category

    open fun getNodeRefNumber(position: Int): Any? =
        if (graph?.getNodeAtPosition(position)?.data!!.referenceNumber != null) {
            graph?.getNodeAtPosition(position)?.data!!.referenceNumber

        } else {
            graph?.getNodeAtPosition(position)?.data!!.structureId.toString()

        }

    open fun getNodeCreatedDate(position: Int): Any? =
        if (graph?.getNodeAtPosition(position)?.data!!.createdDate != null) {
            graph?.getNodeAtPosition(position)?.data!!.createdDate
        } else {
            graph?.getNodeAtPosition(position)?.data!!.transferDate

        }

    open fun getNodeCreatedBy(position: Int): Any? =
        graph?.getNodeAtPosition(position)?.data!!.createdBy


    open fun getNodeUserID(position: Int): Int =
        if (graph?.getNodeAtPosition(position)?.data!!.userId != null){
            graph?.getNodeAtPosition(position)?.data!!.userId!!

        }else{
            0
        }


    open fun getNodeStructureID(position: Int): Int =
        if (graph?.getNodeAtPosition(position)?.data!!.structureId != null){
            graph?.getNodeAtPosition(position)?.data!!.structureId!!
        }else{
            0
        }


    open fun getSelectedNode(position: Int): VisualTrackingResponseItem = graph?.getNodeAtPosition(position)!!.data

    /**
     * Submits a new graph to be displayed.
     *
     *
     * If a graph is already being displayed, you need to dispatch Adapter.notifyItem.
     *
     * @param graph The new graph to be displayed.
     */
    open fun submitGraph(@Nullable graph: Graph?) {
        this.graph = graph
    }
}