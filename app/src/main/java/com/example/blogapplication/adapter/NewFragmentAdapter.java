package com.example.blogapplication.adapter;
 


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.blogapplication.entity.FragmentInfo;

import java.util.ArrayList;


public class NewFragmentAdapter extends FragmentStatePagerAdapter {
    //FragmentStatePagerAdapter
    // FragmentPagerAdapter
    //两者的区别在于一个状态FragmentStatePagerAdapter表示只创建一个PageView
    // FragmentPagerAdapter一次性全部创建
    ArrayList<FragmentInfo> mFragments;
    public NewFragmentAdapter(FragmentManager fm, ArrayList<FragmentInfo> fragments) {
        super(fm);
        this.mFragments=fragments;
    }
 
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position).getmFragment();
    }
 
    @Override
    public int getCount() {
        return mFragments.size();
    }
    //返回页面标题
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).getmTitle();
    }
}