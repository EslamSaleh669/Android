package intalio.cts.mobile.android.ui.fragment.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cts.mobile.android.R
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.ui.activity.splash.SplashActivity
import intalio.cts.mobile.android.ui.fragment.main.MainViewModel
import intalio.cts.mobile.android.util.AutoDispose
import intalio.cts.mobile.android.util.Constants
import intalio.cts.mobile.android.util.MyApplication
import intalio.cts.mobile.android.util.launchActivity
import kotlinx.android.synthetic.main.fragment_change_language.*
import kotlinx.android.synthetic.main.toolbar_layout.*


import java.util.*
import javax.inject.Inject
import javax.inject.Named


class ChangeLanguageFragment : Fragment() {

    @Inject
    @field:Named("profile")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ProfleViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProfleViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()

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

        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editInterface()
        updateLanguage()


        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }


    }

    private fun updateLanguage() {

        arBtn.setOnClickListener {
            context?.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)?.edit {
                putString(Constants.LANG_KEY, "ar")
            }

            Lingver.getInstance().setLocale(requireContext(), Locale("ar"))
            activity?.launchActivity(SplashActivity::class.java)
            activity?.finish()
        }

        enBtn.setOnClickListener {
            context?.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)?.edit {
                putString(Constants.LANG_KEY, "en")
            }
            Lingver.getInstance().setLocale(requireContext(), Locale("en"))
            activity?.launchActivity(SplashActivity::class.java)
            activity?.finish()
        }

        frBtn.setOnClickListener {
            context?.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)?.edit {
                putString(Constants.LANG_KEY, "fr")
            }
            Lingver.getInstance().setLocale(requireContext(), Locale("fr"))
            activity?.launchActivity(SplashActivity::class.java)
            activity?.finish()
        }
    }


    private fun editInterface() {
        val translator = viewModel.readDictionary()!!.data!!
        when {
            viewModel.currentLanguage() == "ar" -> {
                arabictxt.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                arabicval.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                requireActivity().findViewById<TextView>(R.id.centered_txt).text = "تغيير اللغة"
                entxt.text = "الإنجليزية"
                frtxt.text = "الفرنسية"
                arabictxt.text = "العربية"

            }
            viewModel.currentLanguage() == "en" -> {
                entxt.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                enval.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                requireActivity().findViewById<TextView>(R.id.centered_txt).text = "change language"
                entxt.text = "English"
                frtxt.text = "French"
                arabictxt.text = "ِArabic"


            }
            viewModel.currentLanguage() == "fr" -> {
                frtxt.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                frval.setTextColor(requireActivity().resources.getColor(R.color.appcolor))
                requireActivity().findViewById<TextView>(R.id.centered_txt).text = "changer de langue"
                entxt.text = "L'Anglais"
                frtxt.text = "Français"
                arabictxt.text = "L'arabe"


            }
        }
    }
}
