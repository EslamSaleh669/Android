package intalio.cts.mobile.android.ui.activity.auth.login

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.HttpException
import com.cts.mobile.android.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.data.model.ScanResponse
import intalio.cts.mobile.android.data.model.UserCredentials
import intalio.cts.mobile.android.data.model.viewer.TokenManager
import intalio.cts.mobile.android.data.network.response.DictionaryResponse
import intalio.cts.mobile.android.data.network.response.LoginResponseError
import intalio.cts.mobile.android.data.network.response.TokenResponse
import intalio.cts.mobile.android.data.network.response.UserFullDataResponseItem
import intalio.cts.mobile.android.ui.HomeActivity
import intalio.cts.mobile.android.ui.activity.splash.SplashActivity
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class LoginActivity : AppCompatActivity() {

    @Inject
    @field:Named("login")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }
    private val autoDispose: AutoDispose = AutoDispose()
    var dialog: Dialog? = null

    var msgs: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as MyApplication).appComponent?.inject(this)
        autoDispose.bindTo(this.lifecycle)

        val lan = viewModel.readLanguage()
        Lingver.getInstance().setLocale(this, Locale(lan))




        val sharedPref = getSharedPreferences(Constants.SCANNER_PREF, Context.MODE_PRIVATE)
        val scannedModel = Gson().fromJson(
            sharedPref.getString(Constants.SCANNER_MODEL, ""), ScanResponse::class.java
        )

//        val jsonFileString = getJsonDataFromAsset(applicationContext, "dictionary.json")
//
//        val gson = Gson()
//        val keywordsList = object : TypeToken<DictionaryResponse>() {}.type
//        val Keywords: DictionaryResponse = gson.fromJson(jsonFileString, keywordsList)
//        viewModel.saveDictionary(Keywords)

        login.setOnClickListener {
            Constants.BASE_URL = scannedModel.serviceUrl!!
            Constants.BASE_URL2 = scannedModel.url!!
            Constants.CLIENT_ID = scannedModel.clientId!!
            Constants.VIEWER_URL = scannedModel.ViewerUrl!!
            //   makeToast("ServiceUrl:${Constants.BASE_URL} \n Url : ${Constants.BASE_URL2} \n ClientID ${Constants.CLIENT_ID }")


            Log.d("scannercodes",scannedModel.serviceUrl!!)
            Log.d("scannercodes",scannedModel.url!!)
            Log.d("scannercodes",scannedModel.clientId!!)
            Log.d("scannercodes",scannedModel.ViewerUrl!!)

            val emailStr = username.text.toString().trim()
            val passwordStr = password.text.toString().trim()


            when {
                emailStr.isEmpty() -> {
                    makeToast(getString(R.string.requiredField))
                }
                passwordStr.isEmpty() -> {
                    makeToast(getString(R.string.requiredField))
                }

                else -> {
                    dialog = launchLoadingDialog()

                    viewModel.userLogin(
                        Constants.CLIENT_ID,
                        Constants.GRANT_TYPE, emailStr, passwordStr
                    ).enqueue(object :
                        Callback<ResponseBody?> {
                        override fun onResponse(
                            call: Call<ResponseBody?>,
                            response: Response<ResponseBody?>
                        ) {
                            dialog!!.dismiss()

                            if (response.code() == 200) {


                                val userCredentials = UserCredentials()
                                userCredentials.clientId = Constants.CLIENT_ID
                                userCredentials.userName = emailStr
                                userCredentials.password = passwordStr
                                viewModel.saveUserCredentials(userCredentials)

                                var responseRecieved: Any? = null
                                responseRecieved = response.body()!!.string()

                                val tokeResponse = Gson().fromJson(
                                    responseRecieved, TokenResponse::class.java
                                )
                                Log.d("tokenresponse", responseRecieved)

                                viewModel.saveUserToken(tokeResponse)
                                getUserBasicInfo(tokeResponse.accessToken!!)
                                TokenManager.accessToken = tokeResponse.accessToken


                            }

                            else if (response.code() == 400) {

                                var responseRecieved: Any? = null
                                responseRecieved = response.errorBody()!!.string()

                                 val loginError = Gson().fromJson(
                                    responseRecieved, LoginResponseError::class.java
                                )
                                if (loginError.error == "unauthorized_client") {
                                    makeToast(getString(R.string.invalidLoginMessage))
                                } else {
                                    makeToast(loginError.error.toString())
                                }


                            }


                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            dialog!!.dismiss()
                             if (t.message.toString().contains("Failed to connect to")) {
                                makeToast("Please check your current network Connection")

                            } else {
                                makeToast(getString(R.string.network_error))
                            }

                            Log.d("zaaaaaaaaaa",t.toString())
                        }
                    })


//                    autoDispose.add(
//                        viewModel.userLogin(
//                            Constants.CLIENT_ID,
//                            Constants.GRANT_TYPE,emailStr,passwordStr
//                        ).observeOn(AndroidSchedulers.mainThread()).subscribe({
//
//                            Log.d("scannnnerror", it.accessToken!!)
//
//
//
//                            if (it.accessToken.isNullOrEmpty()){
//                                makeToast(it.error.toString())
//                                dialog!!.dismiss()
//
//                            }else{
//                                val userCredentials = UserCredentials()
//                                userCredentials.clientId = Constants.CLIENT_ID
//                                userCredentials.userName = emailStr
//                                userCredentials.password = passwordStr
//                                viewModel.saveUserCredentials(userCredentials)
//
//                                viewModel.saveUserToken(it)
//                                getUserBasicInfo(it.accessToken)
//                                TokenManager.accessToken = it.accessToken
//
//                            }
//
//                        }, {
//                            dialog!!.dismiss()
//                            Timber.e(it)
//                          val code =  (it as HttpException).statusCode
//                            Log.d("scannnnerror", code.toString())
//
////                            makeToast(getString(R.string.invalidLoginMessage))
//
//                        }
//                            )
//
//
//                    )
                }
            }
        }

        lang_icon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(Constants.INTENT_TYPE, "lang")
            startActivity(intent)
        }

        scan_Qr.setOnClickListener {
            scanNewQRCode()
        }
    }


    private fun getUserBasicInfo(token: String) {
        autoDispose.add(viewModel.userBasicInfo(token).observeOn(Schedulers.io()).subscribe({
            viewModel.saveUserBasicInfo(it)

            getDictionary(token, Constants.BASE_URL)


        }, {
            dialog!!.dismiss()

            Timber.e(it)
        }))
    }

    private fun getDictionary(token: String, baseUrl: String) {
        autoDispose.add(
            viewModel.getDictionary(token, baseUrl).observeOn(Schedulers.io()).subscribe({
                viewModel.saveDictionary(it)
                dialog!!.dismiss()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(Constants.INTENT_TYPE, "login")
                startActivity(intent)

//                launchActivityFinishCurrent(HomeActivity::class.java)
            }, {
                Timber.e(it)
                makeToast(it.toString())


            })
        )

//        dialog!!.dismiss()
//
//        launchActivityFinishCurrent(HomeActivity::class.java)
//

    }


    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun scanNewQRCode() {


        var continueConfirmatiuon = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                continueConfirmatiuon = "Are you sure that you want to proceed?"
                yes = "Yes"
                no = "No"


            }
            viewModel.readLanguage() == "ar" -> {

                continueConfirmatiuon = "هل أنت متأكد أنك تريد المتابعة؟"
                yes = "نعم"
                no = "لا"

            }
            viewModel.readLanguage() == "fr" -> {

                continueConfirmatiuon = "Êtes-vous sûr de vouloir continuer?"
                yes = "Oui"
                no = "Non"

            }
        }


        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = android.app.AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(continueConfirmatiuon)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    viewModel.logout()

                    launchActivityFinishCurrent(SplashActivity::class.java)
                    val sharedPref =
                        getSharedPreferences(Constants.SCANNER_PREF, Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply()


                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()

                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    override fun onBackPressed() {
        moveTaskToBack(true)

    }
}