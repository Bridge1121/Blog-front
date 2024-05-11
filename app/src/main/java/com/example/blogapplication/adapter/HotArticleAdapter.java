package com.example.blogapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blogapplication.R;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HotArticleAdapter extends BaseAdapter {
    private List<Article> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public HotArticleAdapter(Context context,List<Article> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }



    public static final class Compoent{
        public TextView title;
        public ImageView hot;
        public TextView order;
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

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Compoent compoent=null;
        if(convertView==null){
            compoent= new Compoent();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.hot_article_item, null);
            compoent.title=convertView.findViewById(R.id.article_title);
            compoent.hot=convertView.findViewById(R.id.hot_fire);//文章热度
            compoent.order = convertView.findViewById (R.id.order);//文章排名
            convertView.setTag(compoent);
        }else{
            compoent= (Compoent) convertView.getTag();
        }
        //绑定数据
        Article article = data.get (i);
        compoent.title.setText (article.getTitle());
        if(i<3){
            compoent.hot.setImageResource(R.drawable.hot);
        }

        compoent.order.setText(i+1+"");
        return convertView;
    }
}
