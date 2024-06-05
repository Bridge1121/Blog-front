package com.example.blogapplication.vo;

import com.example.blogapplication.entity.SearchContent;

import java.util.List;

public class SearchContentResponseVo {
    private List<SearchContent> rows;
    private Long total;

    public List<SearchContent> getRows() {
        return rows;
    }

    public void setRows(List<SearchContent> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
