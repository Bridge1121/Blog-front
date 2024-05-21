package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blogapplication.databinding.ActivitySelectCategoryBinding;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCategoryActivity extends AppCompatActivity {
    private ViewDataBinding binding;
    private Spinner spinner_category;
    private ArrayAdapter arrayAdapter;
    //定义字符串数组,指定数组的元素
    private String[] spinner = new String[]{};
    private List<CategoryResponse> categoryResponses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_category);
        spinner_category = findViewById(R.id.spinner_category);
        //参数1.上下文对象 参数2.列表项的样式,Android为我们提供的资源样式为：android.R.layout.simple_spinner_item
        //参数3.定义的字符串数组
        arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,spinner);
        //设置适配器列表框下拉时的列表样式
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器与下拉列表框关联起来
        spinner_category.setAdapter(arrayAdapter);
        String s = spinner_category.getSelectedItem().toString();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}