package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class LectureModel {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("module_id")
    @Expose
    private String module;

    @SerializedName("datetime")
    @Expose
    private Date date;

    @Override
    public String toString() {
        return "LectureListModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", module='" + module + '\'' +
                ", date=" + date +
                '}';
    }

    public LectureModel(int id, String title, String module, Date date) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
