package com.example.blogapplication.entity.response;

import com.example.blogapplication.entity.UserInfo;

import java.util.List;

public class UserInfoResponse {
    private List<UserInfo> rows;
    private int total;

    public List<UserInfo> getRows() {
        return rows;
    }

    public void setRows(List<UserInfo> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
