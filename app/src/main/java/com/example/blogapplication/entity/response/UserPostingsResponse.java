package com.example.blogapplication.entity.response;

import com.example.blogapplication.vo.UserPostingsVo;

import java.util.List;

public class UserPostingsResponse {

    private List<UserPostingsVo> rows;
    private int total;

    public List<UserPostingsVo> getRows() {
        return rows;
    }

    public void setRows(List<UserPostingsVo> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
