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

    @SerializedName("module")
    @Expose
    private ModuleModel module;

    @SerializedName("module_id")
    @Expose
    private int moduleId = -1;

    @SerializedName("datetime")
    @Expose
    private Date date;

    @SerializedName("secret")
    private String secret;

    @SerializedName("present")
    @Expose(serialize = false)
    private int present = -1;

    @SerializedName("info")
    @Expose
    private String info;

    @Override
    public String toString() {
        return "LectureModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", module=" + module +
                ", date=" + date +
                ", secret='" + secret + '\'' +
                ", present=" + present +
                '}';
    }

    public LectureModel(int id, String title, ModuleModel module, Date date, String secret, int present) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.date = date;
        this.secret = secret;
        this.present = present;
    }

    public LectureModel(int id, String title, ModuleModel module, Date date, String secret) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.date = date;
        this.secret = secret;
    }

    public LectureModel(int id, String title, int module, Date date, String secret) {
        this.id = id;
        this.title = title;
        this.moduleId = module;
        this.date = date;
        this.secret = secret;
    }

    public LectureModel(int id, String title, ModuleModel module, String info, Date date, String secret) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.info = info;
        this.date = date;
        this.secret = secret;
    }


    public int getModuleId() {
        if (moduleId <= 0) {
            return module.getId();
        } else {
            return moduleId;
        }
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getPresent() {
        return present;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ModuleModel getModule() {
        return module;
    }

    public void setModule(ModuleModel module) {
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

    public int isPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }
}
