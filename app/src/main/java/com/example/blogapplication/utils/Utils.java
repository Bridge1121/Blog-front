package com.example.blogapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.blogapplication.entity.User;

public class Utils {

    public static void saveToken(Context context, String token) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static void saveUserInfo(Context context, User user) {

        String userJson = user.toJson();
        // 获取 SharedPreferences 对象并保存 User 对象的 JSON 字符串
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", userJson);
        editor.apply();
    }

    //获取token
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    public static void deleteToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();
    }

    public static User getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", "");

        // 从 JSON 字符串解析为 User 对象
        User user = User.fromJson(userJson);
        return user;
    }

    public static void deleteUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
    }



}
