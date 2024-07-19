package com.devsonics.thoughtpong.retofit_api.response_model

data class ResponseCall(
    val callToken: CallToken?,
    val channelName: String?,
    val `data`: Data?,
    val user: User?
) {
    data class CallToken(
        val `data`: Data
    ) {
        data class Data(
            val token: String
        )
    }

    data class Data(
        val token: String
    )

    data class User(
        val email: String,
        val fullName: String,
        val id: Int,
        val image: String,
        val phone: String
    )
}