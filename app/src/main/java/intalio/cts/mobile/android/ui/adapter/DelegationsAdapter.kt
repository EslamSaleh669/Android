package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R

import intalio.cts.mobile.android.data.network.response.DelegationDataItem
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import intalio.cts.mobile.android.data.network.response.CategoryResponseItem


class DelegationsAdapter(
    private val Delegations: ArrayList<DelegationDataItem>,
    val activity: Activity,
    private val onDeleteCLickListener: OnDeleteDelegationClicked,
    private val categoriesArray: ArrayList<CategoryResponseItem>?,

    ) : RecyclerView.Adapter<DelegationsAdapter.AllNewsVHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.delegation_viewshape,parent,false)
        )

    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

        holder.delegatedName.text = Delegations.get(position).toUser
        holder.delegatedStartDate.text = Delegations[position].fromDate
        holder.delegatedEndDate.text = Delegations[position].toDate
        val font = ResourcesCompat.getFont(activity, R.font.myriadpro_regular)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 30, 0)
        val params2: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(0, 0, 10, 0)
        params2.gravity = Gravity.CENTER_VERTICAL

        for (ids in Delegations[position].categoryIds!!){
            for (obj in categoriesArray!!){
                if (ids == obj.id){
                    val img = ImageView(activity)
                    img.setImageResource(R.drawable.ic_dot_meduim)
                    img.layoutParams = params2



                    val mytxt = TextView(activity)
                    mytxt.text = obj.text
                    mytxt.setTextColor(activity.resources.getColor(R.color.black2))
                    mytxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
                    mytxt.typeface = font
                    mytxt.layoutParams = params

                    holder.categoriesLinear.addView(img)
                    holder.categoriesLinear.addView(mytxt)

                }
            }


        }



        holder.deleteDelegation.setOnClickListener {
            onDeleteCLickListener.onDeleteClicked(Delegations[position].id!!)
        }

        holder.editDelegation.setOnClickListener {
            onDeleteCLickListener.onEditClicked(position,Delegations[position])
        }

        if (position == Delegations.size - 1) {
            holder.vieww.visibility = View.GONE
        }
    }

    override fun getItemCount() = Delegations.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delegatedName : TextView =itemView.findViewById(R.id.delgated_name)
        val delegatedStartDate : TextView =itemView.findViewById(R.id.delegated_startdate)
        val delegatedEndDate : TextView =itemView.findViewById(R.id.delegated_enddate)
        val deleteDelegation : ImageView =itemView.findViewById(R.id.deletedelegation)
        val editDelegation : ImageView =itemView.findViewById(R.id.editdelegation)
        val categoriesLinear : LinearLayout =itemView.findViewById(R.id.categorylin)
     //   val LinkedDate : TextView =itemView.findViewById(R.id.linked_date)
         val vieww : View =itemView.findViewById(R.id.vieww)

    }

    fun addDelegations(moreDelegations: ArrayList<DelegationDataItem>){
        this.Delegations.addAll(moreDelegations)
        notifyDataSetChanged()
    }


    fun removeDelegation(delegationId: Int) {
        for (item in Delegations) {
            if (item.id == delegationId) {
                Delegations.remove(item)
                break
            }
        }
        notifyDataSetChanged()
    }

    public interface OnDeleteDelegationClicked {
        fun onDeleteClicked(delegationId: Int)
        fun onEditClicked(position:Int,model: DelegationDataItem)

    }
}