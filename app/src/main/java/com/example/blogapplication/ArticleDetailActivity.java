package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.blogapplication.comment.CustomCommentModel;
import com.example.blogapplication.comment.CustomCommentViewHolder;
import com.example.blogapplication.comment.CustomReplyViewHolder;
import com.example.blogapplication.comment.CustomUseInLocalActivity;
import com.example.blogapplication.comment.CustomViewStyleConfigurator;
import com.example.blogapplication.comment.DefaultViewStyleConfigurator;
import com.example.blogapplication.databinding.ActivityArticleDetailBinding;
import com.example.blogapplication.utils.RichUtils;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.jidcoo.android.widget.commentview.CommentView;
import com.jidcoo.android.widget.commentview.callback.CustomCommentItemCallback;
import com.jidcoo.android.widget.commentview.callback.CustomReplyItemCallback;
import com.jidcoo.android.widget.commentview.callback.OnCommentLoadMoreCallback;
import com.jidcoo.android.widget.commentview.callback.OnItemClickCallback;
import com.jidcoo.android.widget.commentview.callback.OnPullRefreshCallback;
import com.jidcoo.android.widget.commentview.callback.OnReplyLoadMoreCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {
    private ActivityArticleDetailBinding binding;
    private Long articleId;
    private ApiService apiService;
    private ArticleDetailVo articleDetailVo;
    private CommentView commentView;
    private CustomCommentModel customCommentModel;
    private String imgPath;
    private int currentPgeNum = 1;
    private int pageSize = 10;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());

//        commentView = binding.commentView;
        //获取要显示的文章信息
        Intent intent = getIntent();
        articleId = intent.getLongExtra("id",1);
        binding.commentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleDetailActivity.this,CommentActivity.class);
                intent.putExtra("id",articleId);
                startActivity(intent);
            }
        });
//        SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
//        String title = sharedPreferences.getString("title", "title");
//        String content = sharedPreferences.getString("content", "");
        apiService = RetrofitClient.getInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getArticleDetail(articleId).enqueue(new Callback<ResponseResult<ArticleDetailVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleDetailVo>> call, Response<ResponseResult<ArticleDetailVo>> response) {
                articleDetailVo = response.body().getData();
                initWebView(articleDetailVo.getContent());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(articleDetailVo.getTitle());

            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleDetailVo>> call, Throwable t) {

            }
        });
//        commentView.setViewStyleConfigurator(new DefaultViewStyleConfigurator(this));
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getCommentList(1,10,articleId).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
            @Override
            public void onResponse(Call<ResponseResult<CustomCommentModel>> call, Response<ResponseResult<CustomCommentModel>> response) {
                customCommentModel = response.body().getData();
                binding.commentCount.setText("共"+customCommentModel.getTotalDataSize()+"条热评");
                binding.commentItemContent.setText(customCommentModel.getComments().get(0).getContent());
                binding.commentItemTime.setText(customCommentModel.getComments().get(0).getCreateTime());
                binding.prizes.setText(customCommentModel.getComments().get(0).getPrizes()+"");
                binding.commentItemUserName.setText(customCommentModel.getComments().get(0).getUserName());
                apiService.getAvatar(customCommentModel.getComments().get(0).getCreateBy()).enqueue(new Callback<ResponseResult<String>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                        Picasso.get().load(response.body().getData()).into(binding.ico);
                    }

                    @Override
                    public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                        Log.e("获取头像出错啦！！！",t.getMessage());
                    }
                });


            }

            @Override
            public void onFailure(Call<ResponseResult<CustomCommentModel>> call, Throwable t) {

            }
        });
        setContentView(binding.getRoot());
    }


    private void loadMoreData(int pageNum,int pageSize){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getCommentList(pageNum,pageSize,articleId).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
            @Override
            public void onResponse(Call<ResponseResult<CustomCommentModel>> call, Response<ResponseResult<CustomCommentModel>> response) {
                if (response.body().getData()!=null){
//                    customCommentModel.getComments().addAll(response.body().getData().getComments());
                    customCommentModel = response.body().getData();
                    commentView.loadMoreComplete(customCommentModel);
                    isFinished = false;
                }else{
                    isFinished = true;//评论加载完了
                    commentView.loadMoreComplete(customCommentModel);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<CustomCommentModel>> call, Throwable t) {
                Log.e("加载更多评论出错啦！！！",t.getMessage());
            }
        });
    }

    private String getAvatar(Long userId){
        apiService = RetrofitClient.getInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getAvatar(userId).enqueue(new Callback<ResponseResult<String>>() {
            @Override
            public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                imgPath = response.body().getData();
            }

            @Override
            public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                Log.e("获取头像出错啦！！！",t.getMessage());
            }
        });
        return imgPath;
    }

    /**
     * 下拉刷新回调类
     */
    class MyOnPullRefreshCallback implements OnPullRefreshCallback {

        @Override
        public void refreshing() {
            loadMoreData(1,10);


        }

        @Override
        public void complete() {
            //加载完成后的操作
        }

        @Override
        public void failure(String msg) {

        }
    }

    /**
     * 上拉加载更多回调类
     */
    class MyOnCommentLoadMoreCallback implements OnCommentLoadMoreCallback {

        @Override
        public void loading(int currentPage, int willLoadPage, boolean isLoadedAllPages) {
            //因为测试数据写死了，所以这里的逻辑也是写死的
            if (!isLoadedAllPages){
                loadMoreData(currentPgeNum+1,pageSize);
            }
        }

        @Override
        public void complete() {
            //加载完成后的操作
            if (isFinished) {
                Toast.makeText(getApplicationContext(),"评论已全部加载完毕",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(String msg) {
        }
    }

    /**
     * 回复加载更多回调类
     */
    class MyOnReplyLoadMoreCallback implements OnReplyLoadMoreCallback<CustomCommentModel.CustomComment.CustomReply> {


        @Override
        public void loading(CustomCommentModel.CustomComment.CustomReply reply, int willLoadPage) {
//            loadMoreData();
        }

        @Override
        public void complete() {

        }

        @Override
        public void failure(String msg) {

        }
    }

    /**
     * 点击事件回调
     */
    class MyOnItemClickCallback implements OnItemClickCallback<CustomCommentModel.CustomComment, CustomCommentModel.CustomComment.CustomReply> {


        @Override
        public void commentItemOnClick(int position, CustomCommentModel.CustomComment comment, View view) {
            Toast.makeText(ArticleDetailActivity.this,"你点击的评论："+comment.getContent(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void replyItemOnClick(int c_position, int r_position, CustomCommentModel.CustomComment.CustomReply reply, View view) {
            Toast.makeText(ArticleDetailActivity.this,"你点击的回复："+reply.getContent(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initWebView(String data) {

        WebSettings settings = binding.webView.getSettings();

        //settings.setUseWideViewPort(true);//调整到适合webview的大小，不过尽量不要用，有些手机有问题
        settings.setLoadWithOverviewMode(true);//设置WebView是否使用预览模式加载界面。
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);


        binding.webView.setVerticalScrollBarEnabled(false);//不能垂直滑动
        binding.webView.setHorizontalScrollBarEnabled(false);//不能水平滑动
        settings.setTextSize(WebSettings.TextSize.NORMAL);//通过设置WebSettings，改变HTML中文字的大小
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        //设置WebView属性，能够执行Javascript脚本
        binding.webView.getSettings().setJavaScriptEnabled(true);//设置js可用

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//支持内容重新布局
        binding.webView.setWebViewClient(webViewClient);
        binding.webView.setWebChromeClient(new WebChromeClient());
        data = "</Div><head><style>body{font-size:16px}</style>" +
                "<style>img{ width:100% !important;margin-top:0.4em;margin-bottom:0.4em}</style>" +
                "<style>ul{ padding-left: 1em;margin-top:0em}</style>" +
                "<style>ol{ padding-left: 1.2em;margin-top:0em}</style>" +
                "</head>" + data;

        ArrayList<String> arrayList = RichUtils.returnImageUrlsFromHtml(data);
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).contains("http")) {
                    //如果不是http,那么就是本地绝对路径，要加上file
                    data = data.replace(arrayList.get(i), "file://" + arrayList.get(i));
                }
            }
        }

        binding.webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    public WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return super.shouldOverrideUrlLoading(view, request);
        }


    };

}