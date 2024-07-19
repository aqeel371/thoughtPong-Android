package com.devsonics.thoughtpong.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL = "https://bdpos.store/api/thought/"

    private val logging: HttpLoggingInterceptor by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return@lazy logging
    }

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(logging)
        authenticator(ForbiddenAuthenticator())
        addInterceptor(AuthInterceptor())
    }.build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}