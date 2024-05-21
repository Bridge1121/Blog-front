package com.example.blogapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.blogapplication.entity.response.CategoryResponse;

import java.util.List;

public class CategorySpinner extends AppCompatSpinner {

    public CategorySpinner(Context context) {
        super(context);
    }

    public CategorySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategorySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCategories(List<CategoryResponse> categories) {
        ArrayAdapter<CategoryResponse> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
    }
}