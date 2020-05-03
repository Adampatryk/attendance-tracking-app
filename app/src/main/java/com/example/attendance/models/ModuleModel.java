package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ModuleModel {
	@SerializedName("id")
	@Expose
	private int id;

	@SerializedName("title")
	@Expose
	private String title;

	@SerializedName("moduleCode")
	@Expose
	private String moduleCode;

	@SerializedName("academicYearStart")
	@Expose
	private Date academicYearStart;

	@SerializedName("active")
	@Expose
	private boolean active;

	@SerializedName("info")
	@Expose
	private String info;

	@SerializedName("weight")
	@Expose
	private int credits;

	@SerializedName("students")
	@Expose
	private UserModel[] students;

	@SerializedName("professors")
	@Expose
	private UserModel[] professors;

	public ModuleModel(int id, String title, String moduleCode, Date academicYearStart, boolean active, UserModel[] students, UserModel[] professors) {
		this.id = id;
		this.title = title;
		this.moduleCode = moduleCode;
		this.academicYearStart = academicYearStart;
		this.active = active;
		this.students = students;
		this.professors = professors;
	}

	public ModuleModel(int id, String title, String moduleCode, Date academicYearStart, String info,  int credits, boolean active, UserModel[] students, UserModel[] professors) {
		this.id = id;
		this.title = title;
		this.moduleCode = moduleCode;
		this.academicYearStart = academicYearStart;
		this.info = info;
		this.credits = credits;
		this.active = active;
		this.students = students;
		this.professors = professors;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public Date getAcademicYearStart() {
		return academicYearStart;
	}

	public void setAcademicYearStart(Date academicYearStart) {
		this.academicYearStart = academicYearStart;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public Date getacademicYearStart() {
		return academicYearStart;
	}

	public void setacademicYearStart(Date datestamp) {
		this.academicYearStart = datestamp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public UserModel[] getStudents() {
		return students;
	}

	public void setStudents(UserModel[] students) {
		this.students = students;
	}

	public UserModel[] getProfessors() {
		return professors;
	}

	public void setProfessors(UserModel[] professors) {
		this.professors = professors;
	}
}
