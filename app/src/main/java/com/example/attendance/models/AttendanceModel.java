package com.example.attendance.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AttendanceModel {
	@SerializedName("lecture")
	private int lectureId;

	@SerializedName("qrcode")
	private String qrCode;

	@SerializedName("deviceId")
	private String deviceId;

	@SerializedName("timestamp")
	private long timestamp;

	@SerializedName("date")
	private Date date;

	@SerializedName("Error")
	private String error;

	@SerializedName("present")
	@Expose(serialize = false)
	private boolean present;

	@SerializedName("studentId")
	@Expose(serialize = false)
	private int studentId;

	@SerializedName("student")
	private UserModel student;

	public AttendanceModel(int lectureId, String deviceId, int studentId, boolean present) {
		this.lectureId = lectureId;
		this.deviceId = deviceId;
		this.studentId = studentId;
		this.present = present;
	}

	public AttendanceModel(Date date, UserModel student, boolean present) {
		this.date = date;
		this.present = present;
		this.student = student;
	}

	public AttendanceModel(int lectureId, String qrCode, String deviceId, long timestamp) {
		this.lectureId = lectureId;
		this.qrCode = qrCode;
		this.deviceId = deviceId;
		this.timestamp = timestamp;
	}

	public AttendanceModel(int lectureId, String qrCode, String deviceId, String error, boolean present) {
		this.lectureId = lectureId;
		this.qrCode = qrCode;
		this.deviceId = deviceId;
		this.error = error;
		this.present = present;
	}

	public UserModel getStudent() {
		return student;
	}

	public void setStudent(UserModel student) {
		this.student = student;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isPresent() {
		return present;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public int getLectureId() {
		return lectureId;
	}

	public void setLectureId(int lectureId) {
		this.lectureId = lectureId;
	}

	public String getSecret() {
		return qrCode;
	}

	public void setSecret(String secret) {
		this.qrCode = secret;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "AttendanceModel{" +
				"lectureId=" + lectureId +
				", deviceId='" + deviceId + '\'' +
				", error='" + error + '\'' +
				", present=" + present +
				'}';
	}
}
