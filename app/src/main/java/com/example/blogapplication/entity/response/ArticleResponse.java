package com.example.blogapplication.entity.response;

import com.example.blogapplication.entity.Article;

import java.util.List;

public class ArticleResponse {
    private List<Article> rows;
    private int total;

    public List<Article> getRows() {
        return rows;
    }

    public void setRows(List<Article> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
