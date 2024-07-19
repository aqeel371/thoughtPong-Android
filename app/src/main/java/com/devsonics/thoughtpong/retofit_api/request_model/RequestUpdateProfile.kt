package com.devsonics.thoughtpong.retofit_api.request_model

data class RequestUpdateProfile(
    val email: String,
    val image: String?,
    val name: String
)