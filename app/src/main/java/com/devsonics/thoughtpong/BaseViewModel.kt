package com.devsonics.thoughtpong

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.devsonics.thoughtpong.utils.NetworkResult
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Created by Aarfeen at Devsonics on 24/01/2024.
 * Project: BDPOS
 */

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    suspend fun <T> request(response: MutableLiveData<NetworkResult<T>>, api: suspend () -> Response<T>) {
        response.postValue(NetworkResult.Loading())
        try {
            if (hasInternetConnection())
                handleResponse(api(), response)
            else
                handleInternetError(response)

        } catch (t: Throwable) {
            handleThrowableErrors(t, response)
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<MyApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun <T> handleResponse(response: Response<T>, mutableLiveData: MutableLiveData<NetworkResult<T>>) =
        if (response.isSuccessful && response.body() != null) mutableLiveData.postValue(NetworkResult.Success(response.body()!!))
        else if (response.errorBody() != null && response.code() >= 400 && response.code() < 500) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            mutableLiveData.postValue(NetworkResult.Error(errorObj.getString("error"), responseCode = response.code()))
        } else mutableLiveData.postValue(NetworkResult.Error(response.message(), responseCode = response.code()))

    private fun <T> handleThrowableErrors(t: Throwable, mutableLiveData: MutableLiveData<NetworkResult<T>>) = when (t) {
        is HttpException -> mutableLiveData.postValue(NetworkResult.Error("Unexpected Response!"))
        is IOException -> mutableLiveData.postValue(NetworkResult.Error("Network Failure!"))
        is JSONException -> mutableLiveData.postValue(NetworkResult.Error("Conversion Error!"))
        else -> mutableLiveData.postValue(NetworkResult.Error("Error! ${t.message}"))
    }

    private fun <T> handleInternetError(mutableLiveData: MutableLiveData<NetworkResult<T>>) =
        mutableLiveData.postValue(NetworkResult.Error("No internet connection!"))

}