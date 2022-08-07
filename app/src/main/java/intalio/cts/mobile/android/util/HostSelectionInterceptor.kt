package intalio.cts.mobile.android.util

import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val host: String = Constants.BASE_URL

        val newUrl = request.url.newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}