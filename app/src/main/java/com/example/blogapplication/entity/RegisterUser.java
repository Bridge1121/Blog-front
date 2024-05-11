package com.example.blogapplication.entity;

import com.google.gson.Gson;

public class RegisterUser {
    private String username;
    private String password;
    private String email;
    private String nickName;
    private char sex;

    public RegisterUser(String username,String password, String email, String nickName, char sex) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.sex = sex;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
