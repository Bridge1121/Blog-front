package com.example.blogapplication;

import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.example.blogapplication.adapter.CategorySpinnerAdapter;
import com.example.blogapplication.databinding.ActivityBlogEditBinding;
import com.example.blogapplication.dto.AddArticleDto;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.utils.KeyBoardUtils;
import com.example.blogapplication.utils.RichUtils;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.utils.popup.CommonPopupWindow;
import com.example.blogapplication.view.RichEditor;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCropActivity;
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

public class BlogEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityBlogEditBinding binding;
    RxPermissions rxPermissions;
    private ArrayList<ImageItem> selectImages = new ArrayList<>();

    private CommonPopupWindow popupWindow; //编辑图片的pop
    private String currentUrl = "";

    private int isFrom;//0:表示正常编辑  1:表示是重新编辑

    private Spinner spinner;
    private Long categoryId;
    private List<CategoryResponse> categoryResponses = new ArrayList<>();

    private ApiService apiService;

    private String imgPath;
    private Dialog dialog;
    private View inflate;
    private AddArticleDto addArticleDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            categoryResponses = bundle.getParcelableArrayList("categories");
//        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blog_edit);
        HideUtil.init(BlogEditActivity.this);
        isFrom = getIntent().getIntExtra("isFrom", 0);
        binding.setOnClickListener(this);
        rxPermissions = new RxPermissions(this);
        spinner = binding.spinnerCategory;
        categoryResponses = TokenUtils.getUserCategoryInfo(getApplicationContext());
        if (categoryResponses!=null && categoryResponses.size()>0){
            CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(categoryResponses,getApplicationContext());
            spinner.setAdapter(categorySpinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    categoryId = new Long(categoryResponses.get(i).getId());
                    Toast.makeText(getApplicationContext(),categoryResponses.get(i).getName(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        initPop();
        initEditor();
        if (isFrom == 1) {//如果是重新编辑的话
//            SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
//            String title = sharedPreferences.getString("title", "title");
//            String content = sharedPreferences.getString("content", "");
            String title = getIntent().getStringExtra("title");
            String content = getIntent().getStringExtra("content");
            String imgPath = getIntent().getStringExtra("thumbnail");
            Long categoryId = getIntent().getLongExtra("categoryId",0);
            for (int i = 0;i<categoryResponses.size();i++){
                if (categoryResponses.get(i).getId()==categoryId){
                    spinner.setSelection(i);
                }
            }

            binding.editName.setText(title);
            binding.richEditor.setHtml(content);
            Picasso.get().load(imgPath).into(binding.imageViewthumbnail);
            binding.txtPublish.setSelected(true);
            binding.txtPublish.setEnabled(true);
        }
    }

    private void initEditor() {
        //输入框显示字体的大小
        binding.richEditor.setEditorFontSize(18);
        //输入框显示字体的颜色
        binding.richEditor.setEditorFontColor(getResources().getColor(R.color.black1b));
        //输入框背景设置
        binding.richEditor.setEditorBackgroundColor(Color.WHITE);
        //输入框文本padding
        binding.richEditor.setPadding(10, 10, 10, 10);
        //输入提示文本
        binding.richEditor.setPlaceholder("请开始你的创作！~");

        //文本输入框监听事件
        binding.richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.e("富文本文字变动", text);
                if (TextUtils.isEmpty(binding.editName.getText().toString().trim())) {
                    binding.txtPublish.setSelected(false);
                    binding.txtPublish.setEnabled(false);
                    return;
                }

                if (TextUtils.isEmpty(text)) {
                    binding.txtPublish.setSelected(false);
                    binding.txtPublish.setEnabled(false);
                } else {

                    if (TextUtils.isEmpty(Html.fromHtml(text))) {
                        binding.txtPublish.setSelected(false);
                        binding.txtPublish.setEnabled(false);
                    } else {
                        binding.txtPublish.setSelected(true);
                        binding.txtPublish.setEnabled(true);
                    }

                }

            }
        });

        binding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String html = binding.richEditor.getHtml();

                if (TextUtils.isEmpty(html)) {
                    binding.txtPublish.setSelected(false);
                    binding.txtPublish.setEnabled(false);
                    return;
                } else {
                    if (TextUtils.isEmpty(Html.fromHtml(html))) {
                        binding.txtPublish.setSelected(false);
                        binding.txtPublish.setEnabled(false);
                        return;
                    } else {
                        binding.txtPublish.setSelected(true);
                        binding.txtPublish.setEnabled(true);
                    }
                }


                if (TextUtils.isEmpty(s.toString())) {
                    binding.txtPublish.setSelected(false);
                    binding.txtPublish.setEnabled(false);
                } else {
                    binding.txtPublish.setSelected(true);
                    binding.txtPublish.setEnabled(true);
                }


            }
        });

        binding.richEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                ArrayList<String> flagArr = new ArrayList<>();
                for (int i = 0; i < types.size(); i++) {
                    flagArr.add(types.get(i).name());
                }

                if (flagArr.contains("BOLD")) {
                    binding.buttonBold.setImageResource(R.mipmap.bold_);
                } else {
                    binding.buttonBold.setImageResource(R.mipmap.bold);
                }

                if (flagArr.contains("UNDERLINE")) {
                    binding.buttonUnderline.setImageResource(R.mipmap.underline_);
                } else {
                    binding.buttonUnderline.setImageResource(R.mipmap.underline);
                }


                if (flagArr.contains("ORDEREDLIST")) {
                    binding.buttonListUl.setImageResource(R.mipmap.list_ul);
                    binding.buttonListOl.setImageResource(R.mipmap.list_ol_);
                } else {
                    binding.buttonListOl.setImageResource(R.mipmap.list_ol);
                }

                if (flagArr.contains("UNORDEREDLIST")) {
                    binding.buttonListOl.setImageResource(R.mipmap.list_ol);
                    binding.buttonListUl.setImageResource(R.mipmap.list_ul_);
                } else {
                    binding.buttonListUl.setImageResource(R.mipmap.list_ul);
                }

            }
        });


        binding.richEditor.setImageClickListener(new RichEditor.ImageClickListener() {
            @Override
            public void onImageClick(String imageUrl) {
                currentUrl = imageUrl;
                popupWindow.showBottom(binding.getRoot(), 0.5f);
            }
        });


    }


    private void initPop() {
        View view = LayoutInflater.from(BlogEditActivity.this).inflate(R.layout.newapp_pop_picture, null);
        view.findViewById(R.id.linear_cancle).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        view.findViewById(R.id.linear_editor).setOnClickListener(v -> {
            //编辑图片

            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                if (aBoolean) {
                    if (ActivityCompat.checkSelfPermission(BlogEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BlogEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(BlogEditActivity.this, UCropActivity.class);
                        intent.putExtra("filePath", currentUrl);
                        String destDir = getFilesDir().getAbsolutePath().toString();
                        String fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg";
                        intent.putExtra("outPath", destDir + fileName);
                        startActivityForResult(intent, 11);
                        popupWindow.dismiss();

                    }
                } else {
                    Toast.makeText(BlogEditActivity.this, "相册需要此权限", Toast.LENGTH_SHORT).show();
                }
            });
        });

        view.findViewById(R.id.linear_delete_pic).setOnClickListener(v -> {
            //删除图片

            String removeUrl = "<img src=\"" + currentUrl + "\" alt=\"dachshund\" width=\"100%\"><br>";

            String newUrl = binding.richEditor.getHtml().replace(removeUrl, "");
            currentUrl = "";
            binding.richEditor.setHtml(newUrl);
            if (RichUtils.isEmpty(binding.richEditor.getHtml())) {
                binding.richEditor.setHtml("");
            }
            popupWindow.dismiss();
        });
        popupWindow = new CommonPopupWindow.Builder(BlogEditActivity.this)
                .setView(view)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)//在外不可用手指取消
                .setAnimationStyle(R.style.pop_animation)//设置popWindow的出场动画
                .create();


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.richEditor.setInputEnabled(true);
            }
        });
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
        binding.imageViewthumbnail.setImageBitmap(bm);
        imgPath = imagePath;

    }

    public void show(View view){
        dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog_save, null);
        //初始化控件
//        save = (TextView) inflate.findViewById(R.id.choosePhoto);
//        takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        inflate.findViewById(R.id.save).setOnClickListener(this);
        inflate.findViewById(R.id.publish).setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    //重新编辑且未编辑thumbnail时保存草稿
    private void draftArticleAgain(){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        HideUtil.hideSoftKeyboard(BlogEditActivity.this);
        addArticleDto = new AddArticleDto(getIntent().getLongExtra("id",0),getIntent().getStringExtra("thumbnail"),"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","1","1");
        Log.i("articleInfo", addArticleDto.toJson());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
        apiService.updateArticle(requestBody).enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                Toast.makeText(BlogEditActivity.this, "保存草稿成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.e("保存草稿出错啦！！！",t.getMessage());
            }
        });
    }

    //初次编辑保存草稿
    private void draftArticle(){
        HideUtil.hideSoftKeyboard(BlogEditActivity.this);
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
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

                if (isFrom==1){//重新编辑，调用更新接口
                    addArticleDto = new AddArticleDto(addArticleDto.getId(),imgPath,"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","1","1");
                    Log.i("articleInfo", addArticleDto.toJson());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
                    apiService.updateArticle(requestBody).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            Toast.makeText(BlogEditActivity.this, "保存草稿成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("重新编辑草稿出错啦！！！",t.getMessage());
                        }
                    });
                }else{//新增，调用新增接口
                    addArticleDto = new AddArticleDto(imgPath,"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","1","1");
                    Log.i("articleInfo", addArticleDto.toJson());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
                    apiService.add(requestBody).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            Toast.makeText(BlogEditActivity.this, "保存草稿成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("保存草稿出错啦！！！",t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<String>> call, Throwable t) {

            }
        });
    }

    private void publishArticle(){
        HideUtil.hideSoftKeyboard(BlogEditActivity.this);
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        // 将 imagePath 转换为 File 对象
        File imageFile1 = new File(imgPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile1);
        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("img", imageFile1.getName(), requestBody);
        // 调用接口上传图片
        apiService.uploadImg(filePart1).enqueue(new Callback<ResponseResult<String>>() {
            @Override
            public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                imgPath = response.body().getData();
                Toast.makeText(getApplicationContext(),"图片上传成功",Toast.LENGTH_SHORT);
                Log.i("imgpath!!!!!!!",imgPath);

                if (isFrom==1){//重新编辑
                    addArticleDto = new AddArticleDto(addArticleDto.getId(),imgPath,"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","0","1");
                    Log.i("articleInfo", addArticleDto.toJson());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
                    apiService.updateArticle(requestBody).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            Toast.makeText(BlogEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("重新编辑文章出错啦！！！",t.getMessage());
                        }
                    });

                }else{
                    addArticleDto = new AddArticleDto(imgPath,"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","0","1");
                    Log.i("articleInfo", addArticleDto.toJson());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
                    apiService.add(requestBody).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            Toast.makeText(BlogEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("新增文章出错啦！！！",t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<String>> call, Throwable t) {

            }
        });
    }

    private void publishArticleAgain(){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
//        KeyBoardUtils.closeKeybord(binding.richEditor,getApplicationContext());
        HideUtil.hideSoftKeyboard(BlogEditActivity.this);
        addArticleDto = new AddArticleDto(getIntent().getLongExtra("id",0),getIntent().getStringExtra("thumbnail"),"hhhhhhhh",binding.editName.getText().toString().trim(),binding.richEditor.getHtml(),categoryId,"0","0","1");
        Log.i("articleInfo", addArticleDto.toJson());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), addArticleDto.toJson());
        apiService.updateArticle(requestBody).enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                Toast.makeText(BlogEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BlogEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.e("新增文章出错啦！！！",t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewthumbnail://选择文章缩略图
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

                break;

            case R.id.save://保存为草稿
                draftArticle();
                dialog.dismiss();
                break;
            case R.id.publish://发布文章
                publishArticle();
                dialog.dismiss();
                break;
            case R.id.txt_finish:
                finish();
                break;

            case R.id.txt_publish://点击保存，打开弹窗
                HideUtil.hideSoftKeyboard(BlogEditActivity.this);
                new AlertView("如何保存", null, "取消", null,
                        new String[]{"发布文章", "保存为草稿"},
                        this, AlertView.Style.ActionSheet, new OnItemClickListener(){
                    public void onItemClick(Object o,int position){
                        Toast.makeText(getApplicationContext(), "点击了第" + position + "个",
                                Toast.LENGTH_SHORT).show();
                        if (position == 0){//发布文章
                            if (imgPath==null){
                                publishArticleAgain();
                            }else{
                                publishArticle();
                            }
                        }
                        if (position==1){//保存为草稿
                            if (imgPath==null){
                                draftArticleAgain();
                            }else{
                                draftArticle();
                            }
                        }
                    }
                }).show();
//                show(v);
                break;

            case R.id.button_rich_do:
                //反撤销
                binding.richEditor.redo();
                break;
            case R.id.button_rich_undo:
                //撤销
                binding.richEditor.undo();
                break;

            case R.id.button_bold:
                //加粗
                againEdit();
                binding.richEditor.setBold();
                break;

            case R.id.button_underline:
                //加下划线
                againEdit();
                binding.richEditor.setUnderline();
                break;

            case R.id.button_list_ul:
                //加带点的序列号
                againEdit();
                binding.richEditor.setBullets();
                break;

            case R.id.button_list_ol:
                //加带数字的序列号
                againEdit();
                binding.richEditor.setNumbers();
                break;


            case R.id.button_image:
                if (!TextUtils.isEmpty(binding.richEditor.getHtml())) {
                    ArrayList<String> arrayList = RichUtils.returnImageUrlsFromHtml(binding.richEditor.getHtml());
                    if (arrayList != null && arrayList.size() >= 9) {
                        Toast.makeText(BlogEditActivity.this, "最多添加9张照片~", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (ActivityCompat.checkSelfPermission(BlogEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BlogEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            selectImage(104, selectImages);
                            KeyBoardUtils.closeKeybord(binding.richEditor, BlogEditActivity.this);
                        }
                    } else {
                        Toast.makeText(BlogEditActivity.this, "相册需要此权限~", Toast.LENGTH_SHORT).show();

                    }
                });

                break;


        }
    }

    private void againEdit() {
        //如果第一次点击例如加粗，没有焦点时，获取焦点并弹出软键盘
        binding.richEditor.focusEditor();
        KeyBoardUtils.openKeybord(binding.editName, BlogEditActivity.this);
    }

    public void selectImage(int requestCode, ArrayList<ImageItem> imageItems) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, imageItems);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (requestCode == 104) {
                selectImages.clear();
                ArrayList<ImageItem> selects = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selectImages.addAll(selects);

                againEdit();
                binding.richEditor.insertImage(selectImages.get(0).path, "dachshund");

                KeyBoardUtils.openKeybord(binding.editName, BlogEditActivity.this);
                binding.richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.richEditor != null) {
                            binding.richEditor.scrollToBottom();
                        }
                    }
                }, 200);
            }
        } else if (resultCode == -1) {
            if (requestCode == 11) {
                String outPath = data.getStringExtra(EXTRA_OUTPUT_URI);
                if (!TextUtils.isEmpty(outPath)) {
                    String newHtml = binding.richEditor.getHtml().replace(currentUrl, outPath);

                    binding.richEditor.setHtml(newHtml);
                    currentUrl = "";
                }
            }
        }
    }



}