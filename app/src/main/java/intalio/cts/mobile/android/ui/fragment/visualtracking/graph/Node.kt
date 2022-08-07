package intalio.cts.mobile.android.ui.fragment.visualtracking.graph

import intalio.cts.mobile.android.data.network.response.VisualTrackingResponseItem
import intalio.cts.mobile.android.ui.fragment.visualtracking.visualtrackingutil.Size
import intalio.cts.mobile.android.ui.fragment.visualtracking.visualtrackingutil.VectorF

//    data class Node(var data: String, var todata: String, var currentdate:String) {
data class Node(var data: VisualTrackingResponseItem) {
    // TODO make private
    val position: VectorF = VectorF()
    val size: Size = Size()

    var height: Int
        get() = size.height
        set(value) {
            size.height = value
        }

    var width: Int
        get() = size.width
        set(value) {
            size.width = value
        }

    var x: Float
        get() = position.x
        set(value) {
            position.x = value
        }

    var y: Float
        get() = position.y
        set(value) {
            position.y = value
        }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setPosition(position: VectorF) {
        this.x = position.x
        this.y = position.y
    }
}
