package com.devsonics.thoughtpong.retofit_api.response_model;

public class ResponseLogin {
    private UserData data;
    private long expIn;
    private String token;
    private String refreshToken;

    public UserData getData() { return data; }
    public void setData(UserData value) { this.data = value; }

    public long getExpIn() { return expIn; }
    public void setExpIn(long value) { this.expIn = value; }

    public String getToken() { return token; }
    public void setToken(String value) { this.token = value; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String value) { this.refreshToken = value; }
}

