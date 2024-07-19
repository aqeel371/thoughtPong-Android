package com.devsonics.thoughtpong.network

import com.devsonics.thoughtpong.utils.SharedPreferenceManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Created by Aarfeen at Devsonics on 12/02/2024.
 * Project: Client Portal
 */

class ForbiddenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) {
            return null // Give up, we've already attempted to authenticate.
        }

        println("Authenticating for response: $response")
        println("Challenges: ${response.challenges()}")
        if (response.request.url.toString().contains("login"))
            return null
        if (response.request.url.toString().contains("signup"))
            return null
        synchronized(this) {

            var token = ""
            var refreshToken = ""
            val res = runBlocking { RetrofitInstance.api.refreshToken() }
            if (res.isSuccessful && res.errorBody() == null) {
                token = res.body()!!.token.toString()
                refreshToken = res.body()!!.refreshToken.toString()
            }

            SharedPreferenceManager.accessToken = token
//            SharedPreferenceManager.accessToken = token
            SharedPreferenceManager.refreshToken = refreshToken

            return response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
    }
}
