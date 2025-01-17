package com.devsonics.thoughtpong.retofit_api.response_model;

public class UserData {
    private String image;
    private String phone;
    private String fullName;
    private long id;
    private String email;

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        this.image = value;
    }

    public long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String value) {
        this.fullName = value;
    }

    public void setid(long value) {
        this.id = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }
}
