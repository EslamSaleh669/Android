package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import intalio.cts.mobile.android.data.network.response.CorrespondenceDataItem
import intalio.cts.mobile.android.data.network.response.DelegationRequestsResponseItem
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceViewModel
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import intalio.cts.mobile.android.util.AutoDispose
import intalio.cts.mobile.android.util.Constants
import intalio.cts.mobile.android.util.makeToast
import intalio.cts.mobile.android.viewer.Utility
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CorrespondenceAdapter(
    private val Messages: ArrayList<CorrespondenceDataItem>,
    val activity: Activity,
    val NodeInherit: String,
    private val interfacePositionCard: InterfacePositionCard,
    val viewModel: CorrespondenceViewModel,
    val autoDispose: AutoDispose,
    val delegationId: Int

) : RecyclerView.Adapter<CorrespondenceAdapter.AllNewsVHolder>() {

    var lastChecked: RelativeLayout? = null
    var dialog: Dialog? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_viewshape, parent, false)
        )


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

        if (position == Messages.size - 1) {
            holder.correspondenceView.visibility = View.GONE
        }

        val normalTypeface = ResourcesCompat.getFont(activity, R.font.myriadpro_regular)
        val boldTypeface = ResourcesCompat.getFont(activity, R.font.myriadpro_bold)

        when (Messages[position].priorityId) {
            1 -> {

                holder.messageDot.setImageResource(R.drawable.ic_dot_normal)

            }
            2 -> {
                holder.messageDot.setImageResource(R.drawable.ic_dot_meduim)


            }
            3 -> {
                holder.messageDot.setImageResource(R.drawable.ic_dot_urgent)

            }
        }
        if (NodeInherit == "Inbox") {



            if (Messages[position].fromUser.isNullOrEmpty()) {
                holder.messageSentFrom.text = Messages[position].fromStructure

            } else {
                val fullName = "${Messages[position].fromStructure}/${Messages[position].fromUser}"

                if ("${Messages[position].fromStructure}/${Messages[position].fromUser}".length > 20) {
                    holder.messageSentFrom.text = "${fullName.substring(0, 20)}..."
                } else {
                    holder.messageSentFrom.text =
                        "${Messages[position].fromStructure}/${Messages[position].fromUser}"

                }
            }

            if (Messages[position].subject != null){
                if (Messages[position].subject!!.length > 20) {
                    holder.messageSub.text = "${Messages[position].subject!!.substring(0, 20)}..."
                } else {
                    holder.messageSub.text = Messages[position].subject

                }
            }


            if (Messages[position].referenceNumber != null){
                holder.messageRefNumber.text = Messages[position].referenceNumber
            }

            if (Messages[position].transferDate != null){
                holder.messageTime.text = Messages[position].transferDate

            }


            if (Messages[position].isRead!!) {
                holder.messageSentFrom.typeface = normalTypeface
                holder.messageRefNumber.typeface = normalTypeface

            } else {
                holder.messageSentFrom.typeface = boldTypeface
                holder.messageRefNumber.typeface = boldTypeface
            }


            viewModel.readSavedDelegator().let {
                if (it != null) {

                    if (it.fromUserId == 0) {

                        if (checkBroadCast(Messages[position].categoryId!!)) {
//                holder.messageLock.visibility = View.INVISIBLE
                            holder.messageCced.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.VISIBLE

                            if (Messages[position].sentToStructure == false) {
                                holder.messageLock.visibility = View.INVISIBLE
                                holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                            } else {


                                holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                                if (Messages[position].isLocked == false) {
                                    holder.messageLock.visibility = View.INVISIBLE

                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                    holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                    holder.messageLock.visibility = View.VISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                    holder.messageLock.setImageResource(R.drawable.ic_lock)
                                    holder.messageLock.visibility = View.VISIBLE


                                }
                            }

                        }
                        else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                            holder.messageSentTo.setImageResource(R.drawable.ic_bulding)

                            holder.messageLock.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.INVISIBLE
                            holder.messageCced.visibility = View.VISIBLE


                        }
                        else {
                            Log.d("whereareyou", "IAm at else")

                            holder.messageCced.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.INVISIBLE

                            if (Messages[position].sentToStructure == false) {
                                holder.messageLock.visibility = View.INVISIBLE
                                holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                            } else {
                                holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                                if (Messages[position].isLocked == false) {
                                    holder.messageLock.visibility = View.INVISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                    holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                    holder.messageLock.visibility = View.VISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                    holder.messageLock.setImageResource(R.drawable.ic_lock)
                                    holder.messageLock.visibility = View.VISIBLE


                                }
                            }
                        }
                    }

                    else {
                        if (checkBroadCast(Messages[position].categoryId!!)) {
//                holder.messageLock.visibility = View.INVISIBLE
                            holder.messageCced.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.VISIBLE

                            if (Messages[position].sentToStructure == false) {
                                holder.messageLock.visibility = View.INVISIBLE
                                holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                            } else {


                                holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                                if (Messages[position].isLocked == false) {
                                    holder.messageLock.visibility = View.INVISIBLE

                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy == it.fromUser) {
                                    holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                    holder.messageLock.visibility = View.VISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != it.fromUser) {
                                    holder.messageLock.setImageResource(R.drawable.ic_lock)
                                    holder.messageLock.visibility = View.VISIBLE


                                }
                            }

                        }
                        else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                            holder.messageSentTo.setImageResource(R.drawable.ic_bulding)

                            holder.messageLock.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.INVISIBLE
                            holder.messageCced.visibility = View.VISIBLE


                        }
                        else {
                            Log.d("whereareyou", "IAm at else")

                            holder.messageCced.visibility = View.INVISIBLE
                            holder.messageBroadcast.visibility = View.INVISIBLE

                            if (Messages[position].sentToStructure == false) {
                                holder.messageLock.visibility = View.INVISIBLE
                                holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                            } else {
                                holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                                if (Messages[position].isLocked == false) {
                                    holder.messageLock.visibility = View.INVISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy == it.fromUser) {
                                    holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                    holder.messageLock.visibility = View.VISIBLE


                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != it.fromUser) {
                                    holder.messageLock.setImageResource(R.drawable.ic_lock)
                                    holder.messageLock.visibility = View.VISIBLE


                                }
                            }
                        }
                    }

                } else {

                    if (checkBroadCast(Messages[position].categoryId!!)) {
//                holder.messageLock.visibility = View.INVISIBLE
                        holder.messageCced.visibility = View.INVISIBLE
                        holder.messageBroadcast.visibility = View.VISIBLE

                        if (Messages[position].sentToStructure == false) {
                            holder.messageLock.visibility = View.INVISIBLE
                            holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                        } else {


                            holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                            if (Messages[position].isLocked == false) {
                                holder.messageLock.visibility = View.INVISIBLE

                            } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                holder.messageLock.visibility = View.VISIBLE


                            } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                holder.messageLock.setImageResource(R.drawable.ic_lock)
                                holder.messageLock.visibility = View.VISIBLE


                            }
                        }

                    } else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                        holder.messageSentTo.setImageResource(R.drawable.ic_bulding)

                        holder.messageLock.visibility = View.INVISIBLE
                        holder.messageBroadcast.visibility = View.INVISIBLE
                        holder.messageCced.visibility = View.VISIBLE


                    } else {
                        Log.d("whereareyou", "IAm at else")

                        holder.messageCced.visibility = View.INVISIBLE
                        holder.messageBroadcast.visibility = View.INVISIBLE

                        if (Messages[position].sentToStructure == false) {
                            holder.messageLock.visibility = View.INVISIBLE
                            holder.messageSentTo.setImageResource(R.drawable.ic_touser)

                        } else {
                            holder.messageSentTo.setImageResource(R.drawable.ic_bulding)
                            if (Messages[position].isLocked == false) {
                                holder.messageLock.visibility = View.INVISIBLE


                            } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                holder.messageLock.setImageResource(R.drawable.ic_unlocked_icon)
                                holder.messageLock.visibility = View.VISIBLE


                            } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                holder.messageLock.setImageResource(R.drawable.ic_lock)
                                holder.messageLock.visibility = View.VISIBLE


                            }
                        }
                    }
                }
            }




            if (Messages[position].isOverDue!!) {
                holder.messageOverdue.visibility = View.VISIBLE
            } else {
                holder.messageOverdue.visibility = View.INVISIBLE

            }

            if (Messages[position].importanceId != null) {

                when (Messages[position].importanceId) {

                    1 -> {

                        holder.messageImportance.setColorFilter(activity.getColor(R.color.normal))
                        holder.messageImportance.visibility = View.VISIBLE

                    }
                    2 -> {
                        holder.messageImportance.setColorFilter(activity.getColor(R.color.important))
                        holder.messageImportance.visibility = View.VISIBLE

                    }
                    3 -> {
                        holder.messageImportance.setColorFilter(activity.getColor(R.color.very_important))
                        holder.messageImportance.visibility = View.VISIBLE

                    }

                }
            } else {
                holder.messageImportance.visibility = View.INVISIBLE

            }


        }

        else if (NodeInherit == "MyRequests") {
            holder.messageSentTo.visibility = View.INVISIBLE

            holder.messageImportance.visibility = View.INVISIBLE
            holder.messageLock.visibility = View.INVISIBLE

            holder.messageSentFrom.visibility = View.GONE

            if (Messages[position].isOverDue!!) {
                holder.messageOverdue.visibility = View.VISIBLE
            } else {
                holder.messageOverdue.visibility = View.INVISIBLE

            }

            if (Messages[position].subject != null){
                if (Messages[position].subject!!.length > 20) {
                    holder.messageSub.text = "${Messages[position].subject!!.substring(0, 20)}..."
                } else {
                    holder.messageSub.text = Messages[position].subject

                }
            }


            if (Messages[position].referenceNumber != null){
                holder.messageRefNumber.text = Messages[position].referenceNumber

            }
            if (Messages[position].createdDate != null){
                holder.messageTime.text = Messages[position].createdDate

            }


        }
        else if (NodeInherit == "Closed") {
            holder.messageSentTo.visibility = View.INVISIBLE

            holder.messageImportance.visibility = View.INVISIBLE
            holder.messageLock.visibility = View.INVISIBLE

            holder.messageSentFrom.visibility = View.GONE

            if (Messages[position].isOverDue!!) {
                holder.messageOverdue.visibility = View.VISIBLE
            } else {
                holder.messageOverdue.visibility = View.INVISIBLE

            }

            if (Messages[position].subject != null){
                if (Messages[position].subject!!.length > 20) {
                    holder.messageSub.text = "${Messages[position].subject!!.substring(0, 20)}..."
                } else {
                    holder.messageSub.text = Messages[position].subject

                }
            }


            if (Messages[position].referenceNumber != null){
                holder.messageRefNumber.text = Messages[position].referenceNumber

            }

            if (Messages[position].createdDate != null){
                holder.messageTime.text = Messages[position].createdDate

            }



        }
        else if (NodeInherit == "Sent") {

            holder.messageSentTo.visibility = View.INVISIBLE
            holder.messageImportance.visibility = View.INVISIBLE
            holder.messageOverdue.visibility = View.INVISIBLE
            holder.messageLock.visibility = View.INVISIBLE



            if (Messages[position].toUser.isNullOrEmpty()) {
                holder.messageSentFrom.text = Messages[position].toStructure

            } else {
                val fullName = "${Messages[position].toStructure}/${Messages[position].toUser}"

                if ("${Messages[position].toStructure}/${Messages[position].toUser}".length > 20) {
                    holder.messageSentFrom.text = "${fullName.substring(0, 20)}..."
                } else {
                    holder.messageSentFrom.text =
                        "${Messages[position].toStructure}/${Messages[position].toUser}"

                }
            }

            if (Messages[position].subject != null){
                if (Messages[position].subject!!.length > 20) {
                    holder.messageSub.text = "${Messages[position].subject!!.substring(0, 20)}..."
                } else {
                    holder.messageSub.text = Messages[position].subject

                }
            }

            if (Messages[position].referenceNumber != null){
                holder.messageRefNumber.text = Messages[position].referenceNumber

            }
            if (Messages[position].transferDate != null){
                holder.messageTime.text = Messages[position].transferDate

            }



            if (Messages[position].isRead == false) {
                holder.messageRecall.visibility = View.VISIBLE
            } else {
                holder.messageRecall.visibility = View.GONE

            }


            holder.messageRecall.setOnClickListener {
                recallTransfer(Messages[position].id!!, holder.messageRecall)
            }


        }

        else {
            holder.messageSentTo.visibility = View.INVISIBLE
            holder.messageImportance.visibility = View.INVISIBLE
            holder.messageOverdue.visibility = View.INVISIBLE
            holder.messageLock.visibility = View.INVISIBLE



            if (Messages[position].fromUser.isNullOrEmpty()) {
                holder.messageSentFrom.text = Messages[position].fromStructure

            } else {
                val fullName = "${Messages[position].fromStructure}/${Messages[position].fromUser}"
                if ("${Messages[position].fromStructure}/${Messages[position].fromUser}".length > 20) {
                    holder.messageSentFrom.text = "${fullName.substring(0, 20)}..."
                } else {
                    holder.messageSentFrom.text =
                        "${Messages[position].fromStructure}/${Messages[position].fromUser}"

                }
            }


            if (Messages[position].subject!!.length > 20) {
                holder.messageSub.text = "${Messages[position].subject!!.substring(0, 20)}..."
            } else {
                holder.messageSub.text = Messages[position].subject

            }
            holder.messageRefNumber.text = Messages[position].referenceNumber
            holder.messageTime.text = Messages[position].transferDate


        }


//        holder.relativeLayout.setOnLongClickListener(OnLongClickListener {
//            if (lastChecked == null) {
//                lastChecked = holder.relativeLayout
//
//                lastChecked!!.setBackgroundColor(
//                    activity.resources.getColor(R.color.dark_gray)
//                )
//                interfacePositionCard.getPosition(position, 1, Messages[position])
//            } else {
//                lastChecked!!.setBackgroundColor(
//                    activity.resources.getColor(R.color.grey)
//                )
//                lastChecked = holder.relativeLayout
//                lastChecked!!.setBackgroundColor(
//                    activity.resources.getColor(R.color.dark_gray)
//                )
//                interfacePositionCard.getPosition(position, 1, Messages[position])
//            }
//            true
//        })

        holder.relativeLayout.setOnClickListener {

//            if (lastChecked != null) {
//                lastChecked!!.setBackgroundColor(
//                    activity.resources.getColor(R.color.grey)
//                )
//                interfacePositionCard.getPosition(position, 0, Messages[position])
//            } else {
            if (NodeInherit == "Inbox") {

                viewModel.readSavedDelegator().let {
                    if (it != null) {

                        if (it.fromUserId == 0) {

                            if (Messages[position].cced == false && checkBroadCast(Messages[position].categoryId!!)) {

                                if (Messages[position].sentToStructure == false) {
                                    Messages[position].isexternalbroadcast =
                                        checkExternalBroadCast(Messages[position].categoryId!!)
                                    val bundle = Bundle()
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )

                                    Messages[position].messageLock = "broadcastnotlocked"
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }
                                } else {
                                    if (Messages[position].isLocked == false) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        lockTransfer(Messages[position], "broadcast", NodeInherit)


                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        //// locked by me
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "broadcastlockedbyme"
                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        //// locked by another one (viewonly)
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "broadcastlockedbyanother"

                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }
                                    }
                                }


                            }
                            //original broadcast
                            else if (Messages[position].cced == true && checkBroadCast(Messages[position].categoryId!!)) {
                                Messages[position].isexternalbroadcast =
                                    checkExternalBroadCast(Messages[position].categoryId!!)

                                val bundle = Bundle()
                                Messages[position].messageLock = "broadcastnotlocked"
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )


                                        }
                                    )
                                    addToBackStack("")

                                }

                            }

                            else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                                val bundle = Bundle()
                                Messages[position].messageLock = "cced"
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )


                                        }
                                    )
                                    addToBackStack("")

                                }
                            }
                            else {
                                if (Messages[position].sentToStructure == false) {
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "notlocked"
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )
                                            }
                                        )
                                        addToBackStack("")

                                    }
                                } else {
                                    if (Messages[position].isLocked == false) {
                                        lockTransfer(
                                            Messages[position],
                                            "notbroadcast",
                                            NodeInherit
                                        )

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {

                                        //// locked by me
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "lockedbyme"
                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {

                                        //// locked by another one (viewonly)
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "lockedbyanother"

                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }
                                    }
                                }
                            }

                        } else {
                            if (Messages[position].cced == false && checkBroadCast(Messages[position].categoryId!!)) {

                                if (Messages[position].sentToStructure == false) {
                                    Messages[position].isexternalbroadcast =
                                        checkExternalBroadCast(Messages[position].categoryId!!)
                                    val bundle = Bundle()
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )

                                    Messages[position].messageLock = "broadcastnotlocked"
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }
                                } else {
                                    if (Messages[position].isLocked == false) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        lockTransfer(Messages[position], "broadcast", NodeInherit)


                                    } else if (Messages[position].isLocked == true &&
                                        Messages[position].lockedBy == it.fromUser) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        //// locked by me
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "broadcastlockedbyme"
                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy != it.fromUser) {
                                        Messages[position].isexternalbroadcast =
                                            checkExternalBroadCast(Messages[position].categoryId!!)

                                        //// locked by another one (viewonly)
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "broadcastlockedbyanother"

                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }
                                    }
                                }


                            }
                            //original broadcast
                            else if (Messages[position].cced == true && checkBroadCast(Messages[position].categoryId!!)) {
                                Messages[position].isexternalbroadcast =
                                    checkExternalBroadCast(Messages[position].categoryId!!)

                                val bundle = Bundle()
                                Messages[position].messageLock = "broadcastnotlocked"
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )


                                        }
                                    )
                                    addToBackStack("")

                                }

                            }
                            else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                                val bundle = Bundle()
                                Messages[position].messageLock = "cced"
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )


                                        }
                                    )
                                    addToBackStack("")

                                }
                            }
                            else {
                                if (Messages[position].sentToStructure == false) {
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "notlocked"
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )
                                            }
                                        )
                                        addToBackStack("")

                                    }
                                } else {
                                    if (Messages[position].isLocked == false) {
                                        lockTransfer(
                                            Messages[position],
                                            "notbroadcast",
                                            NodeInherit
                                        )

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy == it.fromUser) {

                                        //// locked by me
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "lockedbyme"
                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }

                                    } else if (Messages[position].isLocked == true && Messages[position].lockedBy != it.fromUser) {

                                        //// locked by another one (viewonly)
                                        val bundle = Bundle()
                                        Messages[position].messageLock = "lockedbyanother"

                                        bundle.putSerializable(
                                            Constants.Correspondence_Model,
                                            Messages[position]
                                        )
                                        (activity as AppCompatActivity).supportFragmentManager.commit {
                                            replace(R.id.fragmentContainer,
                                                CorrespondenceDetailsFragment().apply {
                                                    arguments = bundleOf(
                                                        Pair(Constants.PATH, "node"),
                                                        Pair(
                                                            Constants.Correspondence_Model,
                                                            Messages[position]
                                                        )
                                                    )


                                                }
                                            )
                                            addToBackStack("")

                                        }
                                    }
                                }
                            }

                            ////////////////////////////////
                        }

                    } else {
                        if (Messages[position].cced == false && checkBroadCast(Messages[position].categoryId!!)) {

                            if (Messages[position].sentToStructure == false) {
                                Messages[position].isexternalbroadcast =
                                    checkExternalBroadCast(Messages[position].categoryId!!)
                                val bundle = Bundle()
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )

                                Messages[position].messageLock = "broadcastnotlocked"
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )


                                        }
                                    )
                                    addToBackStack("")

                                }
                            } else {
                                if (Messages[position].isLocked == false) {
                                    Messages[position].isexternalbroadcast =
                                        checkExternalBroadCast(Messages[position].categoryId!!)

                                    lockTransfer(Messages[position], "broadcast", NodeInherit)


                                } else if
                                               (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {
                                    Messages[position].isexternalbroadcast =
                                        checkExternalBroadCast(Messages[position].categoryId!!)

                                    //// locked by me
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "broadcastlockedbyme"
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }

                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {
                                    Messages[position].isexternalbroadcast =
                                        checkExternalBroadCast(Messages[position].categoryId!!)

                                    //// locked by another one (viewonly)
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "broadcastlockedbyanother"

                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }
                                }
                            }


                        }
                        //original broadcast
                        else if (Messages[position].cced == true && checkBroadCast(Messages[position].categoryId!!)) {
                            Messages[position].isexternalbroadcast =
                                checkExternalBroadCast(Messages[position].categoryId!!)

                            val bundle = Bundle()
                            Messages[position].messageLock = "broadcastnotlocked"
                            bundle.putSerializable(
                                Constants.Correspondence_Model,
                                Messages[position]
                            )
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.PATH, "node"),
                                            Pair(Constants.Correspondence_Model, Messages[position])
                                        )


                                    }
                                )
                                addToBackStack("")

                            }

                        }
                        else if (Messages[position].cced == true && !checkBroadCast(Messages[position].categoryId!!)) {
                            val bundle = Bundle()
                            Messages[position].messageLock = "cced"
                            bundle.putSerializable(
                                Constants.Correspondence_Model,
                                Messages[position]
                            )
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.PATH, "node"),
                                            Pair(Constants.Correspondence_Model, Messages[position])
                                        )


                                    }
                                )
                                addToBackStack("")

                            }
                        }
                        else {
                            if (Messages[position].sentToStructure == false) {
                                val bundle = Bundle()
                                Messages[position].messageLock = "notlocked"
                                bundle.putSerializable(
                                    Constants.Correspondence_Model,
                                    Messages[position]
                                )
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceDetailsFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.PATH, "node"),
                                                Pair(
                                                    Constants.Correspondence_Model,
                                                    Messages[position]
                                                )
                                            )
                                        }
                                    )
                                    addToBackStack("")

                                }
                            } else {
                                if (Messages[position].isLocked == false) {
                                    lockTransfer(Messages[position], "notbroadcast", NodeInherit)

                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy == viewModel.readUserinfo().fullName) {

                                    //// locked by me
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "lockedbyme"
                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }

                                } else if (Messages[position].isLocked == true && Messages[position].lockedBy != viewModel.readUserinfo().fullName) {

                                    //// locked by another one (viewonly)
                                    val bundle = Bundle()
                                    Messages[position].messageLock = "lockedbyanother"

                                    bundle.putSerializable(
                                        Constants.Correspondence_Model,
                                        Messages[position]
                                    )
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(
                                                        Constants.Correspondence_Model,
                                                        Messages[position]
                                                    )
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }
                                }
                            }
                        }
                    }
                }


            }
            else {
                val bundle = Bundle()
                bundle.putSerializable(Constants.Correspondence_Model, Messages[position])
                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        CorrespondenceDetailsFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.PATH, "node"),
                                Pair(Constants.Correspondence_Model, Messages[position])
                            )


                        }
                    )
                    addToBackStack("")

                }
            }
        }



        //   }
    }


    private fun lockTransfer(model: CorrespondenceDataItem, status: String, NodeInherit: String) {

        var confirmlock = ""
        var yes = ""
        var no = ""
        var error = ""
        var alreadyLockedBy = ""

        when {
            viewModel.readLanguage() == "en" -> {

                confirmlock =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "LockConfirmation" }!!.en!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.en!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.en!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.en!!
                alreadyLockedBy =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "AlreadyLockedBy" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                confirmlock =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "LockConfirmation" }!!.ar!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.ar!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.ar!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.ar!!
                alreadyLockedBy =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "AlreadyLockedBy" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {


                confirmlock =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "LockConfirmation" }!!.fr!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.fr!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.fr!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.fr!!
                alreadyLockedBy =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "AlreadyLockedBy" }!!.fr!!


            }
        }


        val width = (activity.resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(confirmlock)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()



                    viewModel.lockTransfer(
                        model.id!!,
                        delegationId

                    ).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {

                            var responseRecieved: Any? = null
                            var lockedByName: Any? = null
                            lockedByName = response.body()!!.string()
                            responseRecieved = response.body()!!.string()



                                if (lockedByName.toString().contains("true")) {
                                    val bundle = Bundle()

                                    if (status == "broadcast") {
                                        model.messageLock = "broadcastlockedbyme"
                                    } else {
                                        model.messageLock = "lockedbyme"

                                    }

                                    if (delegationId == 0) {

                                        model.lockedBy = "you"
//                                        model.lockedByDelegatedUser = "false"

                                    } else {

                                        model.lockedBy = "delegator"
                                        model.lockedByDelegatedUser = "true"

                                    }
                                    model.lockedDate = getToday()

                                    bundle.putSerializable(Constants.Correspondence_Model, model)
                                    (activity as AppCompatActivity).supportFragmentManager.commit {
                                        replace(R.id.fragmentContainer,
                                            CorrespondenceDetailsFragment().apply {
                                                arguments = bundleOf(
                                                    Pair(Constants.PATH, "node"),
                                                    Pair(Constants.Correspondence_Model, model)
                                                )


                                            }
                                        )
                                        addToBackStack("")

                                    }

                                } else {
                                    activity.makeToast(alreadyLockedBy+" "+lockedByName)
                                    interfacePositionCard.refreshInbox(NodeInherit)


                                }



                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {


                         activity.makeToast(error)

                        }


                    }


                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    val bundle = Bundle()
                    if (status == "broadcast") {
                        model.messageLock = "broadcastlockedbyanother"
                    } else {
                        model.messageLock = "lockedbyanother"

                    }
                    bundle.putSerializable(Constants.Correspondence_Model, model)
                    (activity as AppCompatActivity).supportFragmentManager.commit {
                        replace(R.id.fragmentContainer,
                            CorrespondenceDetailsFragment().apply {
                                arguments = bundleOf(
                                    Pair(Constants.PATH, "node"),
                                    Pair(Constants.Correspondence_Model, model)
                                )
                            }
                        )
                        addToBackStack("")

                    }

                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.format(Date())
    }

    override fun getItemCount() = Messages.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageSentFrom: TextView = itemView.findViewById(R.id.message_sentfrom)
        val messageSub: TextView = itemView.findViewById(R.id.message_subject)
        val messageRefNumber: TextView = itemView.findViewById(R.id.message_refnumber)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)

        val messageLock: ImageView = itemView.findViewById(R.id.message_lock)
        val messageSentTo: ImageView = itemView.findViewById(R.id.message_sentto)
        val messageImportance: ImageView = itemView.findViewById(R.id.message_importance)
        val messageOverdue: ImageView = itemView.findViewById(R.id.message_overdue)
        val messageCced: ImageView = itemView.findViewById(R.id.message_cced)
        val messageBroadcast: ImageView = itemView.findViewById(R.id.message_broadcast)
        val messageDot: ImageView = itemView.findViewById(R.id.message_dot)
        val messageRecall: RelativeLayout = itemView.findViewById(R.id.message_recall)

        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.messagerel)
        val correspondenceView: View = itemView.findViewById(R.id.viewwinbox)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun checkBroadCast(categoryId: Int): Boolean {
        val categoryItem = viewModel.readFullCategories().find { it.id == categoryId }
        var isExist = false
        if (categoryItem!!.basicAttribute != null) {

            val basicAttribute: String = categoryItem.basicAttribute!!


            val formattedKey: String = Utility.toFormattedCase(
                basicAttribute.replace("(.)(\\p{Upper})".toRegex(), "$1_$2").lowercase()
            )

            val pureFormattedKey = formattedKey.replace("\"", "")


            isExist = pureFormattedKey.contains("Broadcast Receiving Entity:true")

        }



        return isExist
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkExternalBroadCast(categoryId: Int): Boolean {
        val categoryItem = viewModel.readFullCategories().find { it.id == categoryId }
        var isExist = false
        if (categoryItem!!.basicAttribute != null) {

            val basicAttribute: String = categoryItem.basicAttribute!!

            val formattedKey: String = Utility.toFormattedCase(
                basicAttribute.replace("(.)(\\p{Upper})".toRegex(), "$1_$2").lowercase()
            )

            val pureFormattedKey = formattedKey.replace("\"", "")

            if (pureFormattedKey.contains("Name: Receiving Entity, Type:external") &&
                pureFormattedKey.contains("Broadcast Receiving Entity:true")
            ) {
                isExist = true
            }


        }



        return isExist
    }

    private fun recallTransfer(transferID: Int, messageRecall: RelativeLayout) {

        var confirmrecall = ""
        var yes = ""
        var no = ""
        var error = ""



        when {
            viewModel.readLanguage() == "en" -> {

                confirmrecall =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "RecallConfirmation" }!!.en!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.en!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.en!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                confirmrecall =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "RecallConfirmation" }!!.ar!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.ar!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.ar!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {


                confirmrecall =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "RecallConfirmation" }!!.fr!!
                yes = viewModel.readDictionary()!!.data!!.find { it.keyword == "Yes" }!!.fr!!
                no = viewModel.readDictionary()!!.data!!.find { it.keyword == "No" }!!.fr!!
                error =
                    viewModel.readDictionary()!!.data!!.find { it.keyword == "ErrorOccured" }!!.fr!!


            }
        }

        val width = (activity.resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(confirmrecall)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()

//                    dialog = activity.launchLoadingDialog()
                    viewModel.recallTransfer(
                        transferID,
                        delegationId

                    ).enqueue(object : Callback<ResponseBody> {


                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
//                            dialog!!.dismiss()

                            try {

                                var responseRecieved: Any? = null
                                responseRecieved = response.body()!!.string()

                                if (responseRecieved.toString().isEmpty()) {
                                    activity.onBackPressed()

                                } else {


                                    if (responseRecieved.toString() == "true") {


                                        messageRecall.visibility = View.GONE
                                        interfacePositionCard.refreshSent(NodeInherit)


                                    }

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()

                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                            dialog!!.dismiss()

                            activity.makeToast(error)
                        }

                    }

                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()


                }).show().window!!.setLayout(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
    }


    fun addMessages(morenews: ArrayList<CorrespondenceDataItem>) {
        this.Messages.addAll(morenews)
        notifyDataSetChanged()
    }


    interface InterfacePositionCard {
        fun getPosition(position: Int, status: Int, model: CorrespondenceDataItem?)
        fun refreshInbox(NodeInherit: String)
        fun refreshSent(NodeInherit: String)
    }

}