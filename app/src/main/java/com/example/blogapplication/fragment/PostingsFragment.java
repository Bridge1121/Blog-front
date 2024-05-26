package com.example.blogapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.ArticleDetailActivity;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.PostingsAdapter;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.entity.response.UserPostingsResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.UserPostingsVo;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostingsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView mRecyclerView;
    private PostingsAdapter postingsAdapter;
    private List<UserPostingsVo> postingsVos = new ArrayList<>();
    private ApiService apiService;

    public static Fragment newInstance() {
        return new PostingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        NineGridView.setImageLoader(new PicassoImageLoader());//初始化图片加载器

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        // 为 RecyclerView 中的每个项设置点击监听器
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
        return view;
    }

    /** Picasso 加载 */
    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Picasso.get().load(url)//
                    .placeholder(R.drawable.default_img)//
                    .error(R.drawable.default_img)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    private void onItemClick(int position) {
//        Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
//        intent.putExtra("id",articles.get(position).getId());
//        startActivity(intent);
//        Toast.makeText(getActivity(),articles.get(position).getId().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        // Handle click events if needed
    }

    //每次回到该页面都会执行一次
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        apiService = RetrofitClient.getInstance(TokenUtils.getToken(getActivity())).create(ApiService.class);
        apiService.postingslist(1, 10).enqueue(new Callback<ResponseResult<UserPostingsResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<UserPostingsResponse>> call, Response<ResponseResult<UserPostingsResponse>> response) {
                postingsVos = response.body().getData().getRows();
                postingsAdapter = new PostingsAdapter(getActivity(), postingsVos);
                mRecyclerView.setAdapter(postingsAdapter);
            }

            @Override
            public void onFailure(Call<ResponseResult<UserPostingsResponse>> call, Throwable t) {
                Log.e("查询动态出错啦！！！", t.getMessage());
            }
        });
    }

}
