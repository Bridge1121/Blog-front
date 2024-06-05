package com.example.blogapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.HistoryArticle;
import com.example.blogapplication.utils.KeyWordUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryArticle> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public HistoryAdapter(Context context, List<HistoryArticle> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public HistoryAdapter(List<HistoryArticle> data, Context context) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;
        TextView articleThumb;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            articleThumb = itemView.findViewById(R.id.content_thumbnail);
            time = itemView.findViewById(R.id.time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryArticle article = data.get(position);

        Picasso.get().load(article.getThumbnail()).into(holder.thumbnail);
        holder.time.setText("浏览于"+article.getTime());
        holder.title.setText(article.getTitle());
        holder.articleThumb.setText(article.getContent());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
