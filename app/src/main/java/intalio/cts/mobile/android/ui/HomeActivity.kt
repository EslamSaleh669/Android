package intalio.cts.mobile.android.ui

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.cts.mobile.android.R
import intalio.cts.mobile.android.ui.activity.auth.login.LoginViewModel
import intalio.cts.mobile.android.ui.activity.splash.SplashActivity
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvancedSearchFragment
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import intalio.cts.mobile.android.ui.fragment.delegations.DelegationsFragment
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceResultFragment
import intalio.cts.mobile.android.ui.fragment.main.MainFragment
import intalio.cts.mobile.android.ui.fragment.note.AllNotesFragment
import intalio.cts.mobile.android.ui.fragment.profile.ChangeLanguageFragment
import intalio.cts.mobile.android.util.*
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.system.exitProcess
import androidx.fragment.app.Fragment
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.data.network.response.DictionaryResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {

    private lateinit var translator: ArrayList<DictionaryDataItem>

    @Inject
    @field:Named("login")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    private var pressedTime: Long = 0

    private val autoDispose: AutoDispose = AutoDispose()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        (application as MyApplication).appComponent?.inject(this)
        autoDispose.bindTo(this.lifecycle)


        val extras = intent.extras ?: return

        val intentType = extras.getString(Constants.INTENT_TYPE)


        if (intentType == "lang") {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, ChangeLanguageFragment())
            }

        }
        else {
            translator = viewModel.readDictionary()!!.data!!

            val lan = viewModel.readLanguage()
            Lingver.getInstance().setLocale(applicationContext, Locale(lan))

            val toggle = ActionBarDrawerToggle(
                this, drawer_layout, mainToolbar, 0, 0
            )
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()

            setUpNavMenu()


            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, MainFragment())
                addToBackStack("")
            }
        }


    }


    private fun setUpNavMenu() {
        val lang = findViewById<TextView>(R.id.currentlanguage)

        lang.text = viewModel.readLanguage()
        when {
            viewModel.readLanguage() == "en" -> {
//                findViewById<TextView>(R.id.menuhometxt).text = translator.find { it.keyword == "Home" }!!.en
                findViewById<TextView>(R.id.menudelegationtxt).text =
                    translator.find { it.keyword == "Delegation" }!!.en
                findViewById<TextView>(R.id.menudashboardtxt).text =
                    translator.find { it.keyword == "Dashboard" }!!.en
                findViewById<TextView>(R.id.menutodolisttxt).text =
                    translator.find { it.keyword == "ToDoList" }!!.en
                findViewById<TextView>(R.id.menuadvancedstxt).text =
                    translator.find { it.keyword == "Search" }!!.en
                findViewById<TextView>(R.id.menusignouttxt).text =
                    translator.find { it.keyword == "Logout" }!!.en
                lang.text = "English"
            }
            viewModel.readLanguage() == "ar" -> {
//                findViewById<TextView>(R.id.menuhometxt).text = translator.find { it.keyword == "Home" }!!.ar
                findViewById<TextView>(R.id.menudelegationtxt).text =
                    translator.find { it.keyword == "Delegation" }!!.ar
                findViewById<TextView>(R.id.menudashboardtxt).text =
                    translator.find { it.keyword == "Dashboard" }!!.ar
                findViewById<TextView>(R.id.menutodolisttxt).text =
                    translator.find { it.keyword == "ToDoList" }!!.ar
                findViewById<TextView>(R.id.menuadvancedstxt).text =
                    translator.find { it.keyword == "Search" }!!.ar
                findViewById<TextView>(R.id.menusignouttxt).text =
                    translator.find { it.keyword == "Logout" }!!.ar
                lang.text = "العربية"

            }
            viewModel.readLanguage() == "fr" -> {
//                findViewById<TextView>(R.id.menuhometxt).text = translator.find { it.keyword == "Home" }!!.fr
                findViewById<TextView>(R.id.menudelegationtxt).text =
                    translator.find { it.keyword == "Delegation" }!!.fr
                findViewById<TextView>(R.id.menudashboardtxt).text =
                    translator.find { it.keyword == "Dashboard" }!!.fr
                findViewById<TextView>(R.id.menutodolisttxt).text =
                    translator.find { it.keyword == "ToDoList" }!!.fr
                findViewById<TextView>(R.id.menuadvancedstxt).text =
                    translator.find { it.keyword == "Search" }!!.fr
                findViewById<TextView>(R.id.menusignouttxt).text =
                    translator.find { it.keyword == "Logout" }!!.fr
                lang.text = "Français"

            }
        }



        findViewById<View>(R.id.menuhome).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainFragment()).commit()
        }
        findViewById<View>(R.id.menudelegation).setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("")
                .replace(R.id.fragmentContainer, DelegationsFragment()).commit()

            drawer_layout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.menuadvanceds).setOnClickListener {

            supportFragmentManager.commit {
                replace(R.id.fragmentContainer,
                    AdvancedSearchFragment().apply {
                        arguments = bundleOf(
                            Pair(Constants.SEARCH_TYPE, 1)
                        )

                    }
                )
                addToBackStack("")

            }

            drawer_layout.closeDrawer(GravityCompat.START)
        }

        findViewById<View>(R.id.menulanguge).setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("")
                .replace(R.id.fragmentContainer, ChangeLanguageFragment()).commit()

            drawer_layout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.menusignout).setOnClickListener {

            val lan = viewModel.readLanguage()
            viewModel.logout()

            launchActivityFinishCurrent(SplashActivity::class.java)
            getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)?.edit {
                putString(Constants.LANG_KEY, lan)
            }
            Lingver.getInstance().setLocale(this, Locale(lan))


            drawer_layout.closeDrawer(GravityCompat.START)
        }

        findViewById<View>(R.id.menubarcode).setOnClickListener {

            scanNewQRCode()
            val lan = viewModel.readLanguage()

            getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)?.edit {
                putString(Constants.LANG_KEY, lan)
            }
            Lingver.getInstance().setLocale(this, Locale(lan))

        }


        findViewById<View>(R.id.menudashboard).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.menureports).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.menutodolist).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

    }

    private fun scanNewQRCode() {

        var continueConfirmation = ""
        var yes = ""
        var no = ""

        when {
            viewModel.readLanguage() == "en" -> {

                continueConfirmation =
                    translator.find { it.keyword == "ProceedConfirmation" }!!.en!!
                yes = translator.find { it.keyword == "Yes" }!!.en!!
                no = translator.find { it.keyword == "No" }!!.en!!


            }
            viewModel.readLanguage() == "ar" -> {

                continueConfirmation =
                    translator.find { it.keyword == "ProceedConfirmation" }!!.ar!!
                yes = translator.find { it.keyword == "Yes" }!!.ar!!
                no = translator.find { it.keyword == "No" }!!.ar!!

            }
            viewModel.readLanguage() == "fr" -> {

                continueConfirmation =
                    translator.find { it.keyword == "ProceedConfirmation" }!!.fr!!
                yes = translator.find { it.keyword == "Yes" }!!.fr!!
                no = translator.find { it.keyword == "No" }!!.fr!!

            }
        }


        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = android.app.AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(continueConfirmation)
            .setPositiveButton(
                yes,
                DialogInterface.OnClickListener { dialogg, i ->
                    dialogg.dismiss()
                    viewModel.logout()

                    launchActivityFinishCurrent(SplashActivity::class.java)
                    val sharedPref =
                        getSharedPreferences(Constants.SCANNER_PREF, Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply()



                    drawer_layout.closeDrawer(GravityCompat.START)


                })
            .setNegativeButton(
                no,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    drawer_layout.closeDrawer(GravityCompat.START)


                }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen((GravityCompat.START)) -> {

                drawer_layout.closeDrawer(GravityCompat.START)
            }
            supportFragmentManager.fragments.last() is MainFragment -> {

                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    moveTaskToBack(true)
                } else {

                    var msg = ""

//                    when {
//                        viewModel.readLanguage() == "en" -> {
//                            msg ="Press back again to exit"
//
//                         }
//                        viewModel.readLanguage() == "ar" -> {
//                            msg ="اضغط مرة اخري للخروج"
//
//                        }
//                        viewModel.readLanguage() == "fr" -> {
//                            msg ="Appuyez à nouveau pour quitter"
//
//                        }
//                    }

                    Toast.makeText(
                        this,
                        getString(R.string.press_back_aagain),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                pressedTime = System.currentTimeMillis()


            }
            supportFragmentManager.fragments.last() is CorrespondenceFragment -> {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer, MainFragment())
                }
            }

            supportFragmentManager.fragments.last() is CorrespondenceDetailsFragment -> {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        CorrespondenceFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.NODE_INHERIT, viewModel.readCurrentNode())
                            )
                        }
                    )
                }

            }
            else -> {
                super.onBackPressed()

            }
        }


    }


//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
////        Locale.setDefault(newConfig.locale);
////        baseContext.resources.updateConfiguration(newConfig, resources.displayMetrics);
//
//        val lan = viewModel.readLanguage()
////        Lingver.getInstance().setLocale(this, Locale(lan))
//        Lingver.getInstance().setLocale(applicationContext, Locale(lan))
//
//    }


}