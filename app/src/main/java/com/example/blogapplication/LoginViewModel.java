package com.example.blogapplication;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blogapplication.entity.LoginUser;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.utils.TokenUtils;

import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<ResponseResult<LoginUser>> loginResult = new MutableLiveData<>();
    private ApiService apiService;
    private String token;

    public LoginViewModel() {
    }

    public LiveData<ResponseResult<LoginUser>> login(String username, String password) {
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        String jsonBody = "{\"username\":\"" + username + "\", \"password\":" + password + "}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonBody);
        apiService.login(requestBody).enqueue(new Callback<ResponseResult<LoginUser>>() {
            @Override
            public void onResponse(Call<ResponseResult<LoginUser>> call, Response<ResponseResult<LoginUser>> response) {
                if (response.isSuccessful()) {
                    LoginUser loginUser = response.body().getData();
                    loginResult.setValue(new ResponseResult<>(0, "Login successful", loginUser));
                } else {
                    loginResult.setValue(new ResponseResult<>(1, "Login failed", null));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<LoginUser>> call, Throwable t) {
                loginResult.setValue(new ResponseResult<>(1, "Error: " + t.getMessage(), null));
            }
        });

        return loginResult;
    }

    public LiveData<ResponseResult<Void>> logout(Context context) {
        // 获取保存的token
        token = TokenUtils.getToken(context);
        MutableLiveData<ResponseResult<Void>> logoutResult = new MutableLiveData<>();
        apiService = RetrofitClient.getInstance(token).create(ApiService.class);


        apiService.logout().enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                if (response.isSuccessful()) {
                    // 退出登录成功
                    TokenUtils.deleteToken(context);
                    TokenUtils.deleteCategoryInfo(context);
                    if (!Objects.isNull(TokenUtils.getUserInfo(context))){
                        TokenUtils.deleteUserInfo(context);
                    }
                    logoutResult.setValue(new ResponseResult<>(0, "退出登录成功", null));
                } else {
                    // 退出登录失败
                    logoutResult.setValue(new ResponseResult<>(1, "退出成功失败", null));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                logoutResult.setValue(new ResponseResult<>(1, "Error: " + t.getMessage(), null));
                Log.e("退出登录出错啦！！！",t.getMessage());
            }
        });

        return logoutResult;
    }
}