package com.devsonics.thoughtpong.network

import android.util.Log
import com.devsonics.thoughtpong.utils.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = SharedPreferenceManager.accessToken
        val refreshToken = SharedPreferenceManager.refreshToken
        Log.d("AccessTokken", token?:" ")

        val request = chain.request()
            .newBuilder().apply {
                // Ignore the refreshToken request
                if (!chain.request().url.toString().contains("refreshToken"))
                    this.addHeader("Authorization", "Bearer $token")
                else
                    this.addHeader("Authorization", "Bearer $refreshToken")

            }
            .build()


        return chain.proceed(request)
    }
}