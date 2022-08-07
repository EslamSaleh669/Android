package intalio.cts.mobile.android.ui.fragment.nonarchattachments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.TypesResponseItem
import intalio.cts.mobile.android.ui.adapter.NonArchTypesAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_addnonarch.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AddNonArchsFragment : Fragment() {
     private var DocumentId:Int = 0
     private var TransferId:Int = 0
     private var typeSelectedId = 0


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

        return inflater.inflate(R.layout.fragment_addnonarch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        centered_txt.text = requireActivity().getText(R.string.add)
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it

        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        initTypesAutoComplete()
        btnSaveattch.setOnClickListener {
            val desctription = attchdesc.text.toString().trim()
            val quantity = attchquantity.text.toString().trim()

            if (typeSelectedId == 0 || attchquantity.text.toString() == "")
            {
                requireActivity().makeToast(getString(R.string.requiredField))
            }else{
                dialog = requireContext().launchLoadingDialog()

                addNonArch(DocumentId,TransferId,desctription,quantity.toInt())

            }
        }
        btnCancelattch.setOnClickListener {
            activity?.onBackPressed()
        }


    }


    private fun initTypesAutoComplete() {
        attchtypeautocomplete.threshold = 0
        val attachmentAutoCompleteArray = viewModel.readTypesData()!!

        val arrayAdapter =
            NonArchTypesAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                attachmentAutoCompleteArray
            )
        attchtypeautocomplete.setAdapter(arrayAdapter)




        attchtypeautocomplete.setOnClickListener {
            attchtypeautocomplete.showDropDown()

        }


        attchtypeautocomplete.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                requireActivity().hideKeyboard(requireActivity())
                attchtypeautocomplete.showDropDown()

            } else {
                attchtypeautocomplete.setText("")
            }

        }



        attchtypeautocomplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                requireActivity().hideKeyboard(requireActivity())
                attchtypeautocomplete.clearFocus()
                attchtypeautocomplete.dismissDropDown()
                val selectedObject= parent!!.getItemAtPosition(position) as TypesResponseItem
                attchtypeautocomplete.setText(selectedObject.text.toString())
                typeSelectedId = selectedObject.id!!

            }

    }

    private fun addNonArch(DoctId: Int , transID:Int , desc:String , quantity : Int) {

        autoDispose.add(viewModel.saveNonArch(DoctId,transID,typeSelectedId,desc,quantity).observeOn(AndroidSchedulers.mainThread()).subscribe(
            {

                dialog!!.dismiss()

                if (it.id!! > 0){
                    activity?.onBackPressed()
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