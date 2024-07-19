package com.devsonics.thoughtpong.retofit_api.response_model

data class ResponseRefreshCall(
    val `data`: List<Data>
) {
    data class Data(
        val callCode: String,
        val category: String,
        val id: Int
    )
}