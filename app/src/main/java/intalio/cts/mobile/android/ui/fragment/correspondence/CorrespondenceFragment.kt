package intalio.cts.mobile.android.ui.fragment.correspondence

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.CorrespondenceDataItem
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.NodeResponseItem
import intalio.cts.mobile.android.ui.adapter.CorrespondenceAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_correspondence.*
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CorrespondenceFragment : Fragment(), CorrespondenceAdapter.InterfacePositionCard {

    private var CatNode_Inherit: String = ""
    private var popupWindow = PopupWindow()
    private var delegationId = 0
    private lateinit var Nodes: java.util.ArrayList<NodeResponseItem>

    private lateinit var translator: ArrayList<DictionaryDataItem>


    @Inject
    @field:Named("correspondence")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: CorrespondenceViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CorrespondenceViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_correspondence, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref =
            activity?.getSharedPreferences(Constants.ORIGINAL_FILE_PREF, Context.MODE_PRIVATE)
        val editor = sharedPref!!.edit()
        editor.putString(Constants.ORIGINAL_FILE_ID, "")
        editor.apply()
        coresp_home_icon.setOnClickListener {
            requireActivity().drawer_layout.openDrawer(GravityCompat.START)
        }

        homemenuicon.setOnClickListener { v ->

            popupWindow.showAsDropDown(v, 0, 40)

        }

        homebackicon3.setOnClickListener { v ->

            requireActivity().onBackPressed()

        }

        setLabels()

        Nodes = viewModel.readNodes()
        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }





        arguments?.getString(Constants.NODE_INHERIT)?.let {
            if (it != "") {
                CatNode_Inherit = it
                //               initBottomNav(CatNode_Inherit)
                when (it) {
                    "Inbox" -> {
                        getInbox(delegationId)
                    }
                    "Sent" -> {
                        getSent(delegationId)
                    }
                    "Completed" -> {
                        getICompleted(delegationId)
                    }
                    "Closed" -> {
                        getClosed(delegationId)
                    }
                    "MyRequests" -> {
                        getRequested(delegationId)
                    }
                }

            }
        }

        corswipecontainer.setColorSchemeColors(resources.getColor(R.color.appcolor))
        corswipecontainer.setOnRefreshListener {
            stopDispose()
            corswipecontainer.isRefreshing = false


            when (CatNode_Inherit) {
                "Inbox" -> {
                    getInbox(delegationId)
                }
                "Sent" -> {
                    getSent(delegationId)
                }
                "Completed" -> {
                    getICompleted(delegationId)
                }
                "Closed" -> {
                    getClosed(delegationId)
                }
                "MyRequests" -> {
                    getRequested(delegationId)
                }
            }

    }
    // bottomNavigationClick()


}


//    private fun initActionToolbar(model: CorrespondenceDataItem) {
//
//
//        val inflater = requireActivity().applicationContext
//            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//
//        val view: View = inflater.inflate(R.layout.corresponence_menu, null, false)
//
//        view.findViewById<View>(R.id.menu_complete).setOnClickListener {
//            popupWindow.dismiss()
//            dialog = requireContext().launchLoadingDialog()
//            val transferId = arrayOf(model.id)
//
//            autoDispose.add(
//                viewModel.completeTransfer(transferId).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        {
//                            dialog!!.dismiss()
//
//                            if (it[0].updated == true) {
//
//                                (activity as AppCompatActivity).supportFragmentManager.commit {
//                                    replace(R.id.fragmentContainer,
//                                        CorrespondenceFragment().apply {
//                                            arguments = bundleOf(
//                                                Pair(Constants.NODE_ID, 2)
//                                            )
//
//                                        }
//                                    )
//
//                                }
//
//                            } else {
//                                requireActivity().makeToast(it[0].message!!)
//                            }
//
//                        },
//                        {
//                            Timber.e(it)
//                            dialog!!.dismiss()
//                            requireActivity().makeToast(getString(R.string.network_error))
//                            requireActivity().onBackPressed()
//
//                        })
//            )
//
//        }
//
//        view.findViewById<View>(R.id.menu_transfer).setOnClickListener {
//            popupWindow.dismiss()
//
//            (activity as AppCompatActivity).supportFragmentManager.commit {
//                replace(R.id.fragmentContainer,
//                    AddTransferFragment().apply {
//                        arguments = bundleOf(
//                            Pair(Constants.Correspondence_Model, model)
//                        )
//
//
//                    }
//                )
//                addToBackStack("")
//
//
//            }
//
//
//        }
//
//        view.findViewById<View>(R.id.menu_dismisscopy).setOnClickListener {
//            popupWindow.dismiss()
//
//            if (model.cced == false) {
//                requireActivity().makeToast(getString(R.string.not_carbon_copy))
//
//            } else {
//                dialog = requireContext().launchLoadingDialog()
//                val transferId = arrayOf(model.id)
//
//                autoDispose.add(
//                    viewModel.transferDismissCopy(transferId)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                            {
//                                dialog!!.dismiss()
//
//                                if (it[0].updated == true) {
//
//                                    (activity as AppCompatActivity).supportFragmentManager.commit {
//                                        replace(R.id.fragmentContainer,
//                                            CorrespondenceFragment().apply {
//                                                arguments = bundleOf(
//                                                    Pair(Constants.NODE_ID, 2)
//                                                )
//
//                                            }
//                                        )
//
//                                    }
//
//                                } else {
//                                    requireActivity().makeToast(it[0].updated.toString())
//                                }
//
//                            },
//                            {
//                                Timber.e(it)
//                                dialog!!.dismiss()
//                                requireActivity().makeToast(getString(R.string.network_error))
//                                requireActivity().onBackPressed()
//
//                            })
//                )
//
//            }
//
//
//        }
//
//
//
//        requireActivity().findViewById<View>(R.id.homereplyicon).setOnClickListener {
//            popupWindow.dismiss()
//            (activity as AppCompatActivity).supportFragmentManager.commit {
//                replace(R.id.fragmentContainer,
//                    ReplyToUserFragment().apply {
//                        arguments = bundleOf(
//                            Pair(Constants.Correspondence_Model, model)
//                        )
//
//
//                    }
//                )
//                addToBackStack("")
//
//            }
//
//        }
//
//
//        requireActivity().findViewById<View>(R.id.homelockicon).setOnClickListener {
//            popupWindow.dismiss()
//            dialog = requireContext().launchLoadingDialog()
//
//            viewModel.lockTransfer(
//                model.id!!
//
//            ).enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    dialog!!.dismiss()
//                    if (response.code() != 200) {
//
//                        requireActivity().makeToast(getString(R.string.network_error))
//                        activity!!.onBackPressed()
//
//                    } else {
//
//
//                        (activity as AppCompatActivity).supportFragmentManager.commit {
//                            replace(R.id.fragmentContainer,
//                                CorrespondenceFragment().apply {
//                                    arguments = bundleOf(
//                                        Pair(Constants.NODE_ID, 2)
//                                    )
//
//
//                                }
//                            )
//
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    dialog!!.dismiss()
//                    requireActivity().makeToast(getString(R.string.network_error))
//                    activity!!.onBackPressed()
//
//                }
//
//
//            }
//
//            )
//        }
//
//        val width = (resources.displayMetrics.widthPixels * 0.40).toInt()
//        val hight = (resources.displayMetrics.widthPixels * 0.80).toInt()
//        popupWindow = PopupWindow(view, width, LinearLayout.LayoutParams.WRAP_CONTENT, true)
//
//    }

private fun getInbox(delegationId: Int) {

    var noMoreData = ""
    when {
        viewModel.readLanguage() == "en" -> {

            noMoreData = "No more data"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

        }
        viewModel.readLanguage() == "ar" -> {
            noMoreData = "لا يوجد المزيد"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

        }
        viewModel.readLanguage() == "fr" -> {
            noMoreData = "Plus de données"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
        }
    }


    viewModel.saveCurrentNode(Nodes.find { it.inherit == "Inbox" }!!.inherit!!)

    correspondenceactionstoolbar.visibility = View.GONE
    correspondence_maintoolbar.visibility = View.VISIBLE
    dialog = requireContext().launchLoadingDialog()
    viewModel.inboxMessages = ReplaySubject.create()

    correspondence_recyclerview.adapter =
        CorrespondenceAdapter(
            arrayListOf(),
            requireActivity(),
            Nodes.find { it.inherit == "Inbox" }!!.inherit!!,
            this,
            viewModel,
            autoDispose,
            delegationId

        )
    correspondence_recyclerview.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)





    autoDispose.add(
        viewModel.inboxMessages.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.inboxLimit == 0) {
                noDataFound.visibility = View.VISIBLE
                correspondence_recyclerview.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.removeIf {
                            it.categoryId == 6
                        }
                    }
                    (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                        it
                    )

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        }, {
            noDataFound.visibility = View.VISIBLE
            correspondence_recyclerview.visibility = View.GONE
            Timber.e(it)

        })
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        correspondence_recyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.checkForInboxLoading(lastPosition, delegationId)

        }
    }

    viewModel.loadMoreInboxes(delegationId)

}

private fun getSent(delegationId: Int) {


    var noMoreData = ""

    when {
        viewModel.readLanguage() == "en" -> {

            noMoreData = "No more data"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

        }
        viewModel.readLanguage() == "ar" -> {
            noMoreData = "لا يوجد المزيد"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

        }
        viewModel.readLanguage() == "fr" -> {
            noMoreData = "Plus de données"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
        }
    }


    viewModel.saveCurrentNode(Nodes.find { it.inherit == "Sent" }!!.inherit!!)

    correspondenceactionstoolbar.visibility = View.GONE
    correspondence_maintoolbar.visibility = View.VISIBLE
    dialog = requireContext().launchLoadingDialog()

    viewModel.sentMessages = ReplaySubject.create()

    correspondence_recyclerview.adapter =
        CorrespondenceAdapter(
            arrayListOf(),
            requireActivity(),
            Nodes.find { it.inherit == "Sent" }!!.inherit!!,
            this,
            viewModel, autoDispose, delegationId
        )
    correspondence_recyclerview.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



    autoDispose.add(viewModel.sentMessages.observeOn(AndroidSchedulers.mainThread()).subscribe({
        dialog!!.dismiss()

        val lastPosition: Int =
            (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        if (it!!.isEmpty() && viewModel.sentLimit == 0) {
            noDataFound.visibility = View.VISIBLE
            correspondence_recyclerview.visibility = View.GONE

        } else {
            if (it.isNotEmpty()) {
                Timber.d("Data Loaded")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.removeIf {
                        it.categoryId == 6
                    }
                }

                (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(it)

            } else if (lastPosition > 10) {
                requireContext().makeToast(noMoreData)
            }
        }
    }, {
        noDataFound.visibility = View.VISIBLE
        correspondence_recyclerview.visibility = View.GONE
        Timber.e(it)

    }))


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        correspondence_recyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.checkForSentLoading(lastPosition, delegationId)

        }
    }

    viewModel.loadMoreSent(delegationId)

}


private fun getICompleted(delegationId: Int) {


    var noMoreData = ""

    when {
        viewModel.readLanguage() == "en" -> {

            noMoreData = "No more data"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

        }
        viewModel.readLanguage() == "ar" -> {
            noMoreData = "لا يوجد المزيد"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

        }
        viewModel.readLanguage() == "fr" -> {
            noMoreData = "Plus de données"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
        }
    }
    viewModel.saveCurrentNode(Nodes.find { it.inherit == "Completed" }!!.inherit!!)

    correspondenceactionstoolbar.visibility = View.GONE
    correspondence_maintoolbar.visibility = View.VISIBLE
    dialog = requireContext().launchLoadingDialog()
    viewModel.completedMessages = ReplaySubject.create()

    correspondence_recyclerview.adapter =
        CorrespondenceAdapter(
            arrayListOf(),
            requireActivity(),
            Nodes.find { it.inherit == "Completed" }!!.inherit!!,
            this,
            viewModel, autoDispose, delegationId
        )
    correspondence_recyclerview.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



    autoDispose.add(
        viewModel.completedMessages.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.completedLimit == 0) {
                noDataFound.visibility = View.VISIBLE
                correspondence_recyclerview.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.removeIf {
                            it.categoryId == 6
                        }
                    }
                    (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                        it
                    )

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        }, {
            noDataFound.visibility = View.VISIBLE
            correspondence_recyclerview.visibility = View.GONE
            Timber.e(it)

        })
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        correspondence_recyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.checkForCompletedLoading(lastPosition, delegationId)

        }
    }

    viewModel.loadMoreCompleted(delegationId)

}


private fun getRequested(delegationId: Int) {


    var noMoreData = ""

    when {
        viewModel.readLanguage() == "en" -> {

            noMoreData = "No more data"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

        }
        viewModel.readLanguage() == "ar" -> {
            noMoreData = "لا يوجد المزيد"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

        }
        viewModel.readLanguage() == "fr" -> {
            noMoreData = "Plus de données"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
        }
    }
    viewModel.saveCurrentNode(Nodes.find { it.inherit == "MyRequests" }!!.inherit!!)

    correspondenceactionstoolbar.visibility = View.GONE
    correspondence_maintoolbar.visibility = View.VISIBLE
    dialog = requireContext().launchLoadingDialog()
    viewModel.requestedMessages = ReplaySubject.create()

    correspondence_recyclerview.adapter =
        CorrespondenceAdapter(
            arrayListOf(),
            requireActivity(),
            Nodes.find { it.inherit == "MyRequests" }!!.inherit!!,
            this,
            viewModel, autoDispose, delegationId
        )
    correspondence_recyclerview.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


    autoDispose.add(
        viewModel.requestedMessages.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.requestedLimit == 0) {
                noDataFound.visibility = View.VISIBLE
                correspondence_recyclerview.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.removeIf {
                            it.categoryId == 6
                        }
                    }
                    (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        }, {
            noDataFound.visibility = View.VISIBLE
            correspondence_recyclerview.visibility = View.GONE
            Timber.e(it)

        })
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        correspondence_recyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.checkForRequestedLoading(lastPosition, delegationId)

        }
    }

    viewModel.loadMoreRequested(delegationId)

}


private fun getClosed(delegationId: Int) {

    var noMoreData = ""

    when {
        viewModel.readLanguage() == "en" -> {

            noMoreData = "No more data"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.en!!

        }
        viewModel.readLanguage() == "ar" -> {
            noMoreData = "لا يوجد المزيد"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.ar!!

        }
        viewModel.readLanguage() == "fr" -> {
            noMoreData = "Plus de données"
            noDataFound.text = translator.find { it.keyword == "NoDataToDisplay" }!!.fr!!
        }
    }
    viewModel.saveCurrentNode(Nodes.find { it.inherit == "Closed" }!!.inherit!!)

    correspondenceactionstoolbar.visibility = View.GONE
    correspondence_maintoolbar.visibility = View.VISIBLE
    dialog = requireContext().launchLoadingDialog()
    viewModel.closedMessages = ReplaySubject.create()

    correspondence_recyclerview.adapter =
        CorrespondenceAdapter(
            arrayListOf(),
            requireActivity(),
            Nodes.find { it.inherit == "Closed" }!!.inherit!!,
            this,
            viewModel, autoDispose, delegationId
        )
    correspondence_recyclerview.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


    autoDispose.add(
        viewModel.closedMessages.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (it!!.isEmpty() && viewModel.closedLimit == 0) {
                noDataFound.visibility = View.VISIBLE
                correspondence_recyclerview.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.removeIf {
                            it.categoryId == 6
                        }
                    }
                    (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                        it
                    )

                } else if (lastPosition > 10) {
                    requireContext().makeToast(noMoreData)
                }
            }
        }, {
            noDataFound.visibility = View.VISIBLE
            correspondence_recyclerview.visibility = View.GONE
            Timber.e(it)

        })
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        correspondence_recyclerview.setOnScrollChangeListener { view, i, i2, i3, i4 ->

            val lastPosition: Int =
                (correspondence_recyclerview.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.checkForClosedLoading(lastPosition, delegationId)

        }
    }

    viewModel.loadMoreClosed(delegationId)

}


private fun initBottomNav(NodeInherit: String) {
    Nodes.find { it.inherit == "Inbox" }?.inherit.let {
        if (it == null) {
            inboxNavLayout.visibility = View.GONE
        }
    }

    Nodes.find { it.inherit == "Sent" }?.inherit.let {
        if (it == null) {
            sentNavLayout.visibility = View.GONE
        }
    }

    Nodes.find { it.inherit == "Completed" }?.inherit.let {
        if (it == null) {
            completedNavLayout.visibility = View.GONE
        }
    }

    Nodes.find { it.inherit == "MyRequests" }?.inherit.let {
        if (it == null) {
            requestedNavLayout.visibility = View.GONE
        }
    }

    Nodes.find { it.inherit == "Closed" }?.inherit.let {
        if (it == null) {
            closedNavLayout.visibility = View.GONE
        }
    }




    NodeInherit.let {

        when (it) {
            "Inbox" -> {
                val imageView = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
                imageView.setImageResource(R.drawable.ic_nav_inbox_active)
                val textView = requireActivity().findViewById<TextView>(R.id.navInboxText)
                textView.setTextColor(Color.parseColor("#FF12A78A"))
                //
                val imageView1 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
                imageView1.setImageResource(R.drawable.ic_nav_sent)
                val textView1 = requireActivity().findViewById<TextView>(R.id.navSentText)
                textView1.setTextColor(Color.parseColor("#333333"))
                //
                val imageView2 =
                    requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
                imageView2.setImageResource(R.drawable.ic_nav_completed)
                val textView2 =
                    requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                textView2.setTextColor(Color.parseColor("#333333"))
                //
                val imageView3 =
                    requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                imageView3.setImageResource(R.drawable.ic_nav_requested)
                val textView3 =
                    requireActivity().findViewById<TextView>(R.id.navRequestedText)
                textView3.setTextColor(Color.parseColor("#333333"))
                //
                val imageView4 =
                    requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
                imageView4.setImageResource(R.drawable.closed)
                val textView4 =
                    requireActivity().findViewById<TextView>(R.id.navclosedtText)
                textView4.setTextColor(Color.parseColor("#333333"))
            }
            "Sent" -> {
                val imageView4 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
                imageView4.setImageResource(R.drawable.ic_nav_inbox)
                val textView4 = requireActivity().findViewById<TextView>(R.id.navInboxText)
                textView4.setTextColor(Color.parseColor("#333333"))
                //
                val imageView5 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
                imageView5.setImageResource(R.drawable.ic_nav_sent_active)
                val textView5 = requireActivity().findViewById<TextView>(R.id.navSentText)
                textView5.setTextColor(Color.parseColor("#FF12A78A"))
                //
                val imageView6 =
                    requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
                imageView6.setImageResource(R.drawable.ic_nav_completed)
                val textView6 =
                    requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                textView6.setTextColor(Color.parseColor("#333333"))
                //
                val imageView7 =
                    requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                imageView7.setImageResource(R.drawable.ic_nav_requested)
                val textView7 =
                    requireActivity().findViewById<TextView>(R.id.navRequestedText)
                textView7.setTextColor(Color.parseColor("#333333"))
                //
                val imageView8 =
                    requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
                imageView8.setImageResource(R.drawable.closed)
                val textView8 =
                    requireActivity().findViewById<TextView>(R.id.navclosedtText)
                textView8.setTextColor(Color.parseColor("#333333"))
            }
            "Completed" -> {
                val imageView8 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
                imageView8.setImageResource(R.drawable.ic_nav_inbox)
                val textView8 = requireActivity().findViewById<TextView>(R.id.navInboxText)
                textView8.setTextColor(Color.parseColor("#333333"))
                //
                val imageView9 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
                imageView9.setImageResource(R.drawable.ic_nav_sent)
                val textView9 = requireActivity().findViewById<TextView>(R.id.navSentText)
                textView9.setTextColor(Color.parseColor("#333333"))
                //
                val imageView10 =
                    requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
                imageView10.setImageResource(R.drawable.ic_nav_completed_active)
                val textView10 =
                    requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                textView10.setTextColor(Color.parseColor("#FF12A78A"))
                //
                val imageView11 =
                    requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                imageView11.setImageResource(R.drawable.ic_nav_requested)
                val textView11 =
                    requireActivity().findViewById<TextView>(R.id.navRequestedText)
                textView11.setTextColor(Color.parseColor("#333333"))
                //
                val imageView12 =
                    requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
                imageView12.setImageResource(R.drawable.closed)
                val textView12 =
                    requireActivity().findViewById<TextView>(R.id.navclosedtText)
                textView12.setTextColor(Color.parseColor("#333333"))
            }
            "Closed" -> {
                val imageView8 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
                imageView8.setImageResource(R.drawable.ic_nav_inbox)
                val textView8 = requireActivity().findViewById<TextView>(R.id.navInboxText)
                textView8.setTextColor(Color.parseColor("#333333"))
                //
                val imageView9 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
                imageView9.setImageResource(R.drawable.ic_nav_sent)
                val textView9 = requireActivity().findViewById<TextView>(R.id.navSentText)
                textView9.setTextColor(Color.parseColor("#333333"))
                //
                val imageView10 =
                    requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
                imageView10.setImageResource(R.drawable.ic_nav_completed)
                val textView10 =
                    requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                textView10.setTextColor(Color.parseColor("#333333"))
                //
                val imageView11 =
                    requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                imageView11.setImageResource(R.drawable.ic_nav_requested)
                val textView11 =
                    requireActivity().findViewById<TextView>(R.id.navRequestedText)
                textView11.setTextColor(Color.parseColor("#333333"))
                //
                val imageView12 =
                    requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
                imageView12.setImageResource(R.drawable.closed_active)
                val textView12 =
                    requireActivity().findViewById<TextView>(R.id.navclosedtText)
                textView12.setTextColor(Color.parseColor("#FF12A78A"))
            }
            "MyRequests" -> {
                val imageView12 =
                    requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
                imageView12.setImageResource(R.drawable.ic_nav_inbox)
                val textView12 = requireActivity().findViewById<TextView>(R.id.navInboxText)
                textView12.setTextColor(Color.parseColor("#333333"))
                //
                val imageView13 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
                imageView13.setImageResource(R.drawable.ic_nav_sent)
                val textView13 = requireActivity().findViewById<TextView>(R.id.navSentText)
                textView13.setTextColor(Color.parseColor("#333333"))
                //
                val imageView14 =
                    requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
                imageView14.setImageResource(R.drawable.ic_nav_completed)
                val textView14 =
                    requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                textView14.setTextColor(Color.parseColor("#333333"))
                //
                val imageView15 =
                    requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                imageView15.setImageResource(R.drawable.ic_nav_requested_active)
                val textView15 =
                    requireActivity().findViewById<TextView>(R.id.navRequestedText)
                textView15.setTextColor(Color.parseColor("#FF12A78A"))
                //
                val imageView1 =
                    requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
                imageView1.setImageResource(R.drawable.closed)
                val textView1 =
                    requireActivity().findViewById<TextView>(R.id.navclosedtText)
                textView1.setTextColor(Color.parseColor("#333333"))
            }
        }
    }
}

private fun setLabels() {
    translator = viewModel.readDictionary()!!.data!!
    when {
        viewModel.readLanguage() == "en" -> {
            navInboxText.text = translator.find { it.keyword == "Inbox" }!!.en
            navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.en
            navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.en
            navSentText.text = translator.find { it.keyword == "Sent" }!!.en
            navclosedtText.text = translator.find { it.keyword == "Closed" }!!.en

        }
        viewModel.readLanguage() == "ar" -> {
            navInboxText.text = translator.find { it.keyword == "Inbox" }!!.ar
            navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.ar
            navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.ar
            navSentText.text = translator.find { it.keyword == "Sent" }!!.ar
            navclosedtText.text = translator.find { it.keyword == "Closed" }!!.ar


        }
        viewModel.readLanguage() == "fr" -> {
            navInboxText.text = translator.find { it.keyword == "Inbox" }!!.fr
            navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.fr
            navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.fr
            navSentText.text = translator.find { it.keyword == "Sent" }!!.fr
            navclosedtText.text = translator.find { it.keyword == "Closed" }!!.fr


        }
    }

}

private fun bottomNavigationClick() {

    inboxNavLayout.setOnClickListener {
        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
        stopDispose()
        noDataFound.visibility = View.GONE
        correspondence_recyclerview.visibility = View.VISIBLE
        getInbox(delegationId)
        val imageView = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
        imageView.setImageResource(R.drawable.ic_nav_inbox_active)
        val textView = requireActivity().findViewById<TextView>(R.id.navInboxText)
        textView.setTextColor(Color.parseColor("#FF12A78A"))
        //
        val imageView1 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
        imageView1.setImageResource(R.drawable.ic_nav_sent)
        val textView1 = requireActivity().findViewById<TextView>(R.id.navSentText)
        textView1.setTextColor(Color.parseColor("#333333"))
        //
        val imageView2 = requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
        imageView2.setImageResource(R.drawable.ic_nav_completed)
        val textView2 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
        textView2.setTextColor(Color.parseColor("#333333"))
        //
        //
        val imageView11 =
            requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
        imageView11.setImageResource(R.drawable.closed)
        val textView11 = requireActivity().findViewById<TextView>(R.id.navclosedtText)
        textView11.setTextColor(Color.parseColor("#333333"))
        //
        val imageView3 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
        imageView3.setImageResource(R.drawable.ic_nav_requested)
        val textView3 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
        textView3.setTextColor(Color.parseColor("#333333"))
    }

    sentNavLayout.setOnClickListener {
        stopDispose()
        noDataFound.visibility = View.GONE
        correspondence_recyclerview.visibility = View.VISIBLE
        getSent(delegationId)
        val imageView4 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
        imageView4.setImageResource(R.drawable.ic_nav_inbox)
        val textView4 = requireActivity().findViewById<TextView>(R.id.navInboxText)
        textView4.setTextColor(Color.parseColor("#333333"))
        //
        val imageView5 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
        imageView5.setImageResource(R.drawable.ic_nav_sent_active)
        val textView5 = requireActivity().findViewById<TextView>(R.id.navSentText)
        textView5.setTextColor(Color.parseColor("#FF12A78A"))
        //
        val imageView6 = requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
        imageView6.setImageResource(R.drawable.ic_nav_completed)
        val textView6 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
        textView6.setTextColor(Color.parseColor("#333333"))
        //
        val imageView11 =
            requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
        imageView11.setImageResource(R.drawable.closed)
        val textView11 = requireActivity().findViewById<TextView>(R.id.navclosedtText)
        textView11.setTextColor(Color.parseColor("#333333"))
        //
        val imageView7 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
        imageView7.setImageResource(R.drawable.ic_nav_requested)
        val textView7 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
        textView7.setTextColor(Color.parseColor("#333333"))
    }

    completedNavLayout.setOnClickListener {
        stopDispose()
        noDataFound.visibility = View.GONE
        correspondence_recyclerview.visibility = View.VISIBLE
        getICompleted(delegationId)
        val imageView8 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
        imageView8.setImageResource(R.drawable.ic_nav_inbox)
        val textView8 = requireActivity().findViewById<TextView>(R.id.navInboxText)
        textView8.setTextColor(Color.parseColor("#333333"))
        //
        val imageView9 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
        imageView9.setImageResource(R.drawable.ic_nav_sent)
        val textView9 = requireActivity().findViewById<TextView>(R.id.navSentText)
        textView9.setTextColor(Color.parseColor("#333333"))
        //
        val imageView10 = requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
        imageView10.setImageResource(R.drawable.ic_nav_completed_active)
        val textView10 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
        textView10.setTextColor(Color.parseColor("#FF12A78A"))
        //
        val imageView1 =
            requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
        imageView1.setImageResource(R.drawable.closed)
        val textView1 = requireActivity().findViewById<TextView>(R.id.navclosedtText)
        textView1.setTextColor(Color.parseColor("#333333"))
        //
        val imageView11 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
        imageView11.setImageResource(R.drawable.ic_nav_requested)
        val textView11 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
        textView11.setTextColor(Color.parseColor("#333333"))
    }

    closedNavLayout.setOnClickListener {
        stopDispose()
        noDataFound.visibility = View.GONE
        correspondence_recyclerview.visibility = View.VISIBLE
        getClosed(delegationId)
        val imageView8 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
        imageView8.setImageResource(R.drawable.ic_nav_inbox)
        val textView8 = requireActivity().findViewById<TextView>(R.id.navInboxText)
        textView8.setTextColor(Color.parseColor("#333333"))
        //
        val imageView9 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
        imageView9.setImageResource(R.drawable.ic_nav_sent)
        val textView9 = requireActivity().findViewById<TextView>(R.id.navSentText)
        textView9.setTextColor(Color.parseColor("#333333"))
        //
        val imageView10 = requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
        imageView10.setImageResource(R.drawable.ic_nav_completed)
        val textView10 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
        textView10.setTextColor(Color.parseColor("#333333"))
        //
        val imageView12 =
            requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
        imageView12.setImageResource(R.drawable.closed_active)
        val textView12 = requireActivity().findViewById<TextView>(R.id.navclosedtText)
        textView12.setTextColor(Color.parseColor("#FF12A78A"))
        //
        val imageView11 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
        imageView11.setImageResource(R.drawable.ic_nav_requested)
        val textView11 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
        textView11.setTextColor(Color.parseColor("#333333"))
    }

    requestedNavLayout.setOnClickListener {
        stopDispose()
        noDataFound.visibility = View.GONE
        correspondence_recyclerview.visibility = View.VISIBLE
        getRequested(delegationId)
        val imageView12 = requireActivity().findViewById<ImageView>(R.id.navInboxBtn)
        imageView12.setImageResource(R.drawable.ic_nav_inbox)
        val textView12 = requireActivity().findViewById<TextView>(R.id.navInboxText)
        textView12.setTextColor(Color.parseColor("#333333"))
        //
        val imageView13 = requireActivity().findViewById<ImageView>(R.id.navSentBtn)
        imageView13.setImageResource(R.drawable.ic_nav_sent)
        val textView13 = requireActivity().findViewById<TextView>(R.id.navSentText)
        textView13.setTextColor(Color.parseColor("#333333"))
        //
        val imageView14 = requireActivity().findViewById<ImageView>(R.id.navCompletedtBtn)
        imageView14.setImageResource(R.drawable.ic_nav_completed)
        val textView14 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
        textView14.setTextColor(Color.parseColor("#333333"))
        //
        val imageView11 =
            requireActivity().findViewById<ImageView>(R.id.navclosedBtn)
        imageView11.setImageResource(R.drawable.closed)
        val textView11 = requireActivity().findViewById<TextView>(R.id.navclosedtText)
        textView11.setTextColor(Color.parseColor("#333333"))
        //
        val imageView15 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
        imageView15.setImageResource(R.drawable.ic_nav_requested_active)
        val textView15 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
        textView15.setTextColor(Color.parseColor("#FF12A78A"))
    }
}

private fun stopDispose() {
    viewModel.disposable?.dispose()
    viewModel.inboxStart = 0
    viewModel.inboxLimit = 0
    viewModel.sentStart = 0
    viewModel.sentLimit = 0
    viewModel.requestedStart = 0
    viewModel.requestedLimit = 0
    viewModel.completedStart = 0
    viewModel.completedLimit = 0
    viewModel.closedStart = 0
    viewModel.closedLimit = 0

}

override fun onDestroyView() {
    super.onDestroyView()
    viewModel.disposable?.dispose()
    viewModel.inboxStart = 0
    viewModel.inboxLimit = 0
    viewModel.sentStart = 0
    viewModel.sentLimit = 0
    viewModel.requestedStart = 0
    viewModel.requestedLimit = 0
    viewModel.completedStart = 0
    viewModel.completedLimit = 0
    viewModel.closedStart = 0
    viewModel.closedLimit = 0
}

override fun getPosition(position: Int, status: Int, model: CorrespondenceDataItem?) {
    if (status == 0) {
        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
    } else {
        //   initActionToolbar(model!!)
        correspondence_maintoolbar.visibility = View.GONE
        correspondenceactionstoolbar.visibility = View.VISIBLE

    }
}

override fun refreshInbox(NodeInherit: String) {
    (activity as AppCompatActivity).supportFragmentManager.commit {
        replace(R.id.fragmentContainer,
            CorrespondenceFragment().apply {
                arguments = bundleOf(
                    Pair(Constants.NODE_INHERIT, NodeInherit)
                )
            }
        )


    }
}

override fun refreshSent(NodeInherit: String) {
    (activity as AppCompatActivity).supportFragmentManager.commit {
        replace(R.id.fragmentContainer,
            CorrespondenceFragment().apply {
                arguments = bundleOf(
                    Pair(Constants.NODE_INHERIT, NodeInherit)
                )
            }
        )


    }
}


}