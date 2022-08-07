package intalio.cts.mobile.android.ui.activity.splash

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import com.google.gson.Gson
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.data.model.ScanResponse
import intalio.cts.mobile.android.data.model.UserCredentials
import intalio.cts.mobile.android.data.model.viewer.TokenManager
import intalio.cts.mobile.android.ui.HomeActivity
import intalio.cts.mobile.android.ui.activity.auth.login.LoginActivity
import intalio.cts.mobile.android.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named


class SplashActivity : AppCompatActivity() {

    @Inject
    @field:Named("splash")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()


        }
        setContentView(R.layout.activity_splash)
        (application as MyApplication).appComponent?.inject(this)

        autoDispose.bindTo(this.lifecycle)

        val lan = viewModel.readLanguage()
        Lingver.getInstance().setLocale(this, Locale(lan))

        autoDispose.add(
            Observable.fromCallable {

            }.subscribeOn(Schedulers.io()).delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    val sharedPref =
                        getSharedPreferences(Constants.SCANNER_PREF, Context.MODE_PRIVATE)
                    val scannedModel = Gson().fromJson(
                        sharedPref.getString(Constants.SCANNER_MODEL, ""), ScanResponse::class.java
                    )

                    viewModel.userData()?.let {
                        if (scannedModel != null) {
                            Constants.BASE_URL = scannedModel.serviceUrl!!
                            Constants.BASE_URL2 = scannedModel.url!!
                            Constants.CLIENT_ID = scannedModel.clientId!!
                            Constants.VIEWER_URL = scannedModel.ViewerUrl!!
                        }

                        val userCredentials = viewModel.readUserCredentials()
                        if (userCredentials.toString().isNotEmpty()) {
                            Log.d("splashloggs","user Credentials not empty")
                            if (viewModel.readTokenData()!!.expiresIn!! > getToday()) {
                                Log.d("splashloggs","token expiration ${viewModel.readTokenData()!!.expiresIn}" +
                                        " is bigger than get today ${getToday()}")

                                launchActivity(HomeActivity::class.java)
                                finish()
                            } else {

                                Log.d("splashloggs","token expiration ${viewModel.readTokenData()!!.expiresIn}" +
                                        " is less than get today ${getToday()}")

                                refreshToken(
                                    userCredentials.clientId!!,
                                    userCredentials.userName!!,
                                    userCredentials.password!!
                                )

                            }

                        } else {
                            launchActivity(HomeActivity::class.java)
                            finish()
                        }


                    } ?: run {

                        if (scannedModel == null) {
                            startActivity(
                                Intent(this, ScanningActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(
                                    this,
                                    img,
                                    "logo"
                                ).toBundle()
                            )
                        } else {
                            Constants.BASE_URL = scannedModel.serviceUrl!!
                            Constants.BASE_URL2 = scannedModel.url!!
                            Constants.CLIENT_ID = scannedModel.clientId!!
                            Constants.VIEWER_URL = scannedModel.ViewerUrl!!


                            startActivity(
                                Intent(this, LoginActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(
                                    this,
                                    img,
                                    "logo"
                                ).toBundle()
                            )
                        }


                        finish()
                    }
                }, {
                    makeToast(it.message.toString())
                    Log.d("splashloggs", it.message!!)

                    Timber.e(it)
                })
        )

    }

    private fun getToday(): Int {
        val calendar = Calendar.getInstance()
        val thisYear = calendar[Calendar.YEAR]
        val thisMonth = calendar[Calendar.MONTH]
        val thisDay = calendar[Calendar.DAY_OF_MONTH]

//        Log.d("splashloggsmill","${calendar.timeInMillis}")

        return thisDay + (thisMonth + 1) * 31 + thisYear * 12 * 31
    }

    private fun refreshToken(clientId: String, emailStr: String, passwordStr: String) {
        autoDispose.add(
            viewModel.userLogin(
                clientId,
                Constants.GRANT_TYPE, emailStr, passwordStr
            ).observeOn(AndroidSchedulers.mainThread()).subscribe({

                if (it.accessToken.isNullOrEmpty()) {
                    makeToast(getString(R.string.invalidLoginMessage))

                } else {
                    val userCredentials = UserCredentials()
                    userCredentials.clientId = clientId
                    userCredentials.userName = emailStr
                    userCredentials.password = passwordStr
                    viewModel.saveUserCredentials(userCredentials)
                    viewModel.saveUserToken(it)
                    TokenManager.accessToken = it.accessToken

                    Log.d("splashloggs", "now we have a new token ${it.accessToken}")
                    Log.d("splashloggs", "now we have a new expiration date ${it.expiresIn}")

                    launchActivity(HomeActivity::class.java)
                    finish()
                }

            }, {

                Timber.e(it)

                makeToast(getString(R.string.invalidLoginMessage))

            })
        )
    }

}