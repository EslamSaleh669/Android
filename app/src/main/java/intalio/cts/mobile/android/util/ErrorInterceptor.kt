package intalio.cts.mobile.android.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response



class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            400 -> {

                 Log.d("**interrceptor400", response.body.toString())
                 Log.d("**interrceptor400", response.message)
            }
            401 -> {
                Log.d("**interrceptor401", response.body.toString())
                Log.d("**interrceptor401", response.message)            }

            403 -> {
                Log.d("**interrceptor403", response.body.toString())
                Log.d("**interrceptor403", response.message)            }

            404 -> {
                Log.d("**interrceptor404", response.body.toString())
                Log.d("**interrceptor404", response.message)            }

            // ... and so on

        }

        Log.d("**interrceptorelse", response.body.toString())
        Log.d("**interrceptorelse", response.message)
        Log.d("**interrceptorelse", response.code.toString())
        Log.d("**interrceptorelse", response.toString())
        return response
    }

}