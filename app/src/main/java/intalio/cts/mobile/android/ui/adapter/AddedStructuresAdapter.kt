package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AllStructuresItem
import intalio.cts.mobile.android.util.makeToast

class AddedStructuresAdapter(
    private val activity: Activity?,
    private var Categories: ArrayList<AllStructuresItem>,
    private val onDeleteClickedListener: OnDeleteClicked,
) :
    RecyclerView.Adapter<AddedStructuresAdapter.AddedCategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedCategoriesViewHolder =
        AddedCategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.transfer_list_item_layout,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = Categories.size

    override fun onBindViewHolder(holder: AddedCategoriesViewHolder, position: Int) {

        holder.catTitle.text = Categories.get(position).name
        holder.catDelete.setOnClickListener {
            onDeleteClickedListener.onDeleteClicked(Categories[position].id!!,Categories[position].name!!)
        }


    }

    fun addCategories(morecats: AllStructuresItem){
        if (Categories.contains(morecats)){

            activity!!.makeToast(activity.getString(R.string.duplictedcategory))

        }else{

            this.Categories.add(morecats)
            notifyDataSetChanged()
        }

    }

    fun removeCategory(catid: Int) {
        for (cat in Categories) {
            if (cat.id == catid) {
                Categories.remove(cat)
                break
            }
        }
        notifyDataSetChanged()
    }

    class AddedCategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val catTitle: TextView = itemView.findViewById(R.id.cattitle)
         val catDelete: ImageView = itemView.findViewById(R.id.catdelete)

    }

    public interface OnDeleteClicked {
        fun onDeleteClicked(catid: Int,catname : String)
    }
}