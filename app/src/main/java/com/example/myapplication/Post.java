package com.example.myapplication;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKED_USERS = "likedUsers";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription (String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage (ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser (ParseUser user){
        put(KEY_USER, user);
    }

    public long getLikesCount(JSONArray jsonArray){
        return jsonArray.length();
    }

    public JSONArray getLikedUsers() {
        return getJSONArray(KEY_LIKED_USERS);
    }

    public void setLikedUsers(JSONArray jsonArray) {
        put(KEY_LIKED_USERS, jsonArray);
    }

    public Boolean getIsLiked(JSONArray jsonArray, String id) throws JSONException {
        for (int i = 0; i < jsonArray.length(); ++i){
            if (jsonArray.getString(i).equals(id)){
                return true;
            }
        }
        return false;
    }


    public void likePost(ParseUser user){
        add(KEY_LIKED_USERS, user.getObjectId());
    }

    public int returnPosition(JSONArray array, String id) throws JSONException {
        for (int i = 0; i < array.length(); ++i){
            if (array.getString(i).equals(id)){
                return i;
            }
        }
        return -1;
    }

    public void unLikePost(ParseUser user, JSONArray array) throws JSONException {
        String id = user.getObjectId();
        int position = returnPosition(array, id);
        array.remove(position);
        setLikedUsers(array);
    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

}
