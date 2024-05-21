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

import com.example.blogapplication.databinding.ActivityUserInfoBinding;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.utils.TokenUtils;
import com.squareup.picasso.Picasso;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
                //调用相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(nickname.getText().toString(), email.getText().toString(), gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0', userInfo.getAvatar());
                Log.i("UserInfo", user.toJson());
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), user.toJson());
                apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                apiService.updateUserInfo(requestBody).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        userInfo.setSex(gender.getCheckedRadioButtonId() == R.id.radioButtonFemale ? '1' : '0');
                        userInfo.setEmail(email.getText().toString());
                        userInfo.setNickName(nickname.getText().toString());
                        //todo 头像上传
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
    private void showImage(String imaePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imaePath, options);
        options.inSampleSize = 1;
        int side = 1000;//(int) getResources().getDimension(R.dimen.iv_personal_side);
        int a = options.outWidth / side;
        options.inSampleSize = a;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(imaePath, options);
        avatar.setImageBitmap(bm);
    }

}

