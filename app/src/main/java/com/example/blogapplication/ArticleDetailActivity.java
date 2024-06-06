package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapplication.broadcastreceiver.CommentReceiver;
import com.example.blogapplication.broadcastreceiver.LikeReceiver;
import com.example.blogapplication.comment.CustomCommentModel;
import com.example.blogapplication.comment.CustomCommentViewHolder;
import com.example.blogapplication.comment.CustomReplyViewHolder;
import com.example.blogapplication.comment.CustomUseInLocalActivity;
import com.example.blogapplication.comment.CustomViewStyleConfigurator;
import com.example.blogapplication.comment.DefaultViewStyleConfigurator;
import com.example.blogapplication.databinding.ActivityArticleDetailBinding;
import com.example.blogapplication.entity.Comment;
import com.example.blogapplication.entity.UserInfo;
import com.example.blogapplication.utils.RichUtils;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.example.blogapplication.vo.UserInfoVo;
import com.google.android.flexbox.FlexboxLayout;
import com.jidcoo.android.widget.commentview.CommentView;
import com.jidcoo.android.widget.commentview.callback.CustomCommentItemCallback;
import com.jidcoo.android.widget.commentview.callback.CustomReplyItemCallback;
import com.jidcoo.android.widget.commentview.callback.OnCommentLoadMoreCallback;
import com.jidcoo.android.widget.commentview.callback.OnItemClickCallback;
import com.jidcoo.android.widget.commentview.callback.OnPullRefreshCallback;
import com.jidcoo.android.widget.commentview.callback.OnReplyLoadMoreCallback;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yinglan.keyboard.HideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {
    private ActivityArticleDetailBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private Long articleId;
    private Long userId;
    private ApiService apiService;
    private ArticleDetailVo articleDetailVo;
    private CommentView commentView;
    private CustomCommentModel customCommentModel;
    private String imgPath;
    private int currentPgeNum = 1;
    private int pageSize = 10;
    private boolean isFinished = false;

    private CircleImageView avatar;
    private TextView authorName;
    private TextView time;
    private ShineButton praiseButton;
    private ShineButton starButton;
    private TextView starCount;
    private TextView praiseCount;
    private Button follow;
    private int isMe;
    private FlexboxLayout flexboxLayout;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
//        //注册广播
//        LikeReceiver likeReceiver = new LikeReceiver();
//        IntentFilter filter = new IntentFilter("com.example.ACTION_LIKE");
//        registerReceiver(likeReceiver, filter);
//
//        CommentReceiver commentReceiver = new CommentReceiver();
//        IntentFilter filter1 = new IntentFilter("com.example.ACTION_COMMENT");
//        registerReceiver(commentReceiver,filter1);

        flexboxLayout = binding.flexboxLayout;
        refreshLayout = binding.refreshLayout;
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                initData();
                refreshLayout.setRefreshing(false);
            }
        });
        follow = binding.followButton;
        //获取要显示的文章信息
        Intent intent = getIntent();
        articleId = intent.getLongExtra("id",1);
        isMe = intent.getIntExtra("isMe",-1);
        userId = TokenUtils.getUserInfo(ArticleDetailActivity.this).getId();
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(ArticleDetailActivity.this)).create(ApiService.class);
        starCount = binding.starCount;
        praiseCount = binding.praiseCount;
        avatar = binding.authorAvatar;
        authorName = binding.authorName;
        time = binding.publishTime;
        praiseButton = binding.praiseButton;
        praiseButton.init(ArticleDetailActivity.this);
        praiseButton.setOnClickListener(new View.OnClickListener() {//点赞还是取消
            @Override
            public void onClick(View view) {
                if (articleDetailVo.isPraise()){//取消点赞
                    apiService.dislike(articleId,userId).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            praiseButton.setBtnColor(Color.GRAY);
                            praiseButton.setBtnFillColor(Color.GRAY);
                            praiseCount.setText((int) (articleDetailVo.getPraises()-1)+"");
                            articleDetailVo.setPraise(!articleDetailVo.isPraise());
                            articleDetailVo.setPraises(articleDetailVo.getPraises()-1);
                            praiseCount.setTextColor(Color.GRAY);
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {

                        }
                    });
                }else{
                    apiService.like(articleId,userId).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            // 在点赞操作中发送广播
//                            Intent likeIntent = new Intent("com.example.ACTION_LIKE");
//                            likeIntent.putExtra("author", articleDetailVo.getAuthor().getId());
//                            likeIntent.putExtra("articleId",articleId);
//                            sendBroadcast(likeIntent);
                            praiseButton.setBtnColor(Color.RED);
                            praiseButton.setBtnFillColor(Color.RED);
                            praiseCount.setText((int) (articleDetailVo.getPraises()+1)+"");
                            articleDetailVo.setPraise(!articleDetailVo.isPraise());
                            articleDetailVo.setPraises(articleDetailVo.getPraises()+1);
                            praiseCount.setTextColor(Color.RED);
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {

                        }
                    });
                }

            }
        });
        starButton = binding.starButton;
        starButton.init(ArticleDetailActivity.this);
        starButton.setOnClickListener(new View.OnClickListener() {//点赞还是取消
            @Override
            public void onClick(View view) {
                if (articleDetailVo.isStar()){//取消收藏
                    apiService.deleteStar(articleId,userId).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            starButton.setBtnColor(Color.GRAY);
                            starButton.setBtnFillColor(Color.GRAY);
                            starCount.setText((int) (articleDetailVo.getStars()-1)+"");
                            articleDetailVo.setStar(!articleDetailVo.isStar());
                            articleDetailVo.setStars(articleDetailVo.getStars()-1);
                            starCount.setTextColor(Color.GRAY);
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("取消收藏出错啦！！",t.getMessage());
                        }
                    });
                }else{
                    apiService.star(articleId,userId).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            starButton.setBtnColor(Color.RED);
                            starButton.setBtnFillColor(Color.RED);
                            starCount.setText((int) (articleDetailVo.getStars()+1)+"");
                            articleDetailVo.setStar(!articleDetailVo.isStar());
                            articleDetailVo.setStars(articleDetailVo.getStars()+1);
                            starCount.setTextColor(Color.RED);
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("收藏出错啦！！",t.getMessage());
                        }
                    });
                }

            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ArticleDetailActivity.this,HomePageActivity.class);
                intent1.putExtra("isMe",isMe);//0是我自己的主页1是别人的主页
                if (isMe==1){
                    UserInfo author = articleDetailVo.getAuthor();
                    intent1.putExtra("authorAvatar", author.getAvatar());
                    intent1.putExtra("authorEmail", author.getEmail());
                    intent1.putExtra("authorFans", author.getFans());
                    intent1.putExtra("authorFollow", author.isFollow());
                    intent1.putExtra("authorFollowers", author.getFollowers());
                    intent1.putExtra("authorId", author.getId());
                    intent1.putExtra("authorNickName", author.getNickName());

                }
                startActivity(intent1);
            }
        });
//        commentView = binding.commentView;
        if (isMe == 0){
            follow.setVisibility(View.GONE);//如果是自己的文章就不显示关注用户按钮
        }
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (articleDetailVo.getAuthor().isFollow()){//取消关注
                    apiService.cancelFollow(userId,new Long(articleDetailVo.getAuthor().getId())).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            follow.setBackgroundColor(getResources().getColor(com.github.florent37.materialviewpager.R.color.red));
                            follow.setText("关注");
                            articleDetailVo.getAuthor().setFollow(!articleDetailVo.getAuthor().isFollow());
                            Toast.makeText(ArticleDetailActivity.this,"取消关注成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("取消关注出错啦！！",t.getMessage());
                        }
                    });
                }else{//关注
                    apiService.follow(userId,new Long(articleDetailVo.getAuthor().getId())).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            follow.setBackgroundColor(getResources().getColor(R.color.grey_400));
                            follow.setText("已关注");
                            articleDetailVo.getAuthor().setFollow(!articleDetailVo.getAuthor().isFollow());
                            Toast.makeText(ArticleDetailActivity.this,"关注成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e("关注出错啦！！",t.getMessage());
                        }
                    });
                }
            }
        });
        binding.commentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleDetailActivity.this,CommentActivity.class);
                intent.putExtra("id",articleId);
                intent.putExtra("authorId",articleDetailVo.getAuthor().getId());
                startActivity(intent);
            }
        });
//        SharedPreferences sharedPreferences = getSharedPreferences("art", MODE_PRIVATE);
//        String title = sharedPreferences.getString("title", "title");
//        String content = sharedPreferences.getString("content", "");
        initData();


        binding.commentWrite.setOnClickListener(new View.OnClickListener() {//发表评论
            @Override
            public void onClick(View view) {
                DialogPlus dialog = DialogPlus.newDialog(ArticleDetailActivity.this)
                        .setContentHolder(new ViewHolder(R.layout.dialog_comment))
                        .setPadding(16,16,16,16)
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
                View holderView = dialog.getHolderView();
                Button btnComment = holderView.findViewById(R.id.btnPostComment);
                EditText content = holderView.findViewById(R.id.etComment);
                btnComment.setOnClickListener(new View.OnClickListener() {//发表根评论
                    @Override
                    public void onClick(View view) {

                        if (TokenUtils.getToken(ArticleDetailActivity.this) != ""){//已经登录才能发评论
                            Comment comment = new Comment("0",articleId,new Long(-1),content.getText().toString(),new Long(-1),new Long(-1));
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), comment.toJson());
                            apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
                            apiService.addComment(requestBody).enqueue(new Callback<ResponseResult>() {
                                @Override
                                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                                    initData();
                                    Toast.makeText(view.getContext(), "发表评论成功",Toast.LENGTH_SHORT).show();
                                    HideUtil.hideSoftKeyboard(view);
                                    dialog.dismiss();
//                                    Intent commentIntent = new Intent("com.example.ACTION_COMMENT");
//                                    commentIntent.putExtra("author", articleDetailVo.getAuthor().getId());
//                                    commentIntent.putExtra("articleId",articleId);
//                                    sendBroadcast(commentIntent);
                                }

                                @Override
                                public void onFailure(Call<ResponseResult> call, Throwable t) {

                                }
                            });
                        }else{//未登录先登录
                            Intent intent1 = new Intent(ArticleDetailActivity.this,LoginActivity.class);
                            Toast.makeText(ArticleDetailActivity.this, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }


                    }
                });
            }
        });

        setContentView(binding.getRoot());
    }

    private void initData() {
        apiService.getArticleDetail(articleId,TokenUtils.getUserInfo(ArticleDetailActivity.this).getId()).enqueue(new Callback<ResponseResult<ArticleDetailVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<ArticleDetailVo>> call, Response<ResponseResult<ArticleDetailVo>> response) {
                articleDetailVo = response.body().getData();
                if (articleDetailVo.isPraise()){
                    praiseButton.setBtnColor(Color.RED);
                    praiseCount.setTextColor(Color.RED);
                }
                if (articleDetailVo.isStar()){
                    starButton.setBtnColor(Color.RED);
                    starCount.setTextColor(Color.RED);
                }
                UserInfo author = articleDetailVo.getAuthor();
                if (author.isFollow()){
                    follow.setBackgroundColor(getResources().getColor(R.color.grey_400));
                    follow.setText("已关注");
                }
                authorName.setText(author.getNickName());
                Picasso.get().load(author.getAvatar()).into(avatar);
                time.setText("发布于"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(articleDetailVo.getCreateTime()));
                starCount.setText(articleDetailVo.getStars()+"");
                praiseCount.setText(articleDetailVo.getPraises()+"");
                loading = binding.loading;
                loading.setVisibility(View.VISIBLE);
                initWebView(articleDetailVo.getContent());
                loading.setVisibility(View.GONE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(articleDetailVo.getTitle());
                List<String> articleTags = Arrays.asList(articleDetailVo.getTags().split("#"));
                flexboxLayout.removeAllViews();

                for (String tag : articleTags) {
                    TextView textView = new TextView(ArticleDetailActivity.this);
                    textView.setText(tag);
                    textView.setPadding(8, 4, 8, 4);
                    textView.setTextColor(ContextCompat.getColor(ArticleDetailActivity.this, android.R.color.black));
                    textView.setBackgroundResource(R.drawable.tag_background);
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 16, 16);
                    textView.setLayoutParams(layoutParams);
                    flexboxLayout.addView(textView);
                }


            }

            @Override
            public void onFailure(Call<ResponseResult<ArticleDetailVo>> call, Throwable t) {

            }
        });
//        commentView.setViewStyleConfigurator(new DefaultViewStyleConfigurator(this));
        apiService.getCommentList(1,10,articleId,TokenUtils.getUserInfo(getApplicationContext()).getId(),true).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
            @Override
            public void onResponse(Call<ResponseResult<CustomCommentModel>> call, Response<ResponseResult<CustomCommentModel>> response) {
                if (response.body().getData() != null ) {
                    customCommentModel = response.body().getData();
                    binding.commentCount.setText("共"+customCommentModel.getTotalDataSize()+"条评论");


                }

            }

            @Override
            public void onFailure(Call<ResponseResult<CustomCommentModel>> call, Throwable t) {

            }
        });
    }


    private void loadMoreData(int pageNum,int pageSize){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getCommentList(pageNum,pageSize,articleId,TokenUtils.getUserInfo(getApplicationContext()).getId(),true).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
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