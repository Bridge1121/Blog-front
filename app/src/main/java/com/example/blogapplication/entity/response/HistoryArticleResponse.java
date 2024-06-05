package com.example.blogapplication.entity.response;

import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.HistoryArticle;

import java.util.List;

public class HistoryArticleResponse {
    private List<HistoryArticle> rows;
    private int total;

    public List<HistoryArticle> getRows() {
        return rows;
    }

    public void setRows(List<HistoryArticle> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
