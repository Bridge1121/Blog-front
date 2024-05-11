package com.example.blogapplication.entity.response;

import com.example.blogapplication.entity.Article;

import java.util.List;

public class ArticleResponse {
    private List<Article> articles;
    private int total;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
