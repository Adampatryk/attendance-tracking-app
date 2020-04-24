package com.example.attendance.models;

import com.google.gson.annotations.SerializedName;

public class AttendanceModel {
	@SerializedName("lectureId")
	private int lectureId;

	@SerializedName("secret")
	private String secret;

	@SerializedName("deviceId")
	private String deviceId;

	@SerializedName("Error")
	private String error;

	@SerializedName("present")
	private boolean present;

	public AttendanceModel(int lectureId, String secret, String deviceId, String error, boolean present) {
		this.lectureId = lectureId;
		this.secret = secret;
		this.deviceId = deviceId;
		this.error = error;
		this.present = present;
	}

	public boolean isPresent() {
		return present;
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
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
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
				", secret='" + secret + '\'' +
				", deviceId='" + deviceId + '\'' +
				", error='" + error + '\'' +
				", present=" + present +
				'}';
	}
}
