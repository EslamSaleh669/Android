package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.ui.adapter.TransferHistory_Adapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.allnotes_fragment.noDataFounded
import kotlinx.android.synthetic.main.fragment_transferhistory.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TransferHistoryFragment : Fragment() {
    private var DocumentId:Int = 0
    private var TransferId:Int = 0
    private var delegationId = 0

    private lateinit var translator:  ArrayList<DictionaryDataItem>


    @Inject
    @field:Named("correspondenceDetails")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: CorrespondenceDetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CorrespondenceDetailsViewModel::class.java)
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

        return inflater.inflate(R.layout.fragment_transferhistory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.Transfers = ReplaySubject.create()
        translator = viewModel.readDictionary()!!.data!!

        when {
            viewModel.readLanguage() == "en" -> {
                centered_txt.text = translator.find { it.keyword == "TransfersHistory" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {
                centered_txt.text = translator.find { it.keyword == "TransfersHistory" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                centered_txt.text = translator.find { it.keyword == "TransfersHistory" }!!.fr

            }
        }

        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it
        }



        getTransfers(DocumentId,delegationId)



    }


    private fun getTransfers(DoctId: Int, delegationId: Int) {
        var noMoreData = ""

        when {
            viewModel.readLanguage() == "en" -> {

                noMoreData = "No more data"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                noMoreData = "لا يوجد المزيد"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {
                noMoreData = "Plus de données"
                noDataFounded.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
            }
        }
        transferhistory_recycler.adapter =
            TransferHistory_Adapter(arrayListOf(), requireActivity(),viewModel,translator)
        transferhistory_recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        autoDispose.add(viewModel.Transfers.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()
            val lastPosition: Int =
                (transferhistory_recycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

             if (it!!.isEmpty() && viewModel.limit == 0) {
                 noDataFounded.visibility = View.VISIBLE
                 transferhistory_recycler.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (transferhistory_recycler.adapter as TransferHistory_Adapter).addTransfer(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        },{
            noDataFounded.visibility = View.VISIBLE
            transferhistory_recycler.visibility = View.GONE
            Timber.e(it)


        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            transferhistory_recycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (transferhistory_recycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForTransfersLoading(lastPosition, DoctId,delegationId)

            }
        }

        viewModel.loadMoreTransfers(DoctId,delegationId)

    }



    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0
    }


}