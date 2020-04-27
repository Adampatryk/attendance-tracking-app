package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DeleteLectureModel {
	@SerializedName("id")
	@Expose
	private int id;

	@SerializedName("module")
	@Expose
	private int moduleId;


	public DeleteLectureModel(int id, int moduleId) {
		this.id = id;
		this.moduleId = moduleId;
	}

	@Override
	public String toString() {
		return "DeleteLectureModel{" +
				"id=" + id +
				", moduleId=" + moduleId +
				'}';
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

}
