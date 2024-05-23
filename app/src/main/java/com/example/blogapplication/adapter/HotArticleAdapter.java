package com.example.blogapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HotArticleAdapter extends RecyclerView.Adapter<HotArticleAdapter.ViewHolder> {
    private List<Article> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public HotArticleAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView hot;
        TextView order;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            hot = itemView.findViewById(R.id.hot_fire);
            order = itemView.findViewById(R.id.order);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.hot_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = data.get(position);
        holder.title.setText(article.getTitle());
        if (position < 3) {
            holder.hot.setImageResource(R.drawable.hot);
        }

        holder.order.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}