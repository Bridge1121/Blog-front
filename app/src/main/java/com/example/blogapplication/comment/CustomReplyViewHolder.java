package com.example.blogapplication.comment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blogapplication.R;
import com.jidcoo.android.widget.commentview.view.RoundAngleImageView;
import com.jidcoo.android.widget.commentview.view.ViewHolder;

public class CustomReplyViewHolder extends ViewHolder {
    public TextView userName, time, reply, prizes;
    public ImageView prize;
    public RoundAngleImageView ico;

    public CustomReplyViewHolder(View view) {
        super(view);
        userName = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.reply_item_userName);
        time = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.reply_item_time);
        reply = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.reply_item_content);
        prize = (ImageView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.reply_item_like);
        prizes = (TextView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.prizes);
        ico = (RoundAngleImageView) view.findViewById(com.jidcoo.android.widget.commentview.R.id.ico);
    }
}
