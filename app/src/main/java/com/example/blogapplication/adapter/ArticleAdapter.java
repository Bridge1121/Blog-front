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

import com.example.blogapplication.R;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends BaseAdapter{
    private List<ArticleDetailVo> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ArticleAdapter(Context context,List<ArticleDetailVo> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class compoent{
        public TextView title;
        public TextView viewpoint;
        public ImageView thumbnail;
        public TextView articleThumb;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        compoent compoent=null;
        if(convertView==null){
            compoent=new compoent();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.article_item, null);
            compoent.title=convertView.findViewById(R.id.article_title);
            compoent.viewpoint = convertView.findViewById (R.id.text_viewpoint);
            compoent.thumbnail=convertView.findViewById(R.id.thumbnail);//文章缩略图
            compoent.articleThumb = convertView.findViewById (R.id.content_thumbnail);//文章内容截取
            convertView.setTag(compoent);
        }else{
            compoent= (compoent) convertView.getTag();
        }
        //绑定数据
        ArticleDetailVo article = data.get (position);
        compoent.title.setText (article.getTitle());
        compoent.viewpoint.setText(article.getViewCount().toString());
        Picasso.get().load(article.getThumbnail()).into(compoent.thumbnail);
        compoent.articleThumb.setText(article.getSummary());
        return convertView;
    }
}
