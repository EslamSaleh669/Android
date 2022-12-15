package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.NotesDataItem
import intalio.cts.mobile.android.data.network.response.TransferRequestModel
import intalio.cts.mobile.android.ui.fragment.note.NotesViewModel
import intalio.cts.mobile.android.ui.fragment.transfer.TransfersViewModel


class TransferGrids_Adapter(
    private val Transfers: ArrayList<TransferRequestModel>,
    val activity: Activity,
    private val onTransferGridCLickListener: OnTransferGridClicked,
    val viewModel: TransfersViewModel,
    val translator: ArrayList<DictionaryDataItem>

) : RecyclerView.Adapter<TransferGrids_Adapter.AllCategoriesVHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCategoriesVHolder =
        AllCategoriesVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transfergrid_viewshape, parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AllCategoriesVHolder, position: Int) {


        holder.transferTo.text = Transfers[position].name
        holder.transferPurpose.text = Transfers[position].purposeName
        holder.transferDueDate.text = Transfers[position].dueDate
//        holder.transferInstructions.text = Transfers[position].instruction

        holder.transferDelete.setOnClickListener {
            onTransferGridCLickListener.onDeleteClicked(Transfers[position].transferOfflineId!!)
        }

       holder.transferGridLinear.setOnClickListener {
           instructionPopUp(Transfers[position].instruction!!)
        }



    }

    override fun getItemCount() = Transfers.size
    private fun instructionPopUp(instruction:String) {
        val customDialog = Dialog(activity, R.style.ConfirmationStyle)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.instruction_dialog)



        when {
            viewModel.readLanguage() == "en" -> {

                customDialog.findViewById<Button>(R.id.instruction_popupbtn).text =
                    translator.find { it.keyword == "Close" }!!.en
                customDialog.findViewById<TextView>(R.id.instruction_popuptitle).text =
                    translator.find { it.keyword == "Instruction" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {


                customDialog.findViewById<Button>(R.id.instruction_popupbtn).text =
                    translator.find { it.keyword == "Close" }!!.ar
                customDialog.findViewById<TextView>(R.id.instruction_popuptitle).text =
                    translator.find { it.keyword == "Instruction" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {

                customDialog.findViewById<Button>(R.id.instruction_popupbtn).text =
                    translator.find { it.keyword == "Close" }!!.fr
                customDialog.findViewById<TextView>(R.id.instruction_popuptitle).text =
                    translator.find { it.keyword == "Instruction" }!!.fr
            }
        }
        customDialog.findViewById<TextView>(R.id.instruction_popupmsg).text = instruction

        customDialog.findViewById<Button>(R.id.instruction_popupbtn).setOnClickListener {
            customDialog.dismiss()

        }
        customDialog.show()

    }
    class AllCategoriesVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val transferTo : TextView =itemView.findViewById(R.id.transfergrid_reciever)
        val transferPurpose : TextView =itemView.findViewById(R.id.transfergrid_purpose)
        val transferDueDate : TextView =itemView.findViewById(R.id.transfergrid_duedate)
        val transferDelete : CardView =itemView.findViewById(R.id.deletetransgrid)
        val transferGridLinear : LinearLayout =itemView.findViewById(R.id.transfergrid_lin)
//      val transferInstructions : ReadMoreTextView =itemView.findViewById(R.id.transfergrid_reciever)

//        val vieww : View =itemView.findViewById(R.id.tvieww)
    }


    fun addTransferGrid(transfer: TransferRequestModel) {
        this.Transfers.add(transfer)
        notifyDataSetChanged()
    }



    fun removeTransferGrid(transferId: Int) {
        for (transfer in Transfers) {
            if (transfer.transferOfflineId == transferId) {
                Transfers.remove(transfer)
                break
            }
        }
        notifyDataSetChanged()
    }



    public interface OnTransferGridClicked {
        fun onDeleteClicked(transferId: Int)

    }
}