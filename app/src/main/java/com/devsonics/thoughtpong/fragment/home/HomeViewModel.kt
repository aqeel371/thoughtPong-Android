package com.devsonics.thoughtpong.fragment.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devsonics.thoughtpong.BaseViewModel
import com.devsonics.thoughtpong.network.RetrofitInstance
import com.devsonics.thoughtpong.retofit_api.model.ResponseGetTopic
import com.devsonics.thoughtpong.retofit_api.request_model.RequestLogin
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : BaseViewModel(app) {

    private val _getTopicsLiveData = MutableLiveData<NetworkResult<ResponseGetTopic>>()
    val getTopicsLiveData: LiveData<NetworkResult<ResponseGetTopic>> get() = _getTopicsLiveData

    /** Methods */

    fun getTopicsApi() = viewModelScope.launch {
        request(_getTopicsLiveData) {
            RetrofitInstance.api.getTopics()
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