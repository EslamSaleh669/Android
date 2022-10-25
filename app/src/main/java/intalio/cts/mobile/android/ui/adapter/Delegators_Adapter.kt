package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.fragment.main.MainFragment

import intalio.cts.mobile.android.ui.fragment.transfer.TransfersViewModel


class Delegators_Adapter(
    private val Delegators: ArrayList<DelegationRequestsResponseItem>,
    val activity: Activity,
    private val OnDelegatorClickListener: OnDelegatorClicked,
    private val currentDelegator : Int


    ) : RecyclerView.Adapter<Delegators_Adapter.AllNewsVHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.delegator_item,parent,false)
        )



    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {


        holder.delegatorName.text = Delegators[position].fromUser

        if (Delegators[position].fromUserId == currentDelegator){
            holder.delegatorCard.setBackgroundColor(activity.resources.getColor(R.color.appcolor))
            holder.delegatorName.setTextColor(activity.resources.getColor(R.color.white))
        }

        holder.delegatorCard.setOnClickListener {
            OnDelegatorClickListener.onDelegatorSelected(Delegators[position])
        }
        if (position == Delegators.size - 1) {
            holder.vieww.visibility = View.GONE
        }
    }

    override fun getItemCount() = Delegators.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val delegatorCard : CardView =itemView.findViewById(R.id.delegatorcard)
        val delegatorName : TextView =itemView.findViewById(R.id.delegatorname)
        val vieww : View =itemView.findViewById(R.id.delegatorview)

    }



    public interface OnDelegatorClicked {
        fun onDelegatorSelected(delegator: DelegationRequestsResponseItem)
    }
}