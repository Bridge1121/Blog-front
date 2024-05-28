package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blogapplication.databinding.ActivityHomePageBinding;
import com.example.blogapplication.entity.FragmentInfo;
import com.example.blogapplication.entity.User;
import com.example.blogapplication.fragment.ArticleFragment;
import com.example.blogapplication.fragment.PostingsFragment;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.material.tabs.TabLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomePageBinding binding;
    private int isMe;//是否是登录用户的主页
    private CircleImageView avatar;
    private TextView username;
    private TextView email;
    private TextView followers;
    private TextView fans;
    private MaterialViewPager mViewPager;
    static final int TAPS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        mViewPager = binding.materialViewPager;

        View headerLogo = mViewPager.findViewById(R.id.header);
        // 从headerLogo布局中找到对应的子组件
        username = headerLogo.findViewById(R.id.current_userName);
        email = headerLogo.findViewById(R.id.current_userEmail);
        avatar = headerLogo.findViewById(R.id.current_userAvater);
        followers = headerLogo.findViewById(R.id.followers);
        fans = headerLogo.findViewById(R.id.fans);
//        avatar = binding.currentUserAvater;
//        username = binding.currentUserName;
//        email = binding.currentUserEmail;
//        followers = binding.followers;
//        fans = binding.fans;
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        isMe = intent.getIntExtra("isMe",-1);//0是1不是
        if (isMe == 0){//自己的主页
            User userInfo = TokenUtils.getUserInfo(getApplicationContext());
            Picasso.get().load(userInfo.getAvatar()).into(avatar);
            username.setText(userInfo.getNickName());
            email.setText(userInfo.getEmail());
            followers.setText("关注："+userInfo.getFollowers().toString());
            fans.setText("粉丝："+userInfo.getFans().toString());
        }

        //设置viewpager的背景图片
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.blue,
                                "http://scy727740.hd-bkt.clouddn.com/2024/05/28/e522371b1db54aa4b00b67d784b47b7d.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                com.github.florent37.materialviewpager.R.color.green,
                                "http://scy727740.hd-bkt.clouddn.com/2024/05/28/e6bd892590b9428c8fa485369568971e.jpg");
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
                        return ArticleFragment.newInstance();
                    default://热门文章页面
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
                        return "文章";

                    default:
                        return "动态";
                }
            }
        });

        //设置setViewPager
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 在这里处理返回按钮被点击的操作，例如返回上一页
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tv_articles:
//                viewPager.setCurrentItem(0);
//                tvArticles.setBackgroundColor(Color.RED);
//                tvPostings.setBackgroundColor(Color.WHITE);
//                break;
//            case R.id.tv_postings:
//                viewPager.setCurrentItem(1);
//                tvArticles.setBackgroundColor(Color.WHITE);
//                tvPostings.setBackgroundColor(Color.RED);
//                break;
        }
    }





}