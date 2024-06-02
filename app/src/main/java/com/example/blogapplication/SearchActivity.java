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
import com.example.blogapplication.entity.response.ArticleResponse;
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
            //搜索内容改变
//            Toast.makeText(SearchActivity.this,searchContent,Toast.LENGTH_SHORT).show();
            return null;
        }, searchContent -> {
            //键盘点击了搜索
//            Toast.makeText(SearchActivity.this,searchContent,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchActivity.this,ArticleListActivity.class);
            intent.putExtra("content",searchContent);
            startActivity(intent);
            searchList.doSearchContent(searchContent);
            return null;
        });

        //设置用于测试的热门搜索列表
        searchList.setHotList(getHotList());
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
        String[] testHotList = {"二流小码农", "三流小可爱", "Android", "Kotlin", "iOS", "Java", "Python", "Php是世界上最好的语言"};

        for (int i = 0; i < testHotList.length; i++) {
            SearchBean bean = new SearchBean();
            bean.setContent(testHotList[i]);
            bean.setShowLeftIcon(true);

            Drawable drawable;
            if (i < 2) {
                drawable = ContextCompat.getDrawable(this, R.drawable.shape_circle_select);
            } else if (i == 2) {
                drawable = ContextCompat.getDrawable(this, R.drawable.shape_circle_ordinary);
            } else {
                drawable = ContextCompat.getDrawable(this, R.drawable.shape_circle_normal);
            }
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                bean.setLeftIcon(drawable);
            }

            hotList.add(bean);
        }

        return hotList;
    }


}