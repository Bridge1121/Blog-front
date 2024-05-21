package com.example.blogapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.blogapplication.adapter.ContentPagerAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.fragment.HotFragment;
import com.example.blogapplication.fragment.RecommendFragment;
import com.example.blogapplication.ui.login.LoginViewModelFactory;
import com.example.blogapplication.utils.TokenUtils;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerlayout;
    private NavigationView navigationView;
    private ContentPagerAdapter contentPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private ImageButton search;
    private CircleImageView iconImg;//头像
    private TextView mail;
    private TextView phone;
    private TextView username;

    private ViewPager mPageVp;
    /**
     * Tab显示内容TextView
     */
    private TextView tHot, tRecommend;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLineIv;
    /**
     * Fragment
     */
    private RecommendFragment recommendFragment;
    private HotFragment hotFragment;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    private LoginViewModel loginViewModel;
    private CircleImageView avatar;
    private ApiService apiService;
    private RetrofitClient retrofitClient;
    List<CategoryResponse> categoryResponses = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        search = findViewById(R.id.img_search);
        iconImg = headerView.findViewById(R.id.icon_avatar);
        avatar = findViewById(R.id.icon_avatar);
        username = headerView.findViewById(R.id.username);
        mail = headerView.findViewById(R.id.mailtext);
        phone = headerView.findViewById(R.id.phoneNumber);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);  //打开homeAsUp按钮
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);   //为这个按钮设置图片


        }
        //drawer菜单项点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home){
                    if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this)==""){
                        Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "跳转到我的主页", Toast.LENGTH_SHORT).show();
                    }

                }
                if (item.getItemId() == R.id.collect){
                    if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this)==""){
                        Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "跳转到我的收藏", Toast.LENGTH_SHORT).show();
                    }

                }
                if (item.getItemId()== R.id.exit){//退出登录
                    if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this)==""){//未登录
                        Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{//已登录
                        loginViewModel.logout(getApplicationContext()).observe(MainActivity.this, new Observer<ResponseResult>() {
                            @Override
                            public void onChanged(ResponseResult responseResult) {
                                if (responseResult.getCode()==0) {
                                    // 退出登录成功
                                    username.setText("未登录/点击登录");
                                    iconImg.setImageResource(R.drawable.default_avatar);
                                    avatar.setImageResource(R.drawable.default_avatar);
                                    phone.setText("");
                                    mail.setText("");
                                    Toast.makeText(MainActivity.this, "退出登录成功！！", Toast.LENGTH_SHORT).show();

                                } else {
                                    // 登录失败，处理错误信息
                                    String errorMessage = responseResult.getMsg();
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    Log.e("退出登录出错啦！！！",errorMessage);
                                }
                            }
                        });
                    }
                    Toast.makeText(MainActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.draft){
                    if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this)==""){
                        Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "跳转到我的草稿", Toast.LENGTH_SHORT).show();
                    }

                }
                if (item.getItemId() == R.id.edit){
                    if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this)==""){
                        Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                        apiService.getCategoryList(TokenUtils.getUserInfo(getApplicationContext()).getId().toString()).enqueue(new Callback<ResponseResult<List<CategoryResponse>>>() {
                            @Override
                            public void onResponse(Call<ResponseResult<List<CategoryResponse>>> call, Response<ResponseResult<List<CategoryResponse>>> response) {
                                categoryResponses = response.body().getData();
                                TokenUtils.saveUserCategory(getApplicationContext(),categoryResponses);
                                Intent intent = new Intent(MainActivity.this,BlogEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("categories", (ArrayList<? extends Parcelable>) categoryResponses);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "跳转到写博客", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseResult<List<CategoryResponse>>> call, Throwable t) {

                            }
                        });

                    }

                }
                return true;
            }
        });

        //搜索图标点击事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                Toast.makeText(MainActivity.this,"跳转到搜索页面",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        //drawer里的头像点击事件
        iconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TokenUtils.getToken(MainActivity.this)!=""){//已登录
                    //todo 已登录就跳转到个人中心
                    Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                    startActivity(intent);
                }else{//未登录
                    Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //设置标题栏里的头像

        if (TokenUtils.getUserInfo(getApplicationContext())!=null){
            Picasso.get().load(TokenUtils.getUserInfo(getApplicationContext()).getAvatar()).into(avatar);
        }else{//未登录就显示默认头像
            avatar.setImageResource(R.drawable.default_avatar);
        }

        //标题栏里的头像点击事件
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TokenUtils.getToken(MainActivity.this)!=""){//已登录
                    //已登录就是修改个人信息
                    Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                    startActivity(intent);
                }else{//未登录
                    Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TokenUtils.getToken(MainActivity.this)!=""){//已登录
                    //todo 已登录就跳转到个人中心
                }else{//未登录
                    Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        tRecommend =  findViewById(R.id.id_recommend);
        tHot =  findViewById(R.id.id_hot);

        mTabLineIv =  findViewById(R.id.id_tab_line_iv);

        mPageVp =  findViewById(R.id.id_page_vp);
        init();
        initTabLineWidth();


    }

    //drawer菜单键点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home：是HomeAsUp按钮的默认id
        if(item.getItemId() == android.R.id.home){
            mDrawerlayout.openDrawer(GravityCompat.START);  //点击按钮后打开这个滑动窗口
            Toast.makeText(MainActivity.this,"打开了drawer",Toast.LENGTH_SHORT).show();
            User user = TokenUtils.getUserInfo(MainActivity.this);
            if (TokenUtils.getToken(MainActivity.this)!="" && !Objects.isNull(TokenUtils.getUserInfo(MainActivity.this))){//已登录
                username.setText(user.getNickName());
                Picasso.get().load(user.getAvatar()).into(iconImg);
                phone.setText(user.getPhonenumber());
                mail.setText(user.getEmail());
            }else{//未登录
                username.setText("未登录/点击登录");
                iconImg.setImageResource(R.drawable.default_avatar);
            }
        }
        return true;
    }




    private void init() {
        recommendFragment = new RecommendFragment();
        hotFragment = new HotFragment();
        mFragmentList.add(recommendFragment);
        mFragmentList.add(hotFragment);

        mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mPageVp.setAdapter(mFragmentAdapter);
        mPageVp.setCurrentItem(0);

        mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();

                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */

                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                }
                mTabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                int color = ContextCompat.getColor(MainActivity.this, R.color.teal_700);
                switch (position) {
                    case 0:
                        tRecommend.setTextColor(color);
                        break;
                    case 1:
                        tHot.setTextColor(color);
                        break;
                }
                currentIndex = position;
            }
        });

    }

    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 2;
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        tRecommend.setTextColor(Color.BLACK);
        tHot.setTextColor(Color.BLACK);
    }





}
