package intalio.cts.mobile.android.util

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.cts.mobile.android.BuildConfig
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.yariksoffice.lingver.Lingver
import intalio.cts.mobile.android.di.*
import timber.log.Timber

class MyApplication() : Application() {

   var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()


        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule((baseContext)))
            .storageModule(StorageModule(baseContext))
            .viewModelFactoryModule(ViewModelFactoryModule()).build()


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        Lingver.init(
            this,
            getSharedPreferences(
                Constants.SHARED_NAME,
                Context.MODE_PRIVATE
            ).getString(Constants.LANG_KEY, "en") ?: "en"
        )
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        RxPaparazzo.register(this)
    }
 }