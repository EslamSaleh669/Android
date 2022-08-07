package intalio.cts.mobile.android.ui.fragment.nonarchattachments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.NonArchDataItem
import intalio.cts.mobile.android.data.network.response.TypesResponseItem
import intalio.cts.mobile.android.ui.adapter.NonArchAdapter
import intalio.cts.mobile.android.ui.adapter.NonArchTypesAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_nonarch.*
import kotlinx.android.synthetic.main.fragment_nonarch.noDataFounded
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class NonArchAttachmentsFragment : Fragment() , NonArchAdapter.OnDeleteNoteClicked{
    private var Node_Id :Int = 0
    private var DocumentId:Int = 0
    private var TransferId:Int = 0
    private var canDoAction:Boolean = false


    @Inject
    @field:Named("nonarchattachments")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NonArchAttachmentsViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(NonArchAttachmentsViewModel::class.java)
        }

    private val autoDispose: AutoDispose = AutoDispose()
    var dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            autoDispose.bindTo(this.lifecycle)


            (activity?.application as MyApplication).appComponent?.inject(this)

        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog = requireContext().launchLoadingDialog()

        return inflater.inflate(R.layout.fragment_nonarch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.NonArch = ReplaySubject.create()


        centered_txt.text = requireActivity().getText(R.string.non_arch_attachments)
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        requireArguments().getInt(Constants.NODE_ID).let {
            Node_Id = it
        }

        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it
        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        requireArguments().getBoolean(Constants.CANDOACTION).let {
            canDoAction = it
        }

        if (Node_Id == 2 && canDoAction) {
            nonrel.visibility = View.VISIBLE }

        getNonArch(DocumentId)


        addnonarch.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AddNonArchsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.DOCUMENT_ID,DocumentId),
                            Pair(Constants.TRANSFER_ID,TransferId)
                        )


                    }
                )
                addToBackStack("")

            }
        }




    }


    private fun getNonArch(DoctId: Int) {
        nonarchrecycler.adapter =
            NonArchAdapter(arrayListOf(), requireActivity(),this,Node_Id,canDoAction)
        nonarchrecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



        autoDispose.add(viewModel.NonArch.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()
            val lastPosition: Int =
                (nonarchrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

             if (it!!.isEmpty() && viewModel.limit == 0) {
                 noDataFounded.visibility = View.VISIBLE
                 nonarchrecycler.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (nonarchrecycler.adapter as NonArchAdapter).addNonArch(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(getString(R.string.no_moredata))
                }
            }
        },{
            dialog!!.dismiss()
            noDataFounded.visibility = View.VISIBLE
            nonarchrecycler.visibility = View.GONE
            Timber.e(it)

        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nonarchrecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (nonarchrecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForNonArchLoading(lastPosition, DoctId)

            }
        }

        viewModel.loadMoreNonArch(DoctId)

    }



    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0
    }

    override fun onDeleteClicked(nonarchid: Int) {
        showDialog(nonarchid)

    }

    private fun showDialog(nonarchid: Int) {
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.delete_item_confirm))
            .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, i ->
                dialog.dismiss()

                autoDispose.add(viewModel.deleteNonArch(nonarchid,DocumentId,TransferId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        if (it.equals(true)){
                            requireActivity().makeToast(getString(R.string.deleted))
                            (nonarchrecycler.adapter as NonArchAdapter).removeNoneArch(nonarchid)

                        }

                    },
                    {
                        Timber.e(it)


                    }))
            })
            .setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()


            }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }


    override fun onEditClicked(position: Int, model: NonArchDataItem) {
        showNonArchDialog(position,model)
    }

    private fun showNonArchDialog(position: Int , model: NonArchDataItem) {
        var typeSelectedId = model.typeId

        val customDialog = Dialog(requireContext(),R.style.FullScreenDialog)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.edit_nonarch_layout)

        customDialog.findViewById<TextView>(R.id.centered_txt).setText(R.string.edit_attachment)
        customDialog.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            customDialog.dismiss()
        }
        val attachTypeAutoComplete = customDialog.findViewById<AutoCompleteTextView>(R.id.attchtypeautocomplete)
        val attachmentCount = customDialog.findViewById<EditText>(R.id.attchquantity)
        val attachmentDescription= customDialog.findViewById<EditText>(R.id.attchdesc)

        attachmentCount.setText(model.quantity.toString(), TextView.BufferType.EDITABLE)
        if (model.description.toString() != "null"){
            attachmentDescription.setText(model.description.toString(), TextView.BufferType.EDITABLE)
        }
        attachTypeAutoComplete.setText(model.type.toString(), TextView.BufferType.EDITABLE)

        attachTypeAutoComplete.threshold = 0
        val attachmentAutoCompleteArray = viewModel.readTypesData()!!

        val arrayAdapter =
            NonArchTypesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                attachmentAutoCompleteArray
            )
        attachTypeAutoComplete.setAdapter(arrayAdapter)




        attachTypeAutoComplete.setOnClickListener {
            attachTypeAutoComplete.showDropDown()

        }


        attachTypeAutoComplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                attachTypeAutoComplete.showDropDown()

            } else {
                attachTypeAutoComplete.setText("")
            }

        }



        attachTypeAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                attachTypeAutoComplete.clearFocus()
                attachTypeAutoComplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as TypesResponseItem
                attachTypeAutoComplete.setText(selectedObject.text.toString())
                typeSelectedId = selectedObject.id!!

            }





    val yesBtn = customDialog.findViewById(R.id.btnSaveattch) as TextView
        val noBtn = customDialog.findViewById(R.id.btnCancelattch) as TextView
        yesBtn.setOnClickListener {



            val desctription = attachmentDescription.text.toString().trim()
            val quantity = attachmentCount.text.toString().trim()

            if (quantity == "")
            {
                requireActivity().makeToast(getString(R.string.requiredField))
            }else{

                model.description = desctription
                model.quantity = quantity.toInt()
                model.type = attachTypeAutoComplete.text.toString().trim()
                dialog = requireContext().launchLoadingDialog()

                addEditedNonArch(DocumentId,TransferId,desctription.trim(),quantity.toInt(),
                    typeSelectedId!!, model.id!!
                )

            }

            (nonarchrecycler.adapter as NonArchAdapter).modifyNonArch(position,model)

            customDialog.dismiss()
        }
        noBtn.setOnClickListener {
            customDialog.dismiss()

        }
        customDialog.show()

    }


    private fun addEditedNonArch(DoctId: Int , transID:Int , desc:String , quantity : Int,selectedType:Int ,nonarchid: Int) {

        autoDispose.add(viewModel.saveEditedNonArch(DoctId,transID,selectedType,desc,quantity,nonarchid).observeOn(AndroidSchedulers.mainThread()).subscribe(
            {

                dialog!!.dismiss()

                if (it.id!! > 0){
                    requireActivity().makeToast(getString(R.string.edited))
                }else{
                    requireActivity().makeToast(it.message.toString())
                }

            },
            {
                Timber.e(it)
                dialog!!.dismiss()


            }))

    }






}