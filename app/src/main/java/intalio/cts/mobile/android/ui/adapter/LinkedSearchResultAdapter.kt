package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AdvancedSearchResponseDataItem
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceViewModel


class LinkedSearchResultAdapter(
    private val Messages: ArrayList<AdvancedSearchResponseDataItem>,
    val activity: Activity,
    private val onSelectionClickedListner: OnSelectionClicked,
    private val DocumentId: Int,
    private val viewModel: LinkedCorrespondenceViewModel

) : RecyclerView.Adapter<LinkedSearchResultAdapter.AllNewsVHolder>() {


    var checkedIds = ArrayList<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
          LayoutInflater.from(parent.context)
        .inflate(R.layout.linked_correspondence_sresult_viewshape,parent,false)
    )



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

        val statuses = viewModel.readStatuses()
        val categories = viewModel.readCategoriesData()
        for (item in statuses) {
            if (item.id == Messages[position].statusId) {
                holder.linkedStatus.text = item.text
                holder.linkedCard.setCardBackgroundColor(Color.parseColor(item.color))
            }
        }


        holder.linkedCreatedBy.text = categories.find { it.id == Messages[position].categoryId}!!.text

        holder.linkedCRefNum.text = Messages[position].referenceNumber
        holder.linkedDate.text = Messages[position].createdDate

        holder.linkedSwitch.setOnClickListener {

            if (holder.linkedSwitch.isChecked){
                checkedIds.add(Messages[position].id!!)
            }else{
                checkedIds.remove(Messages[position].id)

            }

            onSelectionClickedListner.onSelectedLinkedClicked(checkedIds)

        }
    }

    override fun getItemCount() = Messages.size

    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val linkedCreatedBy : TextView =itemView.findViewById(R.id.linked_createdby)
        val linkedCRefNum : TextView =itemView.findViewById(R.id.linkedc_refnum)
        val linkedDate : TextView =itemView.findViewById(R.id.linkedc_date)
        val linkedStatus : TextView =itemView.findViewById(R.id.linked_status)
        val linkedCard : CardView =itemView.findViewById(R.id.linked_card)
        val linkedSwitch: CheckBox =itemView.findViewById(R.id.linkedattach)

    }
    fun addMessages(moreitems: ArrayList<AdvancedSearchResponseDataItem>){
        this.Messages.addAll(moreitems)
        notifyDataSetChanged()
    }

    fun removeItem(id: Int) {
        for (item in Messages) {
            if (item.id == id) {
                Messages.remove(item)
                break
            }
        }
        notifyDataSetChanged()
    }

    public interface OnSelectionClicked {
        fun onSelectedLinkedClicked(documentsIds: ArrayList<Int>)
    }

}