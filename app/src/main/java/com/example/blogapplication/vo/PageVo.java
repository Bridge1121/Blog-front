package com.example.blogapplication.vo;

import java.util.List;

public class PageVo {
    private List rows;
    private Long total;

    public PageVo(List rows, long total) {
        this.rows= rows;
        this.total = total;
    }
}