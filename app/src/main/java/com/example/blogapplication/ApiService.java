package com.example.blogapplication;


import com.example.blogapplication.vo.ArticleDetailVo;

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
    @POST("/login")
    Call<String> login(@Body RequestBody requestBody);
    @GET("comment/commentList")
    Call<String> getCommentList(@Query("pageNum") int pageNum,@Query("pageSize") int pageSize,@Query("articleId") Long articleId);
    @GET("article/{id}")
    Call<ResponseResult<ArticleDetailVo>> getArticleDetail(@Path("id") Long articleId);
}
