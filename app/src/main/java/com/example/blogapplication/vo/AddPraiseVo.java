package com.example.blogapplication.vo;

public class AddPraiseVo {
    private boolean praise;
    private Long articleId;

    public AddPraiseVo(boolean praise, Long articleId) {
        this.praise = praise;
        this.articleId = articleId;
    }

    public boolean isPraise() {
        return praise;
    }

    public void setPraise(boolean praise) {
        praise = praise;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}