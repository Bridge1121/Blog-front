package com.example.blogapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.response.CategoryResponse;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {

    private List<CategoryResponse> categoryResponses;
    private Context context;

    public CategorySpinnerAdapter(List<CategoryResponse> categoryResponses, Context context) {
        this.categoryResponses = categoryResponses;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categoryResponses.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryResponses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(context);
        view=_LayoutInflater.inflate(R.layout.custom_spinner_item, null);
        if(view!=null)
        {
            TextView name = view.findViewById(R.id.custom_text_view);
            name.setText(categoryResponses.get(i).getName());
        }
        return view;
    }
}
