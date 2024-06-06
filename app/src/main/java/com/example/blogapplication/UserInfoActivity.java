package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.blogapplication.databinding.ActivityUserInfoBinding;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.utils.TokenUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;
    private ImageView avatar;
    private EditText email;
    private EditText nickname;
    private RadioGroup gender;
    private Button finish;
    private ApiService apiService;
    private ProgressBar loading;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        avatar = binding.imageViewAvatar;
        email = binding.editTextEmail;
        nickname = binding.editTextNickname;
        finish = binding.editUserinfo;
        gender = binding.radioGroupGender;
        loading = binding.loading;
        User userInfo = TokenUtils.getUserInfo(getApplicationContext());
        Picasso.get().load(userInfo.getAvatar()).into(avatar);
        email.setText(userInfo.getEmail());
        nickname.setText(userInfo.getNickName());
        gender.check(userInfo.getSex() == '0' ? R.id.radioButtonMale : R.id.radioButtonFemale);


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });



        //个人信息修改完成
        finish.setOnClickListener(new View.OnClickListener() {
            User user = null;
            RequestBody requestBody = null;
            @Override
            public void onClick(View view) {
                apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                if (imgPath!=null){//修改了头像
                    // 将 imagePath 转换为 File 对象
                    File imageFile = new File(imgPath);
                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("img", imageFile.getName(), requestBody1);
                    // 调用接口上传图片
                    apiService.uploadImg(filePart).enqueue(new Callback<ResponseResult<String>>() {
                        @Override
                        public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                            imgPath = response.body().getData();
                            Toast.makeText(getApplicationContext(),"图片上传成功",Toast.LENGTH_SHORT);
                            Log.i("imgpath!!!!!!!",imgPath);
                            user = new User(TokenUtils.getUserInfo(getApplicationContext()).getId(),nickname.getText().toString(), email.getText().toString(), gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0', imgPath);
                            Log.i("UserInfo", user.toJson());
                            requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), user.toJson());
                            apiService.updateUserInfo(requestBody).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    userInfo.setSex(gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0');
                                    userInfo.setEmail(email.getText().toString());
                                    userInfo.setNickName(nickname.getText().toString());
                                    userInfo.setAvatar(imgPath);
                                    TokenUtils.saveUserInfo(getApplicationContext(), userInfo);//更新保存的用户信息
                                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(UserInfoActivity.this,"个人信息修改成功！",Toast.LENGTH_SHORT).show();
                                    Log.i("修改个人信息成功", "hhhhhhhhhhhh");
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("修改个人信息出错啦", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResponseResult<String>> call, Throwable t) {

                        }
                    });
                }else{//没有修改头像
                    user = new User(TokenUtils.getUserInfo(getApplicationContext()).getId(),nickname.getText().toString(), email.getText().toString(), gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0', TokenUtils.getUserInfo(getApplicationContext()).getAvatar());
                    Log.i("UserInfo", user.toJson());
                    requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), user.toJson());
                    apiService.updateUserInfo(requestBody).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            userInfo.setSex(gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0');
                            userInfo.setEmail(email.getText().toString());
                            userInfo.setNickName(nickname.getText().toString());
                            userInfo.setAvatar(user.getAvatar());
                            TokenUtils.saveUserInfo(getApplicationContext(), userInfo);//更新保存的用户信息
                            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                            Log.i("修改个人信息成功", "hhhhhhhhhhhh");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("修改个人信息出错啦", t.getMessage());
                        }
                    });
                }





            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    //加载图片(使用压缩读取技术)
    private void showImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = 1;
        int side = 1000;//(int) getResources().getDimension(R.dimen.iv_personal_side);
        int a = options.outWidth / side;
        options.inSampleSize = a;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
        avatar.setImageBitmap(bm);
        imgPath = imagePath;

    }

}

