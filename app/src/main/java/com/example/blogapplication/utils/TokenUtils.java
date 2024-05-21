package com.example.blogapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.blogapplication.entity.User;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TokenUtils {

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

    public static void saveUserCategory(Context context, List<CategoryResponse> categoryList) {
        // 将 List 转换为 JSON 字符串
        Gson gson = new Gson();
        String categoryListJson = gson.toJson(categoryList);

        // 获取 SharedPreferences 对象并保存 List 的 JSON 字符串
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_category", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("categoryList", categoryListJson);
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

    public static List<CategoryResponse> getUserCategoryInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_category", Context.MODE_PRIVATE);
        String categoryListJson = sharedPreferences.getString("categoryList", "");

        // 将 JSON 字符串转换为 List<CategoryResponse> 对象
        Gson gson = new Gson();
        Type categoryListType = new TypeToken<List<CategoryResponse>>() {}.getType();
        List<CategoryResponse> categoryList = gson.fromJson(categoryListJson, categoryListType);
        return categoryList;
    }

    public static void deleteUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
    }

    public static void deleteCategoryInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_category", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("category");
    }





}
