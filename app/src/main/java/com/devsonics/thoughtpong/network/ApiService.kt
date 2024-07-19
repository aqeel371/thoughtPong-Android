package com.devsonics.thoughtpong.network


import com.devsonics.thoughtpong.retofit_api.request_model.RequestCall
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseGetTopic
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseLogin
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseRefreshToken
import com.devsonics.thoughtpong.retofit_api.request_model.RequestLogin
import com.devsonics.thoughtpong.retofit_api.request_model.RequestSignUp
import com.devsonics.thoughtpong.retofit_api.request_model.RequestUpdateProfile
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseCall
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseRefreshCall
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUpdateProfile
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUploadImage
import com.devsonics.thoughtpong.retofit_api.response_model.UserData
import com.devsonics.thoughtpong.utils.SharedPreferenceManager
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body req: RequestLogin): Response<ResponseLogin>

    @POST("signup")
    suspend fun signUp(@Body req: RequestSignUp): Response<ResponseLogin>

    @GET("getTopics")
    suspend fun getTopics(): Response<ResponseGetTopic>


    @POST("call")
    suspend fun callApi(@Body req: RequestCall): Response<ResponseCall>

    @POST("endCall")
    suspend fun endCallApi(@Body req: RequestCall): Response<ResponseCall>


    @GET("getUser/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserData>


    @GET("refreshCall")
    suspend fun refreshCall(): Response<ResponseRefreshCall>

    @Multipart
    @PATCH("uploadImage")
    suspend fun uploadImage(@Part image :  MultipartBody.Part):Response<ResponseUploadImage>


    @PATCH("updateProfile")
    suspend fun updateProfile(@Body req: RequestUpdateProfile): Response<ResponseUpdateProfile>

    @GET("refreshToken")
    suspend fun refreshToken(@Header("Authorization") token: String = "Bearer ${SharedPreferenceManager.refreshToken}"):
            Response<ResponseRefreshToken>

}





















