package com.example.blogapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private List<Fragment> list;
    private View view;
    private ViewPager viewPager;
    private Button buttonHot,buttonRecommend;
    private ListView article_listview;
    private ArticleAdapter articleAdapter;
    private List<Article> articles = new ArrayList<>();
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_recommend,container,false);
        initView();
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

    private void initView(){
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.getRecommandArticleList(1,10).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                articles = response.body().getData().getArticles();
                article_listview = view.findViewById(R.id.article_listview);
                articleAdapter = new ArticleAdapter(getActivity(),  articles);
                article_listview.setAdapter(articleAdapter);
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("推荐文章出错啦！！！",t.getMessage());

            }
        });

    }

}
