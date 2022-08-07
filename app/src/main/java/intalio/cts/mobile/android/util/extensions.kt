package intalio.cts.mobile.android.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.Image
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URISyntaxException
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Base64
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout

import androidx.lifecycle.Lifecycle
import com.cts.mobile.android.R


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.math.round


fun Context.makeToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.makeToastList(msgs: List<String>) {
    for (msg in msgs) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.launchActivity(activityClass: Class<*>) {
    startActivity(Intent(this, activityClass))
}

fun Activity.launchActivityFinishCurrent(activityClass: Class<*>) {
    startActivity(Intent(this, activityClass))
    finish()
}


@SuppressLint("MissingPermission")
fun Context.checkInternetConnectivity(): Boolean {
    val manager: ConnectivityManager? =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return manager?.activeNetworkInfo?.isConnected ?: false
}


@Throws(URISyntaxException::class)
fun Uri.toFile(context: Context): File? {
    var uri = this
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
            context,
            uri
        )
    ) {//DocumentsContract.isDocumentUri(context.getApplicationContext(), uri))
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return File(Environment.getExternalStorageDirectory().path + "/" + split[1])
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            uri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("image" == type) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            selection = "_id=?"
            selectionArgs = arrayOf(split[1])
        }
    }
    if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor!!.moveToFirst()) {
                return File(cursor!!.getString(column_index))
            }
        } catch (e: Exception) {
        }

    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return File(uri.path)
    }
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}


fun File.toImagePart(partName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        partName,
        this.name,
        RequestBody.create("image/*".toMediaTypeOrNull(), this)
    )
}


fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Activity.hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

@Throws(java.lang.Exception::class)
fun changeDateFormat(input: String?, output: String?, dateString: String?): String? {
    val inputDate: DateFormat = SimpleDateFormat(input)
    val outputDate: DateFormat = SimpleDateFormat(output)
    val date = inputDate.parse(dateString)
    return outputDate.format(date)
}

fun Lifecycle.lifeCycleDisposable(): AutoDispose {
    return AutoDispose().apply {
        this.bindTo(this@lifeCycleDisposable)
    }
}


fun View.expand() {
    this.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    val targetHeight: Int = this.measuredHeight

    this.layoutParams.height = 1
    this.visibility = View.VISIBLE

    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            this@expand.layoutParams.height = if (interpolatedTime == 1f)
                LinearLayout.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            this@expand.requestLayout()

        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = (targetHeight / this.context.resources.displayMetrics.density).toInt().toLong()
    this.startAnimation(a)

}

fun View.collapse() {
    val initialHeight: Int = this.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                this@collapse.visibility = View.GONE
            } else {
                this@collapse.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = (initialHeight / this.context.resources.displayMetrics.density).toInt().toLong()
    this.startAnimation(a)
}
//
//
//fun ImageView.loadUrl(imageRelativePath: String) {
//    Timber.d("loading image url => ${Constants.STORAGE_URL}$imageRelativePath")
//    Glide.with(this.context).load("${Constants.STORAGE_URL}$imageRelativePath").into(this)
//}
//
//fun ImageView.loadGifUrl(imageRelativePath: String) {
//    Timber.d("loading image url => ${Constants.STORAGE_URL}$imageRelativePath")
//    Glide.with(this.context).asGif().load("${Constants.STORAGE_URL}$imageRelativePath").into(this)
//}


fun Context.launchLoadingDialog(): Dialog {
    return Dialog(this, R.style.FullScreenDialog).apply {
        setContentView(R.layout.dialog_loading)
        show()
    }
}

fun Float.roundPrice(): Float {
    return round(this * 10) / 10
}

fun Context.getKeyHash(): String? {
    try {
        val info = getPackageManager().getPackageInfo(
            "com.ezuru",
            PackageManager.GET_SIGNATURES
        )
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT)
        }
    } catch (e: PackageManager.NameNotFoundException) {

    } catch (e: NoSuchAlgorithmException) {

    }
    return null
}
//fun Context.getArrayAdapter(items:ArrayList<Any>) todo


/*
*
*  */