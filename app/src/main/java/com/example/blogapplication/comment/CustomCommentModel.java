package com.example.blogapplication.comment;

import androidx.core.net.ParseException;

import com.google.gson.Gson;
import com.jidcoo.android.widget.commentview.model.AbstractCommentModel;
import com.jidcoo.android.widget.commentview.model.CommentEnable;
import com.jidcoo.android.widget.commentview.model.ReplyEnable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class CustomCommentModel extends AbstractCommentModel<CustomCommentModel.CustomComment> {
    private List<CustomComment> comments;

    public String toJson(CustomCommentModel customCommentModel){
        Gson gson = new Gson();
        return gson.toJson(customCommentModel);
    }

    @Override
    public List<CustomComment> getComments() {
        return comments;
    }

    public void setComments(List<CustomComment> comments) {
        this.comments = comments;
    }

    public class CustomComment extends CommentEnable {
        private Long id;
        private Long articleId;
        private Long rootId;
        private String content;
        private Long toCommentUserId;
        private String toCommentUserName;
        private Long toCommentId;
        private Long createBy;
        private String createTime;
        private String userName;
        private int prizes;//当前评论点赞数
        private boolean praise;//当前评论是否被当前登录用户点赞
        private List<CustomReply> replies;




        public CustomComment() {
        }

        public boolean isPraise() {
            return praise;
        }

        public void setPraise(boolean praise) {
            this.praise = praise;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public int getPrizes() {
            return prizes;
        }

        public void setPrizes(int prizes) {
            this.prizes = prizes;
        }

        public Long getArticleId() {
            return articleId;
        }

        public void setArticleId(Long articleId) {
            this.articleId = articleId;
        }

        public Long getRootId() {
            return rootId;
        }

        public void setRootId(Long rootId) {
            this.rootId = rootId;
        }

        public Long getToCommentUserId() {
            return toCommentUserId;
        }

        public void setToCommentUserId(Long toCommentUserId) {
            this.toCommentUserId = toCommentUserId;
        }

        public String getToCommentUserName() {
            return toCommentUserName;
        }

        public void setToCommentUserName(String toCommentUserName) {
            this.toCommentUserName = toCommentUserName;
        }

        public Long getToCommentId() {
            return toCommentId;
        }

        public void setToCommentId(Long toCommentId) {
            this.toCommentId = toCommentId;
        }

        public Long getCreateBy() {
            return createBy;
        }

        public void setCreateBy(Long createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public List<CustomReply> getReplies() {
            return replies;
        }

        public void setReplies(List<CustomReply> replies) {
            this.replies = replies;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public class CustomReply extends ReplyEnable {
            private Long id;
            private Long articleId;
            private Long rootId;
            private String content;
            private Long toCommentUserId;//回复的评论的发表者id
            private String toCommentUserName;//回复的评论的发表者用户名
            private Long toCommentId;//回复的评论的id
            private Long createBy;
            private String createTime;
            private String userName;//当前评论的发表者
            private int prizes;//当前评论点赞数
            private boolean prize;//当前回复是否被当前登录用户点赞过

            public CustomReply() {
            }

            public boolean isPrize() {
                return prize;
            }

            public void setPrize(boolean prize) {
                this.prize = prize;
            }

            public int getPrizes() {
                return prizes;
            }

            public void setPrizes(int prizes) {
                this.prizes = prizes;
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public void setToCommentId(Long toCommentId) {
                this.toCommentId = toCommentId;
            }

            public Long getCreateBy() {
                return createBy;
            }

            public void setCreateBy(Long createBy) {
                this.createBy = createBy;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public Long getArticleId() {
                return articleId;
            }

            public void setArticleId(Long articleId) {
                this.articleId = articleId;
            }

            public Long getRootId() {
                return rootId;
            }

            public void setRootId(Long rootId) {
                this.rootId = rootId;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public Long getToCommentUserId() {
                return toCommentUserId;
            }

            public void setToCommentUserId(Long toCommentUserId) {
                this.toCommentUserId = toCommentUserId;
            }

            public String getToCommentUserName() {
                return toCommentUserName;
            }

            public void setToCommentUserName(String toCommentUserName) {
                this.toCommentUserName = toCommentUserName;
            }

            public Long getToCommentId() {
                return toCommentId;
            }

        }
    }
}