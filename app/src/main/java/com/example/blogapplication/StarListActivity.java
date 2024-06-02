package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.example.blogapplication.adapter.DraftAdapter;
import com.example.blogapplication.databinding.ActivityStarListBinding;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.utils.TokenUtils;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StarListActivity extends AppCompatActivity {
    private ActivityStarListBinding binding;
    private SwipeRecyclerView swipeRecyclerView;
    private SwipeMenuCreator menuCreator;
    private ApiService apiService;
    private List<Article> articles;
    private DraftAdapter adapter;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private SwipeRefreshLayout mRefreshLayout;
    private static int i = 1;
    private AlertView mAlertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStarListBinding.inflate(getLayoutInflater());

        getSupportActionBar().setTitle("我的收藏");
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
                Intent intent = new Intent(StarListActivity.this,ArticleDetailActivity.class);
                intent.putExtra("id",articles.get(position).getId());
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), articles.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        swipeRecyclerView.setItemViewSwipeEnabled(true);//侧滑删除

        swipeRecyclerView.setItemViewSwipeEnabled(true);//侧滑删除


        OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {

            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 此方法在Item拖拽交换位置时被调用。
                // 第一个参数是要交换为之的Item，第二个是目标位置的Item。
                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                Collections.swap(articles, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {//侧滑删除item
                // 此方法在Item在侧滑删除时被调用。
                // 从数据源移除该Item对应的数据，并刷新Adapter。
                int position1 = srcHolder.getAdapterPosition();
//                Toast.makeText(getApplicationContext(), articles.get(position1).getId().toString(), Toast.LENGTH_SHORT).show();
                mAlertView = new AlertView("确认", "确定取消收藏改文章吗？", "取消", new String[]{"确定"}, null, StarListActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if(o == mAlertView && position != AlertView.CANCELPOSITION){//点击的是弹窗,而且是非取消按钮
                            deleteStar(position1);
                        }else{
                            loadData();
                        }
                    }
                }).setCancelable(true).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {

                    }
                });
                mAlertView.show();
//                deleteDraft(position);
            }
        };

        swipeRecyclerView.setOnItemMoveListener(mItemMoveListener);// 监听拖拽，更新UI。


        setContentView(binding.getRoot());


    }


    private void loadMore() {

        mRefreshLayout = binding.refreshLayout;
        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。


        // 自定义的核心就是DefineLoadMoreView类。
        StarListActivity.DefineLoadMoreView loadMoreView = new StarListActivity.DefineLoadMoreView(getApplicationContext());
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
                    Log.i("刷新之后的i为：",i+"");
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
//                    Toast.makeText(StarListActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                    loadMoreItems();
                }
            }, 1000);//  延时1000ms，运行
        }
    };

    // 加载更多是调用此方法 添加更多数据
    protected List<Article> createDataList() {
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.starList(1, 10, TokenUtils.getUserInfo(getApplicationContext()).getId()).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                if (response.body().getData()!=null){
                    articles = response.body().getData().getRows();
                    adapter = new DraftAdapter(getApplicationContext(), articles);
                    swipeRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                }


                // 第一次加载数据：一定要掉用这个方法。
                // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
                // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
                swipeRecyclerView.loadMoreFinish(false, true);
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("获取收藏列表出错啦！！！",t.getMessage());
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

        apiService.starList(currentPage, pageSize, TokenUtils.getUserInfo(getApplicationContext()).getId()).enqueue(new Callback<ResponseResult<ArticleResponse>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleResponse>> call, Response<ResponseResult<ArticleResponse>> response) {
                List<Article> moreArticles = response.body().getData().getRows();

                if (moreArticles != null && moreArticles.size() > 0) {
                    articles.addAll(moreArticles); // 将新加载的数据添加到原有数据集合中
                    adapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    swipeRecyclerView.loadMoreFinish(false, true);
                } else {
                    adapter.notifyDataSetChanged(); // 更新Adapter以显示新数据
                    swipeRecyclerView.loadMoreFinish(true, false);//没有更多数据了
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleResponse>> call, Throwable t) {
                Log.e("加载更多草稿出错啦！！！！", t.getMessage());
            }
        });
    }

    //删除草稿
    private void deleteStar(int position) {
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.deleteStar(articles.get(position).getId(),TokenUtils.getUserInfo(getApplicationContext()).getId()).enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                articles.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "取消收藏成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.e("取消收藏出错啦！！！！", t.getMessage());
            }
        });
    }

}