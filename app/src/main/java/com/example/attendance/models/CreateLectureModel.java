package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CreateLectureModel {
	@SerializedName("title")
	@Expose
	private String title;

	@SerializedName("module")
	@Expose
	private int moduleId;

	@SerializedName("datetime")
	@Expose
	private String date;

	public CreateLectureModel(String title, int moduleId, String date) {
		this.title = title;
		this.moduleId = moduleId;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "CreateLectureModel{" +
				"title='" + title + '\'' +
				", moduleId=" + moduleId +
				", date=" + date +
				'}';
	}
}
