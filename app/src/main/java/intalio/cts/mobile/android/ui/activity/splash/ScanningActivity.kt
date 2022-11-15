package intalio.cts.mobile.android.ui.activity.splash

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.cts.mobile.android.R
import com.google.gson.Gson
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.data.model.ScanResponse
import intalio.cts.mobile.android.data.network.response.AttachmentsResponseItem
import intalio.cts.mobile.android.ui.activity.auth.login.LoginActivity
import intalio.cts.mobile.android.util.*
import kotlinx.android.synthetic.main.activity_main3.*
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ScanningActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    @Inject
    @field:Named("splash")
    lateinit var viewModelFactory : ViewModelProvider.Factory

    private val viewModel :SplashViewModel by lazy {
        ViewModelProvider(this,viewModelFactory).get(SplashViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        (application as MyApplication).appComponent?.inject(this)
        autoDispose.bindTo(this.lifecycle)


//        loginlogo.setOnClickListener {
////
//            val  scannerModel = ScanResponse()
//
//            scannerModel.serviceUrl = "http://192.168.1.11:6969"
//            scannerModel.url = "http://192.168.1.11:9949"
//            scannerModel.clientId = "5d2c8fa5-9f58-430c-bcf2-5f4366d425dc"
//            scannerModel.ViewerUrl = "http://192.168.1.11:8080/VIEWER"
////
//
//
////
////            scannerModel.serviceUrl = "http://172.20.10.9:6969"
////            scannerModel.url = "http://172.20.10.9:9949"
////            scannerModel.clientId = "5c7d368c-2053-40a5-b005-caa6647a0070"
////            scannerModel.ViewerUrl = "http://172.20.10.9:8080/VIEWER"
//////
//
//
//
//
//
////            UserDefaultHelper.shared.setServiceURL(serviceUrl: String("https://ctsp.intalio.com/".dropLast()))
////
////            UserDefaultHelper.shared.setURL(url: String("https://iamp.intalio.com/".dropLast()))
////
////            UserDefaultHelper.shared.setClientID(clientID: "5d2c8fa5-9f58-430c-bcf2-5f4366d425dc")
////
////            UserDefaultHelper.shared.setViewerUrl(url: "https://dmsp.intalio.com/VIEWER")
//
////
//       //     makeToast("ServiceUrl:${scannerModel.serviceUrl} \n IAM : ${scannerModel.url} \n ClientID ${scannerModel.clientId}")
//
//            val sharedPref = getSharedPreferences(Constants.SCANNER_PREF,Context.MODE_PRIVATE)
//            val editor = sharedPref.edit()
//            editor.putString(Constants.SCANNER_MODEL, Gson().toJson(scannerModel))
//            editor.apply()
//
//            launchActivityFinishCurrent(LoginActivity::class.java)
//        }


        if (ContextCompat.checkSelfPermission(this@ScanningActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@ScanningActivity, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                val scanningObject = JSONObject(it.text)
                val  scannerModel = ScanResponse()

                Log.d("scannercodes",scanningObject.toString())

                scannerModel.serviceUrl = scanningObject.getString("ServiceUrl")
                scannerModel.url = scanningObject.getString("Url")
                scannerModel.clientId = scanningObject.getString("ClientId")
                scannerModel.ViewerUrl = scanningObject.getString("ViewerUrl")

                val sharedPref = getSharedPreferences(Constants.SCANNER_PREF,Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString(Constants.SCANNER_MODEL, Gson().toJson(scannerModel))
                editor.apply()

                Log.d("scannercodes",scanningObject.getString("ServiceUrl"))
                Log.d("scannercodes",scanningObject.getString("Url"))
                Log.d("scannercodes",scanningObject.getString("ClientId"))
                Log.d("scannercodes",scanningObject.getString("ViewerUrl"))

//                makeToast(
//                    "IAM URL${scanningObject.getString("ServiceUrl")}" +
//                            "CTS URL${scanningObject.getString("Url")}" +
//                            "ClientId${scanningObject.getString("ClientId")}" +
//                            "ViewerUrl URL${scanningObject.getString("ViewerUrl")}",
//
//                    )


                launchActivityFinishCurrent(LoginActivity::class.java)

            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(::codeScanner.isInitialized) {
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized) {
            codeScanner?.releaseResources()
        }
        super.onPause()
    }
}