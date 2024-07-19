package com.devsonics.thoughtpong.utils

import android.content.Context
import android.content.SharedPreferences
import com.devsonics.thoughtpong.retofit_api.response_model.UserData

object SharedPreferenceManager {

    private lateinit var sharedPreferences: SharedPreferences


    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    var accessToken: String?
        get() = sharedPreferences.getString(Constants.KEY_ACCESS_TOKEN, null)
        set(value) {
            sharedPreferences.edit()?.putString(Constants.KEY_ACCESS_TOKEN, value)?.commit()
        }

    var refreshToken: String?
        get() = sharedPreferences.getString(Constants.KEY_REFRESH_TOKEN, null)
        set(value) {
            sharedPreferences.edit()?.putString(Constants.KEY_REFRESH_TOKEN, value)?.commit()
        }

    var isUserLogin: Boolean
        get() = sharedPreferences.getBoolean(Constants.KEY_IS_USER_LOGIN, false)
        set(value) {
            sharedPreferences.edit()?.putBoolean(Constants.KEY_IS_USER_LOGIN, value)?.commit()
        }

    fun setUserData(userData: UserData?) {
        if (userData == null) {
            sharedPreferences.edit()?.remove(Constants.KEY_USER_ID)?.commit()
            sharedPreferences.edit()?.remove(Constants.KEY_USER_EMAIL)?.commit()
            sharedPreferences.edit()?.remove(Constants.KEY_USER_PHONE)?.commit()
            sharedPreferences.edit()?.remove(Constants.KEY_USER_FULL_NAME)?.commit()
            sharedPreferences.edit()?.remove(Constants.KEY_USER_IMAGE)?.commit()
            return
        }
        sharedPreferences.edit()?.putLong(Constants.KEY_USER_ID, userData.getId())?.commit()
        sharedPreferences.edit()?.putString(Constants.KEY_USER_EMAIL, userData.email)?.commit()
        sharedPreferences.edit()?.putString(Constants.KEY_USER_PHONE, userData.phone)?.commit()
        sharedPreferences.edit()?.putString(Constants.KEY_USER_FULL_NAME, userData.fullName)?.commit()
        sharedPreferences.edit()?.putString(Constants.KEY_USER_IMAGE, userData.image)?.commit()
    }

    fun getUserData(): UserData? {
        val id = sharedPreferences.getLong(Constants.KEY_USER_ID, -1)
        val email = sharedPreferences.getString(Constants.KEY_USER_EMAIL, "")
        val phone = sharedPreferences.getString(Constants.KEY_USER_PHONE, "")
        val fullName = sharedPreferences.getString(Constants.KEY_USER_FULL_NAME, "")
        val image = sharedPreferences.getString(Constants.KEY_USER_IMAGE, "")
        val userData = UserData()
        userData.setid(id)
        userData.email = email
        userData.phone = phone
        userData.fullName = fullName
        userData.image = image
        if (id.toInt() == -1) {
            return null
        }
        return userData
    }


}

