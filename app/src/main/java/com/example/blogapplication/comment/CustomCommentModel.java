package com.example.blogapplication.comment;

import androidx.core.net.ParseException;

import com.jidcoo.android.widget.commentview.model.AbstractCommentModel;
import com.jidcoo.android.widget.commentview.model.CommentEnable;
import com.jidcoo.android.widget.commentview.model.ReplyEnable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class CustomCommentModel extends AbstractCommentModel<CustomCommentModel.CustomComment> {
    private List<CustomComment> comments;

    @Override
    public List<CustomComment> getComments() {
        return comments;
    }

    public class CustomComment extends CommentEnable {
        private Long articleId;
        private Long rootId;
        private String content;
        private Long toCommentUserId;
        private String toCommentUserName;
        private Long toCommentId;
        private Long createBy;
        private String createTime;
        private String userName;
        private List<CustomReply> replies;




        public CustomComment() {
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
            private Long articleId;
            private Long rootId;
            private String content;
            private Long toCommentUserId;
            private String toCommentUserName;
            private Long toCommentId;
            private Long createBy;
            private String createTime;
            private String userName;

            public CustomReply() {
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