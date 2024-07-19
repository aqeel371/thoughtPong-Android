package com.devsonics.thoughtpong.utils

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null, val responseCode: Int? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String?, responseCode: Int? = null, data: T? = null) : NetworkResult<T>(data, message, responseCode)
    class Loading<T> : NetworkResult<T>()
}