package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.DraftAdapter;
import com.example.blogapplication.databinding.ActivityArticleListBinding;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleListActivity extends AppCompatActivity {

    private ActivityArticleListBinding binding;
    private SwipeRecyclerView swipeRecyclerView;
    private ImageButton search;
    private CircleImageView avatar;
    private ApiService apiService;
    private List<Article> articles;
    private String searchContent;
    private ArticleAdapter articleAdapter;
    private LinearLayout searchNoneLinearLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private int currentPage = 1;
    private int pageSize = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchNoneLinearLayout = binding.searchNoneLinearlayout;
        searchNoneLinearLayout.setVisibility(View.GONE);
        swipeRecyclerView = binding.recyclerView;
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setHasFixedSize(true);
        swipeRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        searchContent = getIntent().getStringExtra("content");
        loadMore();
        // 为 RecyclerView 中的每个项设置点击监听器
        swipeRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
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


//        searchArticle(searchContent,1,10);

        //搜索图标点击事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleListActivity.this,SearchActivity.class);
//                Toast.makeText(ArticleListActivity.this,"跳转到搜索页面",Toast.LENGTH_SHORT).show();
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

    private void loadMore() {
        mRefreshLayout = binding.refreshLayout;
        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。


        // 自定义的核心就是DefineLoadMoreView类。
        ArticleListActivity.DefineLoadMoreView loadMoreView = new ArticleListActivity.DefineLoadMoreView(getApplicationContext());
//        mRecyclerView.setAutoLoadMore(false); // 拉倒最下面时，是手动点击加载更多，还是自动加载更多，手动加载无加载更多动画

//        mRecyclerView.addHeaderView(loadMoreView);  // 无效
        swipeRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        swipeRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        swipeRecyclerView.setLoadMoreListener(mLoadMoreListener);

        // 初始化，请求服务器加载数据。
        loadData();
    }


    /**
     * 下拉刷新控制。
     * 刷新页面到初始加载的 item ，目的是使加载更多继续有效
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 1000); // 延时模拟请求服务器。
        }
    };


    /**
     * 加载更多。
     * 第一次加载更多现实无数据加载
     * 使用下拉刷新
     * 第二次加载更多数据，有更多数据被加载
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            //todo bug未解决：加载更多之后，再下拉刷新的话不能继续加载更多
            swipeRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreItems();
                }
            }, 1000);//  延时1000ms，运行
        }
    };

    // 加载更多是调用此方法 添加更多数据
    protected List<Article> createDataList() {
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.searchArticle(1, 10, searchContent).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                if (response.body().getData()!=null){
                    articles = response.body().getData().getRows();
                    articleAdapter = new ArticleAdapter(getApplicationContext(), articles,searchContent);
                    swipeRecyclerView.setAdapter(articleAdapter);
                    articleAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);

                    // 第一次加载数据：一定要掉用这个方法。
                    // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
                    // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
                    swipeRecyclerView.loadMoreFinish(false, true);
                }else{
                    Toast.makeText(getApplicationContext(),"暂无该搜索内容相关的文章~",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {

            }
        });
        return articles;
    }


    /**
     * 第一次加载数据。一定要掉用方法 mRecyclerView.loadMoreFinish(false, true); 不然刷新和加载更多失效
     */
    private void loadData() {
        articles = createDataList();

    }



    /**
     * 这是这个类的主角，如何自定义LoadMoreView。
     */
    public static final class DefineLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView, View.OnClickListener {

        private LoadingView mLoadingView;  // 加载更多的动画
        private TextView mTvMessage;

        private SwipeRecyclerView.LoadMoreListener mLoadMoreListener;

        public DefineLoadMoreView(Context context) {
            super(context);
            setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            setGravity(Gravity.CENTER);
            setVisibility(GONE);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

            int minHeight = (int) (displayMetrics.density * 60 + 0.5);
            setMinimumHeight(minHeight);

            inflate(context, R.layout.loadmore_footer, this);
            mLoadingView = findViewById(R.id.loading_view);
            mTvMessage = findViewById(R.id.tv_message);

            int color1 = ContextCompat.getColor(getContext(), R.color.green_normal);
            int color2 = ContextCompat.getColor(getContext(), R.color.purple_normal);
            int color3 = ContextCompat.getColor(getContext(), R.color.colorAccent);

            mLoadingView.setCircleColors(color1, color2, color3);
            // 加载更多动画，只有圆圈转动的动画
            setOnClickListener(this);
        }

        /**
         * 马上开始回调加载更多了，这里应该显示进度条。
         */

        @Override
        public void onLoading() {
            setVisibility(VISIBLE);
            mLoadingView.setVisibility(VISIBLE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText("正在努力加载，请稍后");
        }

        /**
         * 加载更多完成了。
         *
         * @param dataEmpty 是否请求到空数据。
         * @param hasMore   是否还有更多数据等待请求。
         */
        @Override
        public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
            if (!hasMore) {
                setVisibility(VISIBLE);
                mLoadingView.setVisibility(GONE);
                mTvMessage.setVisibility(VISIBLE);
                mTvMessage.setText("没有更多数据啦");

            } else {
                setVisibility(INVISIBLE);
            }
        }

        /**
         * 调用了setAutoLoadMore(false)后，在需要加载更多的时候，这个方法会被调用，并传入加载更多的listener。
         */
        @Override
        public void onWaitToLoadMore(SwipeRecyclerView.LoadMoreListener loadMoreListener) {
            this.mLoadMoreListener = loadMoreListener;
            setVisibility(VISIBLE);
            mLoadingView.setVisibility(GONE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText("点我加载更多");
        }

        /**
         * 加载出错啦，下面的错误码和错误信息二选一。
         *
         * @param errorCode    错误码。
         * @param errorMessage 错误信息。
         */
        @Override
        public void onLoadError(int errorCode, String errorMessage) {
            setVisibility(VISIBLE);
            mLoadingView.setVisibility(GONE);
            mTvMessage.setVisibility(VISIBLE);

            // 这里要不直接设置错误信息，要不根据errorCode动态设置错误数据。
            mTvMessage.setText(errorMessage);
        }

        /**
         * 非自动加载更多时mLoadMoreListener才不为空。
         */
        @Override
        public void onClick(View v) {
            if (mLoadMoreListener != null) mLoadMoreListener.onLoadMore();
        }
    }




    private void loadMoreItems() {
        currentPage++; // 更新页数

        apiService.searchArticle(currentPage, pageSize, searchContent).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                List<Article> moreArticles = response.body().getData().getRows();

                if (moreArticles != null && moreArticles.size() > 0) {
                    articles.addAll(moreArticles); // 将新加载的数据添加到原有数据集合中
                    articleAdapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    swipeRecyclerView.loadMoreFinish(false, true);
                } else {
                    articleAdapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    swipeRecyclerView.loadMoreFinish(true, false);//没有更多数据了
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("加载更多草稿出错啦！！！！", t.getMessage());
            }
        });
    }

    private void onItemClick(int position) {
        apiService.addViewCount(articles.get(position).getId()).enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
//                Toast.makeText(ArticleListActivity.this,"浏览成功！",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                intent.putExtra("id",articles.get(position).getId());
                intent.putExtra("isMe",articles.get(position).getCreateBy()==TokenUtils.getUserInfo(ArticleListActivity.this).getId()?0:1);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.e("浏览出错啦！！！",t.getMessage());
            }
        });
//        Toast.makeText(getApplicationContext(),articles.get(position).getId().toString(),Toast.LENGTH_SHORT).show();
    }

    //搜索文章
    private void searchArticle(String content,Integer pageNum,Integer pageSize){
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.searchArticle(pageNum,pageSize,content).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                articles = response.body().getData().getRows();
                if (articles.size()==0){
                    swipeRecyclerView.setVisibility(View.GONE);
                    searchNoneLinearLayout.setVisibility(View.VISIBLE);
                }else{
                    articleAdapter = new ArticleAdapter(ArticleListActivity.this,articles, searchContent);
                    swipeRecyclerView.setAdapter(articleAdapter);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("搜索文章出错啦！！！！",t.getMessage());
            }
        });

    }
}