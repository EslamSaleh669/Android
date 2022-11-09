package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.fragment.main.MainViewModel


class Delegators_Adapter(
    private val Delegators: ArrayList<DelegationRequestsResponseItem>,
    val activity: Activity,
    private val OnDelegatorClickListener: OnDelegatorClicked,
    private val currentDelegator: Int,
    private val  viewModel: MainViewModel


) : RecyclerView.Adapter<Delegators_Adapter.AllNewsVHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.delegator_item,parent,false)
        )



    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {
        var transfers = ""

        val translator = viewModel.readDictionary().data!!


        when {
            viewModel.readLanguage() == "en" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.fr!!
            }
        }


        if (Delegators[position].fromUserId == 0){
            holder.delegatorName.text = Delegators[position].fromUser
        }else{
            holder.delegatorName.text = "${Delegators[position].fromUser} $transfers"
        }


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