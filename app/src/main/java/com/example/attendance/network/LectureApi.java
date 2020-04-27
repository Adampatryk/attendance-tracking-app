package com.example.attendance.network;

import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.CreateLectureModel;
import com.example.attendance.models.DeleteLectureModel;
import com.example.attendance.models.LectureModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LectureApi {
    @POST("api/lectures/attendance/")
    Observable<AttendanceModel> postAttendance(@Header("Authorization") String token, @Body AttendanceModel attendanceModel);

    @GET("api/lectures/{id}/attendance/")
    Observable<List<AttendanceModel>> getLectureAttendance(@Header("Authorization") String token, @Path("id") int lecture_id);

    @POST("api/lectures/")
    Observable<LectureModel> createLecture(@Header("Authorization") String token, @Body CreateLectureModel lectureToCreate);

    @POST("api/lectures/delete/")
    Observable<LectureModel> deleteLecture(@Header("Authorization") String token, @Body DeleteLectureModel lectureToDelete);

    @GET("api/lectures/?denormalized=true")
    Observable<List<LectureModel>> getLectureList(@Header("Authorization") String token);

    @GET("api/lectures/?denormalized=true")
    Observable<List<LectureModel>> getLecturesForDate(@Header("Authorization") String token, @Query(value="date") String datetime);

    @GET("api/lectures/?denormalized=true")
    Observable<List<LectureModel>> getLecturesForModule(@Header("Authorization") String token, @Query(value="module") int moduleId);

}
