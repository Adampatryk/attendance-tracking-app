package com.example.attendance.network;

import com.example.attendance.models.LectureModel;
import com.example.attendance.models.ModuleModel;
import com.example.attendance.models.UserModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ModuleApi {
	@GET("api/lectures/modules/?denormalized=true")
	Flowable<List<ModuleModel>> getModuleList(@Header("Authorization") String token);

	@GET("api/lectures/modules/{id}?denormalized=true")
	Observable<LectureModel> getModule(@Header("Authorization") String token, @Path(value = "id", encoded = true) int moduleId);

	@GET("api/lectures/modules/{id}/students/")
	Observable<List<UserModel>> getStudentsForModule(@Header("Authorization") String token, @Path(value = "id") int moduleId);
}
