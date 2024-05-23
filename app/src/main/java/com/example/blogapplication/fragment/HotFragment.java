package com.example.blogapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.adapter.HotArticleAdapter;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotFragment extends Fragment implements View.OnClickListener {

    private List<Article> articles = new ArrayList<>();
    private ApiService apiService;
    private RecyclerView mRecyclerView;
    private HotArticleAdapter hotArticleAdapter;

    public static Fragment newInstance() {
        return new HotFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            private float startX, startY;
            private boolean isClick;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = e.getX();
                        startY = e.getY();
                        isClick = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(e.getX() - startX) > 10 || Math.abs(e.getY() - startY) > 10) {
                            isClick = false; // 滑动距离大于阈值则不是点击事件
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            View child = rv.findChildViewUnder(e.getX(), e.getY());
                            int position = rv.getChildAdapterPosition(child);
                            if (position != RecyclerView.NO_POSITION) {
                                // 处理点击事件
                                onItemClick(position);
                            }
                        }
                        break;
                }
                return false;
            }
        });

//        buttonLoadData = view.findViewById(R.id.buttonLoadData);
//        buttonLoadData.setOnClickListener(this);

        return view;
    }

    private void onItemClick(int position) {
        Toast.makeText(getActivity(),articles.get(position).getId().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.getHotArticleList().enqueue(new Callback<ResponseResult<List<Article>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Article>>> call, Response<ResponseResult<List<Article>>> response) {
                articles = response.body().getData();
                hotArticleAdapter = new HotArticleAdapter(getActivity(), articles);
                mRecyclerView.setAdapter(hotArticleAdapter);
            }

            @Override
            public void onFailure(Call<ResponseResult<List<Article>>> call, Throwable t) {
                Log.e("热门文章获取出错啦！！！！", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.buttonLoadData) {
//            initRecyclerView();
//        }
    }
}