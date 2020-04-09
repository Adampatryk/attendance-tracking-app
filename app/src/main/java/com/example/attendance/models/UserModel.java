package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("username")
    @Expose(deserialize = false)
    private String username;

    @SerializedName("password")
    @Expose(deserialize = false)
    private String password;

    @SerializedName("token")
    @Expose(serialize = false)
    private String token;

    @SerializedName("non_field_errors")
    @Expose(serialize = false)
    private String authorisation_error = null;


    public UserModel(String username, String password, String token, String authorisation_error) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.authorisation_error = authorisation_error;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
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
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", authorisation_error='" + authorisation_error + '\'' +
                '}';
    }
}
