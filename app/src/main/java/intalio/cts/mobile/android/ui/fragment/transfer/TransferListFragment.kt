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

        when {
            viewModel.readLanguage() == "en" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.en
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.ar
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                centered_txt.text = translator.find { it.keyword == "MyTransfers" }!!.fr
                send_transfers.text = translator.find { it.keyword == "Transfer" }!!.fr

            }
        }

        val result = arguments?.getSerializable(Constants.TRANSFER_MODEL)

        if (result.toString() != "null") {
            transfers = result as ArrayList<TransferRequestModel>


        }


        transfers_recycler.adapter =
            TransferList_Adapter(transfers, requireActivity(), this,viewModel,translator)
        transfers_recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        send_transfers.setOnClickListener {
            if (transfers.size > 0) {
                sendTransfer()

            } else {
                requireActivity().makeToast(getString(R.string.please_save_transfer))

            }
        }
    }


    private fun sendTransfer() {


        Log.d("aaaaaaaaaaaaaaaaxx", transfers.size.toString())

        dialog = requireActivity().launchLoadingDialog()

        autoDispose.add(
            viewModel.transferTransfer(transfers).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        Log.d("transfererresponse", it.toString())
                        Log.d("transfererresponsep",transfers[0].purposeId.toString())
                         if (it[0].updated == true) {
                            transfers.clear()
                            requireActivity().makeToast(getString(R.string.done))
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_ID, 2)
                                        )
                                    }
                                )
                            }
                        } else if (it[0].updated == false && it[0].message == "OriginalFileInUse") {
                            requireActivity().makeToast(getString(R.string.original_doc_checkedout))

                        } else if (it[0].updated == false && it[0].message == "FileInUse") {
                            requireActivity().makeToast(getString(R.string.there_is_file_checkedout))
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