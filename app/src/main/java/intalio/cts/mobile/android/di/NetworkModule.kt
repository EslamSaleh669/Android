package intalio.cts.mobile.android.di

import android.content.Context
import com.google.gson.Gson

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import intalio.cts.mobile.android.data.model.ScanResponse
import intalio.cts.mobile.android.data.network.ApiClient
import intalio.cts.mobile.android.util.Constants
import intalio.cts.mobile.android.util.ErrorInterceptor
import intalio.cts.mobile.android.util.NullOnEmptyConverterFactory
import intalio.cts.mobile.android.util.NullOnEmptyConverterFactory2
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


@Module
class NetworkModule (val context:Context) {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(ErrorInterceptor())
            .build()

    @Provides
    fun provideApiClient(client: OkHttpClient): ApiClient =
        Retrofit.Builder()

            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory2())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build().create(ApiClient::class.java)

}