package com.example.blogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blogapplication.comment.CustomCommentModel;
import com.example.blogapplication.comment.CustomCommentViewHolder;
import com.example.blogapplication.comment.CustomReplyViewHolder;
import com.example.blogapplication.comment.DefaultCommentModel;
import com.example.blogapplication.databinding.ActivityCommentBinding;
import com.example.blogapplication.entity.Comment;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.PagerRepliesEnableVo;
import com.jidcoo.android.widget.commentview.CommentView;
import com.jidcoo.android.widget.commentview.callback.CustomCommentItemCallback;
import com.jidcoo.android.widget.commentview.callback.CustomReplyItemCallback;
import com.jidcoo.android.widget.commentview.callback.OnCommentLoadMoreCallback;
import com.jidcoo.android.widget.commentview.callback.OnItemClickCallback;
import com.jidcoo.android.widget.commentview.callback.OnPullRefreshCallback;
import com.jidcoo.android.widget.commentview.callback.OnReplyLoadMoreCallback;
import com.jidcoo.android.widget.commentview.view.CommentListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;
    private ApiService apiService;
    private CommentView commentView;
    private CustomCommentModel customCommentModel;
    private Long articleId;
    private String imgPath;
    private boolean isFinished = false;
    private int pageSize=10;
    private List<CustomCommentModel.CustomComment.CustomReply> customReplies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("当前全部评论");
        commentView = binding.commentView;
        articleId = getIntent().getLongExtra("id",0);//获取当前查看评论文章的id
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getCommentList(1,10,articleId).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
            @Override
            public void onResponse(Call<ResponseResult<CustomCommentModel>> call, Response<ResponseResult<CustomCommentModel>> response) {
                customCommentModel = response.body().getData();
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
                                apiService.getAvatar(comment.getCreateBy()).enqueue(new Callback<ResponseResult<String>>() {
                                    @Override
                                    public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                                        Picasso.get().load(response.body().getData()).into(holder.ico);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                                        Log.e("获取头像出错啦！！！",t.getMessage());
                                    }
                                });

                                holder.prizes.setText(comment.getPrizes()+"");
                                holder.userName.setText(comment.getUserName());
                                holder.comment.setText(comment.getContent());
                                holder.time.setText(comment.getCreateTime());
                                return convertView;
                            }
                        })
                        //自定义评论布局(必须使用ViewHolder机制）
                        // 并且自定义ViewHolder类必须继承自com.jidcoo.android.widget.commentview.view.ViewHolder
                        // --CustomReplyItemCallback<R> 泛型R为自定义回复数据类
                        .customReplyItem((CustomReplyItemCallback<CustomCommentModel.CustomComment.CustomReply>) (groupPosition, childPosition, isLastReply, reply, inflater, convertView, parent) -> {
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
                            CustomReplyViewHolder finalHolder = holder;
                            apiService.getAvatar(reply.getCreateBy()).enqueue(new Callback<ResponseResult<String>>() {
                                @Override
                                public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                                    Picasso.get().load(response.body().getData()).into(finalHolder.ico);
                                }

                                @Override
                                public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                                    Log.e("获取头像出错啦！！！",t.getMessage());
                                }
                            });
                            holder.userName.setText(reply.getUserName());
                            holder.reply.setText(reply.getContent());
                            holder.prizes.setText(reply.getPrizes()+"");
                            holder.time.setText(reply.getCreateTime());
                            return convertView;
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
                commentView.loadComplete(customCommentModel);
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

    private void loadMoreReplies(int pageNum, int pageSize, CustomCommentModel.CustomComment.CustomReply reply){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getReplyList(pageNum,pageSize,reply.getRootId()).enqueue(new Callback<ResponseResult<PagerRepliesEnableVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<PagerRepliesEnableVo>> call, Response<ResponseResult<PagerRepliesEnableVo>> response) {
                if (response.body().getData()!=null){
//                    customCommentModel.getComments().addAll(response.body().getData().getComments());
                    customReplies = response.body().getData().getReplies();
//                    int index = 0;
//                    for (int i = 0;i<customCommentModel.getComments().size();i++){
//                        if (customCommentModel.getComments().get(i).getId()==reply.getRootId()){
//                            index = i;
//                            break;
//                        }
//                    }
                    CustomCommentModel customCommentModel1 = new CustomCommentModel();
                    CustomCommentModel.CustomComment customComment = customCommentModel1.new CustomComment();

                    customComment.setId(0L);
                    customComment.setArticleId(0L);
                    customComment.setRootId(0L);
                    customComment.setContent("0");
                    customComment.setToCommentUserId(0L);
                    customComment.setToCommentUserName("0");
                    customComment.setToCommentId(0L);
                    customComment.setCreateBy(0L);
                    customComment.setCreateTime("0");
                    customComment.setUserName("0");
                    customComment.setPrizes(0);

                    List<CustomCommentModel.CustomComment.CustomReply> replies = new ArrayList<>();
                    CustomCommentModel.CustomComment.CustomReply customReply = customComment.new CustomReply();
                    customReply.setId(0L);
                    customReply.setArticleId(0L);
                    customReply.setRootId(0L);
                    customReply.setContent("0");
                    customReply.setToCommentUserId(0L);
                    customReply.setToCommentUserName("0");
                    customReply.setToCommentId(0L);
                    customReply.setCreateBy(0L);
                    customReply.setCreateTime("0");
                    customReply.setUserName("0");
                    customReply.setPrizes(0);
                    replies.add(customReply);

                    customComment.setReplies(replies);

                    List<CustomCommentModel.CustomComment> comments = new ArrayList<>();
                    comments.add(customComment);

                    customCommentModel1.setComments(comments);
                    customCommentModel1.getComments().get(0).setReplies(customReplies);
                    customCommentModel1.getComments().get(0).setCurrentPage(response.body().getData().getCurrentPage());
                    customCommentModel1.getComments().get(0).setNextPage(response.body().getData().getNextPage());
                    customCommentModel1.getComments().get(0).setPrefPage(response.body().getData().getPrefPage());
                    commentView.loadMoreReplyComplete(customCommentModel1);
                    isFinished = false;
                }else{
                    isFinished = true;//评论加载完了
                    commentView.loadMoreComplete(customCommentModel);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<PagerRepliesEnableVo>> call, Throwable t) {
                Log.e("加载更多回复出错啦！！！",t.getMessage());
            }
        });
    }


    private void refreshComment(int pageNum,int pageSize){
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getCommentList(pageNum,pageSize,articleId).enqueue(new Callback<ResponseResult<CustomCommentModel>>() {
            @Override
            public void onResponse(Call<ResponseResult<CustomCommentModel>> call, Response<ResponseResult<CustomCommentModel>> response) {
                if (response.body().getData()!=null){
//                    customCommentModel.getComments().addAll(response.body().getData().getComments());
                    customCommentModel = response.body().getData();
                    commentView.refreshComplete(customCommentModel);
                    isFinished = false;
                }else{
                    isFinished = true;//评论加载完了
                    commentView.refreshComplete(customCommentModel);
                }

            }

            @Override
            public void onFailure(Call<ResponseResult<CustomCommentModel>> call, Throwable t) {
                Log.e("刷新评论出错啦！！！",t.getMessage());
            }
        });
    }


    /**
     * 下拉刷新回调类
     */
    class MyOnPullRefreshCallback implements OnPullRefreshCallback {

        @Override
        public void refreshing() {
            refreshComment(1,10);


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
                loadMoreData(willLoadPage,pageSize);
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
            loadMoreReplies(willLoadPage,2,reply);
        }

        @Override
        public void complete() {
            Toast.makeText(CommentActivity.this,"没有更多回复了哦~",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(CommentActivity.this,"你点击的评论："+comment.getContent(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void replyItemOnClick(int c_position, int r_position, CustomCommentModel.CustomComment.CustomReply reply, View view) {
            Toast.makeText(CommentActivity.this,"你点击的回复："+reply.getContent(),Toast.LENGTH_SHORT).show();
        }
    }

    String img = null;
    private String getAvatar(Long userId){
        apiService = RetrofitClient.getInstance(TokenUtils.getToken(getApplicationContext())).create(ApiService.class);
        apiService.getAvatar(userId).enqueue(new Callback<ResponseResult<String>>() {
            @Override
            public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                img = response.body().getData();
            }

            @Override
            public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                Log.e("获取头像出错啦！！！",t.getMessage());
            }
        });
        return img;
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
}