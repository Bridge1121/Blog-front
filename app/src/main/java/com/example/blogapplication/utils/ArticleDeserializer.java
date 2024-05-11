package com.example.blogapplication.utils;

import com.example.blogapplication.entity.Article;
import com.example.blogapplication.entity.response.ArticleResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArticleDeserializer implements JsonDeserializer<ArticleResponse> {
    @Override
    public ArticleResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int total = jsonObject.get("total").getAsInt();
        JsonArray rows = jsonObject.get("rows").getAsJsonArray();
        List<Article> articles = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            JsonObject jo = (JsonObject) rows.get(i);
            System.out.println(jo);
            Article article = new Article();
            article.setId(jo.get("id").getAsLong());
            article.setTitle(jo.get("title").getAsString());
            article.setContent(jo.get("content").getAsString());
            article.setSummary(jo.get("summary").getAsString());
            article.setCategoryName(jo.get("categoryName").getAsString());
            article.setThumbnail(jo.get("thumbnail").getAsString());
            article.setViewCount(jo.get("viewCount").getAsLong());
            article.setCreateTime(jo.get("createTime").toString());
            articles.add(article);
        }

        // 遍历生成的Article列表
//        for (Article article : rows) {
//            System.out.println("ID: " + article.getId());
//            System.out.println("Title: " + article.getTitle());
//            System.out.println("Content: " + article.getContent());
//            System.out.println("Summary: " + article.getSummary());
//            System.out.println("Category Name: " + article.getCategoryName());
//            System.out.println("Thumbnail: " + article.getThumbnail());
//            System.out.println("View Count: " + article.getViewCount());
//            System.out.println("Create Time: " + article.getCreateTime());
//            System.out.println("----------------------");
//        }
//
//
        ArticleResponse articleResponse = new ArticleResponse();
        articleResponse.setArticles(articles);
        articleResponse.setTotal(total);


        // 解析JSON数据，根据JSON的结构来获取相应的字段值
//        article.setId(json.getAsJsonObject().get("id").getAsLong());
//        article.setTitle(json.getAsJsonObject().get("title").getAsString());
//        article.setContent(json.getAsJsonObject().get("content").getAsString());
//        article.setSummary(json.getAsJsonObject().get("summary").getAsString());
//        article.setCategoryName(json.getAsJsonObject().get("categoryName").getAsString());
//        article.setThumbnail(json.getAsJsonObject().get("thumbnail").getAsString());
//        article.setViewCount(json.getAsJsonObject().get("viewCount").getAsLong());
//        article.setCreateTime(new Date(json.getAsJsonObject().get("createTime").getAsLong()));

        return articleResponse;
    }
}