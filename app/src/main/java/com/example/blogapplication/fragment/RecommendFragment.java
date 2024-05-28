package com.example.blogapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.ArticleDetailActivity;
import com.example.blogapplication.DraftListActivity;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.ShowArtActivity;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.DraftAdapter;
import com.example.blogapplication.comment.CustomUseInLocalActivity;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendFragment extends Fragment implements View.OnClickListener {

    private View view;
    private SwipeRecyclerView mRecyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> articles = new ArrayList<>();
    private ApiService apiService;
    private SwipeRefreshLayout refreshLayout;
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPage;

    public static Fragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        mRecyclerView = view.findViewById(R.id.swiper_recycleview);
        loadMore();
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

    private void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
        intent.putExtra("id",articles.get(position).getId());
        startActivity(intent);
        Toast.makeText(getActivity(),articles.get(position).getId().toString(),Toast.LENGTH_SHORT).show();
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

    private void loadMore() {

        refreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。


        // 自定义的核心就是DefineLoadMoreView类。
        DraftListActivity.DefineLoadMoreView loadMoreView = new DraftListActivity.DefineLoadMoreView(getContext());
//        mRecyclerView.setAutoLoadMore(false); // 拉倒最下面时，是手动点击加载更多，还是自动加载更多，手动加载无加载更多动画

//        mRecyclerView.addHeaderView(loadMoreView);  // 无效
        mRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        mRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener);

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
            mRecyclerView.postDelayed(new Runnable() {
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
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreItems();
                }
            }, 1000);//  延时1000ms，运行
        }
    };

    // 加载更多是调用此方法 添加更多数据
    protected List<Article> createDataList() {
        apiService = RetrofitClient.getInstance(TokenUtils.getToken(getContext())).create(ApiService.class);
        apiService.getRecommandArticleList(1, 10).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                if (response.body().getData()!=null){
                    articles = response.body().getData().getRows();
                    articleAdapter = new ArticleAdapter(getContext(), articles);
                    mRecyclerView.setAdapter(articleAdapter);
                    articleAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }


                // 第一次加载数据：一定要掉用这个方法。
                // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
                // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
                mRecyclerView.loadMoreFinish(false, true);
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
    static final class DefineLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView, View.OnClickListener {

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

        apiService.getRecommandArticleList(currentPage, pageSize).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                List<Article> moreArticles = response.body().getData().getRows();

                if (moreArticles != null && moreArticles.size() > 0) {
                    articles.addAll(moreArticles); // 将新加载的数据添加到原有数据集合中
                    articleAdapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    mRecyclerView.loadMoreFinish(false, true);
                } else {
                    articleAdapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    mRecyclerView.loadMoreFinish(true, false);//没有更多数据了
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("加载更多草稿出错啦！！！！", t.getMessage());
            }
        });
    }

    private void initView() {
        apiService = RetrofitClient.getInstance(null).create(ApiService.class);
        apiService.getRecommandArticleList(1, 10).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                if (response.body().getData()!=null){
                    articles = response.body().getData().getRows();
                    totalPage = response.body().getData().getTotal()/10;
                    articleAdapter = new ArticleAdapter(getActivity(), articles);
                    mRecyclerView.setAdapter(articleAdapter);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("推荐文章出错啦！！！", t.getMessage());
            }
        });
    }


}
