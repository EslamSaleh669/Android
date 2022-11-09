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
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
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

    private var Node_Inherit :String = ""
    private var DocumentId:Int = 0
    private var TransferId:Int = 0
    private var canDoAction:Boolean = false
    private var delegationId = 0

    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>

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

        translator = viewModel.readDictionary()!!.data!!
        setLabels()

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        requireArguments().getString(Constants.NODE_INHERIT).let {
            Node_Inherit = it!!
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

        if (Node_Inherit == "Inbox" && canDoAction) {
            linkrel.visibility = View.VISIBLE
        }

        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }



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

        getLinkedCData(DocumentId,delegationId)

    }






    private fun getLinkedCData(DocuId: Int, delegationId: Int) {
        var noMoreData = ""

        when {
            viewModel.readLanguage() == "en" -> {

                noMoreData = "No more data"
                linkednoDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                noMoreData = "لا يوجد المزيد"
                linkednoDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {
                noMoreData = "Plus de données"
                linkednoDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
            }
        }
        autoDispose.add(viewModel.getLinkedC(DocuId,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe({

            if (it.data.isNullOrEmpty()){
                dialog!!.dismiss()

             //   noDataFounded.visibility = View.VISIBLE
                linkednoDataFounded.visibility = View.VISIBLE
                linkedrecycler.visibility = View.GONE

            }else{

                linkedrecycler.adapter =
                    LinkedCAdapter(it.data, requireActivity(),this,Node_Inherit,canDoAction)
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

        var deleteConfirmation = ""
        var yes = ""
        var no = ""


        when {
            viewModel.readLanguage() == "en" -> {


                deleteConfirmation = translator.find { it.keyword == "DeleteConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                deleteConfirmation = translator.find { it.keyword == "DeleteConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {



                deleteConfirmation = translator.find { it.keyword == "DeleteConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }

        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(deleteConfirmation)
            .setPositiveButton(yes, DialogInterface.OnClickListener { dialog, i ->
                dialog.dismiss()

                autoDispose.add(viewModel.deleteLinkedC(linkcId,DocumentId,TransferId,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        if (it.equals(true)){

                            (linkedrecycler.adapter as LinkedCAdapter).removeLinkedC(linkcId)

                        }

                    },
                    {
                        Timber.e(it)


                    }))
            })
            .setNegativeButton(no, DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()


            }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    private fun setLabels() {


        when {
            viewModel.readLanguage() == "en" -> {

                linkedc_label.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.en
                addlinked.text = translator.find { it.keyword == "Add" }!!.en
                centered_txt.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.en



            }
            viewModel.readLanguage() == "ar" -> {
                linkedc_label.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.ar
                addlinked.text = translator.find { it.keyword == "Add" }!!.ar
                centered_txt.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.ar


            }
            viewModel.readLanguage() == "fr" -> {


                linkedc_label.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.fr
                addlinked.text = translator.find { it.keyword == "Add" }!!.fr
                centered_txt.text = translator.find { it.keyword == "LinkedCorrespondences" }!!.fr


            }
        }
    }


}