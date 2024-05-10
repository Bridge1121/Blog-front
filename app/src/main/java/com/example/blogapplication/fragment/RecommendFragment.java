package com.example.blogapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.blogapplication.R;
import com.example.blogapplication.adapter.ArticleAdapter;
import com.example.blogapplication.adapter.FragmentAdapter;
import com.example.blogapplication.vo.ArticleDetailVo;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private List<Fragment> list;
    private View view;
    private ViewPager viewPager;
    private Button buttonHot,buttonRecommend;
    private ListView article_listview;
    private ArticleAdapter articleAdapter;
    private List<ArticleDetailVo> articles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_recommend,container,false);
        initView();
        return view;
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

    private void initView(){
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
        article_listview = view.findViewById(R.id.article_listview);
        articleAdapter = new ArticleAdapter(getActivity(),  articles);
        article_listview.setAdapter(articleAdapter);
    }

}
