package com.example.blogapplication.entity;



import com.google.gson.Gson;

import java.util.Collection;

public class LoginUser {
    private User user;

    private String token;


    public LoginUser(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // 序列化为 JSON 字符串
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 从 JSON 字符串解析为 LoginUser 对象
    public static LoginUser fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LoginUser.class);
    }
}
