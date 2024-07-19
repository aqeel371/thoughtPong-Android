// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.devsonics.thoughtpong.retofit_api.response_model;

public class ResponseRefreshToken {
    private long expIn;
    private String token;
    private String refreshToken;

    public long getExpIn() { return expIn; }
    public void setExpIn(long value) { this.expIn = value; }

    public String getToken() { return token; }
    public void setToken(String value) { this.token = value; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String value) { this.refreshToken = value; }
}
