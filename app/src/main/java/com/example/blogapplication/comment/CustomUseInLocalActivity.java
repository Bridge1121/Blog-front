package com.example.blogapplication.comment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.databinding.ActivityArticleDetailBinding;
import com.example.blogapplication.utils.RichUtils;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.google.gson.Gson;
import com.jidcoo.android.widget.commentview.CommentView;
import com.jidcoo.android.widget.commentview.callback.CustomCommentItemCallback;
import com.jidcoo.android.widget.commentview.callback.CustomReplyItemCallback;
import com.jidcoo.android.widget.commentview.callback.OnCommentLoadMoreCallback;
import com.jidcoo.android.widget.commentview.callback.OnItemClickCallback;
import com.jidcoo.android.widget.commentview.callback.OnPullRefreshCallback;
import com.jidcoo.android.widget.commentview.callback.OnReplyLoadMoreCallback;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对于CommentView的自定义数据类型和布局的使用实例（使用本地测试数据）
 * 使用自定义样式配置器，自定义数据模型，自定义布局
 * <u>注意：使用自定义数据类型就必须自定义布局实现，否则会抛出数据模型的java.lang.ClassCastException异常</u><br></br>
 * @author Jidcoo
 */
public class CustomUseInLocalActivity extends AppCompatActivity {
    private CommentView commentView;
    private Gson gson;
    private LocalServer localServer;
    private ActivityArticleDetailBinding binding;
    private Long articleId;
    private ApiService apiService;
    private ArticleDetailVo articleDetailVo;

    //
    private static class ActivityHandler extends Handler {
        private final WeakReference<CustomUseInLocalActivity> mActivity;
        public ActivityHandler(CustomUseInLocalActivity activity) {
            mActivity = new WeakReference<CustomUseInLocalActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            CustomUseInLocalActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        //commentView.loadFailed(true);//实际网络请求中如果加载失败调用此方法
                        activity.commentView.loadComplete(activity.gson.fromJson((String)msg.obj,CustomCommentModel.class));
                        break;
                    case 2:
                        //commentView.refreshFailed();//实际网络请求中如果加载失败调用此方法
                        activity.commentView.refreshComplete(activity.gson.fromJson((String)msg.obj, CustomCommentModel.class));
                        break;
                    case 3:
                        //commentView.loadFailed();//实际网络请求中如果加载失败调用此方法
                        activity.commentView.loadMoreComplete(activity.gson.fromJson((String)msg.obj,CustomCommentModel.class));
                        break;
                    case 4:
                        //commentView.loadMoreReplyFailed();//实际网络请求中如果加载失败调用此方法
                        activity.commentView.loadMoreReplyComplete(activity.gson.fromJson((String)msg.obj,CustomCommentModel.class));
                        break;
                }
            }
        }
    }
    private final ActivityHandler activityHandler = new ActivityHandler(this);
    //

     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        setContentView(R.layout.custom_use);
        gson=new Gson();
        localServer=new LocalServer(this,"api2");
         //获取要显示的文章信息
         Intent intent = getIntent();
         articleId = intent.getLongExtra("id",1);
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
        commentView=findViewById(R.id.commentView);
         //设置空视图
         //commentView.setEmptyView(view);
         //设置错误视图
         //commentView.setErrorView(view);
         //添加控件头布局
        // commentView.addHeaderView();
        commentView.setViewStyleConfigurator(new CustomViewStyleConfigurator(this));

        commentView.callbackBuilder()
                //自定义评论布局(必须使用ViewHolder机制)--CustomCommentItemCallback<C> 泛型C为自定义评论数据类
                .customCommentItem(new CustomCommentItemCallback<CustomCommentModel.CustomComment>() {
                    @Override
                    public View buildCommentItem(int groupPosition, CustomCommentModel.CustomComment comment, LayoutInflater inflater, View convertView, ViewGroup parent) {
                        //使用方法就像adapter里面的getView()方法一样
                        final CustomCommentViewHolder holder;
                        if(convertView==null){
                            //使用自定义布局
                            convertView=inflater.inflate(R.layout.custom_item_comment,parent,false);
                            holder=new CustomCommentViewHolder(convertView);
                            //必须使用ViewHolder机制
                            convertView.setTag(holder);
                        }else {
                            holder= (CustomCommentViewHolder) convertView.getTag();
                        }
                        holder.prizes.setText("100");
                        holder.userName.setText(comment.getUserName());
                        holder.comment.setText(comment.getContent());
                        return convertView;
                    }
                })
                //自定义评论布局(必须使用ViewHolder机制）
                // 并且自定义ViewHolder类必须继承自com.jidcoo.android.widget.commentview.view.ViewHolder
                // --CustomReplyItemCallback<R> 泛型R为自定义回复数据类
                .customReplyItem(new CustomReplyItemCallback<CustomCommentModel.CustomComment.CustomReply>() {
                    @Override
                    public View buildReplyItem(int groupPosition, int childPosition, boolean isLastReply, CustomCommentModel.CustomComment.CustomReply reply, LayoutInflater inflater, View convertView, ViewGroup parent) {
                        //使用方法就像adapter里面的getView()方法一样
                        //此类必须继承自com.jidcoo.android.widget.commentview.view.ViewHolder，否则报错
                        CustomReplyViewHolder holder=null;
                        //此类必须继承自com.jidcoo.android.widget.commentview.view.ViewHolder，否则报错
                        if(convertView==null){
                            //使用自定义布局
                            convertView=inflater.inflate(R.layout.custom_item_reply,parent,false);
                            holder=new CustomReplyViewHolder(convertView);
                            //必须使用ViewHolder机制
                            convertView.setTag(holder);
                        }else {
                            holder= (CustomReplyViewHolder) convertView.getTag();
                        }
                        holder.userName.setText(reply.getUserName());
                        holder.reply.setText(reply.getContent());
                        holder.prizes.setText("100");
                        return convertView;
                    }
                })
                //下拉刷新回调
                .setOnPullRefreshCallback(new MyOnPullRefreshCallback())
                //评论、回复Item的点击回调（点击事件回调）
                .setOnItemClickCallback(new MyOnItemClickCallback())
                //回复数据加载更多回调（加载更多回复）
                .setOnReplyLoadMoreCallback(new MyOnReplyLoadMoreCallback())
                //上拉加载更多回调（加载更多评论数据）
                .setOnCommentLoadMoreCallback(new MyOnCommentLoadMoreCallback())
                //设置完成后必须调用CallbackBuilder的buildCallback()方法，否则设置的回调无效
                .buildCallback();
         load(1,1);
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


    //获取数据
    private void load(int code,int handlerId){
        localServer.get(code,activityHandler,handlerId);
    }


    /**
     * 下拉刷新回调类
     */
    class MyOnPullRefreshCallback implements OnPullRefreshCallback {

        @Override
        public void refreshing() {
            load(1,2);


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
                if(willLoadPage==2){
                    load(2,3);
                }else if(willLoadPage==3){
                    load(3,3);
                }
            }
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
     * 回复加载更多回调类
     */
    class MyOnReplyLoadMoreCallback implements OnReplyLoadMoreCallback<CustomCommentModel.CustomComment.CustomReply> {


        @Override
        public void loading(CustomCommentModel.CustomComment.CustomReply reply, int willLoadPage) {
                if(willLoadPage==2){
                    load(5,4);
                }else if(willLoadPage==3){
                    load(6,4);
            }
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
              Toast.makeText(CustomUseInLocalActivity.this,"你点击的评论："+comment.getContent(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void replyItemOnClick(int c_position, int r_position, CustomCommentModel.CustomComment.CustomReply reply, View view) {
            Toast.makeText(CustomUseInLocalActivity.this,"你点击的回复："+reply.getContent(),Toast.LENGTH_SHORT).show();
        }
    }

}

