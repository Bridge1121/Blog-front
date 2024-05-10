package com.example.blogapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.blogapplication.fragment.RecommendFragment;

import java.util.List;

public class ContentPagerAdapter extends FragmentStateAdapter {
    private List<RecommendFragment> datas;

    public ContentPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<RecommendFragment> datas) {
        super(fragmentActivity);
        this.datas = datas;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return datas.get(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
