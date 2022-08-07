package intalio.cts.mobile.android.ui.fragment.visualtracking

import android.util.Log
import intalio.cts.mobile.android.data.network.response.AllStructuresResponse
import intalio.cts.mobile.android.data.network.response.VisualTrackingResponse
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Graph
import intalio.cts.mobile.android.ui.fragment.visualtracking.graph.Node
import intalio.cts.mobile.android.ui.fragment.visualtracking.layouts.tree.BuchheimWalkerConfiguration
import intalio.cts.mobile.android.ui.fragment.visualtracking.layouts.tree.BuchheimWalkerLayoutManager
import intalio.cts.mobile.android.ui.fragment.visualtracking.layouts.tree.TreeEdgeDecoration
import intalio.cts.mobile.android.ui.fragment.visualtracking.visualtrackingutil.GraphActivity
import intalio.cts.mobile.android.util.Constants

class BuchheimWalkerActivity : GraphActivity() {


    public override fun setLayoutManager() {
        val configuration = BuchheimWalkerConfiguration.Builder()
            .setSiblingSeparation(300)
            .setLevelSeparation(300)
            .setSubtreeSeparation(300)
            .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
            .build()
        recyclerView.layoutManager = BuchheimWalkerLayoutManager(this, configuration)
    }

    public override fun setEdgeDecoration() {
        recyclerView.addItemDecoration(TreeEdgeDecoration())
    }


    override fun getStructuresAndUsers(): AllStructuresResponse {
        val structureList =
            intent.getSerializableExtra(Constants.STRUCTURE_MODEL) as AllStructuresResponse
        return structureList
    }

    override fun readLanguage(): String {
        val language =
            intent.getStringExtra(Constants.CURRENT_LANG)
        return language!!
    }

    public override fun createGraph(): Graph {
        val trackingList =
            intent.getSerializableExtra(Constants.TRACKING_MODEL) as VisualTrackingResponse
        val graph = Graph()

        for (item in trackingList.visualTrackingResponse!!) {
            val parentNode = Node(item)
            for (item2 in trackingList.visualTrackingResponse!!) {
                if (item2.parentId.toString() == item.id.toString()) {
                    val childNode = Node(item2)
                    graph.addEdge(parentNode, childNode)

                }
            }


        }



        return graph
    }
}


