package com.devsonics.thoughtpong.fragment.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devsonics.thoughtpong.BaseViewModel
import com.devsonics.thoughtpong.network.RetrofitInstance
import com.devsonics.thoughtpong.retofit_api.request_model.RequestCall
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseCall
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseGetTopic
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseRefreshCall
import com.devsonics.thoughtpong.retofit_api.response_model.UserData
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : BaseViewModel(app) {

    private val _getTopicsLiveData = MutableLiveData<NetworkResult<ResponseGetTopic>>()
    val getTopicsLiveData: LiveData<NetworkResult<ResponseGetTopic>> get() = _getTopicsLiveData

    private val _callLiveData = MutableLiveData<NetworkResult<ResponseCall>>()
    val callLiveData: LiveData<NetworkResult<ResponseCall>> get() = _callLiveData

    private val _refreshCallLiveData = MutableLiveData<NetworkResult<ResponseRefreshCall>>()
    val refreshCallLiveData: LiveData<NetworkResult<ResponseRefreshCall>> get() = _refreshCallLiveData

    private val _endCallLiveData = MutableLiveData<NetworkResult<ResponseCall>>()
    val endCallLiveData: LiveData<NetworkResult<ResponseCall>> get() = _endCallLiveData

    /** Methods */

    fun getTopicsApi() = viewModelScope.launch {
        request(_getTopicsLiveData) {
            RetrofitInstance.api.getTopics()
        }
    }

    fun callApi(call: RequestCall) = viewModelScope.launch {
        request(_callLiveData) {
            RetrofitInstance.api.callApi(call)
        }
    }

    fun refreshCall() = viewModelScope.launch {
        request(_refreshCallLiveData) {
            RetrofitInstance.api.refreshCall()
        }
    }

    fun endCall(call: RequestCall) = viewModelScope.launch {
        request(_endCallLiveData) {
            RetrofitInstance.api.callApi(call)
        }
    }


    /** Factory */

    companion object {
        fun createFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return modelClass.getConstructor(Application::class.java).newInstance(app)
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}