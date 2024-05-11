package com.example.blogapplication.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.data.model.LoggedInUser;
import com.example.blogapplication.entity.LoginUser;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    private ApiService apiService;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public interface LoginCallback {
        void onSuccess(LoginUser loginUser);
        void onFailure(String errorMsg);
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    //保存token
    public void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void login(String username, String password,LoginCallback callback) {
        // handle login
//        Result<LoggedInUser> result = dataSource.login(username, password);
//        if (result instanceof Result.Success) {
//            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
//        }
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        String jsonBody = "{\"username\":\"" + username + "\", \"password\":" + password + "}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonBody);
        apiService.login(requestBody).enqueue(new Callback<ResponseResult<LoginUser>>() {
            @Override
            public void onResponse(Call<ResponseResult<LoginUser>> call, Response<ResponseResult<LoginUser>> response) {

                if (response.isSuccessful()) {
                    LoginUser loginUser = response.body().getData();
                    // 登录成功，调用回调函数
                    callback.onSuccess(loginUser);
                } else {
                    // 登录失败，调用回调函数
                    callback.onFailure("登录失败");
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<LoginUser>> call, Throwable t) {
                Log.e("登录出错啦！！！！！",t.getMessage());
                // 登录出错，调用回调函数
                callback.onFailure(t.getMessage());
            }
        });
    }
}