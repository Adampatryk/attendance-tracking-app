package com.example.attendance.network;

import com.example.attendance.models.LectureModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface LectureApi {
    @GET("api/lectures/")
    Flowable<List<LectureModel>> getLectureList(@Header("Authorization") String token);

    @GET("api/lectures/{id}")
    Flowable<LectureModel> getLecture(@Path(value = "id", encoded = true) String lectureId);
}
