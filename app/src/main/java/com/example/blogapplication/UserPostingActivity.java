package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapplication.adapter.ImageAdapter;
import com.example.blogapplication.databinding.ActivityUserPostingBinding;
import com.example.blogapplication.fragment.PostingsFragment;
import com.example.blogapplication.utils.TokenUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;
import com.yinglan.keyboard.HideUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserPostingActivity extends AppCompatActivity {
    private ActivityUserPostingBinding binding;
    private ImageView addImg;
    private EditText content;
    private ApiService apiService;
    private ImageAdapter imageAdapter;
    private ArrayList<ImageItem> imageList;
    private List<String> imagePth = new ArrayList<>();
    private RecyclerView imageRecyclerView;
    private GridLayout gridLayout;
    private NineGridView nineGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPostingBinding.inflate(getLayoutInflater());
        NineGridView.setImageLoader(new PicassoImageLoader());//初始化图片加载器
        nineGridView = binding.nineGridView;

        gridLayout = binding.imageGridLayout;
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3);
        addImg = binding.addImg;
        content = binding.content;
        imageList = new ArrayList<>();
//        imageRecyclerView = binding.imageRecyclerView;
//        imageAdapter = new ImageAdapter(imageList);
//        imageRecyclerView.setAdapter(imageAdapter);
//        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(100,imageList);
            }
        });
        getSupportActionBar().setTitle("发布动态");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setContentView(binding.getRoot());
    }

    /** Picasso 加载 */
    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Picasso.get().load(url)//
                    .placeholder(R.drawable.default_img)//
                    .error(R.drawable.default_img)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    public void selectImage(int requestCode, ArrayList<ImageItem> imageItems) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(true);
        imagePicker.setShowCamera(true);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, imageItems);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS && data != null) {
            List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images.size()==9){
                addImg.setVisibility(View.GONE);
            }
            if (images != null && !images.isEmpty()) {
                // 处理选中的图片项列表
                switch (requestCode) {
                    // 根据不同的requestCode进行不同的处理
                    case 100:
                        // 处理选择图片的逻辑
                        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                        if (images != null) {
                            for (ImageItem image : images) {
                                imagePth.add(image.path);
                                String filePath = image.path;
                                File file = new File(filePath);
                                Uri uri = Uri.fromFile(file);
                                String url = uri.toString();
                                ImageInfo info = new ImageInfo();
                                info.setThumbnailUrl(url);
                                info.setBigImageUrl(url);
                                imageInfo.add(info);
                            }
                        }
                        nineGridView.setAdapter(new NineGridViewClickAdapter(getApplicationContext(), imageInfo));
                        break;
                    // 可以根据需要添加更多的case
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_posting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.publish://发布动态
                createPosting();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //发布动态
    private void createPosting() {
        HideUtil.hideSoftKeyboard(this);
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        MultipartBody.Part[] fileParts = new MultipartBody.Part[imagePth.size()];
        for(int i = 0;i<imagePth.size();i++){
            // 将 imagePath 转换为 File 对象
            File imageFile = new File(imagePth.get(i));
            RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("imgs", imageFile.getName(), requestBody1);
            fileParts[i]=filePart;

        }
        // 调用接口上传图片
        apiService.uploadImages(fileParts).enqueue(new Callback<ResponseResult<String>>() {
            @Override
            public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                String pth = response.body().getData();
                String jsonBody = "{\"images\":\"" + pth + "\", \"content\":\"" + content.getText() +"\""+ "}";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonBody);
                apiService.createUserPosting(requestBody).enqueue(new Callback<ResponseResult>() {
                    @Override
                    public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                        Toast.makeText(UserPostingActivity.this, "动态发布成功!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserPostingActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResponseResult> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseResult<String>> call, Throwable t) {

            }
        });


    }

}