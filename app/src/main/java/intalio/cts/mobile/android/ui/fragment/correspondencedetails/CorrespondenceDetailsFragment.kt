package intalio.cts.mobile.android.ui.fragment.correspondencedetails

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.cts.mobile.android.R
import com.futuremind.recyclerviewfastscroll.FastScroller
import intalio.cts.mobile.android.data.model.AttachmentModel
import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel

import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.ui.fragment.attachments.AttachmentsFragment
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.metadata.MetaDataFragment
import intalio.cts.mobile.android.ui.fragment.nonarchattachments.NonArchAttachmentsFragment
import intalio.cts.mobile.android.ui.fragment.note.AllNotesFragment
import intalio.cts.mobile.android.ui.fragment.reply.ReplyToStructureFragment
import intalio.cts.mobile.android.ui.fragment.reply.ReplyToUserFragment
import intalio.cts.mobile.android.ui.fragment.transfer.AddTransferFragment
import intalio.cts.mobile.android.ui.fragment.visualtracking.BuchheimWalkerActivity
import intalio.cts.mobile.android.util.*
import intalio.cts.mobile.android.util.Constants.Companion.HAND_NOTE_SIGN_RESULT
import intalio.cts.mobile.android.viewer.Utility
import intalio.cts.mobile.android.viewer.support.PDFConfig
import intalio.cts.mobile.android.viewer.viewer_menu.MenuAdapter
import intalio.cts.mobile.android.viewer.viewer_menu.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.viewer_layout.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*

import javax.inject.Inject
import javax.inject.Named
import intalio.cts.mobile.android.viewer.views.SignatureTemplateDialog


class CorrespondenceDetailsFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var translator: ArrayList<DictionaryDataItem>

    public var myInstance: CorrespondenceDetailsFragment? = null
    private var justLaunched = true
    private var pdfWidth = 0
    private var pdfHeight: Int = 0
    private var viewMode = false
    private var CandoAction =false
    private var delegationId = 0

    lateinit var popupWindow: PopupWindow
    private var Node_Inherit: String = ""
    private var Latest_Path: String = ""
    private var fileId: String = ""
    private lateinit var correspondenceModel: CorrespondenceDataItem
    private lateinit var searchModel: AdvancedSearchResponseDataItem
    private lateinit var requestedModel: MetaDataResponse

    //  private lateinit var originalAttachment: AttachmentsResponseItem


    //    lateinit var viewerAdapter: ViewerAdapter
    private lateinit var _fastScroller: FastScroller
    private lateinit var _rvViewer: RecyclerView
    private lateinit var _rvMenu: RecyclerView
    private lateinit var _llSave: LinearLayout
    private lateinit var _menuLayout: LinearLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var _downloadProgress: ProgressBar
    private lateinit var progressBarViewer: ProgressBar
    private lateinit var tvNoAttachments: TextView
    private lateinit var currentAttachment: AttachmentModel
    private lateinit var originalMail: AttachmentModel

    private var fileVersions: ArrayList<String>? = null


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
        myInstance = this;

        return inflater.inflate(R.layout.viewer_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = requireContext().launchLoadingDialog()


        translator = viewModel.readDictionary()!!.data!!
        init()
        initRecyclerView()

        viewModel.readSavedDelegator().let {
            delegationId = if (it != null) {

                it.id!!

            } else {
                0
            }
        }
        popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
            LinearLayout.LayoutParams.MATCH_PARENT// Window height
        )

        dropedmenu.setOnClickListener {

            popupWindow.showAsDropDown(it, 0, 20);

        }

        back_icon.setOnClickListener {
            requireActivity().onBackPressed()
        }


        arguments?.getString(Constants.PATH)?.let {

            viewModel.savePath(it)

            if (it == "node") {
                Node_Inherit = viewModel.readCurrentNode()

                correspondenceModel =
                    arguments?.getSerializable(Constants.Correspondence_Model) as CorrespondenceDataItem
                title_new_viewer_activity.text = correspondenceModel.referenceNumber

                if (Node_Inherit == "MyRequests") {


                    viewMode = false
                    //my requests returns document id only
                    getDocument(correspondenceModel.id!!, delegationId, Latest_Path)
                    getOriginalDocument(
                        correspondenceModel.id!!,
                        "null",
                        false,
                        viewMode,
                        delegationId
                    )
                } else if (Node_Inherit == "Closed") {

                    viewMode = false
                    //my requests returns document id only
                    getSearchedDocument(correspondenceModel.id!!, delegationId)
                    getOriginalDocument(
                        correspondenceModel.id!!,
                        "null",
                        false,
                        viewMode,
                        delegationId
                    )
                } else {

                    viewMode = Node_Inherit == "Inbox" &&
                            (correspondenceModel.messageLock == "lockedbyme" ||
                                    correspondenceModel.messageLock == "notlocked")

//                    correspondenceModel.messageLock == "broadcastlockedbyme" ||
//                            correspondenceModel.messageLock == "broadcastnotlocked"
                    if (Node_Inherit == "Inbox") {
                        viewDocument(correspondenceModel.id!!, delegationId)

                    }
                    setPopUpWindow(correspondenceModel, delegationId, Latest_Path)
                    getOriginalDocument(
                        correspondenceModel.documentId!!,
                        correspondenceModel.id!!.toString(),
                        false,
                        viewMode,
                        delegationId
                    )


                }


            } else if (it == "search") {


//                _menuLayout.visibility = View.INVISIBLE

                viewMode = false

                searchModel =
                    arguments?.getSerializable(Constants.Correspondence_Model) as AdvancedSearchResponseDataItem
                title_new_viewer_activity.text = searchModel.referenceNumber

                setSearchPopUpWindow(searchModel, delegationId,Latest_Path)
                getOriginalDocument(
                    searchModel.id!!,
                    "null",
                    false,
                    viewMode,
                    delegationId
                )
            } else {


                requireArguments().getString(Constants.FILE_ID).let { id ->

                    fileId = id!!
                }


                requireArguments().getString(Constants.LATEST_PATH).let { latest_path ->

                    Latest_Path = latest_path!!
                }

                requireArguments().getString(Constants.NODE_INHERIT).let {
                    Node_Inherit = it!!

                }


                if (Latest_Path == "requested node" ) {

                    requestedModel =
                        arguments?.getSerializable(Constants.Correspondence_Model) as MetaDataResponse
                    title_new_viewer_activity.text = requestedModel.subject

                    viewMode = false
                    //my requests returns document id only
                    getDocument(requestedModel.id!!, delegationId, Latest_Path)
                    getOriginalDocument(
                        requestedModel.id!!,
                        "null",
                        false,
                        viewMode,
                        delegationId
                    )
                } else if (Latest_Path == "closed node"){
                    requestedModel =
                        arguments?.getSerializable(Constants.Correspondence_Model) as MetaDataResponse
                    viewMode = false
                    title_new_viewer_activity.text = requestedModel.referenceNumber

                    getSearchedDocument(requestedModel.id!!, delegationId)

                    getOriginalDocument(
                        requestedModel.id!!,
                        "null",
                        false,
                        viewMode,
                        delegationId
                    )
                }
                else if (Latest_Path == "inbox node") {

                    requireArguments().getBoolean(Constants.VIEWMODE).let { viewmode ->

                        viewMode = viewmode
                    }

                    requireArguments().getBoolean(Constants.CANDOACTION).let { candoaction ->

                        CandoAction = candoaction
                    }


                    correspondenceModel =
                        arguments?.getSerializable(Constants.Correspondence_Model) as CorrespondenceDataItem
                    title_new_viewer_activity.text = correspondenceModel.referenceNumber


                    //viewDocument(correspondenceModel.id!!,delegationId)

                    setPopUpWindow(correspondenceModel, delegationId,Latest_Path)
                    getOriginalDocument(
                        correspondenceModel.documentId!!,
                        correspondenceModel.id!!.toString(),
                        false,
                        viewMode,
                        delegationId
                    )

                }
                else if (Latest_Path == "node") {
                    Log.d("Latest_Path","I am at node condition")

                    requireArguments().getBoolean(Constants.VIEWMODE).let { viewmode ->

                        viewMode = viewmode
                    }


                    correspondenceModel =
                        arguments?.getSerializable(Constants.Correspondence_Model) as CorrespondenceDataItem
                    title_new_viewer_activity.text = correspondenceModel.referenceNumber


                    //viewDocument(correspondenceModel.id!!,delegationId)
                    setPopUpWindow(correspondenceModel, delegationId,Latest_Path)
                    getOriginalDocument(
                        correspondenceModel.documentId!!,
                        correspondenceModel.id!!.toString(),
                        false,
                        viewMode,
                        delegationId
                    )

                }

                else {



//                    _menuLayout.visibility = View.INVISIBLE


                    viewMode = false

                    searchModel =
                        arguments?.getSerializable(Constants.Correspondence_Model) as AdvancedSearchResponseDataItem
                    title_new_viewer_activity.text = searchModel.referenceNumber

                    setSearchPopUpWindow(searchModel, delegationId,Latest_Path)
                    getOriginalDocument(
                        searchModel.id!!,
                        "null",
                        false,
                        viewMode,
                        delegationId
                    )
                }

            }

        }


    }

    private fun init() {
        // PDFConfig.isOffline = Utility.getLocalData(requireContext(), "Offline").equals("On")
        _downloadProgress = requireActivity().findViewById<ProgressBar>(R.id.downloadProgress)
        val _tvFailedToDownload: TextView =
            requireActivity().findViewById<TextView>(R.id.tvFailedToDownload)
        tvNoAttachments = requireActivity().findViewById<TextView>(R.id.tvNoAttachments)
        _llSave = requireActivity().findViewById<LinearLayout>(R.id.llSave)
        _llSave.setOnClickListener(View.OnClickListener { v: View? ->


        })
    }

    private fun initRecyclerView() {
        _rvViewer = requireActivity().findViewById<RecyclerView>(R.id.rvViewer)
        if (Utility.isLandScape) {
            _rvViewer.layoutParams.width = Utility.getScreenHeight(requireActivity())
            _rvViewer.layoutParams.height = Utility.getScreenWidth(requireActivity())
        } else {
            _rvViewer.layoutParams.width = Utility.getScreenWidth(requireActivity())
            _rvViewer.layoutParams.height = Utility.getScreenHeight(requireActivity())
        }
        _rvViewer.layoutManager = LinearLayoutManager(requireActivity())
        //        _rvViewer.setHasFixedSize(true);
        _rvViewer.layoutDirection = View.LAYOUT_DIRECTION_LTR
        _fastScroller = requireActivity().findViewById<FastScroller>(R.id.fastscroll)
        _fastScroller.setRecyclerView(_rvViewer)
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        _rvMenu = requireActivity().findViewById<RecyclerView>(R.id.rvMenu)
        _rvMenu.setHasFixedSize(true)
        _rvMenu.layoutManager = layoutManager
//        _menuLayout = requireActivity().findViewById<LinearLayout>(R.id.viewerMenuLayout)
//        if (ContextCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) ActivityCompat.requestPermissions(
//            requireActivity(), arrayOf(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ), 1
//        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun openAttachment(
        attachment: AttachmentModel,
        ctsDocumentId: Int,
        ctsTransferID: Int
    ) {

        currentAttachment = attachment

        try {

            if (attachment.pagesCount <= 0) {
                if (attachment.getVersion() == null) {
                    sendRequestToFetchFileVersions(
                        ctsDocumentId,
                        ctsTransferID, false, attachment.id.substringAfter("_")
                    )
                    return
                }
                autoDispose.add(
                    viewModel.getViewerDocumentDetails(
                        ctsDocumentId.toString(),
                        ctsTransferID.toString(),
                        false, attachment.id.substringAfter("_"),
                        attachment.version
                    ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            // dialog!!.dismiss()

                            attachment.setPagesCount(it.pagesCount)
                            attachment.setVersion(it.versionCode)
                            attachment.annotations = it.annotations
                            attachment.isCheckout = it.checkedOut

                            openAttachment(attachment, ctsDocumentId, ctsTransferID)
//                            Log.e("xxxxxxxxxxxxxxaaaaaaaa", attachment.annotations[0].type)


                        },
                        {
                            Timber.e(it)
                            _downloadProgress.visibility = View.GONE

                        })
                )
            } else {
                PDFConfig.attachment = currentAttachment
//            title.setText(com.intalio.cts.ctsproduct.ViewerActivity.currentAttachment.text)
                updateBottomMenu()
                _fastScroller.setRecyclerView(_rvViewer)

                //      viewerAdapter = ViewerAdapter(attachment.annotations, false, this)

//                _rvViewer.adapter = viewerAdapter

//                viewerAdapter.addImages(
//                    requireContext(),
//                    attachment.id.substringAfter("_"),
//                    attachment.version,
//                    attachment.pagesCount,
//                    viewModel.readTokenData()!!.accessToken
//                )
                _downloadProgress.visibility = View.GONE

            }


        } catch (e: Exception) {
            Log.e("xxxxxxxxxxxxxxaaaaaaaa", e.message!!)
            e.printStackTrace()
        }

    }


    private fun updateBottomMenu() {

        _menuLayout.visibility = View.VISIBLE

        _llSave.visibility =
            if (currentAttachment.isCheckout) View.VISIBLE else View.GONE

        val viewerMenuItems: Vector<MenuItem> =
            PDFConfig.initMenu(
                requireContext(),
                currentAttachment,
                "node",
                viewModel.currentLanguage()
            )

        val _menuAdapter = MenuAdapter(requireContext(), viewerMenuItems, this)
        _rvMenu.adapter = _menuAdapter
        PDFConfig.menuAdapter = _menuAdapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getOriginalDocument(
        ctsDocumentId: Int,
        ctsTransferID: String,
        isDraft: Boolean,
        viewMode: Boolean,
        delegationId: Int
    ) {

        var noAttachments = ""

        when {
            viewModel.readLanguage() == "en" -> {

                noAttachments = "No Attachments"

            }
            viewModel.readLanguage() == "ar" -> {
                noAttachments = "لا يوجد ملفات مرفقة"


            }
            viewModel.readLanguage() == "fr" -> {
                noAttachments = "pas de pièces jointes"

            }
        }

        val sharedPref =
            activity?.getSharedPreferences(Constants.ORIGINAL_FILE_PREF, Context.MODE_PRIVATE)


        if (fileId.toCharArray().isNotEmpty()) {
            val isOriginalFile: Boolean

            requireArguments().getBoolean(Constants.FILE_PARENT_ID).let { fileParentId ->

                isOriginalFile = fileParentId
            }
            webviewviewer(
                fileId,
                ctsDocumentId,
                ctsTransferID,
                isDraft,
                isOriginalFile
            )
        } else {

            val originalFileId = sharedPref!!.getString(Constants.ORIGINAL_FILE_ID, "")

            if (!originalFileId.isNullOrEmpty()) {

                webviewviewer(
                    originalFileId,
                    ctsDocumentId,
                    ctsTransferID,
                    isDraft,
                    true
                )
            } else {

                autoDispose.add(viewModel.getOriginalDocument(ctsDocumentId, delegationId)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {

                            for (i in it[0].children!!) {
                                val folder: AttachmentModel = i

                                if (folder.id.equals("folder_originalMail") && folder.children != null && folder.children.size > 0) {

                                    originalMail = folder.children[0]

                                    //                          openAttachment(originalMail, ctsDocumentId, ctsTransferID)

                                    val editor = sharedPref.edit()
                                    editor.putString(
                                        Constants.ORIGINAL_FILE_ID,
                                        originalMail.id.substringAfter("_")
                                    )
                                    editor.apply()
                                    webviewviewer(
                                        originalMail.id.substringAfter("_"),
                                        ctsDocumentId,
                                        ctsTransferID,
                                        isDraft,
                                        true

                                    )

                                } else if (folder.id.equals("folder_originalMail") && folder.children == null) {
                                    webview.visibility = View.GONE
                                    NoAttachments.text = noAttachments
                                    NoAttachments.visibility = View.VISIBLE

                                    _downloadProgress.visibility = View.GONE

                                }
                            }


                        }, {
                            dialog!!.dismiss()
                            Timber.e(it)
                            Log.d("ressssssssssssp", it.toString())


                        })
                )
            }

        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendRequestToFetchFileVersions(
        ctsDocumentId: Int,
        ctsTransferID: Int,
        isDraft: Boolean,
        documentId: String
    ) {


        autoDispose.add(
            viewModel.getViewerDocumentVersions(
                ctsDocumentId.toString(),
                ctsTransferID.toString(),
                isDraft, documentId
            ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    // dialog!!.dismiss()

//                    it.last().VersionNumber
//                    fileVersions = ArrayList()
//                    fileVersions!!.addAll(it.stream().map {
//                            v -> v.VersionNumber.toString()
//                     }.collect(Collectors.toList()))

                    currentAttachment.setVersion(it.last().VersionNumber.toString())
                    openAttachment(currentAttachment, ctsDocumentId, ctsTransferID)


                },
                {
                    Timber.e(it)
                    Log.d("errorfiel", it.toString())
                    _downloadProgress.visibility = View.GONE

                })
        )
    }


    private fun getDocument(documentId: Int, delegationId: Int, Latest_Path: String) {

        autoDispose.add(viewModel.getDocument(documentId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {


                    setRequestedPopUpWindow(it, delegationId,Latest_Path)


                }, {

                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }

    private fun getSearchedDocument(documentId: Int, delegationId: Int) {

        autoDispose.add(viewModel.getSearchDocumentInfo(documentId, delegationId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {


                    setRequestedPopUpWindow(it, delegationId, Latest_Path)


                }, {

                    dialog!!.dismiss()
                    Timber.e(it)


                })
        )


    }

    private fun viewDocument(transferID: Int, delegationId: Int) {
        dialog!!.dismiss()



        viewModel.viewTransfer(transferID, delegationId).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                if (response.code() != 200) {


                    requireActivity().makeToast(getString(R.string.error))

                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                requireActivity().makeToast(t.toString())
            }
        })

    }


    private fun replaceAttachment(userFile: File) {

        val body: MultipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            // 1505356
            //originalMail
            .addFormDataPart("id", originalMail.id.toString())
            .addFormDataPart("documentId", correspondenceModel.documentId!!.toString())
            .addFormDataPart("transferId", correspondenceModel.id!!.toString())
            .addFormDataPart("parentId", originalMail.parentId.toString())
            .addFormDataPart("categoryId", correspondenceModel.categoryId!!.toString())
            .addFormDataPart("delegationId", "")
            .addFormDataPart(
                "",
                userFile.name,
                userFile.asRequestBody("file".toMediaTypeOrNull())

            )
            .build()

        autoDispose.add(
            viewModel.replaceAttachment(body).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {

                    dialog!!.dismiss()
                    if (it.id != "0") {
                        activity?.onBackPressed()
                    }


                },
                {
                    Timber.e(it)
                    dialog!!.dismiss()
                    Log.d("errorfiel", it.toString())

                })
        )
    }

    private fun hideInboxActions() {

        val inflater = requireActivity().applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.menu_layout, null, false)
//        view.findViewById<View>(R.id.link_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.notes_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.nonarchive_attachs_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.visual_tracking_btn).visibility = View.GONE

    }

    private fun setPopUpWindow(
        model: CorrespondenceDataItem,
        delegationId: Int,
        Latest_Path: String
    ) {
        dialog!!.dismiss()
        val inflater = requireActivity().applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.menu_layout, null, false)

        var canDoAction: Boolean = false

        setPopUpLabels(view)



        if (Node_Inherit == "Inbox" && (model.messageLock == "lockedbyme" || model.messageLock == "notlocked")) {

            // Correspondence is not lock or locked by me
            canDoAction = true
            if (model.messageLock == "notlocked") {
                view.findViewById<View>(R.id.unlock_btn).visibility = View.GONE
                view.findViewById<View>(R.id.complete_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.reply_user_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.reply_structure_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.transfer_btn).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.unlock_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.complete_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.reply_user_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.reply_structure_btn).visibility = View.VISIBLE
                view.findViewById<View>(R.id.transfer_btn).visibility = View.VISIBLE
            }




        }
        else {


            canDoAction = false
            view.findViewById<View>(R.id.unlock_btn).visibility = View.GONE
            view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
            view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
            view.findViewById<View>(R.id.transfer_btn).visibility = View.GONE
            view.findViewById<View>(R.id.complete_btn).visibility = View.GONE




            if (model.messageLock == "cced") {
                view.findViewById<View>(R.id.dismis_copy_btn).visibility = View.VISIBLE


            } else
                if (model.messageLock == "broadcastlockedbyme") {
                    view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
                    if (model.isexternalbroadcast) {
                        view.findViewById<View>(R.id.transfer_btn).visibility = View.VISIBLE
                        view.findViewById<View>(R.id.reply_user_btn).visibility = View.VISIBLE
                        view.findViewById<View>(R.id.reply_structure_btn).visibility = View.VISIBLE
                    } else {
                        view.findViewById<View>(R.id.transfer_btn).visibility = View.GONE
                        view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
                        view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
                    }
                    view.findViewById<View>(R.id.complete_btn).visibility = View.VISIBLE
                    view.findViewById<View>(R.id.unlock_btn).visibility = View.VISIBLE

                    canDoAction = true

                } else if (model.messageLock == "broadcastnotlocked") {
                    view.findViewById<View>(R.id.unlock_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.transfer_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.complete_btn).visibility = View.VISIBLE
                    canDoAction = false
                    if (model.isexternalbroadcast) {
                        view.findViewById<View>(R.id.transfer_btn).visibility = View.VISIBLE
                        view.findViewById<View>(R.id.reply_user_btn).visibility = View.VISIBLE
                        view.findViewById<View>(R.id.reply_structure_btn).visibility = View.VISIBLE
                        canDoAction = true
                    } else {
                        view.findViewById<View>(R.id.transfer_btn).visibility = View.GONE
                        view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
                        view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
                        canDoAction = false
                    }


                } else {
                    canDoAction = false
                    view.findViewById<View>(R.id.unlock_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.reply_user_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.reply_structure_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.transfer_btn).visibility = View.GONE
                    view.findViewById<View>(R.id.complete_btn).visibility = View.GONE

                }
        }


        if (Latest_Path == "inbox node" && CandoAction){
            canDoAction = true
            view.findViewById<View>(R.id.unlock_btn).visibility = View.VISIBLE
            view.findViewById<View>(R.id.complete_btn).visibility = View.VISIBLE
            view.findViewById<View>(R.id.reply_user_btn).visibility = View.VISIBLE
            view.findViewById<View>(R.id.reply_structure_btn).visibility = View.VISIBLE
            view.findViewById<View>(R.id.transfer_btn).visibility = View.VISIBLE
        }

        view.findViewById<View>(R.id.metadat_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    MetaDataFragment().apply {

                        arguments = bundleOf(
                            Pair(Constants.TRANSFER_ID, model.id),
                            Pair(Constants.LATEST_PATH, Latest_Path)

                            )
                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                addToBackStack("")
            }

        }
        view.findViewById<View>(R.id.link_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(
                    R.id.fragmentContainer,
                    LinkedCorrespondenceFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.DOCUMENT_ID, model.documentId),
                            Pair(Constants.TRANSFER_ID, model.id),
                            Pair(Constants.CANDOACTION, canDoAction)
                        )


                    }, "LinkedC"
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.mytransfers_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    MyTransferFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.TRANSFER_ID, model.id),
                            Pair(Constants.LOCKED_BY, model.lockedBy),
                            Pair(Constants.LOCKED_BY_Delegator, model.lockedByDelegatedUser),
                            Pair(Constants.LOCKED_DATE, model.lockedDate)
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.notes_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AllNotesFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.documentId
                            ),
                            Pair(
                                Constants.TRANSFER_ID,
                                model.id
                            ),
                            Pair(
                                Constants.CANDOACTION,
                                canDoAction
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }




        view.findViewById<View>(R.id.reply_user_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    ReplyToUserFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.Correspondence_Model, model
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.reply_structure_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    ReplyToStructureFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.Correspondence_Model, model
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.transfer_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AddTransferFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.Correspondence_Model, model
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }
        view.findViewById<View>(R.id.unlock_btn).setOnClickListener {
            popupWindow.dismiss()

            unlockTransfer(model.id!!, delegationId)

        }
        view.findViewById<View>(R.id.dismis_copy_btn).setOnClickListener {

            popupWindow.dismiss()
            val transferId = arrayOf(model.id)
            dismissTransferCopy(transferId, delegationId)
        }
        view.findViewById<View>(R.id.complete_btn).setOnClickListener {
            popupWindow.dismiss()
            val transferId = arrayOf(model.id)
            completeTransfer(transferId, delegationId)


        }

        view.findViewById<View>(R.id.show_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.Correspondence_Model, model
                            ),
                            Pair(
                                Constants.CANDOACTION, canDoAction
                            ),
                            Pair(
                                Constants.VIEWMODE, viewMode
                            )
                        )
                    }
                )
                addToBackStack("")

            }

        }
        view.findViewById<View>(R.id.nonarchive_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    NonArchAttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.documentId
                            ),
                            Pair(
                                Constants.TRANSFER_ID,
                                model.id
                            ),
                            Pair(
                                Constants.CANDOACTION, canDoAction
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.transferhistory_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    TransferHistoryFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.documentId
                            ),
                            Pair(
                                Constants.TRANSFER_ID,
                                model.id
                            )
                        )
                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }
        view.findViewById<View>(R.id.visual_tracking_btn).setOnClickListener {
            popupWindow.dismiss()
            dialog = requireContext().launchLoadingDialog()

            autoDispose.add(viewModel.getVisualTracking(model.documentId!!, delegationId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        dialog!!.dismiss()

                        val intent = Intent(requireActivity(), BuchheimWalkerActivity::class.java)
                        val bundle = Bundle()
                        val modell = VisualTrackingResponse()
                        modell.visualTrackingResponse = it

                        bundle.putSerializable(Constants.TRACKING_MODEL, modell)
                        bundle.putSerializable(
                            Constants.STRUCTURE_MODEL,
                            viewModel.readAllStructureData()
                        )
                        bundle.putString(Constants.CURRENT_LANG, viewModel.readLanguage())
//                        bundle.putSerializable(Constants.TRANSLATION_MODEL, viewModel.readDictionary()!!.data!!)
                        intent.putExtras(bundle)
                        startActivity(intent)


                    }, {

                        dialog!!.dismiss()
                        Timber.e(it)


                    })
            )


        }


        val x = (resources.displayMetrics.widthPixels * 0.80).toInt()
        popupWindow = PopupWindow(view, x, LinearLayout.LayoutParams.WRAP_CONTENT, true)
    }


    private fun setPopUpLabels(view: View) {


        when {
            viewModel.readLanguage() == "en" -> {


                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.en
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.en
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.en
                view.findViewById<TextView>(R.id.mytransfertxt).text =
                    translator.find { it.keyword == "MyTransfer" }!!.en
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.en
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.en
                view.findViewById<TextView>(R.id.replytousertxt).text =
                    translator.find { it.keyword == "ReplyToUser" }!!.en
                view.findViewById<TextView>(R.id.replytostructuretxt).text =
                    translator.find { it.keyword == "ReplyToStructure" }!!.en
                view.findViewById<TextView>(R.id.transfertxt).text =
                    translator.find { it.keyword == "Transfer" }!!.en
                view.findViewById<TextView>(R.id.unlocktxt).text =
                    translator.find { it.keyword == "Unlock" }!!.en
                view.findViewById<TextView>(R.id.dismisscoppytxt).text =
                    translator.find { it.keyword == "DismissCopy" }!!.en
                view.findViewById<TextView>(R.id.completetxt).text =
                    translator.find { it.keyword == "Complete" }!!.en
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.en
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.en
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.en
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.en
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.en
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.en
            }
            viewModel.readLanguage() == "ar" -> {
                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.ar
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.ar
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.ar
                view.findViewById<TextView>(R.id.mytransfertxt).text =
                    translator.find { it.keyword == "MyTransfer" }!!.ar
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.ar
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.ar
                view.findViewById<TextView>(R.id.replytousertxt).text =
                    translator.find { it.keyword == "ReplyToUser" }!!.ar
                view.findViewById<TextView>(R.id.replytostructuretxt).text =
                    translator.find { it.keyword == "ReplyToStructure" }!!.ar
                view.findViewById<TextView>(R.id.transfertxt).text =
                    translator.find { it.keyword == "Transfer" }!!.ar
                view.findViewById<TextView>(R.id.unlocktxt).text =
                    translator.find { it.keyword == "Unlock" }!!.ar
                view.findViewById<TextView>(R.id.dismisscoppytxt).text =
                    translator.find { it.keyword == "DismissCopy" }!!.ar
                view.findViewById<TextView>(R.id.completetxt).text =
                    translator.find { it.keyword == "Complete" }!!.ar
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.ar
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.ar
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.ar
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.ar
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.ar
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.fr
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.fr
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.fr
                view.findViewById<TextView>(R.id.mytransfertxt).text =
                    translator.find { it.keyword == "MyTransfer" }!!.fr
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.fr
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.fr
                view.findViewById<TextView>(R.id.replytousertxt).text =
                    translator.find { it.keyword == "ReplyToUser" }!!.fr
                view.findViewById<TextView>(R.id.replytostructuretxt).text =
                    translator.find { it.keyword == "ReplyToStructure" }!!.fr
                view.findViewById<TextView>(R.id.transfertxt).text =
                    translator.find { it.keyword == "Transfer" }!!.fr
                view.findViewById<TextView>(R.id.unlocktxt).text =
                    translator.find { it.keyword == "Unlock" }!!.fr
                view.findViewById<TextView>(R.id.dismisscoppytxt).text =
                    translator.find { it.keyword == "DismissCopy" }!!.fr
                view.findViewById<TextView>(R.id.completetxt).text =
                    translator.find { it.keyword == "Complete" }!!.fr
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.fr
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.fr
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.fr
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.fr
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.fr
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.fr

            }
        }

    }

    private fun setSearchPopUpWindow(model: AdvancedSearchResponseDataItem, delegationId: Int, latestPath:String) {
        dialog!!.dismiss()

        val inflater = requireActivity().applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.searchmenu_layout, null, false)

        setSearchPopUpLabels(view)




        view.findViewById<View>(R.id.metadat_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    MetaDataFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.TRANSFER_ID, model.id),
                            Pair(Constants.LATEST_PATH, latestPath)
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.link_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(
                    R.id.fragmentContainer,
                    LinkedCorrespondenceFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.DOCUMENT_ID, model.id),
                        )


                    }, "LinkedC"
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.notes_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AllNotesFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            ),
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.show_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.Correspondence_Model, model)
                        )
                    }
                )
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.nonarchive_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    NonArchAttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.transferhistory_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    TransferHistoryFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            ),

                            )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.visual_tracking_btn).setOnClickListener {
            popupWindow.dismiss()
            dialog = requireContext().launchLoadingDialog()

            autoDispose.add(viewModel.getVisualTracking(model.id!!, delegationId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        dialog!!.dismiss()

                        val intent = Intent(requireActivity(), BuchheimWalkerActivity::class.java)
                        val bundle = Bundle()
                        val modell = VisualTrackingResponse()
                        modell.visualTrackingResponse = it

                        bundle.putSerializable(Constants.TRACKING_MODEL, modell)
                        bundle.putSerializable(
                            Constants.STRUCTURE_MODEL,
                            viewModel.readAllStructureData()
                        )
//                        bundle.putSerializable(Constants.TRANSLATION_MODEL, viewModel.readDictionary()!!.data!!)

                        bundle.putString(Constants.CURRENT_LANG, viewModel.readLanguage())
                        intent.putExtras(bundle)
                        startActivity(intent)


                    }, {

                        dialog!!.dismiss()
                        Timber.e(it)


                    })
            )


        }
//        view.findViewById<View>(R.id.link_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.notes_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.nonarchive_attachs_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.visual_tracking_btn).visibility = View.GONE

        //  hideActions()

        val x = (resources.displayMetrics.widthPixels * 0.80).toInt()
        popupWindow = PopupWindow(view, x, LinearLayout.LayoutParams.WRAP_CONTENT, true)
    }

    private fun setRequestedPopUpWindow(
        model: MetaDataResponse,
        delegationId: Int,
        Latest_Path: String
    ) {
        dialog!!.dismiss()


        val inflater = requireActivity().applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.searchmenu_layout, null, false)
        setSearchPopUpLabels(view)


        view.findViewById<View>(R.id.metadat_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    MetaDataFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.TRANSFER_ID, model.id),
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.LATEST_PATH, Latest_Path),

                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.link_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(
                    R.id.fragmentContainer,
                    LinkedCorrespondenceFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.DOCUMENT_ID, model.id),
                        )


                    }, "LinkedC"
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.notes_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AllNotesFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            ),
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.show_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    AttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(Constants.Correspondence_Model, model)
                        )
                    }
                )
                addToBackStack("")

            }

        }

        view.findViewById<View>(R.id.nonarchive_attachs_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    NonArchAttachmentsFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.NODE_INHERIT, Node_Inherit),
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            )
                        )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.transferhistory_btn).setOnClickListener {
            popupWindow.dismiss()
            (activity as AppCompatActivity).supportFragmentManager.commit {
                add(R.id.fragmentContainer,
                    TransferHistoryFragment().apply {
                        arguments = bundleOf(
                            Pair(
                                Constants.DOCUMENT_ID,
                                model.id
                            ),

                            )


                    }
                ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack("")

            }
        }

        view.findViewById<View>(R.id.visual_tracking_btn).setOnClickListener {
            popupWindow.dismiss()
            dialog = requireContext().launchLoadingDialog()

            autoDispose.add(viewModel.getVisualTracking(model.id!!, delegationId)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        dialog!!.dismiss()

                        val intent = Intent(requireActivity(), BuchheimWalkerActivity::class.java)
                        val bundle = Bundle()
                        val modell = VisualTrackingResponse()
                        modell.visualTrackingResponse = it

                        bundle.putSerializable(Constants.TRACKING_MODEL, modell)
                        bundle.putSerializable(
                            Constants.STRUCTURE_MODEL,
                            viewModel.readAllStructureData()
                        )
//                        bundle.putSerializable(Constants.TRANSLATION_MODEL, viewModel.readDictionary()!!.data!!)

                        bundle.putString(Constants.CURRENT_LANG, viewModel.readLanguage())
                        intent.putExtras(bundle)
                        startActivity(intent)


                    }, {

                        dialog!!.dismiss()
                        Timber.e(it)


                    })
            )


        }

//        view.findViewById<View>(R.id.link_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.notes_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.nonarchive_attachs_btn).visibility = View.GONE
//        view.findViewById<View>(R.id.visual_tracking_btn).visibility = View.GONE

        // hideActions()
        val x = (resources.displayMetrics.widthPixels * 0.80).toInt()
        popupWindow = PopupWindow(view, x, LinearLayout.LayoutParams.WRAP_CONTENT, true)
    }


    private fun setSearchPopUpLabels(view: View) {

        translator = viewModel.readDictionary()!!.data!!
        when {
            viewModel.readLanguage() == "en" -> {
                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.en
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.en
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.en
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.en
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.en
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.en
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.en
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.en
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.en
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.en
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.en
            }
            viewModel.readLanguage() == "ar" -> {
                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.ar
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.ar
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.ar
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.ar
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.ar
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.ar
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.ar
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.ar
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.ar
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.ar
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.ar

            }

            viewModel.readLanguage() == "fr" -> {
                view.findViewById<TextView>(R.id.attrutestring).text =
                    translator.find { it.keyword == "Attributes" }!!.fr
                view.findViewById<TextView>(R.id.attributestxt).text =
                    translator.find { it.keyword == "Attributes" }!!.fr
                view.findViewById<TextView>(R.id.linkedctxt).text =
                    translator.find { it.keyword == "LinkedCorrespondences" }!!.fr
                view.findViewById<TextView>(R.id.actionstring).text =
                    translator.find { it.keyword == "Actions" }!!.fr
                view.findViewById<TextView>(R.id.notestxt).text =
                    translator.find { it.keyword == "Notes" }!!.fr
                view.findViewById<TextView>(R.id.attachmentsstring).text =
                    translator.find { it.keyword == "Attachments" }!!.fr
                view.findViewById<TextView>(R.id.attachmentstxt).text =
                    translator.find { it.keyword == "Attachments" }!!.fr
                view.findViewById<TextView>(R.id.nonarchtxt).text =
                    translator.find { it.keyword == "NonArchivedAttachments" }!!.fr
                view.findViewById<TextView>(R.id.transferhistorystring).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.fr
                view.findViewById<TextView>(R.id.transferhistorytxt).text =
                    translator.find { it.keyword == "TransfersHistory" }!!.fr
                view.findViewById<TextView>(R.id.visualtrackingtxt).text =
                    translator.find { it.keyword == "VisualTracking" }!!.fr

            }
        }

    }


    private fun unlockTransfer(transferID: Int, delegationId: Int) {

        var fileInUSe = ""
        var originalDocumentInUse = ""
        var unlockConfirmMessage = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.en!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.en!!
                unlockConfirmMessage = translator.find { it.keyword == "UnlockConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.ar!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.ar!!
                unlockConfirmMessage = translator.find { it.keyword == "UnlockConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                fileInUSe = translator.find { it.keyword == "FileInUse" }!!.fr!!
                originalDocumentInUse = translator.find { it.keyword == "OriginalFileInUse" }!!.fr!!
                unlockConfirmMessage = translator.find { it.keyword == "UnlockConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }


        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(unlockConfirmMessage)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()

                    dialog = requireContext().launchLoadingDialog()


                    viewModel.unlockTransfer(
                        transferID,
                        delegationId

                    ).enqueue(object : Callback<ResponseBody> {


                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            dialog!!.dismiss()

                            try {

                                var responseRecieved: Any? = null
                                responseRecieved = response.body()!!.string()

                                if (responseRecieved.toString().isEmpty()) {
                                    activity!!.onBackPressed()

                                } else {
                                    if (responseRecieved.toString() == "FileInUse") {
                                        requireActivity().makeToast(fileInUSe)
                                    } else if (responseRecieved.toString() == "OriginalFileInUse") {
                                        requireActivity().makeToast(originalDocumentInUse)

                                    }
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            dialog!!.dismiss()

                            requireActivity().makeToast(requireActivity().getString(R.string.network_error))
                        }

                    }

                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()


                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    private fun completeTransfer(transferID: Array<Int?>, delegationId: Int) {

        var noOriginalMail = ""
        var lockedbyUser = ""
        var mailCheckedOut = ""
        var completeConfirm = ""
        var yes = ""
        var no = ""
        var completed = ""



        when {
            viewModel.readLanguage() == "en" -> {

                noOriginalMail =
                    translator.find { it.keyword == "CorrespondenceNotCompleteNoOriginalMail" }!!.en!!
                lockedbyUser = translator.find { it.keyword == "HasLockedAttachmentsByUser" }!!.en!!
                mailCheckedOut =
                    translator.find { it.keyword == "OriginalDocumentLockedByUser" }!!.en!!
                completeConfirm =
                    translator.find { it.keyword == "CompleteOneTsfConfirmation" }!!.en!!

                completed = translator.find { it.keyword == "Completed" }!!.en!!

                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {

                noOriginalMail =
                    translator.find { it.keyword == "CorrespondenceNotCompleteNoOriginalMail" }!!.ar!!
                lockedbyUser = translator.find { it.keyword == "HasLockedAttachmentsByUser" }!!.ar!!
                mailCheckedOut =
                    translator.find { it.keyword == "OriginalDocumentLockedByUser" }!!.ar!!
                completeConfirm =
                    translator.find { it.keyword == "CompleteOneTsfConfirmation" }!!.ar!!
                completed = translator.find { it.keyword == "Completed" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {


                noOriginalMail =
                    translator.find { it.keyword == "CorrespondenceNotCompleteNoOriginalMail" }!!.fr!!
                lockedbyUser = translator.find { it.keyword == "HasLockedAttachmentsByUser" }!!.fr!!
                mailCheckedOut =
                    translator.find { it.keyword == "OriginalDocumentLockedByUser" }!!.fr!!
                completeConfirm =
                    translator.find { it.keyword == "CompleteOneTsfConfirmation" }!!.fr!!
                completed = translator.find { it.keyword == "Completed" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }
        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(completeConfirm)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    dialog = requireContext().launchLoadingDialog()

                    autoDispose.add(
                        viewModel.completeTransfer(transferID, delegationId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    dialog!!.dismiss()

                                    if (it[0].updated == true) {
                                        if (it[0].uncompletedDocumentReferenceNumber.isNullOrEmpty()){
                                            requireActivity().makeToast(completed)
                                            requireActivity().onBackPressed()
                                        }else{
                                            completeConfirmation()
                                        }


                                    } else if (it[0].updated == false &&
                                        it[0].documentAttachmentIdHasValue == false &&
                                        it[0].message == null
                                    ) {
                                        requireActivity().makeToast(noOriginalMail)

                                    } else if (it[0].updated == false &&
                                        it[0].documentAttachmentIdHasValue == false &&
                                        it[0].message == "HasLockedAttachmentsByUser"
                                    ) {
                                        requireActivity().makeToast(lockedbyUser)
                                    } else if (it[0].updated == false &&
                                        it[0].documentAttachmentIdHasValue == false &&
                                        it[0].message == "OriginalDocumentLockedByUser"
                                    ) {
                                        requireActivity().makeToast(mailCheckedOut)
                                    }

                                },
                                {
                                    Timber.e(it)
                                    dialog!!.dismiss()
                                    requireActivity().makeToast(getString(R.string.error))
                                })
                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()

                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    private fun dismissTransferCopy(transferID: Array<Int?>, delegationId: Int) {
//DismissCarbonCopyOneTsfConfirmation
        var DismissCarbonCopyOneTsfConfirmation = ""
        var yes = ""
        var no = ""
        when {
            viewModel.readLanguage() == "en" -> {

                DismissCarbonCopyOneTsfConfirmation =
                    translator.find { it.keyword == "DismissCarbonCopyOneTsfConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!

            }
            viewModel.readLanguage() == "ar" -> {
                DismissCarbonCopyOneTsfConfirmation =
                    translator.find { it.keyword == "DismissCarbonCopyOneTsfConfirmation" }!!.ar!!

                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!
            }
            viewModel.readLanguage() == "fr" -> {
                DismissCarbonCopyOneTsfConfirmation =
                    translator.find { it.keyword == "DismissCarbonCopyOneTsfConfirmation" }!!.fr!!

                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }
        val width = (requireActivity().resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(DismissCarbonCopyOneTsfConfirmation)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    dialog = requireContext().launchLoadingDialog()

                    autoDispose.add(
                        viewModel.transferDismissCopy(transferID, delegationId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    dialog!!.dismiss()

                                    if (it[0].updated == true) {
                                        requireActivity().onBackPressed()

                                    } else {
                                        requireActivity().makeToast(getString(R.string.serverError))

                                    }

                                },
                                {
                                    Timber.e(it)
                                    dialog!!.dismiss()
                                    requireActivity().makeToast(getString(R.string.serverError))

                                })
                    )
                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()


                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    private fun completeConfirmation() {
        val customDialog = Dialog(requireContext(), R.style.ConfirmationStyle)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setCancelable(false)
        customDialog.setContentView(R.layout.transferr_confirem)



        when {
            viewModel.readLanguage() == "en" -> {

                customDialog.findViewById<Button>(R.id.complete_confirm_button).text =
                    translator.find { it.keyword == "OK" }!!.en
                customDialog.findViewById<TextView>(R.id.completemessage).text =
                    translator.find { it.keyword == "TransferWasCompletedNotLastOpenTransfer" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {


                customDialog.findViewById<Button>(R.id.complete_confirm_button).text =
                    translator.find { it.keyword == "OK" }!!.ar
                customDialog.findViewById<TextView>(R.id.completemessage).text =
                    translator.find { it.keyword == "TransferWasCompletedNotLastOpenTransfer" }!!.ar
            }
            viewModel.readLanguage() == "fr" -> {


                customDialog.findViewById<Button>(R.id.complete_confirm_button).text =
                    translator.find { it.keyword == "OK" }!!.fr
                customDialog.findViewById<TextView>(R.id.completemessage).text =
                    translator.find { it.keyword == "TransferWasCompletedNotLastOpenTransfer" }!!.fr

            }
        }
        customDialog.findViewById<Button>(R.id.complete_confirm_button).setOnClickListener {
            customDialog.dismiss()
            requireActivity().onBackPressed()


        }
        customDialog.show()

    }

    fun editAnnotationPDF(annotation: ViewerAnnotationModel, index: Int) {
//        try {
//            val annotations: ArrayList<ViewerAnnotationModel> = getAnnotations()
//            for (viewerAnnotation in annotations) {
//                if (viewerAnnotation.pageNumber == index + 1 && viewerAnnotation.id.equals(
//                        annotation.id
//                    )
//                ) {
//                    annotations.remove(viewerAnnotation)
//                    annotations.add(annotation)
//                    break
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(javaClass.simpleName, e.message!!)
//            e.printStackTrace()
//        }
    }

//    private fun getAnnotations(): ArrayList<ViewerAnnotationModel> {
////        Log.d("annotationssssss", viewerAdapter.annotations.size.toString());
////        var viewerAdapter = null
////        return viewerAdapter.annotations
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun webviewviewer(
        documentId: String,
        ctsDocumentId: Int,
        ctsTransferID: String,
        isDraft: Boolean,
        isOriginal : Boolean

    ) {


        var result = ""
        if (!viewMode) {
            result = "view"
        }

        val settingss: ArrayList<ParamSettingsResponseItem> = viewModel.readSettings()
        val attachmentEditable = settingss.find { it.keyword == "AttachmentEditable" }!!.content

        var url = ""
        if (attachmentEditable == "false" && !isOriginal){

             url =
                "${Constants.VIEWER_URL}/templates/?documentId=${documentId}&language=${viewModel.currentLanguage()}" +
                        "&token=${viewModel.readTokenData()!!.accessToken}&version=autocheck&ctsDocumentId=${ctsDocumentId}" +
                        "&ctsTransferId=${ctsTransferID}&delegationId=null&isDraft=${isDraft}&viewermode=view"


        }else{
             url =
                "${Constants.VIEWER_URL}/templates/?documentId=${documentId}&language=${viewModel.currentLanguage()}" +
                        "&token=${viewModel.readTokenData()!!.accessToken}&version=autocheck&ctsDocumentId=${ctsDocumentId}" +
                        "&ctsTransferId=${ctsTransferID}&delegationId=null&isDraft=${isDraft}&viewermode=${result}"


        }



        webView = requireActivity().findViewById(R.id.webview)
        webView.performContextClick()
        webView.isFocusable = true
        webView.isFocusableInTouchMode = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY


        // Get the web view settings instance
        val settings = webView.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        // Enable and setup web view cache
//    settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        //  settings.setAppCachePath(cacheDir.path)


        // Enable zooming in web view
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;


        // Zoom web view text
        settings.textZoom = 100

        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true


        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }
        //settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false


        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

        // WebView settings
        webView.fitsSystemWindows = true


        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        // Set web view client
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url)

                val script = "function touchHandler(event)\n" +
                        "{\n" +
                        "    var touches = event.changedTouches,\n" +
                        "        first = touches[0],\n" +
                        "        type = \"\";\n" +
                        "    switch(event.type)\n" +
                        "    {\n" +
                        "        case \"touchstart\": type = \"mousedown\"; break;\n" +
                        "        case \"touchmove\":  type=\"mousemove\"; break;        \n" +
                        "        case \"touchend\":   type=\"mouseup\"; break;\n" +
                        "        default: return;\n" +
                        "    }\n" +
                        "    // initMouseEvent(type, canBubble, cancelable, view, clickCount,\n" +
                        "    //                screenX, screenY, clientX, clientY, ctrlKey,\n" +
                        "    //                altKey, shiftKey, metaKey, button, relatedTarget);\n" +
                        "    var simulatedEvent = document.createEvent(\"MouseEvent\");\n" +
                        "    simulatedEvent.initMouseEvent(type, true, true, window, 1,\n" +
                        "                              first.screenX, first.screenY,\n" +
                        "                              first.clientX, first.clientY, false,\n" +
                        "                              false, false, false, 0/*left*/, null);\n" +
                        "    first.target.dispatchEvent(simulatedEvent);\n" +
                        "    event.preventDefault();\n" +
                        "}\n" +
                        "function init()\n" +
                        "{\n" +
                        "    const element = document.getElementById(\"internalPageDiv\");\n" +
                        "    element.addEventListener(\"touchstart\", touchHandler, true);\n" +
                        "    element.addEventListener(\"touchmove\", touchHandler, true);\n" +
                        "    element.addEventListener(\"touchend\", touchHandler, true);\n" +
                        "    element.addEventListener(\"touchcancel\", touchHandler, true);    \n" +
                        "}\n" +
                        "init();"

                view.evaluateJavascript(script, null);

            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // view!!.performContextClick()


            }
        }

        webView.loadUrl(url);

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

//            Constants.ADD_NAME_CONTENT_VIEWER -> if (resultCode == Activity.RESULT_OK) {
//                val content = data!!.getStringExtra("content")
//                PDFConfig.setDrawType(PDFConfig.AnnotationType.text)
//                PDFConfig.noteText = content
//            }
            Constants.SELECT_SIGNATURE_TEMPLATE -> if (resultCode == Activity.RESULT_OK) {
                PDFConfig.setDrawType(PDFConfig.AnnotationType.AUTOMATIC_SIGNATURE)
                PDFConfig.base64ToDraw = data!!.getStringExtra("image")
                PDFConfig.signatureTemplateId = data.getIntExtra("id", 0)
            }

//
            HAND_NOTE_SIGN_RESULT -> if (resultCode == Activity.RESULT_OK) {
                Log.d("signatureData", resultCode.toString())

                PDFConfig.base64ToDraw =
                    data!!.getStringExtra("noteData")!!.replace("\n".toRegex(), "")
            } else {
                Log.d("signatureData", resultCode.toString())

                PDFConfig.resetDrawType()
            }
            Constants.UNSIGN_DOCUMENT_RESULT -> if (resultCode == Activity.RESULT_OK) {
            }
//            Constants.DISMISS_RESULT -> if (resultCode == Activity.RESULT_OK) {
//                if (Utility.getLocalData(this, "Offline").equals("Off")) {
//                    sendRequestToDismissTransfer()
//                } else {
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    val inboxId = Utility.getLocalData(this, "currentInbox")
//                    intent.putExtra("location", inboxId)
//                    startActivity(intent)
//                }
//            }
//            Constants.ANNOTATION_PERMISSION_RESULT -> if (resultCode == Activity.RESULT_OK) {
//                val annotation = data!!.getSerializableExtra("Annotation") as ViewerAnnotationModel?
//                viewerAdapter.notifyItemChanged(annotation!!.pageNumber - 1)
//                editAnnotationPDF(annotation!!, annotation!!.pageNumber - 1)
//            }
//            PDFConfig.ADD_NOTE_TO_VIEWER -> if (resultCode == Activity.RESULT_OK) {
//                val annotation = data!!.getSerializableExtra("Annotation") as ViewerAnnotationModel?
//                editAnnotationPDF(annotation!!, annotation!!.pageNumber - 1)
//            } else if (resultCode == Constants.STICKY_NOTE_PROPERTIES_RESULT) {
//                if (data!!.getSerializableExtra("Annotation") != null) {
//                    val annotation =
//                        data!!.getSerializableExtra("Annotation") as ViewerAnnotationModel?
//                    if (annotation!!.text == null || annotation!!.text.equals("")) {
//                        deleteAnnotationPDF(annotation)
//                        (PDFConfig.stickerView.parent as ViewGroup).removeView(PDFConfig.stickerView)
//                    }
//                }
//            }
            else -> {}
        }

    }

    fun saveAnnotationToPdf(annotation: ViewerAnnotationModel, all: Boolean) {
//        try {
//            val annotations = getAnnotations()
//            if (all) {
////                for (i in 0 until viewerAdapter.itemCount) {
////                    if (i + 1 != annotation.pageNumber) {
////                        PDFConfig.annotationCounter++
////                        annotations.add(annotation.copy(i + 1))
////                        viewerAdapter.notifyItemChanged(i)
////                    } else annotations.add(annotation)
////                }
//            } else {
//                annotations.add(annotation)
//            }
//        } catch (e: Exception) {
//            Log.e(this.javaClass.simpleName, e.message!!)
//            e.printStackTrace()
//        }
    }

    fun deleteAnnotationPDF(annotation: ViewerAnnotationModel) {
        annotation.isDeleted = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (justLaunched) {
            pdfHeight = _rvViewer.layoutParams.height
            pdfWidth = _rvViewer.layoutParams.width
            justLaunched = false
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Utility.isLandScape = true
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Utility.isLandScape = false
        }

//        _rvViewer.getLayoutParams().height = pdfHeight;
        _rvViewer.layoutParams.width = pdfWidth
        _rvViewer.layoutParams.height = pdfHeight
//        viewerAdapter.notifyDataSetChanged()
    }

    fun saveHandwrittenAnnotation(annotation: ViewerAnnotationModel?) {
//        RetrofitManager.getViewerService()
//            .saveHandwrittenAnnotation(HandwrittenAnnotationRequestModel(annotation))
//            .enqueue(object : Callback<ResponseBody?> {
//                override fun onResponse(
//                    call: Call<ResponseBody?>,
//                    response: Response<ResponseBody?>
//                ) {
//                    if (response.body() != null && response.code() == 200) {
//                        val annotations = getAnnotations()
//                        for (annotation in annotations) {
//                            if (annotation.type.equals(PDFConfig.AnnotationType.handwriting.name) && annotation.imageSource == null) {
//                                try {
//                                    annotation.imageSource =
//                                        "/" + Utility.getViewerAppName(requireContext())
//                                            .toString() + "/api/" + response.body()!!
//                                            .string()
//                                } catch (e: IOException) {
//                                    Utility.showMainAlertServer(
//                                        requireContext(),
//                                        R.string.saveAnnotations,
//                                        getString(R.string.serverError),
//                                        R.string.ok
//                                    )
//                                }
//                            }
//                        }
//                    } else Utility.showMainAlertServer(
//                        requireContext(),
//                        R.string.saveAnnotations,
//                        getString(R.string.serverError),
//                        R.string.ok
//                    )
//                }
//
//                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                    Utility.showMainAlertServer(
//                        requireContext(),
//                        R.string.saveAnnotations,
//                        if (t.message != null) t.message else getString(R.string.serverError),
//                        R.string.ok
//                    )
//                }
//            })

    }

    fun openSignatureTemplate() {
        val i = Intent(requireActivity(), SignatureTemplateDialog::class.java)
        startActivityForResult(i, Constants.SELECT_SIGNATURE_TEMPLATE)
    }



}