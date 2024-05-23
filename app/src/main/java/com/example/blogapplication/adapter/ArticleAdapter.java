package com.example.blogapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ArticleAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView viewpoint;
        ImageView thumbnail;
        TextView articleThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            viewpoint = itemView.findViewById(R.id.text_viewpoint);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            articleThumb = itemView.findViewById(R.id.content_thumbnail);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = data.get(position);
        holder.title.setText(article.getTitle());
        holder.viewpoint.setText(String.valueOf(article.getViewCount()));
        Picasso.get().load(article.getThumbnail()).into(holder.thumbnail);
        holder.articleThumb.setText(article.getSummary());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
