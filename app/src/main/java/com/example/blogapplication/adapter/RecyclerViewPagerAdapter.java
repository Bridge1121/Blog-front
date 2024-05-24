package com.example.blogapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;

import java.util.List;
 
public class RecyclerViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Object> contents;
    public RecyclerViewPagerAdapter(List<Object> contents) {
        this.contents = contents;
    }
    public int getItemCount() {
        return contents.size();
    }
 
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.draft_item, parent, false);
 
        return new RecyclerView.ViewHolder(view) {
        };
 
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
 
    }
 
}