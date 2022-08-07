package intalio.cts.mobile.android.ui.fragment.linkedcorrespondence

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AdvancedSearchRequest
import intalio.cts.mobile.android.ui.adapter.LinkedSearchResultAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_correspondence.noDataFound
import kotlinx.android.synthetic.main.fragment_linkedsearchresult.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.viewer_layout.back_icon
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import org.json.JSONObject


class LinkedCorrespondenceResultFragment : Fragment(),
    LinkedSearchResultAdapter.OnSelectionClicked {


    private lateinit var searchModel: AdvancedSearchRequest
    private var typeOfSearch = 0

    private var DocumentId: Int = 0
    private var TransferId: Int = 0
    private var documentIDs = ArrayList<Int>()


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
        return inflater.inflate(R.layout.fragment_linkedsearchresult, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        centered_txt.text = getString(R.string.search)
        back_icon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnclose.setOnClickListener {
            requireActivity().onBackPressed()
        }



        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it
        }


        requireArguments().getInt(Constants.TRANSFER_ID).let {
            TransferId = it
        }

        val result = arguments?.getSerializable(Constants.SEARCH_MODEL)
        if (result.toString() != "null") {
            searchModel = result as AdvancedSearchRequest
            dialog = requireContext().launchLoadingDialog()
            val searchObject = JSONObject()
            searchObject.put("category", searchModel.category)
            searchObject.put("subject", searchModel.subject)
            searchObject.put("referenceNumber", searchModel.referenceNumber)
            searchObject.put("fromStructure", searchModel.fromStructure)
            searchObject.put("toStructure", searchModel.toStructure)
            searchObject.put("fromTransferDate", searchModel.fromTransferDate)
            searchObject.put("toTransferDate", searchModel.toTransferDate)
            searchObject.put("priority", searchModel.priority)
            searchObject.put("fromDate", searchModel.fromDate)
            searchObject.put("toDate", searchModel.toDate)
            searchObject.put("fromUser", searchModel.fromUser)
            searchObject.put("toUser", searchModel.toUser)
            searchObject.put("documentSender", searchModel.documentSender)
            searchObject.put("documentReceiver", searchModel.documentReceiver)
            searchObject.put("isOverdue", searchModel.isOverdue)
            searchObject.put("documentId", searchModel.documentId)
            searchObject.put("keyword", searchModel.keyword)
            searchObject.put("status", searchModel.status)

            getResult(searchObject)
        }

        requireArguments().getInt(Constants.SEARCH_TYPE).let {
            typeOfSearch = it
        }



        btnSubmit.setOnClickListener {
            if (documentIDs.size > 0) {

                dialog = requireActivity().launchLoadingDialog()
                addLinkedDocument(documentIDs)
            } else {
                requireActivity().makeToast(getString(R.string.please_select_corresp))
            }
        }

    }

    private fun getResult(searchObject: JSONObject) {
        viewModel.Items = ReplaySubject.create()

        linkedrecyclersres.adapter =
            LinkedSearchResultAdapter(arrayListOf(), requireActivity(), this)
        linkedrecyclersres.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        autoDispose.add(viewModel.Items.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (linkedrecyclersres.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.limit == 0) {
                noDataFound.visibility = View.VISIBLE
                linkedrecyclersres.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (linkedrecyclersres.adapter as LinkedSearchResultAdapter).addMessages(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(getString(R.string.no_moredata))
                }
            }
        }, {
            dialog!!.dismiss()
            noDataFound.visibility = View.VISIBLE
            linkedrecyclersres.visibility = View.GONE
            Timber.e(it)

        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            linkedrecyclersres.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (linkedrecyclersres.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForItemsLoading(lastPosition, searchObject)

            }
        }

        viewModel.loadMoreItems(searchObject)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0

    }

    override fun onSelectedLinkedClicked(documentsIds: ArrayList<Int>) {

        documentIDs.clear()
        documentIDs.addAll(documentsIds)

    }

    private fun addLinkedDocument(documentsIds: ArrayList<Int>) {

        val arrayIds: Array<Int> = documentsIds.toTypedArray()

        autoDispose.add(
            viewModel.addLinkedDocument(arrayIds, DocumentId, TransferId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        dialog!!.dismiss()

                        val fmManager = requireActivity().supportFragmentManager
                        if (fmManager.backStackEntryCount > 0) {
                            fmManager.popBackStack(
                                fmManager.getBackStackEntryAt(fmManager.backStackEntryCount - 2).id,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        }

                    },
                    {
                        dialog!!.dismiss()

                        Timber.e(it)
                        requireActivity().makeToast(getString(R.string.error))

                    })
        )
    }

}