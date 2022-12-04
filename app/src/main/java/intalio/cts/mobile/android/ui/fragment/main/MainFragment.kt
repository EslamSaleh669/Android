package intalio.cts.mobile.android.ui.fragment.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.adapter.Delegators_Adapter
import intalio.cts.mobile.android.ui.adapter.NodesAdapter
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.main.nodetreeview.*
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.drawer_icon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class MainFragment : Fragment(), Delegators_Adapter.OnDelegatorClicked {

    private var userId: Int = 0
    private var adapter: TreeViewAdapter? = null

    // private lateinit var translator: DictionaryResponse
    private lateinit var translator: java.util.ArrayList<DictionaryDataItem>
    private lateinit var Delegator: DelegationRequestsResponseItem
    var myCorrespondence = ""

    @Inject
    @field:Named("main")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()
    var dialog: Dialog? = null
    var dialogg: Dialog? = null

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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = requireContext().launchLoadingDialog()


        translator = viewModel.readDictionary().data!!

        var transfers = ""


        when {
            viewModel.readLanguage() == "en" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.en!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.ar!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.fr!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.fr!!
            }
        }


        viewModel.readSavedDelegator().let {
            if (it != null) {

                if (it.fromUserId == 0) {
                    nodestitle.text = myCorrespondence
                    getNodesData()
                } else {
                    nodestitle.text = "${it.fromUser} ${transfers}"
                    getDelegatedNodesData(it.id!!)
                }

            } else {
                nodestitle.text = myCorrespondence
                getNodesData()
            }
        }


        swipecontainer.setColorSchemeColors(resources.getColor(R.color.appcolor))
        swipecontainer.setOnRefreshListener {
            swipecontainer.isRefreshing = false

            viewModel.readSavedDelegator().let {
                if (it != null) {

                    if (it.fromUserId == 0) {
                        nodestitle.text = myCorrespondence
                        getNodesData()
                    } else {
                        nodestitle.text = "${it.fromUser} ${transfers}"
                        getDelegatedNodesData(it.id!!)
                    }

                } else {
                    nodestitle.text = myCorrespondence
                    getNodesData()
                }
            }



        }



        drawer_icon.setOnClickListener {
            requireActivity().drawer_layout.openDrawer(GravityCompat.START)
        }

        delegators_icon.setOnClickListener {
            getDelegators()

        }


        viewModel.getUserBasicInfo()!!.id.also { userId = it!!.toInt() }

        getFullUserData()
        getCategoriesData()
        getStatuses()
        getPurposes()
        getPriorities()
        getPrivacies()
        getImportances()
        getTypes()
        getAllStructureData()
        getSettings()
        getFullCategories()
        getFullStructures()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNodesData() {
        var searchText = ""

        when {
            viewModel.readLanguage() == "en" -> {

                searchText = translator.find { it.keyword == "Search" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                searchText = translator.find { it.keyword == "Search" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {
                searchText = translator.find { it.keyword == "Search" }!!.fr!!
            }
        }
        val bam = NodeResponseItem(name = getString(R.string.dashboard), id = 96, inherit = "BAM")
        val advancedSearch = NodeResponseItem(name = searchText, id = 97, inherit = "Search")

        autoDispose.add(viewModel.nodesData().observeOn(AndroidSchedulers.mainThread()).subscribe({

            val nodes = ArrayList<NodeResponseItem>()
            for (item in it) {
                if (item.inherit != null) {
                    if (item.inherit == "Inbox" || item.inherit == "Completed" || item.inherit == "Closed"
                        || item.inherit == "MyRequests" || item.inherit == "Sent"
                    ) {
                        nodes.add(item)
                    }
                }

            }



            viewModel.saveNodes(nodes)

            val requiredNodes = ArrayList<NodeResponseItem>()
            for (item in it) {

                if (item.visible == true) {

                    requiredNodes.add(item)
                }


            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requiredNodes.removeIf { it.inherit == "Draft" }
                requiredNodes.removeIf { it.inherit == "MyRequests" }
            }
            requiredNodes.sortBy { it -> it.order }
            getNodesAsATree(requiredNodes)


            //   nodes.add(bam)
            nodes.add(advancedSearch)
//
//            noderecycler.adapter =
//                NodesAdapter(nodes, requireActivity(), viewModel, autoDispose)
//            noderecycler.layoutManager =
//                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

            dialog!!.dismiss()

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNodesAsATree(nodeItems: ArrayList<NodeResponseItem>) {

        val nodes: MutableList<TreeNode<*>> = mutableListOf<TreeNode<*>>()


        for (parentItem in nodeItems) {

            if (parentItem.parentNodeId == null && parentItem.inherit == null) {
                val levelOneChildren = getParentChildren(parentItem.id, nodeItems)

                if (levelOneChildren.size > 0) {
                    val levelOneParent = TreeNode(
                        NodeParent(
                            parentItem.name,
                            parentItem.inherit,
                            "outsideParent",
                            0
                        )
                    )
                    nodes.add(levelOneParent)

                    for (levelOneItem in levelOneChildren) {

                        //check child if it is parent or child in level one
                        if (levelOneItem.inherit == null) {
                            val levelTwoChildren = getParentChildren(levelOneItem.id, nodeItems)
                            if (levelTwoChildren.size > 0) {
                                val levelTwoParent =
                                    TreeNode(
                                        NodeParent(
                                            levelOneItem.name,
                                            levelOneItem.inherit,
                                            "insideParent",
                                            25
                                        )
                                    )
                                levelOneParent.addChild(levelTwoParent)

                                for (levelTwoItem in levelTwoChildren) {

                                    //check child if it is parent or child in level one
                                    if (levelTwoItem.inherit == null) {
                                        val levelThreeChildren =
                                            getParentChildren(levelTwoItem.id, nodeItems)
                                        if (levelThreeChildren.size > 0) {
                                            val levelThreeParent = TreeNode(
                                                NodeParent(
                                                    levelTwoItem.name,
                                                    levelTwoItem.inherit, "insideParent",
                                                    50
                                                )
                                            )
                                            levelTwoParent.addChild(levelThreeParent)


                                            for (levelThreeItem in levelThreeChildren) {

                                                if (levelThreeItem.inherit == null) {
                                                    val levelFourChildren =
                                                        getParentChildren(
                                                            levelThreeItem.id,
                                                            nodeItems
                                                        )
                                                    if (levelFourChildren.size > 0) {

                                                        val levelFourParent = TreeNode(
                                                            NodeParent(
                                                                levelThreeItem.name,
                                                                levelThreeItem.inherit,
                                                                "insideParent",
                                                                75
                                                            )
                                                        )
                                                        levelThreeParent.addChild(levelFourParent)


                                                        for (levelFourITem in levelFourChildren) {

                                                            if (levelFourITem.inherit == null) {

                                                                val levelFiveChildren =
                                                                    getParentChildren(
                                                                        levelFourITem.id,
                                                                        nodeItems
                                                                    )

                                                                if (levelFiveChildren.size > 0) {
                                                                    val levelFiveParent = TreeNode(
                                                                        NodeParent(
                                                                            levelFourITem.name,
                                                                            levelFourITem.inherit,
                                                                            "insideParent",
                                                                            100
                                                                        )
                                                                    )
                                                                    levelFourParent.addChild(
                                                                        levelFiveParent
                                                                    )


                                                                    for (levelFivItem in levelFiveChildren) {

                                                                        if (levelFivItem.inherit == null) {
                                                                            val levelSexChildren =
                                                                                getParentChildren(
                                                                                    levelFivItem.id,
                                                                                    nodeItems
                                                                                )

                                                                            if (levelSexChildren.size > 0) {
                                                                                val levelSexParent =
                                                                                    TreeNode(
                                                                                        NodeParent(
                                                                                            levelFivItem.name,
                                                                                            levelFivItem.inherit,
                                                                                            "insideParent",
                                                                                            125
                                                                                        )
                                                                                    )
                                                                                levelFiveParent.addChild(
                                                                                    levelSexParent
                                                                                )

                                                                                for (levelSexItem in levelSexChildren) {
                                                                                    if (levelSexItem.inherit == null) {
                                                                                        val levelSevenChildren =
                                                                                            getParentChildren(
                                                                                                levelSexItem.id,
                                                                                                nodeItems
                                                                                            )

                                                                                        if (levelSevenChildren.size > 0) {

                                                                                            val levelSevenParent =
                                                                                                TreeNode(
                                                                                                    NodeParent(
                                                                                                        levelSexItem.name,
                                                                                                        levelSexItem.inherit,
                                                                                                        "insideParent",
                                                                                                        150
                                                                                                    )
                                                                                                )
                                                                                            levelSexParent.addChild(
                                                                                                levelSevenParent
                                                                                            )


                                                                                        }
                                                                                    } else {
                                                                                        val levelSevenChild =
                                                                                            TreeNode(
                                                                                                NodeChild(
                                                                                                    levelSexItem.name,
                                                                                                    levelSexItem.inherit,
                                                                                                    "insideParent",
                                                                                                    150,
                                                                                                    levelSexItem.id!!,
                                                                                                    viewModel,
                                                                                                    autoDispose,
                                                                                                    levelSexItem.enableTodayCount!!,
                                                                                                    levelSexItem.enableTotalCount!!,

                                                                                                    )
                                                                                            )
                                                                                        levelSexParent.addChild(
                                                                                            levelSevenChild
                                                                                        )


                                                                                    }

                                                                                }

                                                                            }

                                                                        } else {
                                                                            val levelSexChild =
                                                                                TreeNode(
                                                                                    NodeChild(
                                                                                        levelFivItem.name,
                                                                                        levelFivItem.inherit,
                                                                                        "insideParent",
                                                                                        125,
                                                                                        levelFivItem.id!!,
                                                                                        viewModel,
                                                                                        autoDispose,
                                                                                        levelFivItem.enableTodayCount!!,
                                                                                        levelFivItem.enableTotalCount!!,
                                                                                    )
                                                                                )
                                                                            levelFiveParent.addChild(
                                                                                levelSexChild
                                                                            )

                                                                        }

                                                                    }

                                                                }
                                                            } else {

                                                                val levelFiveChild = TreeNode(
                                                                    NodeChild(
                                                                        levelFourITem.name,
                                                                        levelFourITem.inherit,
                                                                        "insideParent",
                                                                        100,
                                                                        levelFourITem.id!!,
                                                                        viewModel,
                                                                        autoDispose,
                                                                        levelFourITem.enableTodayCount!!,
                                                                        levelFourITem.enableTotalCount!!,
                                                                    )
                                                                )
                                                                levelFourParent.addChild(
                                                                    levelFiveChild
                                                                )

                                                            }
                                                        }


                                                    }

                                                } else {
                                                    val levelFourChild = TreeNode(
                                                        NodeChild(
                                                            levelThreeItem.name,
                                                            levelThreeItem.inherit,
                                                            "insideParent",
                                                            75,
                                                            levelThreeItem.id!!,
                                                            viewModel,
                                                            autoDispose,
                                                            levelThreeItem.enableTodayCount!!,
                                                            levelThreeItem.enableTotalCount!!,
                                                        )
                                                    )
                                                    levelThreeParent.addChild(levelFourChild)

                                                }


                                            }

                                        }
                                    } else {
                                        val levelThreeChild = TreeNode(
                                            NodeChild(
                                                levelTwoItem.name,
                                                levelTwoItem.inherit,
                                                "insideParent",
                                                50,
                                                levelTwoItem.id!!,
                                                viewModel,
                                                autoDispose,
                                                levelTwoItem.enableTodayCount!!,
                                                levelTwoItem.enableTotalCount!!,
                                            )
                                        )
                                        levelTwoParent.addChild(levelThreeChild)
                                    }
                                }
                            }


                        } else {
                            val levelOneChild =
                                TreeNode(
                                    NodeChild(
                                        levelOneItem.name, levelOneItem.inherit,
                                        "insideParent", 25,
                                        levelOneItem.id!!,
                                        viewModel,
                                        autoDispose,
                                        levelOneItem.enableTodayCount!!,
                                        levelOneItem.enableTotalCount!!,
                                    )
                                )
                            levelOneParent.addChild(levelOneChild)
                        }


                    }
                }

            } else if (parentItem.parentNodeId == null && parentItem.inherit != null) {
                val parent = TreeNode(
                    NodeChild(
                        parentItem.name, parentItem.inherit,
                        "outsideParent", 0,
                        parentItem.id!!,
                        viewModel,
                        autoDispose,
                        parentItem.enableTodayCount!!,
                        parentItem.enableTotalCount!!,
                    )
                )
                nodes.add(parent)
            }

        }

//        expandAll(nodes)


        noderecycler.layoutManager = LinearLayoutManager(requireContext())
        adapter =
            TreeViewAdapter(nodes, listOf(ChildNodeBinder(), ParentNodeBinder()))
        // whether collapse child nodes when their parent node was close.
//        adapter.ifCollapseChildWhileCollapseParent(true);

        noderecycler.adapter = adapter


        adapter!!.setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener {


            override fun onClick(
                p0: tellh.com.recyclertreeview_lib.TreeNode<*>?,
                p1: RecyclerView.ViewHolder?
            ): Boolean {


                val dirViewHolder: ParentNodeBinder.ParentViewHolder? =
                    p1 as? ParentNodeBinder.ParentViewHolder
                val fileViewHolder: ChildNodeBinder.ChildViewHolder? =
                    p1 as? ChildNodeBinder.ChildViewHolder

                if (fileViewHolder != null) {
                    val fileInherit = fileViewHolder.inherit

                    (activity as AppCompatActivity).supportFragmentManager.commit {
                        replace(R.id.fragmentContainer,
                            CorrespondenceFragment().apply {
                                arguments = bundleOf(
                                    Pair(Constants.NODE_INHERIT, fileInherit),
                                )
                            }
                        )
                        addToBackStack("")

                    }
                }



                if (!p0!!.isLeaf) {

                    onToggle(!p0.isExpand, p1!!)

                }
                return false
            }

            override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder) {
                val dirViewHolder = holder as ParentNodeBinder.ParentViewHolder

                val ivArrow: ImageView = dirViewHolder.ivArrow
                val rotateDegree = if (isExpand) 180 else -180
                ivArrow.animate().rotationBy(rotateDegree.toFloat())
                    .start()


            }
        })


    }


    private fun getParentChildren(
        nodeId: Int?,
        nodeItem: ArrayList<NodeResponseItem>
    ): ArrayList<NodeResponseItem> {

        val children = ArrayList<NodeResponseItem>()

        for (item in nodeItem) {
            if (item.parentNodeId == nodeId) {
                if (item.visible == true) {
                    children.add(item)

                }
            }
        }

        if (children.size > 0) {
            children.sortBy { it.order }
        }


        return children
    }

    private fun expandAll(nodes: MutableList<TreeNode<*>>) {
        for (nodeItem in nodes) {
            val item: TreeNode<*> = nodeItem
            if (!item.isLeaf) {
                item.toggle();
                expandAll(item.childList);
            }
        }
    }

    private fun getCategoriesData() {
        autoDispose.add(viewModel.categoriesData().observeOn(Schedulers.io()).subscribe({

            viewModel.saveCategoriesData(it)

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

    private fun getFullUserData() {
        autoDispose.add(
            viewModel.fullUserData().observeOn(AndroidSchedulers.mainThread()).subscribe({

                viewModel.saveFullUserData(it)
                requireActivity().findViewById<TextView>(R.id.clientname).text = it.fullName
                requireActivity().findViewById<TextView>(R.id.clientposition).text =
                    it.email.toString()


                requireActivity().findViewById<TextView>(R.id.txtmenu).text =
                    "${
                        it.firstName!![0].uppercaseChar().toString()
                    }${it.lastName!![0].uppercaseChar().toString()}"


                getUserStructureData(it.structureIds!!.toTypedArray())


            }, {
                Timber.e(it)
                requireActivity().makeToast(it.toString())

            })
        )
    }

    private fun getStatuses() {
        autoDispose.add(viewModel.getStatuses().observeOn(Schedulers.io()).subscribe({

            viewModel.saveStatuses(it)

        }, {
            Timber.e(it)

        }))
    }

    private fun getPurposes() {
        autoDispose.add(viewModel.getPurposes().observeOn(Schedulers.io()).subscribe({

            viewModel.savePurposes(it)

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())


        }))
    }

    private fun getPriorities() {
        autoDispose.add(viewModel.getPriorities().observeOn(Schedulers.io()).subscribe({

            viewModel.savePriorities(it)

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

    private fun getPrivacies() {
        autoDispose.add(viewModel.getPrivacies().observeOn(Schedulers.io()).subscribe({

            viewModel.savePrivacies(it)

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

    private fun getImportances() {
        autoDispose.add(viewModel.getImportances().observeOn(Schedulers.io()).subscribe({

            viewModel.saveImportnaces(it)

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

    private fun getTypes() {
        autoDispose.add(viewModel.getTypes().observeOn(Schedulers.io()).subscribe(
            {

                viewModel.saveTypes(it)


            }, {
                Timber.e(it)
                requireActivity().makeToast(it.toString())
            })
        )
    }

    private fun getFullCategories() {
        autoDispose.add(viewModel.getFullCategories().observeOn(Schedulers.io()).subscribe(
            {

                viewModel.saveFullCategories(it)


            }, {
                Timber.e(it)
                requireActivity().makeToast(it.toString())

            })
        )
    }

    private fun getFullStructures() {
        var languageCode = 0
        when {
            viewModel.readLanguage() == "en" -> {
                languageCode = 1
            }
            viewModel.readLanguage() == "fr" -> {
                languageCode = 2

            }
            viewModel.readLanguage() == "ar" -> {
                languageCode = 3

            }
        }
        autoDispose.add(viewModel.getFullStructures(languageCode).observeOn(Schedulers.io())
            .subscribe(
                {

                    viewModel.saveFullStructures(it.items!!)


                }, {
                    Timber.e(it)
                    requireActivity().makeToast(it.toString())

                })
        )
    }

    private fun getUserStructureData(structureIds: Array<Int>) {

        autoDispose.add(
            viewModel.usersStructureData(structureIds).observeOn(Schedulers.io()).subscribe({


                val usersarray = ArrayList<UsersStructureItem>()

                for (item in it) {
                    if (userId != item.id) {
                        usersarray.add(item)
                    }
                }

                viewModel.saveUsersStructureData(usersarray)


            }, {
                Timber.e(it)
            })
        )
    }

    private fun getAllStructureData() {

        val structureIds = ArrayList<Int>()

        autoDispose.add(
            viewModel.getAllStructures(structureIds).observeOn(Schedulers.io()).subscribe({



                viewModel.saveAllStructures(it)


            }, {
                Timber.e(it)
//            requireActivity().makeToast(it.toString())

            })
        )
    }

    private fun getSettings() {

        autoDispose.add(viewModel.getSettings().observeOn(Schedulers.io()).subscribe({

            viewModel.saveSettings(it)


        }, {

            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

    private fun getDelegators() {
        var currentDelegator = 0


        viewModel.readSavedDelegator().let {


            if (it != null) {
                if (it.fromUserId == 0) {
                    currentDelegator = 0

                } else {
                    currentDelegator = it.fromUserId!!

                }
            } else {
                currentDelegator = 0

            }
        }

        viewModel.delegationRequests()
            .enqueue(object : Callback<ArrayList<DelegationRequestsResponseItem>> {

                override fun onResponse(
                    call: Call<ArrayList<DelegationRequestsResponseItem>>,
                    response: Response<ArrayList<DelegationRequestsResponseItem>>
                ) {
                    try {

                        var responseRecieved: Any? = null
                        responseRecieved = response.body()


                            showDelegatorsDialog(response.body(), currentDelegator)




                    } catch (e: Exception) {
                        e.printStackTrace()


                    }
                }

                override fun onFailure(
                    call: Call<ArrayList<DelegationRequestsResponseItem>>,
                    t: Throwable
                ) {

                }


            })
    }


    fun showDelegatorsDialog(
        model: ArrayList<DelegationRequestsResponseItem>?,
        currentDelegator: Int
    ) {
        var myCorrespondence = ""

        when {
            viewModel.readLanguage() == "en" -> {

                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.ar!!


            }
            viewModel.readLanguage() == "fr" -> {
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.fr!!

            }
        }
        dialogg = Dialog(requireContext(), R.style.AlertDialogStyle)
        dialogg!!.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialogg!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogg!!.setCancelable(true)
        dialogg!!.setContentView(R.layout.delegators_list)
        val delegatorsRecycler =
            dialogg!!.findViewById(R.id.delegators_recycler) as RecyclerView


            val originalUser = DelegationRequestsResponseItem()
            originalUser.id = 0
            originalUser.fromUser = myCorrespondence
            originalUser.fromUserId = 0
            originalUser.fromUserRoleId = 0

            model!!.add(originalUser)
            model.sortBy { it.fromUserId }


        delegatorsRecycler.adapter =
            Delegators_Adapter(model, requireActivity(), this, currentDelegator, viewModel)


        delegatorsRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        dialogg!!.show()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDelegatorSelected(delegator: DelegationRequestsResponseItem) {
        dialogg!!.dismiss()

        var transfers = ""


        when {
            viewModel.readLanguage() == "en" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.en!!
            }
            viewModel.readLanguage() == "ar" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                transfers = translator.find { it.keyword == "Transfers" }!!.fr!!
            }
        }


        viewModel.saveDelegatorData(delegator)

        if (delegator.fromUserId == 0) {
            getNodesData()
            nodestitle.text = delegator.fromUser

        } else {
            getDelegatedNodesData(delegator.id!!)
            nodestitle.text = "${delegator.fromUser} $transfers"

        }

    }

    private fun getDelegatedNodesData(delegationId: Int) {
        var searchText = ""
        var myCorrespondence = ""

        when {
            viewModel.readLanguage() == "en" -> {

                searchText = translator.find { it.keyword == "Search" }!!.en!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                searchText = translator.find { it.keyword == "Search" }!!.ar!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.en!!


            }
            viewModel.readLanguage() == "fr" -> {
                searchText = translator.find { it.keyword == "Search" }!!.fr!!
                myCorrespondence = translator.find { it.keyword == "MyCorrespondences" }!!.en!!

            }
        }
        val bam = NodeResponseItem(name = getString(R.string.dashboard), id = 96, inherit = "BAM")
        val advancedSearch = NodeResponseItem(name = searchText, id = 97, inherit = "Search")

        autoDispose.add(viewModel.nodesData().observeOn(AndroidSchedulers.mainThread()).subscribe({

            val nodes = ArrayList<NodeResponseItem>()
            for (item in it) {
                if (item.inherit != null && item.parentNodeId == null) {
                    if (item.visible == true) {

                        if (item.inherit == "Inbox" || item.inherit == "Completed" || item.inherit == "Sent"
                        ) {
                            nodes.add(item)
                        }
                    }
                }
            }

            nodes.sortBy { it.order }



            viewModel.saveNodes(nodes)


            noderecycler.adapter =
                NodesAdapter(nodes, requireActivity(), viewModel, autoDispose, delegationId)
            noderecycler.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


//            //   nodes.add(bam)
//            nodes.add(advancedSearch)

            dialog!!.dismiss()

        }, {
            Timber.e(it)
            requireActivity().makeToast(it.toString())

        }))
    }

}