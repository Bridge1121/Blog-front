package com.example.blogapplication;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.blogapplication.adapter.ContentPagerAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.fragment.HotFragment;
import com.example.blogapplication.fragment.RecommendFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerlayout;
    private NavigationView navigationView;
    private ContentPagerAdapter contentPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private ImageButton search;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        search = findViewById(R.id.img_search);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);  //打开homeAsUp按钮
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);   //为这个按钮设置图片
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home){
                    Toast.makeText(MainActivity.this, "跳转到我的主页", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.collect){
                    Toast.makeText(MainActivity.this, "跳转到我的收藏", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId()== R.id.exit){
                    Toast.makeText(MainActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.draft){
                    Toast.makeText(MainActivity.this, "跳转到我的草稿", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.edit){
                    Toast.makeText(MainActivity.this, "跳转到写博客", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                Toast.makeText(MainActivity.this,"跳转到搜索页面",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        tRecommend =  findViewById(R.id.id_recommend);
        tHot =  findViewById(R.id.id_hot);

        mTabLineIv =  findViewById(R.id.id_tab_line_iv);

        mPageVp =  findViewById(R.id.id_page_vp);
        init();
        initTabLineWidth();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home：是HomeAsUp按钮的默认id
        if(item.getItemId() == android.R.id.home){
            mDrawerlayout.openDrawer(GravityCompat.START);  //点击按钮后打开这个滑动窗口
            Toast.makeText(MainActivity.this,"打开了drawer",Toast.LENGTH_SHORT).show();
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
