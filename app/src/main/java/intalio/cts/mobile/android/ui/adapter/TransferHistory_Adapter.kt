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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem

import intalio.cts.mobile.android.data.network.response.TransferHistoryDataItem
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsViewModel
import kotlinx.android.synthetic.main.toolbar_layout.*


class TransferHistory_Adapter(
    private val Transfers: ArrayList<TransferHistoryDataItem>,
    val activity: Activity,
    val viewModel: CorrespondenceDetailsViewModel,
    val translator: ArrayList<DictionaryDataItem>
) : RecyclerView.Adapter<TransferHistory_Adapter.AllNewsVHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transferhistort_viewshape, parent, false)
        )


    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {
        val normalTypeface = ResourcesCompat.getFont(activity, R.font.myriadpro_regular)
        val boldTypeface = ResourcesCompat.getFont(activity, R.font.myriadpro_bold)



        when {
            viewModel.readLanguage() == "en" -> {
                holder.fromTxt.text = "${translator.find { it.keyword == "From" }!!.en}: "
                holder.toTxt.text = "${translator.find { it.keyword == "To" }!!.en}: "
                holder.transferDateTxt.text = "${translator.find { it.keyword == "TransferDate" }!!.en}: "
                holder.instructionTxt.text = "${translator.find { it.keyword == "Instruction" }!!.en}: "
                holder.closedDateTxt.text = "${translator.find { it.keyword == "ClosedDate" }!!.en}: "

            }
            viewModel.readLanguage() == "ar" -> {
                holder.fromTxt.text = "${translator.find { it.keyword == "From" }!!.ar}: "
                holder.toTxt.text = "${translator.find { it.keyword == "To" }!!.ar}: "
                holder.transferDateTxt.text = "${translator.find { it.keyword == "TransferDate" }!!.ar}: "
                holder.instructionTxt.text = "${translator.find { it.keyword == "Instruction" }!!.ar}: "
                holder.closedDateTxt.text = "${translator.find { it.keyword == "ClosedDate" }!!.ar}: "
            }
            viewModel.readLanguage() == "fr" -> {
                holder.fromTxt.text = "${translator.find { it.keyword == "From" }!!.fr}: "
                holder.toTxt.text = "${translator.find { it.keyword == "To" }!!.fr}: "
                holder.transferDateTxt.text = "${translator.find { it.keyword == "TransferDate" }!!.fr}: "
                holder.instructionTxt.text = "${translator.find { it.keyword == "Instruction" }!!.fr}: "
                holder.closedDateTxt.text = "${translator.find { it.keyword == "ClosedDate" }!!.fr}: "

            }
        }


        if (Transfers[position].fromUser.isNullOrEmpty()) {
            holder.TransferSender.text = Transfers[position].fromStructure

        } else {
            val fullName = "${Transfers[position].fromStructure}/${Transfers[position].fromUser}"

            if ("${Transfers[position].fromStructure}/${Transfers[position].fromUser}".length > 30) {
                holder.TransferSender.text = "${fullName.substring(0, 30)}..."
            } else {
                holder.TransferSender.text =
                    "${Transfers[position].fromStructure}/${Transfers[position].fromUser}"

            }
        }


        Log.d("dattaes",Transfers[position].toUser.toString())
        Log.d("dattaes",Transfers[position].toStructure.toString())
        if (Transfers[position].toUser.isNullOrEmpty()) {
            holder.TransferReciever.text = Transfers[position].toStructure

        } else {
            val fullName = "${Transfers[position].toStructure}/${Transfers[position].toUser}"

            if ("${Transfers[position].toStructure}/${Transfers[position].toUser}".length > 30) {
                holder.TransferReciever.text = "${fullName.substring(0, 30)}..."
            } else {
                holder.TransferReciever.text =
                    "${Transfers[position].toStructure}/${Transfers[position].toUser}"

            }
        }
        holder.TransferDate.text = Transfers[position].transferDate
        holder.closedDate.text = Transfers[position].closedDate

        if (!Transfers[position].instruction.isNullOrEmpty()) {
            holder.Instructions.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(Transfers[position].instruction, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(Transfers[position].instruction)
            }
        }



        if (Transfers[position].isRead!!) {
            holder.TransferSender.typeface = normalTypeface
            holder.TransferReciever.typeface = normalTypeface
            holder.TransferDate.typeface = normalTypeface
            holder.Instructions.typeface = normalTypeface
            holder.closedDate.typeface = normalTypeface

            holder.fromTxt.typeface = normalTypeface
            holder.toTxt.typeface = normalTypeface
            holder.transferDateTxt.typeface = normalTypeface
            holder.instructionTxt.typeface = normalTypeface
            holder.closedDateTxt.typeface = normalTypeface



        } else {

            holder.TransferSender.typeface = boldTypeface
            holder.TransferReciever.typeface = boldTypeface
            holder.TransferDate.typeface = boldTypeface
            holder.Instructions.typeface = boldTypeface
            holder.closedDate.typeface = boldTypeface

            holder.fromTxt.typeface = boldTypeface
            holder.toTxt.typeface = boldTypeface
            holder.transferDateTxt.typeface = boldTypeface
            holder.instructionTxt.typeface = boldTypeface
            holder.closedDateTxt.typeface = boldTypeface
        }



        if (Transfers[position].sentToStructure == true) {
            holder.transferSentTo.setImageResource(R.drawable.ic_bulding)

        } else {
            holder.transferSentTo.setImageResource(R.drawable.ic_touser)

        }

       if (Transfers[position].isOverDue == true) {
            holder.transferOverdue.visibility = View.VISIBLE

        } else {
           holder.transferOverdue.visibility = View.GONE

        }


        if (position == Transfers.size - 1) {
            holder.vieww.visibility = View.GONE
        }
    }

    override fun getItemCount() = Transfers.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TransferSender: TextView = itemView.findViewById(R.id.attchsender)
        val TransferReciever: TextView = itemView.findViewById(R.id.attchreciever)
        val TransferDate: TextView = itemView.findViewById(R.id.attchdate)
        val Instructions: ReadMoreTextView = itemView.findViewById(R.id.attachinstructions)
        val transferSentTo: ImageView = itemView.findViewById(R.id.transfer_sentto)
        val closedDate: TextView = itemView.findViewById(R.id.attchcloseddate)


        val fromTxt: TextView = itemView.findViewById(R.id.fromtxt)
        val toTxt: TextView = itemView.findViewById(R.id.totxt)
        val transferDateTxt: TextView = itemView.findViewById(R.id.transferdatetxt)
        val instructionTxt: TextView = itemView.findViewById(R.id.instructionstxt)
        val closedDateTxt: TextView = itemView.findViewById(R.id.closeddatetxt)
        val transferOverdue: ImageView = itemView.findViewById(R.id.transfer_overdue)



        val vieww: View = itemView.findViewById(R.id.vieww)

    }

    fun addTransfer(moretransfers: ArrayList<TransferHistoryDataItem>) {
        this.Transfers.addAll(moretransfers)
        notifyDataSetChanged()
    }

}