package com.example.blogapplication.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.blogapplication.entity.User;
import com.google.gson.Gson;

public class CategoryResponse implements Parcelable {
    private int id;
    private String name;
    private String description;

    public CategoryResponse(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 序列化为 JSON 字符串
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 从 JSON 字符串解析为 User 对象
    public static CategoryResponse fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CategoryResponse.class);
    }

    protected CategoryResponse(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<CategoryResponse> CREATOR = new Creator<CategoryResponse>() {
        @Override
        public CategoryResponse createFromParcel(Parcel in) {
            return new CategoryResponse(in);
        }

        @Override
        public CategoryResponse[] newArray(int size) {
            return new CategoryResponse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
