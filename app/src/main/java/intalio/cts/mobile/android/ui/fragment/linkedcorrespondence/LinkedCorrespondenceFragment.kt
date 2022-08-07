package intalio.cts.mobile.android.ui.fragment.linkedcorrespondence

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.ui.adapter.LinkedCAdapter
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvancedSearchFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.allnotes_fragment.*
import kotlinx.android.synthetic.main.fragment_linkedcorresp.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LinkedCorrespondenceFragment : Fragment(), LinkedCAdapter.OnDeleteLinkedCClicked{

    private var Node_Id :Int = 0
    private var DocumentId:Int = 0
    private var TransferId:Int = 0
    private var canDoAction:Boolean = false

    @Inject
    @field:Named("linkedcorrespondence")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LinkedCorrespondenceViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LinkedCorrespondenceViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_linkedcorresp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        centered_txt.text = requireActivity().getText(R.string.link_correspondence)
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        requireArguments().getInt(Constants.NODE_ID).let {
            Node_Id = it
        }

        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it
        }


        arguments?.getInt(Constants.TRANSFER_ID).let {
            TransferId = it!!
        }

        requireArguments().getBoolean(Constants.CANDOACTION).let {
            canDoAction = it
        }

        if (Node_Id == 2 && canDoAction) {
            linkrel.visibility = View.VISIBLE }





        addlinked.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AdvancedSearchFragment().apply {
                        arguments = bundleOf(

                            Pair(Constants.DOCUMENT_ID,DocumentId),
                            Pair(Constants.TRANSFER_ID,TransferId),
                            Pair(Constants.SEARCH_TYPE, 2)

                        )


                    }
                )
                addToBackStack("")

            }
        }

        getLinkedCData(DocumentId)

    }






    private fun getLinkedCData(DocuId: Int) {

        autoDispose.add(viewModel.getLinkedC(DocuId).observeOn(AndroidSchedulers.mainThread()).subscribe({

            if (it.data.isNullOrEmpty()){
                dialog!!.dismiss()

             //   noDataFounded.visibility = View.VISIBLE
                linkednoDataFounded.visibility = View.VISIBLE
                linkedrecycler.visibility = View.GONE

            }else{

                linkedrecycler.adapter =
                    LinkedCAdapter(it.data, requireActivity(),this,Node_Id,canDoAction)
                linkedrecycler.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

                dialog!!.dismiss()
            }



        }, {
            dialog!!.dismiss()
            Timber.e(it)

        }))
    }

    override fun onDeleteClicked(linkcId: Int) {

        showDialog(linkcId)
    }

    private fun showDialog(linkcId: Int) {
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.delete_item_confirm))
            .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, i ->
                dialog.dismiss()

                autoDispose.add(viewModel.deleteLinkedC(linkcId,DocumentId,TransferId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        if (it.equals(true)){
                            requireActivity().makeToast(getString(R.string.deleted))
                            (linkedrecycler.adapter as LinkedCAdapter).removeLinkedC(linkcId)

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



}