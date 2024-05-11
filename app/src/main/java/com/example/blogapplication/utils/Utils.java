package com.example.blogapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.blogapplication.entity.User;

public class Utils {

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
