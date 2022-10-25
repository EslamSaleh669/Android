package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.NonArchDataItem


class NonArchAdapter(
    private val NonArch: ArrayList<NonArchDataItem>,
    val activity: Activity,
    private val onDeleteCLickListener: OnDeleteNoteClicked,
    private val Node_Inherit: String,
    private val canDoAction :Boolean

) : RecyclerView.Adapter<NonArchAdapter.AllCategoriesVHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCategoriesVHolder =
        AllCategoriesVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.nonarch_viewshape, parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AllCategoriesVHolder, position: Int) {
        if (Node_Inherit != "Inbox" || !canDoAction){
            holder.nonArchDelete.visibility = View.INVISIBLE
            holder.nonArchEdit.visibility = View.INVISIBLE
        }

        if (NonArch[position].isEditable == false){
            holder.nonArchDelete.visibility = View.INVISIBLE
            holder.nonArchEdit.visibility = View.INVISIBLE
        }
        holder.nonArchCreatesBy.text = NonArch[position].createdBy
        holder.nonArchDescription.text = NonArch[position].description
        holder.nonArchFileCount.text = NonArch[position].quantity.toString()
        holder.nonArchFileType.text = NonArch[position].type

        if (position == NonArch.size - 1) {
            holder.vieww.visibility = View.GONE
        }

        holder.nonArchDelete.setOnClickListener {
            onDeleteCLickListener.onDeleteClicked(NonArch[position].id!!)
        }

        holder.nonArchEdit.setOnClickListener {
            onDeleteCLickListener.onEditClicked(position,NonArch[position])
        }



    }

    override fun getItemCount() = NonArch.size

    class AllCategoriesVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nonArchCreatesBy: TextView = itemView.findViewById(R.id.nonarchcreatedby)
        val nonArchDescription: ReadMoreTextView = itemView.findViewById(R.id.nonarchdescription)
        val nonArchFileCount: TextView = itemView.findViewById(R.id.nonarchfilecount)
        val nonArchFileType: TextView = itemView.findViewById(R.id.nonarchfiletype)
        val nonArchDelete: ImageView = itemView.findViewById(R.id.deletenonarch)
        val nonArchEdit: ImageView = itemView.findViewById(R.id.editnonarch)
        val vieww : View =itemView.findViewById(R.id.vieww)
    }

    fun addNonArch(morenonarch: ArrayList<NonArchDataItem>) {
        this.NonArch.addAll(morenonarch)
        notifyDataSetChanged()
    }

    fun removeNoneArch(nonarchid: Int) {
        for (note in NonArch) {
            if (note.id == nonarchid) {
                NonArch.remove(note)
                break
            }
        }
        notifyDataSetChanged()
    }

    fun modifyNonArch(position: Int, model: NonArchDataItem) {
        NonArch[position] = model
        notifyItemChanged(position)
    }


    public interface OnDeleteNoteClicked {
        fun onDeleteClicked(nonarchid: Int)
        fun onEditClicked(position:Int,model: NonArchDataItem)

    }
}