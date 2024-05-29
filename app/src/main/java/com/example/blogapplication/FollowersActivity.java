package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapplication.adapter.DraftAdapter;
import com.example.blogapplication.adapter.FollowerAdapter;
import com.example.blogapplication.databinding.ActivityFollowersBinding;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.UserInfo;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.entity.response.UserInfoResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersActivity extends AppCompatActivity {
    private ActivityFollowersBinding binding;
    private ApiService apiService;
    private SwipeRecyclerView swipeRecyclerView;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private SwipeRefreshLayout mRefreshLayout;
    private SwipeMenuCreator menuCreator;
    private List<UserInfo> userInfos = new ArrayList<>();
    private FollowerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowersBinding.inflate(getLayoutInflater());

        getSupportActionBar().setTitle("我的关注");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键

        swipeRecyclerView = binding.swiperRecycleview;
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRecyclerView.setHasFixedSize(true);
        swipeRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        loadMore();

        swipeRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 设置item点击监听事件
                Intent intent = new Intent(FollowersActivity.this,HomePageActivity.class);
                intent.putExtra("id",userInfos.get(position).getId());
                intent.putExtra("isMe",1);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), userInfos.get(position).getNickName(), Toast.LENGTH_SHORT).show();
            }
        });
        setContentView(binding.getRoot());
    }

    private void loadMore() {

        mRefreshLayout = binding.refreshLayout;
        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。


        // 自定义的核心就是DefineLoadMoreView类。
        FollowersActivity.DefineLoadMoreView loadMoreView = new FollowersActivity.DefineLoadMoreView(getApplicationContext());
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
    protected List<UserInfo> createDataList() {
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(FollowersActivity.this)).create(ApiService.class);
        apiService.followerList(1, 10, TokenUtils.getUserInfo(FollowersActivity.this).getId()).enqueue(new Callback<ResponseResult<UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<UserInfoResponse>> call, Response<ResponseResult<UserInfoResponse>> response) {
                userInfos = response.body().getData().getRows();
                if (userInfos.size()==0){
                    Toast.makeText(FollowersActivity.this,"您还没有关注用户哦~",Toast.LENGTH_SHORT).show();
                }
//                if (userInfos.size()==0){
//                    binding.tvEmpty.setVisibility(View.VISIBLE);
//                    swipeRecyclerView.setVisibility(View.GONE);
//                }else{
//                    binding.tvEmpty.setVisibility(View.GONE);
//                    swipeRecyclerView.setVisibility(View.VISIBLE);
//                }
                adapter = new FollowerAdapter(FollowersActivity.this, userInfos);
                swipeRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);

                // 第一次加载数据：一定要掉用这个方法。
                // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
                // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
                swipeRecyclerView.loadMoreFinish(false, true);
            }

            @Override
            public void onFailure(Call<ResponseResult<UserInfoResponse>> call, Throwable t) {
                Log.e("查询用户关注列表出错啦！！！",t.getMessage());
            }
        });
        return userInfos;
    }


    /**
     * 第一次加载数据。一定要掉用方法 mRecyclerView.loadMoreFinish(false, true); 不然刷新和加载更多失效
     */
    private void loadData() {
        userInfos = createDataList();

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 在这里处理返回按钮被点击的操作，例如返回上一页
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadMoreItems() {
        currentPage++; // 更新页数

        apiService.followerList(currentPage, pageSize, TokenUtils.getUserInfo(getApplicationContext()).getId()).enqueue(new Callback<ResponseResult<UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<UserInfoResponse>> call, Response<ResponseResult<UserInfoResponse>> response) {
                List<UserInfo> userInfos1 = response.body().getData().getRows();

                if (userInfos1 != null && userInfos1.size() > 0) {
//                    binding.tvEmpty.setVisibility(View.GONE);
//                    swipeRecyclerView.setVisibility(View.VISIBLE);
                    userInfos.addAll(userInfos1); // 将新加载的数据添加到原有数据集合中
                    adapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    swipeRecyclerView.loadMoreFinish(false, true);
                } else {
//                    binding.tvEmpty.setVisibility(View.VISIBLE);
//                    swipeRecyclerView.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    swipeRecyclerView.loadMoreFinish(true, false);//没有更多数据了
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<UserInfoResponse>> call, Throwable t) {
                Log.e("加载更多草稿出错啦！！！！", t.getMessage());
            }
        });
    }

}