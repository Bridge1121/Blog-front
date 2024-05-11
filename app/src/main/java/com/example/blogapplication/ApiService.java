package com.example.blogapplication;


import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.LoginUser;
import com.example.blogapplication.vo.ArticleDetailVo;
import com.example.blogapplication.entity.response.ArticleResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("link/getAllLink")
    Call<String> getAllLink();

    @POST("/user/register")
    Call<ResponseResult> register(@Body RequestBody requestBody);

    @POST("/login")
    Call<ResponseResult<LoginUser>> login(@Body RequestBody requestBody);

    @POST("/logout")
    Call<ResponseResult> logout();

    @GET("comment/commentList")
    Call<String> getCommentList(@Query("pageNum") int pageNum,@Query("pageSize") int pageSize,@Query("articleId") Long articleId);

    @GET("article/{id}")
    Call<ResponseResult<ArticleDetailVo>> getArticleDetail(@Path("id") Long articleId);

    @GET("article/articleList")
    Call<ResponseResult<ArticleResponse>> getRecommandArticleList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    @GET("article/hotArticleList")
    Call<ResponseResult<List<Article>>> getHotArticleList();
}
