package com.example.blogapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private List<Fragment> list;
    private View view;
    private ViewPager viewPager;
    private Button buttonHot,buttonRecommend;
    private ListView hotListview;
    private HotArticleAdapter hotArticleAdapter;
    private List<Article> articles = new ArrayList<>();
    private ApiService apiService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_hot,container,false);
        initView();
        return view;
    }

    private void initView() {
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.getHotArticleList().enqueue(new Callback<ResponseResult<List<Article>>>() {
            @Override
            public void onResponse(Call<ResponseResult<List<Article>>> call, Response<ResponseResult<List<Article>>> response) {
//                JsonArray result = (JsonArray) response.body().getData();
//                for(int i = 0;i<result.size();i++){
//                    Article article = new Article();
//                    JsonObject jo = result.get(i).getAsJsonObject();
//                    article.setTitle(jo.get("title").getAsString());
//                    article.setId(jo.get("id").getAsLong());
//                    articles.add(article);
//                }
                articles = response.body().getData();
                hotArticleAdapter = new HotArticleAdapter(getActivity(),  articles);
                hotListview.setAdapter(hotArticleAdapter);


            }

            @Override
            public void onFailure(Call<ResponseResult<List<Article>>> call, Throwable t) {
                Log.e("热门文章获取出错啦！！！！",t.getMessage());
            }
        });
        hotListview = view.findViewById(R.id.hot_article_listview);


        //item点击事件
        hotListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),articles.get(i).getId().toString(),Toast.LENGTH_SHORT).show();
            }
        });
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