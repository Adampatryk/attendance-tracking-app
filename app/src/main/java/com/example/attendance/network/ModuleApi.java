package com.example.attendance.network;

import com.example.attendance.models.LectureModel;
import com.example.attendance.models.ModuleModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ModuleApi {
	@GET("api/lectures/modules/?denormalized=true")
	Flowable<List<ModuleModel>> getModuleList(@Header("Authorization") String token);

	@GET("api/lectures/modules/{id}?denormalized=true")
	Flowable<LectureModel> getModule(@Header("Authorization") String token, @Path(value = "id", encoded = true) int moduleId);
}
