package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AdvancedSearchResponseDataItem
import intalio.cts.mobile.android.data.network.response.CorrespondenceDataItem
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import intalio.cts.mobile.android.ui.fragment.delegations.AddDelegationFragment
import intalio.cts.mobile.android.util.Constants

class AdvancedSearchResultAdapter(
    private val Messages: ArrayList<AdvancedSearchResponseDataItem>,
    val activity: Activity
) : RecyclerView.Adapter<AdvancedSearchResultAdapter.AllNewsVHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
          LayoutInflater.from(parent.context)
        .inflate(R.layout.messages_viewshape,parent,false)
    )



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

            holder.messageCreatedBy.text = Messages[position].createdByUser
            holder.messageSub.text = Messages[position].subject
            holder.messageRefNumber.text = Messages[position].referenceNumber
            holder.messageTime.text = Messages[position].createdDate




            when (Messages[position].priorityId) {

                1 -> {

                    holder.messageImportance.setColorFilter(activity.getColor(R.color.normal))

                }
                2 -> {
                    holder.messageImportance.setColorFilter(activity.getColor(R.color.important))

                }
                3 -> {
                    holder.messageImportance.setColorFilter(activity.getColor(R.color.very_important))

                }

            }











        holder.messageRel.setOnClickListener {

//            (activity as AppCompatActivity).supportFragmentManager.commit {
//                replace(R.id.fragmentContainer,
//                    CorrespondenceDetailsFragment().apply {
//                        arguments = bundleOf(
//                            Pair(Constants.TRANSFER_ID, Messages[position].id),
//                            Pair(Constants.TRANSFER_NAME, Messages[position].subject),
//                            Pair(Constants.NODE_ID,NodeId)
//
//                        )
//
//
//                    }
//                )
//                addToBackStack("")
//
//            }

            val bundle = Bundle()
            bundle.putSerializable(Constants.Correspondence_Model, Messages[position])
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    CorrespondenceDetailsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.PATH, "search"),
                            Pair(Constants.Correspondence_Model, Messages[position]),
                         )


                    }
                )
                addToBackStack("")

            }
        }
    }

    override fun getItemCount() = Messages.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageCreatedBy : TextView =itemView.findViewById(R.id.message_sentfrom)
        val messageSub : TextView =itemView.findViewById(R.id.message_subject)
        val messageRefNumber : TextView =itemView.findViewById(R.id.message_refnumber)
        val messageTime : TextView =itemView.findViewById(R.id.message_time)

        val messageLock : ImageView =itemView.findViewById(R.id.message_lock)
        val messageImportance : ImageView =itemView.findViewById(R.id.message_importance)
        val messageOverdue : ImageView =itemView.findViewById(R.id.message_overdue)
        val messageDot : ImageView =itemView.findViewById(R.id.message_dot)

        val messageRel : RelativeLayout =itemView.findViewById(R.id.messagerel)



    }
    fun addMessages(moreitems: ArrayList<AdvancedSearchResponseDataItem>){
        this.Messages.addAll(moreitems)
        notifyDataSetChanged()
    }

}