package com.devsonics.thoughtpong.activities.waiting

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
import com.devsonics.thoughtpong.retofit_api.response_model.UserData
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch

class CallScreenViewModel(app: Application) : BaseViewModel(app) {


    private val _endCallLiveData = MutableLiveData<NetworkResult<ResponseCall>>()
    val endCallLiveData: LiveData<NetworkResult<ResponseCall>> get() = _endCallLiveData


    private val _user1EndCallLiveData = MutableLiveData<NetworkResult<ResponseCall>>()
    val user1EndCallLiveData: LiveData<NetworkResult<ResponseCall>> get() = _user1EndCallLiveData

    private val _userLiveData = MutableLiveData<NetworkResult<UserData>>()
    val userLiveData: LiveData<NetworkResult<UserData>> get() = _userLiveData


    private val _callLiveData = MutableLiveData<NetworkResult<ResponseCall>>()
    val callLiveData: LiveData<NetworkResult<ResponseCall>> get() = _callLiveData


    fun callApi(call: RequestCall) = viewModelScope.launch {
        request(_callLiveData) {
            RetrofitInstance.api.callApi(call)
        }
    }

    fun endCall(call: RequestCall) = viewModelScope.launch {
        request(_endCallLiveData) {
            RetrofitInstance.api.endCallApi(call)
        }
    }

    fun user1EndCall(call: RequestCall) = viewModelScope.launch {
        request(_user1EndCallLiveData) {
            RetrofitInstance.api.endCallApi(call)
        }
    }

    fun getUser(id: String) = viewModelScope.launch {
        request(_userLiveData) {
            RetrofitInstance.api.getUser(id)
        }
    }

    /** Factory */

    companion object {
        fun createFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CallScreenViewModel::class.java)) {
                    return modelClass.getConstructor(Application::class.java).newInstance(app)
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}