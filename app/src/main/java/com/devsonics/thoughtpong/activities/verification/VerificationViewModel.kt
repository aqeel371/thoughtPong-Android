package com.devsonics.thoughtpong.activities.verification


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.devsonics.thoughtpong.BaseViewModel
import com.devsonics.thoughtpong.retofit_api.request_model.RequestLogin
import com.devsonics.thoughtpong.retofit_api.model.ResponseLogin
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch
import com.devsonics.thoughtpong.network.RetrofitInstance

/**
 * Created by Aarfeen at Devsonics on 08/07/2024.
 * Project: BDPOS
 */

/**
 * Shared ViewModel for all child Fragments
 */
class VerificationViewModel(app: Application) : BaseViewModel(app) {

    /** Variables */
    private val _loginLivedata = MutableLiveData<NetworkResult<ResponseLogin>>()
    val loginLiveData: LiveData<NetworkResult<ResponseLogin>> get() = _loginLivedata

    /** Methods */

    fun loginApi(login: RequestLogin) = viewModelScope.launch {
        request(_loginLivedata) {
            RetrofitInstance.api.login(login)
        }
    }


    /** Factory */

    companion object {
        fun createFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(VerificationViewModel::class.java)) {
                    return modelClass.getConstructor(Application::class.java).newInstance(app)
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}