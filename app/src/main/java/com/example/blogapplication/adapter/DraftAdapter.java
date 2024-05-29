package com.example.blogapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.utils.KeyWordUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.ViewHolder> {
    private List<Article> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private String searchContent;

    public DraftAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public DraftAdapter(List<Article> data, Context context, String searchContent) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchContent = searchContent;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;
        TextView articleThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            articleThumb = itemView.findViewById(R.id.content_thumbnail);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.draft_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = data.get(position);

        Picasso.get().load(article.getThumbnail()).into(holder.thumbnail);
        String spannedText = article.getContent().replaceAll("<[^>]+>", "");
        //设置控件的值
        //搜索高亮显示
        if (searchContent!=null){
            if(article.getContent()!=null&&article.getContent().length()>0){
                SpannableString content= KeyWordUtil.matcherSearchTitle(Color.parseColor("#df4277"), spannedText+"", searchContent);
                holder.articleThumb.setText(content);
            }
            if(article.getTitle()!=null&&article.getTitle().length()>0){
                SpannableString title=KeyWordUtil.matcherSearchTitle(Color.parseColor("#df4277"), article.getTitle(), searchContent);
                holder.title.setText(title);
            }
        }else{
            holder.title.setText(article.getTitle());
            holder.articleThumb.setText(spannedText);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
