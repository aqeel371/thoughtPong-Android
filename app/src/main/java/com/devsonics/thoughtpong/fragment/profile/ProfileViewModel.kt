package com.devsonics.thoughtpong.fragment.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devsonics.thoughtpong.BaseViewModel
import com.devsonics.thoughtpong.network.RetrofitInstance
import com.devsonics.thoughtpong.retofit_api.request_model.RequestCall
import com.devsonics.thoughtpong.retofit_api.request_model.RequestUpdateProfile
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseGetTopic
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUpdateProfile
import com.devsonics.thoughtpong.retofit_api.response_model.ResponseUploadImage
import com.devsonics.thoughtpong.utils.NetworkResult
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(app: Application) : BaseViewModel(app) {


    private val _uploadImageLiveData = MutableLiveData<NetworkResult<ResponseUploadImage>>()
    val uploadImageLiveData: LiveData<NetworkResult<ResponseUploadImage>> get() = _uploadImageLiveData


    private val _updateProfileLiveData = MutableLiveData<NetworkResult<ResponseUpdateProfile>>()
    val updateProfileLiveData: LiveData<NetworkResult<ResponseUpdateProfile>> get() = _updateProfileLiveData


    /** Methods */

    fun uploadImageApi(image: MultipartBody.Part) = viewModelScope.launch {
        request(_uploadImageLiveData) {
            RetrofitInstance.api.uploadImage(image)
        }
    }
    fun updateProfile(call: RequestUpdateProfile) = viewModelScope.launch {
        request(_updateProfileLiveData) {
            RetrofitInstance.api.updateProfile(call)
        }
    }

    /** Factory */

    companion object {
        fun createFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    return modelClass.getConstructor(Application::class.java).newInstance(app)
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}