package com.devsonics.thoughtpong;

import android.app.Application;

import com.devsonics.thoughtpong.utils.SharedPreferenceManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceManager.INSTANCE.setup(getApplicationContext());
    }
}
