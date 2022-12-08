package intalio.cts.mobile.android.ui.fragment.advancedsearch

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.AdvancedSearchRequest
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.ui.adapter.AdvancedSearchResultAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_searchresult.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.viewer_layout.back_icon
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import org.json.JSONObject




class AdvancedSearchResultFragment : Fragment() {
    private lateinit var translator:  ArrayList<DictionaryDataItem>


    private lateinit var searchModel: AdvancedSearchRequest
    private var typeOfSearch = 0


    @Inject
    @field:Named("advancedsearch")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AdvanceSearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AdvanceSearchViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_searchresult, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        translator = viewModel.readDictionary()!!.data!!

        back_icon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val result = arguments?.getSerializable(Constants.SEARCH_MODEL)
        if (result.toString() != "null"){
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
            searchObject.put("ocrContent", searchModel.ocrContent)
            searchObject.put("delegationId", searchModel.delegationId)
            searchObject.put("status", searchModel.status)

            Log.d("searchobject", searchModel.toString())

            getResult(searchObject)
        }

        requireArguments().getInt(Constants.SEARCH_TYPE).let {
            typeOfSearch = it
        }



    }

    private fun getResult(searchObject: JSONObject) {

        var noMoreData = ""

        when {
            viewModel.readLanguage() == "en" -> {

                noMoreData = "No more data"
                resultnoDataFound.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!
                centered_txt.text = translator.find { it.keyword == "Search" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                noMoreData = "لا يوجد المزيد"
                resultnoDataFound.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!
                centered_txt.text = translator.find { it.keyword == "Search" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                noMoreData = "Plus de données"
                resultnoDataFound.text  = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
                centered_txt.text = translator.find { it.keyword == "Search" }!!.fr!!
            }
        }


        viewModel.Items = ReplaySubject.create()
        val categories = viewModel.readCategoriesData()
        result_ecyclerview.adapter =
            AdvancedSearchResultAdapter(arrayListOf(), requireActivity(),categories,viewModel)
        result_ecyclerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        autoDispose.add(viewModel.Items.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (result_ecyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.limit == 0) {
                resultnoDataFound.visibility = View.VISIBLE
                result_ecyclerview.visibility = View.GONE
            }
            else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                   (result_ecyclerview.adapter as AdvancedSearchResultAdapter).addMessages(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        },{
            dialog!!.dismiss()
            resultnoDataFound.visibility = View.VISIBLE
            result_ecyclerview.visibility = View.GONE

            if (it.message!!.contains("BEGIN_ARRAY")){
              requireContext().makeToast("Crawling service not configured")

            }else{
                requireContext().makeToast(it.message!!)

            }

            Timber.e(it)

        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result_ecyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (result_ecyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForItemsLoading(lastPosition,searchObject)

            }
        }

        viewModel.loadMoreItems(searchObject)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0


        searchModel.referenceNumber = ""
        searchModel.category = ""
        searchModel.subject = ""
        searchModel.status = ""
        searchModel.fromDate = ""
        searchModel.toDate = ""
        searchModel.priority = ""
        searchModel.documentSender = ""
        searchModel.documentReceiver = ""
        searchModel.fromUser = ""
        searchModel.toUser = ""
        searchModel.fromStructure = ""
        searchModel.toStructure = ""
        searchModel.fromTransferDate = ""
        searchModel.toTransferDate = ""
        searchModel.keyword = ""
        searchModel.ocrContent = ""
        searchModel.delegationId = ""
        searchModel.isOverdue = false




    }

}