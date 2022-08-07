package intalio.cts.mobile.android.ui.fragment.uploadattachment

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cts.mobile.android.R
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.fragment_uploadattachment.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import android.graphics.Bitmap

import android.content.Context

import android.content.Intent

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File

import android.provider.MediaStore.Images
import android.util.Log
import com.squareup.okhttp.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody

import okhttp3.RequestBody.Companion.asRequestBody


class UploadAttachmentFragment : Fragment() {


    private lateinit var userFileBytes: ByteArray
    private var fileName: String = ""
    private var userFile:  File? = null
    private var fileExtension: String = ""
    private var folderId :String = ""

    private lateinit var model: CorrespondenceDataItem
    private val cameraRequestId  = 1222

    @Inject
    @field:Named("uploadattachment")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: UploadAttachmentViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(UploadAttachmentViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_uploadattachment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }
        btncaancel.setOnClickListener {
             activity?.onBackPressed()
        }
        xcancel.setOnClickListener {
            ivAttachImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_pdf_img))
            tvFileName.text = getString(R.string.fileName)
            fileName = ""
            fileExtension = ""
        }



        centered_txt.text = requireActivity().getText(R.string.upload)
        val result = arguments?.getSerializable(Constants.Correspondence_Model)
        if (result.toString() != "null") {
            model = result as CorrespondenceDataItem
        }

        requireArguments().getString(Constants.SELECTED_FOLDER_ID).let {
            folderId = it!!

//            if (it == "0"){
//                folderId = ""
//            }else{
//                folderId = it.toString()
//            }


        }


        frameFabGallery.setOnClickListener {

           pickAttachment()

        }


        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            )== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                cameraRequestId
            )

        frameFabCamera.setOnClickListener {

            val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraInt,cameraRequestId)

        }



    }

    private fun pickAttachment() {


        autoDispose.add(
            RxPaparazzo.single(this)
              //  .setMultipleMimeType("image/jpeg", "image/jpg", "image/png", "application/pdf")
                .usingFiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ response ->


                    if (response.resultCode() == AppCompatActivity.RESULT_OK) {

                         when (response.data().file.extension) {
                            "pdf" -> Glide.with(this).load(R.drawable.ic_file_icon).into(ivAttachImage)
                            "jpeg", "jpg", "png", "bmp", "gif" -> Glide.with(this).load(response.data().file).into(ivAttachImage)
                            "doc", "docx", "rtf" -> Glide.with(this).load(R.drawable.word).into(ivAttachImage)
                            "xls", "xlsx", "xlsm" -> Glide.with(this).load(R.drawable.excel).into(ivAttachImage)
                            "ppt", "pptx" -> Glide.with(this).load(R.drawable.powerpoint).into(ivAttachImage)
                            "dwg" -> Glide.with(this).load(R.drawable.autocad).into(ivAttachImage)
                            else -> Glide.with(this).load(R.drawable.unknown).into(ivAttachImage)
                        }


                        userFileBytes = response.data().file.readBytes()
                        fileName = response.data().filename
                        fileExtension = response.data().file.extension
                        userFile = response.data().file
                        tvFileName.text = fileName

                    }else{

                        ivAttachImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.error))
                        tvFileName.text = getString(R.string.error)

                    }

                },{

                    Timber.e(it)
                    requireActivity().makeToast(it.toString())


                }

                )
        )
        btnAttach.setOnClickListener {
            if(fileName == ""){
                requireActivity().makeToast(getString(R.string.requiredField))
            }else{
                dialog = requireActivity().launchLoadingDialog()
                uploadAttachment(userFileBytes,fileName,fileExtension, userFile!!)

            }

        }
    }

    private fun uploadAttachment(fileContent: ByteArray, fileName: String, fileExtension: String,userFile :File) {

        val body: MultipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("documentId", model.documentId!!.toString())
            .addFormDataPart("transferId",  model.id!!.toString())
            .addFormDataPart("parentId",  folderId)
            .addFormDataPart("categoryId", model.categoryId!!.toString())
            .addFormDataPart("delegationId", "")
            .addFormDataPart(
                "",
                userFile.name,
                userFile.asRequestBody("file".toMediaTypeOrNull())

            )
            .build()

        val originalBody: MultipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("documentId", model.documentId!!.toString())
            .addFormDataPart("transferId",  model.id!!.toString())
            .addFormDataPart("categoryId", model.categoryId!!.toString())
            .addFormDataPart("delegationId", "")
            .addFormDataPart(
                "",
                userFile.name,
                userFile.asRequestBody("file".toMediaTypeOrNull())

            )
            .build()

        Log.d("aaaaaaazzz", folderId)


        if (folderId == "0"){
            autoDispose.add(
                viewModel.uploadOriginalAttachment(originalBody).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        dialog!!.dismiss()
                        if (it.id != "0") {
                            activity?.onBackPressed()
                        }


                    },
                    {
                        Timber.e(it)
                        dialog!!.dismiss()
                        Log.d("aaaaaaazzz", it.toString())

                        requireActivity().makeToast(getString(R.string.file_Already_exist))

                    })
            )
        }else{
            autoDispose.add(
                viewModel.uploadAttachment(body).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {

                        dialog!!.dismiss()
                        if (it.id != "0") {
                            activity?.onBackPressed()
                        }


                    },
                    {
                        Timber.e(it)
                        dialog!!.dismiss()
                        Log.d("aaaaaaazzz", it.toString())

                        requireActivity().makeToast(getString(R.string.file_Already_exist))

                    })
            )
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestId){

            if (data !=null){
                val images:Bitmap = data.extras?.get("data") as Bitmap
                ivAttachImage.setImageBitmap(images)
                val byteArrayOutputStream = ByteArrayOutputStream()
                images.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                val tempUri = getImageUri(requireContext(), images)
                val finalFile = File(getRealPathFromURI(tempUri!!))
                fileName = finalFile.name
                fileExtension = finalFile.extension
                userFile = finalFile
                tvFileName.text = fileName

                btnAttach.setOnClickListener {
                    if(fileName == ""){
                        requireActivity().makeToast(getString(R.string.requiredField))
                    }else{

                        dialog = requireActivity().launchLoadingDialog()
                        uploadAttachment(byteArray,fileName,fileExtension, userFile!!)
                    }


                }
            }else{
                ivAttachImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.error))
                tvFileName.text = getString(R.string.error)

            }

        }
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        if (requireActivity().contentResolver != null) {
            val cursor: Cursor =
                requireActivity().contentResolver.query(uri, null, null, null, null)!!
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
            path = cursor.getString(idx)
            cursor.close()
        }
        return path
    }
 }