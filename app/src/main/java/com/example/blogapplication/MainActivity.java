package com.example.blogapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.blogapplication.adapter.ContentPagerAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.fragment.HotFragment;
import com.example.blogapplication.fragment.PostingsFragment;
import com.example.blogapplication.fragment.RecommendFragment;
import com.example.blogapplication.ui.login.LoginViewModelFactory;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.material.navigation.NavigationView;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
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
//    private CircleImageView avatar;
    private ApiService apiService;
    private RetrofitClient retrofitClient;
    List<CategoryResponse> categoryResponses = new ArrayList<>();

    private MaterialViewPager mViewPager;
    static final int TAPS = 3;
    private Toolbar toolbar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mViewPager = findViewById(R.id.materialViewPager);
        mDrawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        search = findViewById(R.id.img_search);
        iconImg = headerView.findViewById(R.id.current_userAvater);
//        avatar = findViewById(R.id.icon_avatar);
        username = headerView.findViewById(R.id.current_userName);
        mail = headerView.findViewById(R.id.current_userEmail);
//        phone = headerView.findViewById(R.id.phoneNumber);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            // 在setSupportActionBar之后，添加这段代码
            // 为 ActionBar 的图标设置点击事件
//            actionBar.setIcon(R.drawable.search);
//            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //设置viewpager的背景图片
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        //设置Adapter
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % TAPS) {
                    case 0:
                        return RecommendFragment.newInstance();
                    case 1://热门文章页面
                        return HotFragment.newInstance();
                    default:
                        return PostingsFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return TAPS;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % TAPS) {
                    case 0:
                        return "推荐";

                    case 1:
                        return "热门";
                    default:
                        return "动态";
                }
            }
        });

        //设置setViewPager
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        onNavgationViewMenuItemSelected(navigationView);



        //drawer里的头像点击事件
        iconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TokenUtils.getToken(MainActivity.this) != "") {//已登录
                    //todo 已登录就跳转到主页,主页还没写
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                } else {//未登录
                    Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });



        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TokenUtils.getToken(MainActivity.this) != "") {//已登录
                    //todo 已登录就跳转到个人中心
                } else {//未登录
                    Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    //drawer菜单键点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home：是HomeAsUp按钮的默认id
        if (item.getItemId() == android.R.id.home) {
            mDrawerlayout.openDrawer(GravityCompat.START);  //点击按钮后打开这个滑动窗口
            Toast.makeText(MainActivity.this, "打开了drawer", Toast.LENGTH_SHORT).show();
            User user = TokenUtils.getUserInfo(MainActivity.this);
            if (TokenUtils.getToken(MainActivity.this) != "" && !Objects.isNull(TokenUtils.getUserInfo(MainActivity.this))) {//已登录
                username.setText(user.getNickName());
                Picasso.get().load(user.getAvatar()).into(iconImg);
//                phone.setText(user.getPhonenumber());
                mail.setText(user.getEmail());
            } else {//未登录
                username.setText("未登录/点击登录");
                iconImg.setImageResource(R.drawable.ic_default_avatar_grey_24dp);
            }
            // 取消所有选中的菜单项
            Menu menu = navigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                menuItem.setChecked(false);
            }
        }
        if (item.getItemId() == R.id.action_search){
            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
            Toast.makeText(MainActivity.this,"跳转到搜索页面",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        return true;
    }




    /**
     * drawer菜单栏监听事件
     *
     * @param mNav
     */
    private void onNavgationViewMenuItemSelected(NavigationView mNav) {
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                String msgString = "";

                switch (menuItem.getItemId()) {

                    case R.id.nav_home:
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == "") {
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {//跳转到主页
                            Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "跳转到我的主页", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_favorites:
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == "") {
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, StarListActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "跳转到我的收藏", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.nav_exit://退出登录
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == "") {//未登录
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {//已登录
                            loginViewModel.logout(getApplicationContext()).observe(MainActivity.this, new Observer<ResponseResult>() {
                                @Override
                                public void onChanged(ResponseResult responseResult) {
                                    if (responseResult.getCode() == 0) {
                                        // 退出登录成功
                                        username.setText("未登录/点击登录");
                                        iconImg.setImageResource(R.drawable.ic_default_avatar_grey_24dp);//drawer里的头像
//                                        avatar.setImageResource(R.drawable.default_avatar);//标题栏的头像
//                                        phone.setText("");
                                        mail.setText("");
                                        Toast.makeText(MainActivity.this, "退出登录成功！！", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // 登录失败，处理错误信息
                                        String errorMessage = responseResult.getMsg();
                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        Log.e("退出登录出错啦！！！", errorMessage);
                                    }
                                }
                            });
                        }
                        Toast.makeText(MainActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_draft:
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == "") {
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                            apiService.getCategoryList(TokenUtils.getUserInfo(getApplicationContext()).getId().toString()).enqueue(new Callback<ResponseResult<List<CategoryResponse>>>() {
                                @Override
                                public void onResponse(Call<ResponseResult<List<CategoryResponse>>> call, Response<ResponseResult<List<CategoryResponse>>> response) {
                                    TokenUtils.deleteCategoryInfo(getApplicationContext());
                                    categoryResponses = response.body().getData();
                                    TokenUtils.saveUserCategory(getApplicationContext(), categoryResponses);
                                    Intent intent = new Intent(MainActivity.this, DraftListActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelableArrayList("categories", (ArrayList<? extends Parcelable>) categoryResponses);
//                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "跳转到我的草稿", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseResult<List<CategoryResponse>>> call, Throwable t) {
                                    Log.e("获取全部文章分类列表出错啦！！！",t.getMessage());

                                }
                            });

                        }

                        break;
                    case R.id.nav_write:
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == "") {
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                            apiService.getCategoryList(TokenUtils.getUserInfo(getApplicationContext()).getId().toString()).enqueue(new Callback<ResponseResult<List<CategoryResponse>>>() {
                                @Override
                                public void onResponse(Call<ResponseResult<List<CategoryResponse>>> call, Response<ResponseResult<List<CategoryResponse>>> response) {
                                    TokenUtils.deleteCategoryInfo(getApplicationContext());
                                    categoryResponses = response.body().getData();
                                    TokenUtils.saveUserCategory(getApplicationContext(), categoryResponses);
                                    Intent intent = new Intent(MainActivity.this, BlogEditActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelableArrayList("categories", (ArrayList<? extends Parcelable>) categoryResponses);
//                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "跳转到写博客", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseResult<List<CategoryResponse>>> call, Throwable t) {
                                    Log.e("获取全部文章分类列表出错啦！！！",t.getMessage());

                                }
                            });

                        }

                        break;
                    case R.id.nav_write_posting://跳转到发布动态
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == ""){
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(MainActivity.this,UserPostingActivity.class);
                            startActivity(intent);
                        }
                            break;
                    case R.id.nav_following:
                        if (Objects.isNull(TokenUtils.getToken(MainActivity.this)) || TokenUtils.getToken(MainActivity.this) == ""){
                            Toast.makeText(MainActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(MainActivity.this,FollowersActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
                // Menu item点击后选中，并关闭Drawerlayout
                menuItem.setChecked(true);
                mDrawerlayout.closeDrawers();
                return true;

            }
            });
        }


    }
