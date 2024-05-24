package com.example.blogapplication.dto;


import com.example.blogapplication.entity.User;
import com.google.gson.Gson;

import java.util.List;

public class AddArticleDto {
    private Long id;
    //标题
    private String title;
    //文章内容
    private String content;
    //文章摘要
    private String summary;
    //所属分类id
    private Long categoryId;
    //缩略图
    private String thumbnail;
    //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
    //访问量
    private Long viewCount;

    //是否允许评论 1是，0否
    private String isComment;

    private List<Long> tags;

    public AddArticleDto(String thumbnail,String summary,String title, String content, Long categoryId, String isTop, String status, String isComment) {
        this.thumbnail=thumbnail;
        this.summary = summary;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.isTop = isTop;
        this.status = status;
        this.isComment = isComment;
    }

    public AddArticleDto(Long id ,String thumbnail,String summary,String title, String content, Long categoryId, String isTop, String status, String isComment) {
        this.id = id;
        this.thumbnail=thumbnail;
        this.summary = summary;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.isTop = isTop;
        this.status = status;
        this.isComment = isComment;
    }

    // 序列化为 JSON 字符串
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 从 JSON 字符串解析为 User 对象
    public static AddArticleDto fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, AddArticleDto.class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getIsComment() {
        return isComment;
    }

    public void setIsComment(String isComment) {
        this.isComment = isComment;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }
}
