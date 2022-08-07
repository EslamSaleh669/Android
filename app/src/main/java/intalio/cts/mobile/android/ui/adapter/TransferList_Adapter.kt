package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*

import intalio.cts.mobile.android.ui.fragment.transfer.TransfersViewModel


class TransferList_Adapter(
    private val Transfers: ArrayList<TransferRequestModel>,
    val activity: Activity,
    private val onTransferCLickListener: OnTransferClicked,
    val viewModel: TransfersViewModel,
    val translator: ArrayList<DictionaryDataItem>

) : RecyclerView.Adapter<TransferList_Adapter.AllNewsVHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transferslist_viewshape,parent,false)
        )



    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {

        when {
            viewModel.readLanguage() == "en" -> {
                holder.toLabel.text = "${translator.find { it.keyword == "To" }!!.en}: "
                holder.purposeLabel.text = "${translator.find { it.keyword == "Purpose" }!!.en}: "
                holder.dueDateLabel.text = "${translator.find { it.keyword == "DueDate" }!!.en}: "
                holder.instructionsLabel.text = "${translator.find { it.keyword == "Instruction" }!!.en}: "

            }
            viewModel.readLanguage() == "ar" -> {
                holder.toLabel.text = "${translator.find { it.keyword == "To" }!!.ar}: "
                holder.purposeLabel.text = "${translator.find { it.keyword == "Purpose" }!!.ar}: "
                holder.dueDateLabel.text = "${translator.find { it.keyword == "DueDate" }!!.ar}: "
                holder.instructionsLabel.text = "${translator.find { it.keyword == "Instruction" }!!.ar}: "
            }
            viewModel.readLanguage() == "fr" -> {
                holder.toLabel.text = "${translator.find { it.keyword == "To" }!!.fr}: "
                holder.purposeLabel.text = "${translator.find { it.keyword == "Purpose" }!!.fr}: "
                holder.dueDateLabel.text = "${translator.find { it.keyword == "DueDate" }!!.fr}: "
                holder.instructionsLabel.text = "${translator.find { it.keyword == "Instruction" }!!.fr}: "
            }
        }

        holder.transferTo.text = Transfers[position].name
        holder.transferPurpose.text = Transfers[position].purposeName
        holder.transferDueDate.text = Transfers[position].dueDate
        holder.transferInstructions.text = Transfers[position].instruction

        holder.transferDelete.setOnClickListener {
            onTransferCLickListener.onDeleteClicked(Transfers[position].transferOfflineId!!)
        }
        if (position == Transfers.size - 1) {
            holder.vieww.visibility = View.GONE
        }
    }

    override fun getItemCount() = Transfers.size


    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transferTo : TextView =itemView.findViewById(R.id.transfer_reciever)
        val transferPurpose : TextView =itemView.findViewById(R.id.transfer_purpose)
        val transferDueDate : TextView =itemView.findViewById(R.id.transfer_duedate)
        val transferInstructions : ReadMoreTextView =itemView.findViewById(R.id.transfer_inst)
        val transferDelete : ImageView =itemView.findViewById(R.id.deletetrans)
        val transferEdit : ImageView =itemView.findViewById(R.id.edittrans)
        val vieww : View =itemView.findViewById(R.id.tvieww)

        val toLabel : TextView =itemView.findViewById(R.id.tolbl)
        val purposeLabel : TextView =itemView.findViewById(R.id.purplbl)
        val dueDateLabel : TextView =itemView.findViewById(R.id.duedatelbl)
        val instructionsLabel : TextView =itemView.findViewById(R.id.instructionlbl)


    }

    fun removeTransfer(transferId: Int) {
        for (transfer in Transfers) {
            if (transfer.transferOfflineId == transferId) {
                Transfers.remove(transfer)
                break
            }
        }
        notifyDataSetChanged()
    }

    fun modifyTransfer(position: Int, model: TransferRequestModel) {
        Transfers[position] = model
        notifyItemChanged(position)
    }

    public interface OnTransferClicked {
        fun onDeleteClicked(transferId: Int)
        fun onEditClicked(position:Int,model: TransferRequestModel)

    }
}