package com.devsonics.thoughtpong.retofit_api.request_model

data class RequestCall(
    val roomName: String,
    val topIds: List<String>
)