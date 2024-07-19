package com.devsonics.thoughtpong.activities.signup

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devsonics.thoughtpong.BaseViewModel
import com.devsonics.thoughtpong.network.RetrofitInstance
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseLogin
import com.devsonics.thoughtpong.retofit_api.request_model.RequestSignUp
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch

class CreateAccountViewModel(app: Application) : BaseViewModel(app) {

    private val _signUpLivedata = MutableLiveData<NetworkResult<ResponseLogin>>()
    val signUpLiveData: LiveData<NetworkResult<ResponseLogin>> get() = _signUpLivedata



    fun signUpApi(signUp: RequestSignUp) = viewModelScope.launch {
        request(_signUpLivedata) {
            RetrofitInstance.api.signUp(signUp)
        }
    }


    /** Factory */

    companion object {
        fun createFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CreateAccountViewModel::class.java)) {
                    return modelClass.getConstructor(Application::class.java).newInstance(app)
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}