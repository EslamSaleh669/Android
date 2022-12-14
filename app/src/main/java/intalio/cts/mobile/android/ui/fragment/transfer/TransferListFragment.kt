package intalio.cts.mobile.android.ui.fragment.transfer

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.TransferRequestModel
import intalio.cts.mobile.android.ui.adapter.TransferList_Adapter
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.transferlist_dialog.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TransferListFragment : Fragment(), TransferList_Adapter.OnTransferClicked {

    @Inject
    @field:Named("addtransfer")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TransfersViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(TransfersViewModel::class.java)
    }

    private var transfers = ArrayList<TransferRequestModel>()
    private lateinit var translator:  ArrayList<DictionaryDataItem>

    private var delegationId = 0

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
        return inflater.inflate(R.layout.transferlist_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }


        translator = viewModel.readDictionary()!!.data!!
        var emptyTransfers = ""

        when {
            viewModel.readLanguage() == "en" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.en
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.en
                emptyTransfers = "please save at least one transfer"

            }
            viewModel.readLanguage() == "ar" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.ar
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.ar
                emptyTransfers = "???????????? ?????? ?????????? ?????????? ?????? ??????????"

            }
            viewModel.readLanguage() == "fr" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.fr
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.fr
                emptyTransfers = "veuillez enregistrer au moins un transfert"

            }
        }

        val result = arguments?.getSerializable(Constants.TRANSFER_MODEL)

        if (result.toString() != "null") {
            transfers = result as ArrayList<TransferRequestModel>


        }
        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }

        transfers_recycler.adapter =
            TransferList_Adapter(transfers, requireActivity(), this,viewModel,translator)
        transfers_recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        send_transfers.setOnClickListener {
            if (transfers.size > 0) {
                sendTransfer(delegationId)

            } else {
                requireActivity().makeToast(emptyTransfers)

            }
        }
    }


    private fun sendTransfer(delegationId: Int) {

         dialog = requireActivity().launchLoadingDialog()

        var fileInUSe = ""
        var originalDocumentInUse = ""

        when {
            viewModel.readLanguage() == "en" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.en!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.ar!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.fr!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.fr!!

            }
        }


        autoDispose.add(
            viewModel.transferTransfer(transfers,delegationId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {


                         if (it[0].updated == true) {
                            transfers.clear()

                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_INHERIT,viewModel.readCurrentNode())
                                        )
                                    }
                                )
                            }
                        } else if (it[0].updated == false && it[0].message == "OriginalFileInUse") {
                            requireActivity().makeToast(originalDocumentInUse)

                        } else if (it[0].updated == false && it[0].message == "FileInUse") {
                            requireActivity().makeToast(fileInUSe)
                        }

                        dialog!!.dismiss()
                    }, {
                        dialog!!.dismiss()

                        Timber.e(it)

                    })
        )
    }

    override fun onDeleteClicked(transferId: Int) {
        (transfers_recycler.adapter as TransferList_Adapter).removeTransfer(transferId)

    }

    override fun onEditClicked(position: Int, model: TransferRequestModel) {
    }

}