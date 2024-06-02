package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.blogapplication.databinding.ActivityLoginBinding;
import com.example.blogapplication.databinding.ActivityRegisterBinding;
import com.example.blogapplication.entity.RegisterUser;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.entity.UserInfo;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText nickname;
    private EditText email;
    private RadioGroup genderGroup;
    private Button register;
    private ProgressBar loading;
    private ActivityRegisterBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        nickname = binding.nickname;
        email = binding.email;
        genderGroup = binding.rgGender;
        register = binding.register;
        loading = binding.loading;

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                apiService = RetrofitClient.getInstance(null).create(ApiService.class);
                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() || nickname.getText().toString().isEmpty()
                        || email.getText().toString().isEmpty() || genderGroup.getCheckedRadioButtonId() ==-1){
                    Toast.makeText(RegisterActivity.this,"注册信息不能为空，请填写完整！",Toast.LENGTH_SHORT);
                }else{
                    RegisterUser user = new RegisterUser(username.getText().toString(),password.getText().toString(),email.getText().toString(),nickname.getText().toString(),genderGroup.getCheckedRadioButtonId()== R.id.rbMale ?'0':'1');//0男1女
                    Log.i("registerUserInfo",user.toJson());
//                    String jsonBody = "{\"username\":\"" + username.getText() + "\", \"password\":" + password.getText() + "\", \"email\":" + email.getText() +"\", \"nickname\":" + nickname.getText()+"}";
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),user.toJson());
                    apiService.register(requestBody).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            if (response.body().getCode() ==200){
                                Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                intent.putExtra("registrationSuccess", true);
                                intent.putExtra("username",username.getText().toString());
                                intent.putExtra("password",password.getText().toString());
                                startActivity(intent);
                            }else if (response.body().getCode() == 501){
                                Toast.makeText(RegisterActivity.this,"用户名已存在！",Toast.LENGTH_SHORT).show();
                            }else if (response.body().getCode() == 513){
                                Toast.makeText(RegisterActivity.this,"该邮箱已注册！",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {

                        }
                    });
                }
            }
        });

    }
}