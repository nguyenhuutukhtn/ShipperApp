package com.androidev.maps.Request;

import com.androidev.maps.Model.UserInfo;

public class UpdateUserInfoRequest {
    private int id;
    private String name;
    private boolean status;
    private String avatar;
    private String username;
    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UpdateUserInfoRequest(int id, String name, boolean status, String avatar, String username, String phone) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.avatar = avatar;
        this.username = username;
        this.phone = phone;
    }
}
