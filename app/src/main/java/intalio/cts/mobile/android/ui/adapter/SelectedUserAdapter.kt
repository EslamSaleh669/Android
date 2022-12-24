package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.model.selectedUserModel
import intalio.cts.mobile.android.ui.fragment.transfer.TransfersViewModel
import intalio.cts.mobile.android.util.makeToast

class SelectedUserAdapter(
    private val activity: Activity?,
    private var selectedUsers: ArrayList<selectedUserModel>,
    private val onDeleteClickedListener: OnDeleteSelectedUserClicked,
    private val viewModel: TransfersViewModel,
) :
    RecyclerView.Adapter<SelectedUserAdapter.AddedUsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedUsersViewHolder =
        AddedUsersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.selected_user_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = selectedUsers.size

    override fun onBindViewHolder(holder: AddedUsersViewHolder, position: Int) {

        holder.userTitle.text = selectedUsers.get(position).text
        holder.userDelete.setOnClickListener {
            onDeleteClickedListener.onDeleteSelectedUSerClick(selectedUsers[position].offlineId!!)
        }


    }

    fun addSelectedUser(moreusers: selectedUserModel) {

        var message = ""
        val translator = viewModel.readDictionary()!!.data
        when {
            viewModel.readLanguage() == "en" -> {

                message =
                    translator!!.find { it.keyword == "AlreadyExists" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {

                message =
                    translator!!.find { it.keyword == "AlreadyExists" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                message =
                    translator!!.find { it.keyword == "AlreadyExists" }!!.fr!!

            }
        }


        selectedUsers.find { it.text == moreusers.text }.let {
            if (it != null) {
                activity!!.makeToast(message)

            } else {
                this.selectedUsers.add(moreusers)
                notifyDataSetChanged()

            }
        }
//        Log.d("inadapter","iaminadapter")
//        if (selectedUsers.contains(moreusers)){
//            Log.d("inadapter","alreadyhave")
//
//            activity!!.makeToast(activity.getString(R.string.duplictedcategory))
//
//        }else{
//            Log.d("inadapter","added ${moreusers.text}")
//
//            this.selectedUsers.add(moreusers)
//            notifyDataSetChanged()
//
//
//        }
//        var isInsideList = false
//
//        for (item in selectedUsers){
//            if (item.text == moreusers.text){
//                isInsideList = true
//            }
//        }
//
//        if (!isInsideList){
//            this.selectedUsers.add(moreusers)
//
//        }else{
//            activity!!.makeToast(activity.getString(R.string.duplictedcategory))
//
//        }
        Log.d("inadapter", "${selectedUsers.size}")

    }

    fun removeSelectedUser(offlineId: Int) {
        Log.d("clickdedd", "${offlineId}")

        for (user in selectedUsers) {
            if (user.offlineId == offlineId) {
                selectedUsers.remove(user)
                break
            }
        }
        notifyDataSetChanged()
    }

    class AddedUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTitle: TextView = itemView.findViewById(R.id.selected_user_title)
        val userDelete: ImageView = itemView.findViewById(R.id.selected_user_delete)

    }

    public interface OnDeleteSelectedUserClicked {
        fun onDeleteSelectedUSerClick(offlineId: Int)
    }
}