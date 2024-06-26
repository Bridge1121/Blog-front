package com.example.blogapplication.vo;

import java.util.List;

public class PageVo {
    private List rows;
    private Long total;

    public PageVo(List rows, long total) {
        this.rows= rows;
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}