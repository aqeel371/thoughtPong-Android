package com.devsonics.thoughtpong.retofit_api.response_model

data class ResponseUpdateProfile(
    val email: String,
    val fullName: String,
    val id: Int,
    val image: String,
    val phone: String
)