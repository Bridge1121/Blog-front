package com.example.blogapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.utils.KeyWordUtil;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private String searchContent;

    public ArticleAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public ArticleAdapter(Context context, List<Article> data, String searchContent) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchContent = searchContent;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView praise;
        TextView viewCount;
        ImageView thumbnail;
        TextView articleThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            praise = itemView.findViewById(R.id.text_praises);
            viewCount = itemView.findViewById(R.id.text_viewpoints);
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
        holder.praise.setText(article.getPraises().toString());
        holder.viewCount.setText(article.getViewCount().toString());
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
