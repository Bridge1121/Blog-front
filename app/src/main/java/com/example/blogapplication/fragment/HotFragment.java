package com.example.blogapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blogapplication.R;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.adapter.HotArticleAdapter;
import com.example.blogapplication.vo.ArticleDetailVo;

import java.util.ArrayList;
import java.util.List;

public class HotFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private List<Fragment> list;
    private View view;
    private ViewPager viewPager;
    private Button buttonHot,buttonRecommend;
    private ListView hotListview;
    private HotArticleAdapter hotArticleAdapter;
    private List<ArticleDetailVo> articles = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_hot,container,false);
        initView();
        return view;
    }

    private void initView() {
        hotListview = view.findViewById(R.id.hot_article_listview);
        ArticleDetailVo articleDetailVo1 = new ArticleDetailVo();
        articleDetailVo1.setContent("![Snipaste_20220228_224837.png](https://sg-blog-oss.oss-cn- beijing.aliyuncs.com/2022/08/28/f3938a0368c540ee909ba7f7079a829a.png)\\n\\n# 十大 \\n## 时代的");
        articleDetailVo1.setSummary("啊实打实2");
        articleDetailVo1.setViewCount(new Long(44));
        articleDetailVo1.setTitle("委屈饿驱蚊器");
        articleDetailVo1.setThumbnail("https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/08/28/7659aac2b74247fe8ebd9e054b916dbf.png");
        articles.add(articleDetailVo1);
        ArticleDetailVo articleDetailVo2 = new ArticleDetailVo();
        articleDetailVo2.setContent("![Snipaste_20220228_224837.png](https://sg-blog-oss.oss-cn- beijing.aliyuncs.com/2022/08/28/f3938a0368c540ee909ba7f7079a829a.png)\\n\\n# 十大 \\n## 时代的");
        articleDetailVo2.setSummary("啊实打实2333333333333");
        articleDetailVo2.setViewCount(new Long(74));
        articleDetailVo2.setTitle("hahahah");
        articleDetailVo2.setThumbnail("https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/08/28/7659aac2b74247fe8ebd9e054b916dbf.png");
        articles.add(articleDetailVo2);
        ArticleDetailVo articleDetailVo3 = new ArticleDetailVo();
        articleDetailVo3.setContent("![Snipaste_20220228_224837.png](https://sg-blog-oss.oss-cn- beijing.aliyuncs.com/2022/08/28/f3938a0368c540ee909ba7f7079a829a.png)\\n\\n# 十大 \\n## 时代的");
        articleDetailVo3.setSummary("啊实打实2");
        articleDetailVo3.setViewCount(new Long(104));
        articleDetailVo3.setTitle("我是文章3333");
        articleDetailVo3.setThumbnail("https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/08/28/7659aac2b74247fe8ebd9e054b916dbf.png");
        articles.add(articleDetailVo3);
        ArticleDetailVo articleDetailVo4 = new ArticleDetailVo();
        articleDetailVo4.setContent("![Snipaste_20220228_224837.png](https://sg-blog-oss.oss-cn- beijing.aliyuncs.com/2022/08/28/f3938a0368c540ee909ba7f7079a829a.png)\\n\\n# 十大 \\n## 时代的");
        articleDetailVo4.setSummary("啊实打实4444444");
        articleDetailVo4.setViewCount(new Long(104));
        articleDetailVo4.setTitle("我是文章4444444444444");
        articleDetailVo4.setThumbnail("https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/08/28/7659aac2b74247fe8ebd9e054b916dbf.png");
        articles.add(articleDetailVo4);
        hotArticleAdapter = new HotArticleAdapter(getActivity(),  articles);
        hotListview.setAdapter(hotArticleAdapter);

        //item点击事件
        hotListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),articles.get(i).getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {


    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {

    }

}