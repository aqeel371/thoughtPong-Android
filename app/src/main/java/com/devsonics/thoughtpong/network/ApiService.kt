package com.devsonics.thoughtpong.network


import com.devsonics.thoughtpong.retofit_api.model.ResponseGetTopic
import com.devsonics.thoughtpong.retofit_api.model.ResponseLogin
import com.devsonics.thoughtpong.retofit_api.model.ResponseRefreshToken
import com.devsonics.thoughtpong.retofit_api.request_model.RequestLogin
import com.devsonics.thoughtpong.retofit_api.request_model.RequestSignUp
import com.devsonics.thoughtpong.utils.SharedPreferenceManager
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body req: RequestLogin): Response<ResponseLogin>

    @POST("signup")
    suspend fun signUp(@Body req: RequestSignUp): Response<ResponseLogin>

    @GET("getTopics")
    suspend fun getTopics(): Response<ResponseGetTopic>

    @GET("refreshToken")
    suspend fun refreshToken(@Header("Authorization") token: String = "Bearer ${SharedPreferenceManager.refreshToken}"):
            Response<ResponseRefreshToken>

}





















