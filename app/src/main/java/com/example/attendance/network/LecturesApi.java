package com.example.attendance.network;

import com.example.attendance.ui.lecturelist.LectureModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LecturesApi {
    @GET("api/lectures/")
    Flowable<List<LectureModel>> getLectureList(@Header("Authorization") String token);
}
