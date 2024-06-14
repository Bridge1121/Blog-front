package com.example.blogapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.example.blogapplication.ApiService;
import com.example.blogapplication.ArticleDetailActivity;
import com.example.blogapplication.DraftListActivity;
import com.example.blogapplication.HomePageActivity;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.UserInfo;
import com.example.blogapplication.utils.KeyWordUtil;
import com.example.blogapplication.utils.TokenUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private List<UserInfo> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private AlertView mAlertView;

    public FollowerAdapter(Context context, List<UserInfo> data) {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView avatar;
        Button followButton;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            avatar = itemView.findViewById(R.id.avatar);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }

    @NonNull
    @Override
    public FollowerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.follower_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ApiService apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(context)).create(ApiService.class);
        UserInfo userInfo = data.get(position);
        Picasso.get().load(userInfo.getAvatar()).into(holder.avatar);
        holder.userName.setText(userInfo.getNickName());
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo.isFollow()) {//取消关注
                    mAlertView = new AlertView("确认", "确定取消关注该用户吗吗？", "取消", new String[]{"确定"}, null, context, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int pos) {
                            if (o == mAlertView && pos != AlertView.CANCELPOSITION) {//点击的是弹窗,而且是非取消按钮
                                apiService.cancelFollow(TokenUtils.getUserInfo(context).getId(), new Long(userInfo.getId())).enqueue(new Callback<ResponseResult>() {
                                    @Override
                                    public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
//                                        holder.followButton.setBackgroundColor(context.getResources().getColor(com.github.florent37.materialviewpager.R.color.red));
//                                        holder.followButton.setText("关注");
                                        userInfo.setFollow(!userInfo.isFollow());
                                        data.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "取消关注成功！", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseResult> call, Throwable t) {
                                        Log.e("取消关注出错啦！！", t.getMessage());
                                    }
                                });
                            }
                        }
                    }).setCancelable(true).setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(Object o) {

                        }
                    });
                    mAlertView.show();

                }
            }
        });
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(context, HomePageActivity.class);
                intent1.putExtra("isMe", 1);//0是我自己的主页1是别人的主页

                UserInfo author = data.get(position);
                intent1.putExtra("authorAvatar", author.getAvatar());
                intent1.putExtra("authorEmail", author.getEmail());
                intent1.putExtra("authorFans", author.getFans());
                intent1.putExtra("authorFollow", author.isFollow());
                intent1.putExtra("authorFollowers", author.getFollowers());
                intent1.putExtra("authorId", author.getId());
                intent1.putExtra("authorNickName", author.getNickName());


                context.startActivity(intent1);
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
