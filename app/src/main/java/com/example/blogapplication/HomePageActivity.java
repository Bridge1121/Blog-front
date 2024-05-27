package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.blogapplication.databinding.ActivityHomePageBinding;
import com.example.blogapplication.fragment.PostingsFragment;
import com.example.blogapplication.fragment.RecommendFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomePageBinding binding;
    private int isMe;//是否是登录用户的主页
    private ViewPager viewPager;
    private TextView tvPostings;
    private TextView tvArticles;
    private List<Fragment> list;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        isMe = intent.getIntExtra("isMe",-1);//0是1不是
        viewPager = binding.viewPager;
        tvPostings = binding.tvPostings;
        tvArticles = binding.tvArticles;
        // 设置菜单栏的点击事件
        tvPostings.setOnClickListener(this);
        tvArticles.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());

        //把Fragment添加到List集合里面
        list = new ArrayList<>();
        list.add(RecommendFragment.newInstance());
        list.add(PostingsFragment.newInstance());
        adapter = new PagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tvArticles.setBackgroundColor(Color.RED);//被选中就为红色

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_articles:
                viewPager.setCurrentItem(0);
                tvArticles.setBackgroundColor(Color.RED);
                tvPostings.setBackgroundColor(Color.WHITE);
                break;
            case R.id.tv_postings:
                viewPager.setCurrentItem(1);
                tvArticles.setBackgroundColor(Color.WHITE);
                tvPostings.setBackgroundColor(Color.RED);
                break;
        }
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private FragmentManager mfragmentManager;
        private List<Fragment> fragmentList;

        public PagerAdapter(FragmentManager mfragmentManager,List<Fragment> fragmentList) {
            super(mfragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mfragmentManager = mfragmentManager;
            this.fragmentList = fragmentList;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


    }

    private class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    tvArticles.setBackgroundColor(Color.RED);
                    tvPostings.setBackgroundColor(Color.WHITE);
                    break;
                case 1:
                    tvArticles.setBackgroundColor(Color.WHITE);
                    tvPostings.setBackgroundColor(Color.RED);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}