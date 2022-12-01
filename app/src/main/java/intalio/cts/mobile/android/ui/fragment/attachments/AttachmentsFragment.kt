package intalio.cts.mobile.android.ui.fragment.attachments

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_attachments.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import intalio.cts.mobile.android.ui.fragment.uploadattachment.UploadAttachmentFragment
import intalio.cts.mobile.android.ui.fragment.attachments.treeview.DirectoryNodeBinder

import androidx.recyclerview.widget.RecyclerView

import tellh.com.recyclertreeview_lib.TreeViewAdapter

import intalio.cts.mobile.android.ui.fragment.attachments.treeview.FileNodeBinder

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.network.response.*

import intalio.cts.mobile.android.ui.fragment.attachments.treeview.Dir
import intalio.cts.mobile.android.ui.fragment.attachments.treeview.File
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import kotlinx.android.synthetic.main.categories_viewshape.*
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter.OnTreeNodeListener
import java.util.*
import kotlin.math.log


class AttachmentsFragment : Fragment() {

    private lateinit var model: CorrespondenceDataItem
    private lateinit var searchModel: AdvancedSearchResponseDataItem
    private lateinit var requestedModel: MetaDataResponse

    private lateinit var translator:  ArrayList<DictionaryDataItem>

    private var Node_Inherit :String = ""
    private var canDoAction: Boolean = false

    private var Selected_Folder: String = "-1"
    private var originalHasChild = false

    var lastChecked: LinearLayout? = null
    private var delegationId = 0


    private var adapter: TreeViewAdapter? = null
    private var viewMode: Boolean = false

    @Inject
    @field:Named("attachments")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AttachmentsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AttachmentsViewModel::class.java)
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

        return inflater.inflate(R.layout.fragment_attachments, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        translator = viewModel.readDictionary()!!.data!!
        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        when {
            viewModel.readLanguage() == "en" -> {
                centered_txt.text = translator.find { it.keyword == "Attachments" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {
                centered_txt.text = translator.find { it.keyword == "Attachments" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                centered_txt.text = translator.find { it.keyword == "Attachments" }!!.fr

            }
        }
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        requireArguments().getString(Constants.NODE_INHERIT).let {
            Node_Inherit = it!!
        }
        requireArguments().getBoolean(Constants.CANDOACTION).let {
            canDoAction = it
        }

        requireArguments().getBoolean(Constants.VIEWMODE).let {
            viewMode = it
        }



        arguments?.getSerializable(Constants.Correspondence_Model).let {

            if (viewModel.readPath() == "search") {
                searchModel = it as AdvancedSearchResponseDataItem
                attachmentsData(searchModel.id!!)
            } else {
                if (Node_Inherit == "MyRequests") {
                    requestedModel = it as MetaDataResponse
                    attachmentsData(requestedModel.id!!)

                }else if (Node_Inherit == "Closed"){
                    requestedModel = it as MetaDataResponse
                    attachmentsData(requestedModel.id!!)
                }
                else {

                    model = it as CorrespondenceDataItem
                    attachmentsData(model.documentId!!)

                }

            }


        }

        uploadattach.setOnClickListener {
            if (Selected_Folder == "-1" && !originalHasChild) {
                requireActivity().makeToast(getString(R.string.longclick))
            } else if (Selected_Folder == "-1" && originalHasChild) {
                requireActivity().makeToast(getString(R.string.original_doc__already_uploaded))

            } else {
                //  Log.d("aaaaaaazzz", originalHasChild.toString())
                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        UploadAttachmentFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.Correspondence_Model, model),
                                Pair(Constants.SELECTED_FOLDER_ID, Selected_Folder)
                            )
                        }
                    )

                    addToBackStack("")
                    Selected_Folder = "-1"
                    originalHasChild = false
                }
            }

        }
        dialog = requireActivity().launchLoadingDialog()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun attachmentsData(DoctId: Int) {


        autoDispose.add(
            viewModel.attachmentsData(DoctId,delegationId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {

                    dialog!!.dismiss()
                    if (it.isNotEmpty()) {

                        initData(it)
                    } else {
                        noDataFounded.visibility = View.VISIBLE
                        rvTreeView.visibility = View.GONE
                    }

                },
                {
                    requireActivity().makeToast(it.toString())
                    Timber.e(it)
                    dialog!!.dismiss()


                })
        )

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initData(attachments: ArrayList<AttachmentModel>) {

        val nodes: MutableList<TreeNode<*>> = mutableListOf<TreeNode<*>>()
        for (item in attachments) {


            val parent = TreeNode(Dir(item.text, item.id))
            nodes.add(parent)
            if (item.children != null) {
                val reverseItems: List<AttachmentModel> = item.children.reversed();

                for (subChildItem in reverseItems) {

                    if (subChildItem.type == Constants.FOLDER_TYPE) {
                        val subChild = TreeNode(Dir(subChildItem.text, subChildItem.id))

                        if (subChildItem.children != null) {

                            for (childItem in subChildItem.children) {

                                if (childItem.type == Constants.FOLDER_TYPE) {
                                    val child = TreeNode(Dir(childItem.text, childItem.id))

                                    if (childItem.children != null) {

                                        for (extraChildItem in childItem.children) {
                                            if (extraChildItem.type == Constants.FOLDER_TYPE) {

                                                val extraChild = TreeNode(
                                                    Dir(
                                                        extraChildItem.text,
                                                        extraChildItem.id
                                                    )
                                                )
                                                if (extraChildItem.children != null) {

                                                    for (mostExtraChildItem in extraChildItem.children) {
                                                        if (mostExtraChildItem.type == Constants.FOLDER_TYPE) {

                                                            val mostExtraChild = TreeNode(
                                                                Dir(
                                                                    mostExtraChildItem.text,
                                                                    mostExtraChildItem.id
                                                                )
                                                            )
                                                            extraChild.addChild(mostExtraChild)

                                                        } else {
                                                            val mostExtraChild = TreeNode(
                                                                File(
                                                                    mostExtraChildItem.text,
                                                                    mostExtraChildItem.id,
                                                                    mostExtraChildItem.parentId,
                                                                )
                                                            )
                                                            extraChild.addChild(mostExtraChild)

                                                        }
                                                    }

                                                }
                                                child.addChild(extraChild)

                                            } else {
                                                val extraChild = TreeNode(
                                                    File(
                                                        extraChildItem.text,
                                                        extraChildItem.id,
                                                        extraChildItem.parentId
                                                    )
                                                )

                                                child.addChild(extraChild)

                                            }

                                        }
                                    }

                                    subChild.addChild(child)

                                } else {
                                    val child = TreeNode(File(childItem.text, childItem.id,childItem.parentId))
                                    subChild.addChild(child)

                                }

                            }

                        }
                        parent.addChild(subChild)

                    } else {
                        val subChild = TreeNode(File(subChildItem.text, subChildItem.id,subChildItem.parentId))
                        parent.addChild(subChild)


                    }

                }
            }

        }

        expandAll(nodes)



        rvTreeView.layoutManager = LinearLayoutManager(requireContext())
        adapter =
            TreeViewAdapter(nodes, listOf(FileNodeBinder(), DirectoryNodeBinder()))
        // whether collapse child nodes when their parent node was close.
//        adapter.ifCollapseChildWhileCollapseParent(true);

        rvTreeView.adapter = adapter


        adapter!!.setOnTreeNodeListener(object : OnTreeNodeListener {


            override fun onClick(
                p0: tellh.com.recyclertreeview_lib.TreeNode<*>?,
                p1: RecyclerView.ViewHolder?
            ): Boolean {

                originalHasChild = p0!!.childList != null

                // val dirViewHolder = p1 as DirectoryNodeBinder.ViewHolder
                val dirViewHolder: DirectoryNodeBinder.ViewHolder? =
                    p1 as? DirectoryNodeBinder.ViewHolder
                val fileViewHolder: FileNodeBinder.ViewHolder? = p1 as? FileNodeBinder.ViewHolder

                if (fileViewHolder != null) {
                    val fileId = fileViewHolder.id

                    val fileParentId = fileViewHolder.parentId
                    var isOriginalFile = false

                    isOriginalFile = fileParentId.equals("folder_originalMail")

                    if (viewModel.readPath() == "search") {
                        (activity as AppCompatActivity).supportFragmentManager.commit {
                            replace(R.id.fragmentContainer,
                                CorrespondenceDetailsFragment().apply {
                                    arguments = bundleOf(
                                        Pair(Constants.NODE_INHERIT, Node_Inherit),

                                        Pair(Constants.Correspondence_Model, searchModel),
                                        Pair(Constants.FILE_ID, fileId.substringAfter("_")),
                                        Pair(Constants.FILE_PARENT_ID, isOriginalFile),
                                        Pair(Constants.VIEWMODE, viewMode),
                                        Pair(Constants.PATH, "attachment"),
                                        Pair(Constants.LATEST_PATH, "search"),
                                        )

                                }
                            ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                        }

                    } else if (viewModel.readPath() == "node" || viewModel.readPath() == "attachment"){
                        if (Node_Inherit == "MyRequests") {
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_INHERIT, Node_Inherit),

                                            Pair(Constants.Correspondence_Model, requestedModel),
                                            Pair(Constants.FILE_ID, fileId.substringAfter("_")),
                                            Pair(Constants.FILE_PARENT_ID, isOriginalFile),
                                            Pair(Constants.VIEWMODE, viewMode),
                                            Pair(Constants.PATH, "attachment"),
                                            Pair(Constants.LATEST_PATH, "requested node"),
                                        )

                                    }
                                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                            }
                        }
                        else if (Node_Inherit == "Closed"){
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_INHERIT, Node_Inherit),

                                            Pair(Constants.Correspondence_Model, requestedModel),
                                            Pair(Constants.FILE_ID, fileId.substringAfter("_")),
                                            Pair(Constants.FILE_PARENT_ID, isOriginalFile),
                                            Pair(Constants.VIEWMODE, viewMode),
                                            Pair(Constants.PATH, "attachment"),
                                            Pair(Constants.LATEST_PATH, "closed node"),
                                        )

                                    }
                                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                            }
                        }

                        else if (Node_Inherit == "Inbox"){
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_INHERIT, Node_Inherit),

                                            Pair(Constants.Correspondence_Model, model),
                                            Pair(Constants.FILE_ID, fileId.substringAfter("_")),
                                            Pair(Constants.FILE_PARENT_ID, isOriginalFile),
                                            Pair(Constants.VIEWMODE, viewMode),
                                            Pair(Constants.PATH, "attachment"),
                                            Pair(Constants.LATEST_PATH, "inbox node"),
                                            Pair(Constants.CANDOACTION, canDoAction),

                                        )

                                    }
                                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                            }
                        }

                        else {
                            (activity as AppCompatActivity).supportFragmentManager.commit {
                                replace(R.id.fragmentContainer,
                                    CorrespondenceDetailsFragment().apply {
                                        arguments = bundleOf(
                                            Pair(Constants.NODE_INHERIT, Node_Inherit),

                                            Pair(Constants.Correspondence_Model, model),
                                            Pair(Constants.FILE_ID, fileId.substringAfter("_")),
                                            Pair(Constants.FILE_PARENT_ID, isOriginalFile),
                                            Pair(Constants.VIEWMODE, viewMode),
                                            Pair(Constants.PATH, "attachment"),
                                            Pair(Constants.LATEST_PATH, "node"),
                                        )

                                    }
                                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                            }

                        }

                    }
                    viewModel.savePath("attachment")

                }

                if (dirViewHolder != null) {
                    val nodeId = dirViewHolder.id
                    val nodeTitle = dirViewHolder.tvName
                    val linearLayout: LinearLayout = dirViewHolder.linearLayout
                    when (nodeTitle) {
                        "Original document" -> {
                            Selected_Folder = if (!originalHasChild) {
                                "0"
                            } else {
                                "-1"
                            }

                        }
                        "/" -> {
                            Selected_Folder = "#"
                        }
                        else -> {
                            Selected_Folder = nodeId.substringAfter("_")
                        }
                    }


                    if (lastChecked == null) {
                        lastChecked = linearLayout
                        lastChecked!!.setBackgroundColor(Color.LTGRAY)

                    } else {
                        lastChecked!!.setBackgroundColor(Color.WHITE)

                        lastChecked = linearLayout
                        lastChecked!!.setBackgroundColor(Color.LTGRAY)
                    }
                }


                if (!p0.isLeaf) {

                    onToggle(!p0.isExpand, p1!!)

                }
                return false
            }

            override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder) {
                val dirViewHolder = holder as DirectoryNodeBinder.ViewHolder

                val ivArrow: ImageView = dirViewHolder.ivArrow
                val rotateDegree = if (isExpand) 90 else -90
                ivArrow.animate().rotationBy(rotateDegree.toFloat())
                    .start()


            }
        })


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
}


//                        val root = TreeNode.root()
//                        buildTree2(it, root)
//                        val treeView = AndroidTreeView(requireContext(), root)
//                        rlTreeView.addView(treeView.view)
//                        treeView.expandAll()


//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun buildTree2(attachments: ArrayList<AttachmentsResponseItem>, parent: TreeNode) {
//
//        for (item in attachments) {
//
//            if (item.type == Constants.FOLDER_TYPE) {
//                val child = FolderHolder.IconTreeItemm()
//
//                child.text = item.text
//                child.icon = R.drawable.ic_folder_icon
//                child.hasParent = item.parentId != null
//
//                child.hasFolderParent = item.parentId.toString() != "#"
//                val childNode = TreeNode(child).setViewHolder(FolderHolder(requireContext()))
//
//
//
//                childNode.clickListener =
//                    TreeNodeClickListener { node: TreeNode?, value: Any? ->
//
//
//                    }
//
//
//                childNode.longClickListener = TreeNodeLongClickListener { node, value ->
//                    node!!.isSelectable = true
//                    node.isSelected = true
//                    node.setSelected(true)
//                    childNode.isSelectable = true
//                    childNode.isSelected = true
//                    childNode.setSelected(true)
//
//
//
//                    val snack = Snackbar.make(
//                        requireView(),
//                        "${getString(R.string.selection)} ${item.text}",
//                        Snackbar.LENGTH_LONG
//                    )
//                    snack.setBackgroundTint(requireActivity().getColor(R.color.appcolor))
//
//                   if (item.title == "Original document"){
//                       if (item.children == null){
//                           Selected_Folder = "0"
//                           snack.show()
//
//
//                       }else{
//                           requireActivity().makeToast(getString(R.string.original_doc__already_uploaded))
//                       }
//
//                    }else{
//                        item.id!!.substringAfter("_")
//                        snack.show()
//
//                    }
//
//
//
//                    false
//                }
//
//                parent.addChild(childNode)
//                if (item.children != null) {
//                    buildTree2(item.children, childNode)
//
//                }
//            } else {
//                val child = FileHolder.IconTreeItem()
//                child.text = item.text
//                child.icon = R.drawable.ic_file_icon
//                val childNode = TreeNode(child).setViewHolder(FileHolder(requireContext()))
//                childNode.clickListener =
//                    TreeNodeClickListener { node: TreeNode?, value: Any? ->
//                    }
//                parent.addChild(childNode)
//            }
//
//        }
//    }


//                val snack = Snackbar.make(
//                    requireView(),
//                    "${getString(R.string.selection)} $nodeTitle",
//                    Snackbar.LENGTH_SHORT
//                )
//                snack.setBackgroundTint(requireActivity().getColor(R.color.appcolor))
//                        snack.show()