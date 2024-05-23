package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.databinding.ActivityArticleListBinding;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleListActivity extends AppCompatActivity {

    private ActivityArticleListBinding binding;
    private RecyclerView searchRecyclerView;
    private ImageButton search;
    private CircleImageView avatar;
    private ApiService apiService;
    private List<Article> articles;
    private String searchContent;
    private ArticleAdapter articleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchRecyclerView = binding.recyclerView;
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        // 为 RecyclerView 中的每个项设置点击监听器
        searchRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
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
        search = binding.imgSearch;
        avatar = binding.iconAvatar;
        if (TokenUtils.getToken(ArticleListActivity.this)!=""){//已登录
            Picasso.get().load(TokenUtils.getUserInfo(ArticleListActivity.this).getAvatar()).into(avatar);
        }else{
            avatar.setImageResource(R.drawable.default_avatar);
        }

        searchContent = getIntent().getStringExtra("content");
        searchArticle(searchContent,1,10);

        //搜索图标点击事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleListActivity.this,SearchActivity.class);
                Toast.makeText(ArticleListActivity.this,"跳转到搜索页面",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        //标题栏里的头像点击事件
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TokenUtils.getToken(ArticleListActivity.this)!=""){//已登录
                    //已登录就是修改个人信息
                    Intent intent = new Intent(ArticleListActivity.this,UserInfoActivity.class);
                    startActivity(intent);
                }else{//未登录
                    Toast.makeText(ArticleListActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ArticleListActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void onItemClick(int position) {
        Toast.makeText(getApplicationContext(),articles.get(position).getId().toString(),Toast.LENGTH_SHORT).show();
    }

    //搜索文章
    private void searchArticle(String content,Integer pageNum,Integer pageSize){
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.searchArticle(pageNum,pageSize,content).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                articles = response.body().getData().getArticles();
                articleAdapter = new ArticleAdapter(ArticleListActivity.this,  articles);
                searchRecyclerView.setAdapter(articleAdapter);
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("搜索文章出错啦！！！！",t.getMessage());
            }
        });

    }
}