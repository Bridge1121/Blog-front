package com.example.blogapplication.broadcastreceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.ArticleDetailActivity;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.AddPraiseVo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentReceiver extends BroadcastReceiver {
    private ApiService apiService;

    @Override
    public void onReceive(Context context, Intent intent) {
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(context)).create(ApiService.class);
        // 接收广播中的作者和文章信息
        Long author = intent.getLongExtra("author",0);

        // 模拟当前登录账号
        Long currentUser = TokenUtils.getUserInfo(context).getId();

        apiService.isAddComment(currentUser).enqueue(new Callback<ResponseResult<AddPraiseVo>>() {
            @Override
            public void onResponse(Call<ResponseResult<AddPraiseVo>> call, Response<ResponseResult<AddPraiseVo>> response) {
                AddPraiseVo addPraiseVo = response.body().getData();
                if (addPraiseVo.isPraise()){
                    // 发送通知给对应作者
                    sendNotification(addPraiseVo.getArticleId(), context);
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<AddPraiseVo>> call, Throwable t) {

            }
        });
    }

    private static void sendNotification(Long articleId, Context context) {

        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Comment Channel";
            String channelDescription = "Channel for comment notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("comment_channel", channelName, importance);
            channel.setDescription(channelDescription);

            // 注册通知渠道
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 构建通知内容
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "comment_channel")
                .setContentTitle("你的文章有新的评论啦！")
                .setContentText("快点击看看吧！")
                .setSmallIcon(R.drawable.notification)
                .setAutoCancel(false);

        // 设置通知的点击行为
        Intent notificationIntent = new Intent(context, ArticleDetailActivity.class);
        notificationIntent.putExtra("id", articleId);
        notificationIntent.putExtra("isMe", 0);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // 发送通知给作者
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());
    }
}