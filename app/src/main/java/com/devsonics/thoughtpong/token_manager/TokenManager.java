package com.devsonics.thoughtpong.token_manager;

import com.google.firebase.auth.PhoneAuthProvider;

public class TokenManager {
    private static TokenManager instance;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private TokenManager() {
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void setForceResendingToken(PhoneAuthProvider.ForceResendingToken token) {
        this.forceResendingToken = token;
    }

    public PhoneAuthProvider.ForceResendingToken getForceResendingToken() {
        return forceResendingToken;
    }
}

