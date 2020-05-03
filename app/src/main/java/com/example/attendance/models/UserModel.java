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

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("token")
    @Expose(serialize = false)
    private String token;

    @SerializedName("is_lecturer")
    @Expose(serialize = false)
    private boolean isLecturer;

    @SerializedName("attendance_for_module")
    @Expose
    private float attendanceForModule;

    @SerializedName("non_field_errors")
    @Expose(serialize = false)
    private String authorisation_error = null;

    public UserModel(int id, String username, String firstName, String lastName, boolean isLecturer, long attendanceForModule) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isLecturer = isLecturer;
        this.attendanceForModule = attendanceForModule;
    }

    public UserModel(int id, String username, String password, String token, boolean isLecturer, String authorisation_error) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLecturer = isLecturer;
        this.token = token;
        this.authorisation_error = authorisation_error;
    }

	public UserModel(int id, String username, String password, String first_name, String last_name, String token, boolean lecturer) {
        this.id = id;
        this.username = username;
        this.firstName = first_name;
        this.lastName = last_name;
        this.isLecturer = lecturer;
        this.password = password;
        this.token = token;
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

    public float getAttendanceForModule() {
        return attendanceForModule;
    }

    public void setAttendanceForModule(float attendanceForModule) {
        this.attendanceForModule = attendanceForModule;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setLecturer(boolean lecturer) {
        isLecturer = lecturer;
    }

    public void setAuthorisation_error(String authorisation_error) {
        this.authorisation_error = authorisation_error;
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
