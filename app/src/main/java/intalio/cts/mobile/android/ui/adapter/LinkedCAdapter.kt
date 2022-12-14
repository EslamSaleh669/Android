package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R

import intalio.cts.mobile.android.data.network.response.LinkedCorDataItem
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceViewModel


class LinkedCAdapter(
    private val LinkedC: ArrayList<LinkedCorDataItem>,
    val activity: Activity,
    private val onDeleteCLickListener: OnDeleteLinkedCClicked,
    private val Node_Inherit: String,
    private val canDoAction: Boolean,
    private val DOCUMENT_ID: Int,
    private val TransferId: Int,
    private val  viewModel: LinkedCorrespondenceViewModel


) : RecyclerView.Adapter<LinkedCAdapter.AllNewsVHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.linkedc_viewshape,parent,false)
        )


    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

        val LinkedDeleteForCreatorOnly =
            viewModel.readSettings().find { it.keyword == "LinkedDeleteForCreatorOnly" }!!.content



        if (LinkedDeleteForCreatorOnly == "false"){
            if (Node_Inherit != "Inbox" || !canDoAction || LinkedC[position].allowDelete == false){

                holder.LinkedDelete.visibility = View.INVISIBLE

            }

        }else if (LinkedDeleteForCreatorOnly == "true"){
            if (LinkedC[position].linkedBy == viewModel.readUserinfo().fullName){
                if (Node_Inherit != "Inbox" || !canDoAction || LinkedC[position].allowDelete == false){

                    holder.LinkedDelete.visibility = View.INVISIBLE

                }
            }else{
                holder.LinkedDelete.visibility = View.INVISIBLE

            }

        }




        holder.LinkedRefNum.text = LinkedC[position].linkedDocumentReferenceNumber
        holder.LinkedDate.text = LinkedC[position].createdDate

        if (position == LinkedC.size - 1) {
            holder.vieww.visibility = View.GONE
        }


        holder.LinkedDelete.setOnClickListener {
            onDeleteCLickListener.onDeleteClicked(LinkedC[position].id!!,DOCUMENT_ID,TransferId)
        }

    }

    override fun getItemCount() = LinkedC.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val LinkedRefNum : TextView =itemView.findViewById(R.id.linked_refnum)
        val LinkedDate : TextView =itemView.findViewById(R.id.linked_date)
        val LinkedDelete : ImageView =itemView.findViewById(R.id.deletelinkedc)
        val vieww : View =itemView.findViewById(R.id.vieww)

    }

    fun removeLinkedC(linkedc: Int) {
        for (item in LinkedC) {
            if (item.id == linkedc) {
                LinkedC.remove(item)
                break
            }
        }
        notifyDataSetChanged()
    }

    public interface OnDeleteLinkedCClicked {
        fun onDeleteClicked(linkcId: Int, DOCUMENT_ID: Int,TransferId: Int)
    }

}