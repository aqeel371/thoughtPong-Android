package com.devsonics.thoughtpong.retofit_api.response_model

class ResponseGetTopic : ArrayList<ResponseGetTopic.ResponseGetTopicItem>(){
    data class ResponseGetTopicItem(
        var icon: String?,
        var id: Int?,
        var name: String?
    )
}