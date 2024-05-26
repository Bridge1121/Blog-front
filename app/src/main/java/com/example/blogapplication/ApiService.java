package com.example.blogapplication;


import com.example.blogapplication.comment.CustomCommentModel;
import com.example.blogapplication.dto.AddArticleDto;
import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.Comment;
import com.example.blogapplication.entity.LoginUser;
import com.example.blogapplication.entity.response.CategoryResponse;
import com.example.blogapplication.entity.response.UserPostingsResponse;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.example.blogapplication.vo.PageVo;
import com.example.blogapplication.vo.PagerRepliesEnableVo;


import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("link/getAllLink")
    Call<String> getAllLink();

    @POST("user/register")
    Call<ResponseResult> register(@Body RequestBody requestBody);

    @GET("user/getAvatar")
    Call<ResponseResult<String>> getAvatar(@Query("userId") Long userId);

    @POST("login")
    Call<ResponseResult<LoginUser>> login(@Body RequestBody requestBody);

    @POST("logout")
    Call<ResponseResult> logout();

    @GET("comment/commentList")
    Call<ResponseResult<CustomCommentModel>> getCommentList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Query("articleId") Long articleId,@Query("currentUserId") Long currentUserId);

    @GET("comment/replyList")
    Call<ResponseResult<PagerRepliesEnableVo>> getReplyList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Query("commentId") Long commentId,@Query("currentUserId") Long currentUserId);


    @POST("comment")
    Call<ResponseResult> addComment(@Body RequestBody requestBody);

    @GET("comment/addPrize")
    Call<ResponseResult> addPrize(@Query("commentId") Long commentId,@Query("currentUserId") Long currentUserId);

    @DELETE("comment/deletePrize")
    Call<ResponseResult> deletePrize(@Query("commentId") Long commentId,@Query("currentUserId") Long currentUserId);

    @GET("article/searchArticle")
    Call<ResponseResult<ArticleResponse>> searchArticle(@Query("pageNum") int pageNum,@Query("pageSize") int pageSize,@Query("content") String content);

    @GET("article/{id}")
    Call<ResponseResult<ArticleDetailVo>> getArticleDetail(@Path("id") Long articleId);

    @GET("article/articleList")
    Call<ResponseResult<ArticleResponse>> getRecommandArticleList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    @GET("article/draftList")
    Call<ResponseResult<ArticleResponse>> draftArticleList(@Query("pageNum") Integer pageNum,@Query("pageSize") Integer pageSize,@Query("userId") Long userId);

    @GET("article/hotArticleList")
    Call<ResponseResult<List<Article>>> getHotArticleList();

    @POST("article/add")
    Call<ResponseResult> add(@Body RequestBody requestBody);

    @PUT("article/updateArticle")
    Call<ResponseResult> updateArticle(@Body RequestBody requestBody);

    @DELETE("article/deleteArticle/{id}")
    Call<ResponseResult> deleteArticle(@Path("id") Long id);

    @PUT("user/updateUserInfo")
    Call<Void> updateUserInfo(@Body RequestBody requestBody);

    @GET("category/getCategoryList")
    Call<ResponseResult<List<CategoryResponse>>> getCategoryList(@Query("userId") String userId);

    @Multipart
    @POST("upload")
    Call<ResponseResult<String>> uploadImg(@Part MultipartBody.Part img);


    @GET("postings/list")
    Call<ResponseResult<UserPostingsResponse>> postingslist(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

}
