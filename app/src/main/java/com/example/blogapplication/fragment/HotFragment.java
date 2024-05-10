package com.example.blogapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blogapplication.R;
import com.example.blogapplication.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class HotFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private List<Fragment> list;
    private View view;
    private ViewPager viewPager;
    private Button buttonHot,buttonRecommend;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_hot,container,false);
//        initView();
        return view;
    }



    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {


    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {

    }

}