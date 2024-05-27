package com.example.blogapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.ApiService;
import com.example.blogapplication.ArticleDetailActivity;
import com.example.blogapplication.CommentActivity;
import com.example.blogapplication.LoginActivity;
import com.example.blogapplication.R;
import com.example.blogapplication.ResponseResult;
import com.example.blogapplication.RetrofitClient;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.Comment;
import com.example.blogapplication.utils.KeyWordUtil;
import com.example.blogapplication.utils.TokenUtils;
import com.example.blogapplication.vo.UserPostingsVo;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.yinglan.keyboard.HideUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.ViewHolder> {
    private List<UserPostingsVo> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private String searchContent;
    private ApiService apiService;

    public PostingsAdapter(Context context, List<UserPostingsVo> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public PostingsAdapter(List<UserPostingsVo> data, Context context, String searchContent) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchContent = searchContent;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        NineGridView nineGridView;
        CircleImageView circleImageView;
        TextView userName;
        TextView time;
        TextView content;
        ImageView share;
        ImageView comment;
        TextView commentCount;
        ImageView praise;
        TextView praises;

        public ViewHolder(View itemView) {
            super(itemView);
            nineGridView = itemView.findViewById(R.id.nineGridView);
            circleImageView = itemView.findViewById(R.id.avatarImageView);
            userName = itemView.findViewById(R.id.usernameTextView);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            share = itemView.findViewById(R.id.shareButton);
            comment = itemView.findViewById(R.id.commentButton);
            commentCount = itemView.findViewById(R.id.comment_count);
            praise = itemView.findViewById(R.id.likeButton);
            praises = itemView.findViewById(R.id.praises);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.posting_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserPostingsVo userPostingsVo = data.get(position);
        apiService = RetrofitClient.getTokenInstance(TokenUtils.getToken(context)).create(ApiService.class);
        String[] images = userPostingsVo.getImages().split(",");
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        if (images != null) {
            for (String image : images) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfo.add(info);
            }
        }
        holder.nineGridView.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
        Picasso.get().load(userPostingsVo.getAvatar()).into(holder.circleImageView);
        holder.userName.setText(userPostingsVo.getCreateUserName());
        holder.content.setText(userPostingsVo.getContent());
        holder.time.setText(userPostingsVo.getCreateTime());
        if (userPostingsVo.isPraise()){
            holder.praises.setTextColor(context.getResources().getColor(R.color.red_normal));
            holder.praise.setColorFilter(context.getResources().getColor(R.color.red_normal));
        }else{
            holder.praises.setTextColor(context.getResources().getColor(R.color.grey_1000));
            holder.praise.setColorFilter(context.getResources().getColor(R.color.grey_1000));
        }
        holder.praises.setText(userPostingsVo.getPraises()+"");
        holder.commentCount.setText(userPostingsVo.getComments()+"");
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(context, CommentActivity.class);
                intent1.putExtra("postingId",userPostingsVo.getId());
                context.startActivity(intent1);
            }
        });

        holder.praise.setOnClickListener(new View.OnClickListener() {//给动态点赞
            @Override
            public void onClick(View view) {
                if (TokenUtils.getToken(context)!=null){
                    if (userPostingsVo.isPraise()){//调用取消点赞接口
                        apiService.deletePostingPrize(userPostingsVo.getId(),TokenUtils.getUserInfo(context).getId()).enqueue(new Callback<ResponseResult>() {
                            @Override
                            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                                data.get(position).setPraise(false);
                                data.get(position).setPraises(userPostingsVo.getPraises()-1);
                                notifyDataSetChanged();
                                Toast.makeText(context,"取消点赞成功！",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseResult> call, Throwable t) {

                            }
                        });
                    }else {//调用点赞接口
                        apiService.addPostingPrize(userPostingsVo.getId(),TokenUtils.getUserInfo(context).getId()).enqueue(new Callback<ResponseResult>() {
                            @Override
                            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                                data.get(position).setPraise(true);
                                data.get(position).setPraises(userPostingsVo.getPraises()+1);
                                notifyDataSetChanged();
                                Toast.makeText(context,"点赞成功！",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseResult> call, Throwable t) {

                            }
                        });
                    }
                }else {
                    Intent intent1 = new Intent(context, LoginActivity.class);
                    Toast.makeText(context, "您尚未登录，请先登录。", Toast.LENGTH_SHORT).show();
                    context.startActivity(intent1);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
