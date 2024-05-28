package com.example.blogapplication.entity;

import androidx.fragment.app.Fragment;

public class FragmentInfo {

    private Fragment fragment;
    private String title;

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FragmentInfo(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getmFragment() {
        return fragment;
    }

    public CharSequence getmTitle() {
        return title;
    }
}
