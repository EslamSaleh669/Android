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
import intalio.cts.mobile.android.ui.adapter.CorrespondenceAdapter
import intalio.cts.mobile.android.ui.fragment.reply.ReplyToUserFragment
import intalio.cts.mobile.android.ui.fragment.transfer.AddTransferFragment
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_correspondence.*
import kotlinx.android.synthetic.main.fragment_delegations.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CorrespondenceFragment : Fragment(), CorrespondenceAdapter.InterfacePositionCard {

    private var CatNode_Id: Int = 0
    private var popupWindow = PopupWindow()

    private lateinit var translator:  ArrayList<DictionaryDataItem>


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
        val sharedPref = activity?.getSharedPreferences(Constants.ORIGINAL_FILE_PREF,Context.MODE_PRIVATE)
        val editor = sharedPref!!.edit()
        editor.putString(Constants.ORIGINAL_FILE_ID,"")
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

        arguments?.getInt(Constants.NODE_ID)?.let {
            if (it != 0) {
                CatNode_Id = it
                initBottomNav(CatNode_Id)
                Log.d("recaaaaal", CatNode_Id.toString())
                 when (it) {

                    2 -> {
                        getInbox(2)
                    }
                    6 -> {
                        getSent(6)
                    }
                    3 -> {
                        getICompleted(7)
                    }
                    4 -> {
                        getRequested(4)
                    }
                }

            }
        }

        bottomNavigationClick()


    }


    private fun initActionToolbar(model: CorrespondenceDataItem) {


        val inflater = requireActivity().applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: View = inflater.inflate(R.layout.corresponence_menu, null, false)

        view.findViewById<View>(R.id.menu_complete).setOnClickListener {
            popupWindow.dismiss()
            dialog = requireContext().launchLoadingDialog()
            val transferId = arrayOf(model.id)

            autoDispose.add(
                viewModel.completeTransfer(transferId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            dialog!!.dismiss()

                            if (it[0].updated == true) {
                                requireActivity().makeToast(getString(R.string.completed))
                                (activity as AppCompatActivity).supportFragmentManager.commit {
                                    replace(R.id.fragmentContainer,
                                        CorrespondenceFragment().apply {
                                            arguments = bundleOf(
                                                Pair(Constants.NODE_ID, 2)
                                            )

                                        }
                                    )

                                }

                            } else {
                                requireActivity().makeToast(it[0].message!!)
                            }

                        },
                        {
                            Timber.e(it)
                            dialog!!.dismiss()
                            requireActivity().makeToast(getString(R.string.network_error))
                            requireActivity().onBackPressed()

                        })
            )

        }

        view.findViewById<View>(R.id.menu_transfer).setOnClickListener {
            popupWindow.dismiss()

            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AddTransferFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.Correspondence_Model, model)
                        )


                    }
                )
                addToBackStack("")


            }


        }

        view.findViewById<View>(R.id.menu_dismisscopy).setOnClickListener {
            popupWindow.dismiss()

            if (model.cced == false) {
                requireActivity().makeToast(getString(R.string.not_carbon_copy))

            } else {
                dialog = requireContext().launchLoadingDialog()
                val transferId = arrayOf(model.id)

                autoDispose.add(
                    viewModel.transferDismissCopy(transferId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                dialog!!.dismiss()

                                if (it[0].updated == true) {
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

                                } else {
                                    requireActivity().makeToast(it[0].updated.toString())
                                }

                            },
                            {
                                Timber.e(it)
                                dialog!!.dismiss()
                                requireActivity().makeToast(getString(R.string.network_error))
                                requireActivity().onBackPressed()

                            })
                )

            }


        }



        requireActivity().findViewById<View>(R.id.homereplyicon).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    ReplyToUserFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.Correspondence_Model, model)
                        )


                    }
                )
                addToBackStack("")

            }

        }


        requireActivity().findViewById<View>(R.id.homelockicon).setOnClickListener {
            popupWindow.dismiss()
            dialog = requireContext().launchLoadingDialog()

            viewModel.lockTransfer(
                model.id!!

            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    dialog!!.dismiss()
                    if (response.code() != 200) {

                        requireActivity().makeToast(getString(R.string.network_error))
                        activity!!.onBackPressed()

                    } else {

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
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog!!.dismiss()
                    requireActivity().makeToast(getString(R.string.network_error))
                    activity!!.onBackPressed()

                }


            }

            )
        }

        val width = (resources.displayMetrics.widthPixels * 0.40).toInt()
        val hight = (resources.displayMetrics.widthPixels * 0.80).toInt()
        popupWindow = PopupWindow(view, width, LinearLayout.LayoutParams.WRAP_CONTENT, true)

    }

    private fun getInbox(nodeId: Int) {

        viewModel.saveNodeId(nodeId)

        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
        dialog = requireContext().launchLoadingDialog()
        viewModel.inboxMessages = ReplaySubject.create()

        correspondence_recyclerview.adapter =
            CorrespondenceAdapter(
                arrayListOf(), requireActivity(), nodeId, this, viewModel, autoDispose

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
                        (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                            it
                        )

                        //                      val custom =  (correspondence_recyclerview.adapter as CustomAdapter)
//                        custom.setOnItemLongClickListener {
//                                position, view, `objectt` ->
//                            view.setBackgroundColor(requireActivity().getColor(R.color.red))
//                            requireActivity().makeToast(objectt.subject!!)
//                            true
//                        }


                    } else if (lastPosition > 10) {
                        requireContext().makeToast(getString(R.string.no_moredata))
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
                viewModel.checkForInboxLoading(lastPosition)

            }
        }

        viewModel.loadMoreInboxes()

    }

    private fun getSent(nodeId: Int) {
        viewModel.saveNodeId(nodeId)

        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
        dialog = requireContext().launchLoadingDialog()

        viewModel.sentMessages = ReplaySubject.create()

        correspondence_recyclerview.adapter =
            CorrespondenceAdapter(
                arrayListOf(),
                requireActivity(),
                nodeId,
                this,
                viewModel, autoDispose
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
                    (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(getString(R.string.no_moredata))
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
                viewModel.checkForSentLoading(lastPosition)

            }
        }

        viewModel.loadMoreSent()

    }


    private fun getICompleted(nodeId: Int) {
        viewModel.saveNodeId(nodeId)

        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
        dialog = requireContext().launchLoadingDialog()
        viewModel.completedMessages = ReplaySubject.create()

        correspondence_recyclerview.adapter =
            CorrespondenceAdapter(
                arrayListOf(),
                requireActivity(),
                nodeId,
                this,
                viewModel, autoDispose            )
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
                        (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                            it
                        )

                    } else if (lastPosition > 10) {
                        requireContext().makeToast(getString(R.string.no_moredata))
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
                viewModel.checkForCompletedLoading(lastPosition)

            }
        }

        viewModel.loadMoreCompleted()

    }


    private fun getRequested(nodeId: Int) {
        viewModel.saveNodeId(nodeId)

        correspondenceactionstoolbar.visibility = View.GONE
        correspondence_maintoolbar.visibility = View.VISIBLE
        dialog = requireContext().launchLoadingDialog()
        viewModel.requestedMessages = ReplaySubject.create()

        correspondence_recyclerview.adapter =
            CorrespondenceAdapter(
                arrayListOf(),
                requireActivity(),
                nodeId,
                this,
                viewModel, autoDispose            )
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
                        (correspondence_recyclerview.adapter as CorrespondenceAdapter).addMessages(
                            it
                        )

                    } else if (lastPosition > 10) {
                        requireContext().makeToast(getString(R.string.no_moredata))
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
                viewModel.checkForRequestedLoading(lastPosition)

            }
        }

        viewModel.loadMoreRequested()

    }


    private fun initBottomNav(NodeId: Int) {

        NodeId.let {

            when (it) {
                2 -> {
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
                    val textView2 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                    textView2.setTextColor(Color.parseColor("#333333"))
                    //
                    val imageView3 =
                        requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                    imageView3.setImageResource(R.drawable.ic_nav_requested)
                    val textView3 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
                    textView3.setTextColor(Color.parseColor("#333333"))
                }
                6 -> {
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
                    val textView6 = requireActivity().findViewById<TextView>(R.id.navCompletedtText)
                    textView6.setTextColor(Color.parseColor("#333333"))
                    //
                    val imageView7 =
                        requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
                    imageView7.setImageResource(R.drawable.ic_nav_requested)
                    val textView7 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
                    textView7.setTextColor(Color.parseColor("#333333"))
                }
                3 -> {
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
                    val textView11 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
                    textView11.setTextColor(Color.parseColor("#333333"))
                }
                4 -> {
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
                    val textView15 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
                    textView15.setTextColor(Color.parseColor("#FF12A78A"))
                }
            }
        }
    }

    private fun setLabels(){
        translator = viewModel.readDictionary()!!.data!!
        when {
            viewModel.readLanguage() == "en" -> {
                navInboxText.text = translator.find { it.keyword == "Inbox" }!!.en
                navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.en
                navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.en
                navSentText.text = translator.find { it.keyword == "Sent" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {
                navInboxText.text = translator.find { it.keyword == "Inbox" }!!.ar
                navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.ar
                navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.ar
                navSentText.text = translator.find { it.keyword == "Sent" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                navInboxText.text = translator.find { it.keyword == "Inbox" }!!.fr
                navCompletedtText.text = translator.find { it.keyword == "Completed" }!!.fr
                navRequestedText.text = translator.find { it.keyword == "MyRequests" }!!.fr
                navSentText.text = translator.find { it.keyword == "Sent" }!!.fr

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
            getInbox(2)
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
            val imageView3 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
            imageView3.setImageResource(R.drawable.ic_nav_requested)
            val textView3 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
            textView3.setTextColor(Color.parseColor("#333333"))
        }

        sentNavLayout.setOnClickListener {
            stopDispose()
            noDataFound.visibility = View.GONE
            correspondence_recyclerview.visibility = View.VISIBLE
            getSent(6)
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
            val imageView7 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
            imageView7.setImageResource(R.drawable.ic_nav_requested)
            val textView7 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
            textView7.setTextColor(Color.parseColor("#333333"))
        }

        completedNavLayout.setOnClickListener {
            stopDispose()
            noDataFound.visibility = View.GONE
            correspondence_recyclerview.visibility = View.VISIBLE
            getICompleted(3)
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
            val imageView11 = requireActivity().findViewById<ImageView>(R.id.navRequestedtBtn)
            imageView11.setImageResource(R.drawable.ic_nav_requested)
            val textView11 = requireActivity().findViewById<TextView>(R.id.navRequestedText)
            textView11.setTextColor(Color.parseColor("#333333"))
        }

        requestedNavLayout.setOnClickListener {
            stopDispose()
            noDataFound.visibility = View.GONE
            correspondence_recyclerview.visibility = View.VISIBLE
            getRequested(4)
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
    }

    override fun getPosition(position: Int, status: Int, model: CorrespondenceDataItem?) {
        if (status == 0) {
            correspondenceactionstoolbar.visibility = View.GONE
            correspondence_maintoolbar.visibility = View.VISIBLE
        } else {
            initActionToolbar(model!!)
            correspondence_maintoolbar.visibility = View.GONE
            correspondenceactionstoolbar.visibility = View.VISIBLE

        }
    }


}