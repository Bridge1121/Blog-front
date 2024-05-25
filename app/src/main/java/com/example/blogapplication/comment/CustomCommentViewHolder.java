package com.example.blogapplication.comment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blogapplication.R;
import com.jidcoo.android.widget.commentview.view.RoundAngleImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCommentViewHolder {
    public TextView userName, time, comment, prizes;
    public ImageView prize;
    public RoundAngleImageView ico;

    public CustomCommentViewHolder(View view) {
        userName = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.comment_item_userName);
        time = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.comment_item_time);
        comment = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.comment_item_content);
        prize = (ImageView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.comment_item_like);
        prizes = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.prizes);
        ico = (RoundAngleImageView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.ico);
    }
}
