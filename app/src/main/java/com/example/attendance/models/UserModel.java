package com.example.attendance.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {
    private static final String TAG = "UserModel";

    @SerializedName("id")
    @Expose(serialize = false)
    private int id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("token")
    @Expose(serialize = false)
    private String token;

    @SerializedName("is_lecturer")
    @Expose(serialize = false)
    private boolean isLecturer;

    @SerializedName("non_field_errors")
    @Expose(serialize = false)
    private String authorisation_error = null;

    //TODO constructor overloading
    public UserModel(int id, String username, String password, String token, boolean isLecturer, String authorisation_error) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLecturer = isLecturer;
        this.token = token;
        this.authorisation_error = authorisation_error;
    }

    public int getId() {
        return id;
    }

    public boolean isLecturer() {
        return isLecturer;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken(boolean withTokenString) {
        if (withTokenString) {
            return "Token " + token;
        } else
            return token;
    }

    public boolean hasToken(){
        return token != null;
    }

    public String getAuthorisation_error() {
        return authorisation_error;
    }

    public void setToken(String token){
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", isLecturer=" + isLecturer +
                ", authorisation_error='" + authorisation_error + '\'' +
                '}';
    }
}
