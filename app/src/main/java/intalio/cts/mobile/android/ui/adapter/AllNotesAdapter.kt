package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.NotesDataItem
import intalio.cts.mobile.android.ui.fragment.note.NotesViewModel


class AllNotesAdapter(
    private val Notes: ArrayList<NotesDataItem>,
    val activity: Activity,
    private val onDeleteCLickListener: OnDeleteNoteClicked,
    private val Node_Inherit: String,
    private val canDoAction: Boolean,
    private val viewModel: NotesViewModel
) : RecyclerView.Adapter<AllNotesAdapter.AllCategoriesVHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCategoriesVHolder =
        AllCategoriesVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notes_viewshape, parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AllCategoriesVHolder, position: Int) {

        val translator = viewModel.readDictionary()!!.data!!
        var onBehalfOf = ""
        when {
            viewModel.readLanguage() == "en" -> {
                onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                onBehalfOf = translator.find { it.keyword == "OnBehalfOf" }!!.fr!!
            }
        }

        if (Notes[position].isPrivate!! && Notes[position].createdBy != viewModel.readUserinfo().fullName){
             val  x = "xx"
        }else{
            if (Node_Inherit != "Inbox" || !canDoAction) {
                holder.noteDelete.visibility = View.INVISIBLE
                holder.noteEdit.visibility = View.INVISIBLE
            }

            if (Notes[position].isEditable == false) {
                holder.noteDelete.visibility = View.INVISIBLE
                holder.noteEdit.visibility = View.INVISIBLE
            }

            if (Notes[position].createdByDelegatedUser.isNullOrEmpty()){
                holder.createsBy.text = Notes[position].createdBy

            }else{
                holder.createsBy.text = "${Notes[position].createdBy} $onBehalfOf ${Notes[position].createdByDelegatedUser}"
            }
            holder.noteDate.text = Notes[position].createdDate
            holder.noteDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(Notes[position].notes, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(Notes[position].notes)
            }

            if (position == Notes.size - 1) {
                holder.vieww.visibility = View.GONE
            }

            if (Notes[position].isPrivate == true) {
                holder.notePrivacy.visibility = View.VISIBLE
            } else {
                holder.notePrivacy.visibility = View.GONE

            }

            holder.noteDelete.setOnClickListener {
                onDeleteCLickListener.onDeleteClicked(Notes[position].id!!)
            }

            holder.noteEdit.setOnClickListener {
                onDeleteCLickListener.onEditClicked(position, Notes[position])
            }
        }



    }

    override fun getItemCount() = Notes.size

    class AllCategoriesVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val createsBy: TextView = itemView.findViewById(R.id.notecreatedby)
        val noteDescription: ReadMoreTextView = itemView.findViewById(R.id.notedescription)
        val noteDate: TextView = itemView.findViewById(R.id.notedate)
        val noteDelete: ImageView = itemView.findViewById(R.id.deletenote)
        val noteEdit: ImageView = itemView.findViewById(R.id.editnote)
        val notePrivacy: ImageView = itemView.findViewById(R.id.privatenote)
        val vieww: View = itemView.findViewById(R.id.vieww)
    }


    fun addNotes(morenotes: ArrayList<NotesDataItem>) {
        this.Notes.addAll(morenotes)
        notifyDataSetChanged()
    }

    fun removeNote(noteid: Int) {
        for (note in Notes) {
            if (note.id == noteid) {
                Notes.remove(note)
                break
            }
        }
        notifyDataSetChanged()
    }

    fun modifyNote(position: Int, model: NotesDataItem) {
        Notes[position] = model
        notifyItemChanged(position)
    }

    public interface OnDeleteNoteClicked {
        fun onDeleteClicked(noteid: Int)
        fun onEditClicked(position: Int, model: NotesDataItem)
    }
}