package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapplication.databinding.ActivityLoginBinding;
import com.example.blogapplication.entity.LoginUser;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.ui.login.LoginViewModelFactory;

import androidx.lifecycle.Observer;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private TextView register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        register = binding.register;

        if (getIntent().getBooleanExtra("registrationSuccess", true)) {
            usernameEditText.setText(getIntent().getStringExtra("username"));
            passwordEditText.setText(getIntent().getStringExtra("password"));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString()).observe(LoginActivity.this, new Observer<ResponseResult<LoginUser>>() {
                    @Override
                    public void onChanged(ResponseResult<LoginUser> responseResult) {
                        if (responseResult.getCode()==0) {
                            // 登录成功，处理登录用户数据
                            LoginUser loginUser = responseResult.getData();
                            saveToken(LoginActivity.this, loginUser.getToken());
                            saveUserInfo(LoginActivity.this,loginUser.getUser());
                            Toast.makeText(LoginActivity.this, "登录成功！！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            // 登录失败，处理错误信息
                            String errorMessage = responseResult.getMsg();
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("登录出错啦！！！",errorMessage);
                        }
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveToken(Context context, String token) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void saveUserInfo(Context context, User user) {

        String userJson = user.toJson();
        // 获取 SharedPreferences 对象并保存 User 对象的 JSON 字符串
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", userJson);
        editor.apply();
    }
}