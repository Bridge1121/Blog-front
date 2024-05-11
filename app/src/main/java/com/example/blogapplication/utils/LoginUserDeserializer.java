package com.example.blogapplication.utils;

import com.example.blogapplication.entity.LoginUser;
import com.example.blogapplication.entity.User;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LoginUserDeserializer implements JsonDeserializer<LoginUser> {

    @Override
    public LoginUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        String token = jsonObject.get("token").getAsString();
        
        JsonObject userInfoObject = jsonObject.getAsJsonObject("userInfo");
        String avatar = userInfoObject.get("avatar").getAsString();
        String email = userInfoObject.get("email").getAsString();
        int id = userInfoObject.get("id").getAsInt();
        String nickName = userInfoObject.get("nickName").getAsString();
        char sex = userInfoObject.get("sex").getAsCharacter();
        
        User user = new User();
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setId(new Long(id));
        user.setNickName(nickName);
        user.setSex(sex);
        
        LoginUser loginUser = new LoginUser(user,token);
        
        return loginUser;
    }
}