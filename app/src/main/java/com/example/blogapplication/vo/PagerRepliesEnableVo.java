package com.example.blogapplication.vo;

import com.example.blogapplication.comment.CustomCommentModel;

import java.util.List;

public class PagerRepliesEnableVo {

    private List<CustomCommentModel.CustomComment.CustomReply> replies;
    /**
     * 当前页码
     */
    public int currentPage;
    /**
     * 每一页数据的大小
     */
    private int pageSize;
    /**
     * 总页数
     */
    public int totalPages;
    /**
     * 数据总数
     */
    public int totalDataSize;
    /**
     * 下一个页码
     */
    public int nextPage;
    /**
     * 上一个页码
     */
    public int prefPage;

    public PagerRepliesEnableVo(List<CustomCommentModel.CustomComment.CustomReply> replies, int currentPage, int pageSize, int totalPages, int totalDataSize, int nextPage, int prefPage) {
        this.replies = replies;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalDataSize = totalDataSize;
        this.nextPage = nextPage;
        this.prefPage = prefPage;
    }

    public List<CustomCommentModel.CustomComment.CustomReply> getReplies() {
        return replies;
    }

    public void setReplies(List<CustomCommentModel.CustomComment.CustomReply> replies) {
        this.replies = replies;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalDataSize() {
        return totalDataSize;
    }

    public void setTotalDataSize(int totalDataSize) {
        this.totalDataSize = totalDataSize;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPrefPage() {
        return prefPage;
    }

    public void setPrefPage(int prefPage) {
        this.prefPage = prefPage;
    }
}