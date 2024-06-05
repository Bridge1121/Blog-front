package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.SearchContent;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.PageVo;
import com.example.blogapplication.vo.SearchContentResponseVo;
import com.vip.search.SearchBean;
import com.vip.search.SearchLayout;
import com.vip.search.SearchList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private ApiService apiService;
    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchLayout searchLayout = findViewById(R.id.search_layout);
        SearchList searchList = findViewById(R.id.search_list);

        searchLayout.setOnTextSearchListener(searchContent  -> {
            return null;
        }, searchContent -> {
            //键盘点击了搜索
            Intent intent = new Intent(SearchActivity.this,ArticleListActivity.class);
            intent.putExtra("content",searchContent);
            startActivity(intent);
            searchList.doSearchContent(searchContent);
            return null;
        });

        //设置热门搜索列表
        ArrayList<SearchBean> hotList = new ArrayList<>();
        List<String> hotContentList = new ArrayList<>();
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(SearchActivity.this)).create(ApiService.class);
        apiService.getHotSearchContentList(1,8).enqueue(new Callback<ResponseResult<SearchContentResponseVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<SearchContentResponseVo>> call, Response<ResponseResult<SearchContentResponseVo>> response) {
                if (response.body().getData()!=null){
                    List<SearchContent> searchContents = response.body().getData().getRows();
                    for (SearchContent content:searchContents){
                        hotContentList.add(content.getContent());
                    }
                    for (int i = 0; i < hotContentList.size(); i++) {
                        SearchBean bean = new SearchBean();
                        bean.setContent(hotContentList.get(i));
                        bean.setShowLeftIcon(true);
                        Drawable drawable;
                        if (i < 2) {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_select);
                        } else if (i == 2) {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_ordinary);
                        } else {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_normal);
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            bean.setLeftIcon(drawable);
                        }

                        hotList.add(bean);
                    }
                    searchList.setHotList(hotList);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<SearchContentResponseVo>> call, Throwable t) {
                Log.e("获取热门搜索内容出错啦！！！",t.getMessage());
            }
        });

        //热门搜索条目点击事件
        searchList.setOnHotItemClickListener((searchContent, position) -> {
//            Toast.makeText(this, searchContent, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchActivity.this,ArticleListActivity.class);
            intent.putExtra("content",searchContent);
            startActivity(intent);
            return null;
        });
        //历史搜索条目点击事件
        searchList.setOnHistoryItemClickListener((searchContent, position) -> {
//            Toast.makeText(this, searchContent, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchActivity.this,ArticleListActivity.class);
            intent.putExtra("content",searchContent);
            startActivity(intent);
            return null;
        });
    }

    /**
     * 模拟热门搜索列表
     */
    private ArrayList<SearchBean> getHotList() {
        ArrayList<SearchBean> hotList = new ArrayList<>();
        List<String> hotContentList = new ArrayList<>();
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(SearchActivity.this)).create(ApiService.class);
        apiService.getHotSearchContentList(1,8).enqueue(new Callback<ResponseResult<SearchContentResponseVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<SearchContentResponseVo>> call, Response<ResponseResult<SearchContentResponseVo>> response) {
                Log.e("获取热门搜索啦！！！","hhhhhhhhhhhhhh");
                if (response.body().getData()!=null){
                    List<SearchContent> searchContents = response.body().getData().getRows();
                    for (SearchContent content:searchContents){
                        hotContentList.add(content.getContent());
                    }
                    for (int i = 0; i < hotContentList.size(); i++) {
                        SearchBean bean = new SearchBean();
                        bean.setContent(hotContentList.get(i));
                        bean.setShowLeftIcon(true);

                        Drawable drawable;
                        if (i < 2) {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_select);
                        } else if (i == 2) {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_ordinary);
                        } else {
                            drawable = ContextCompat.getDrawable(SearchActivity.this, R.drawable.shape_circle_normal);
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            bean.setLeftIcon(drawable);
                        }

                        hotList.add(bean);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<SearchContentResponseVo>> call, Throwable t) {
                Log.e("获取热门搜索内容出错啦！！！",t.getMessage());
            }
        });


        return hotList;
    }


}