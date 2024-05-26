package com.example.blogapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.utils.KeyWordUtil;
import com.example.blogapplication.vo.UserPostingsVo;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.ViewHolder> {
    private List<UserPostingsVo> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private String searchContent;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPostingsVo userPostingsVo = data.get(position);

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
        holder.praises.setText(userPostingsVo.getPraises()+"");
        holder.commentCount.setText(userPostingsVo.getComments()+"");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
