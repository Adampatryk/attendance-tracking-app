package com.example.attendance.network;

import com.example.attendance.util.Constants;

import androidx.lifecycle.LiveDataReactiveStreams;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceProvider {
	private static Retrofit instance;

	//Create Retrofit instance if it does not exists and return it
	private static Retrofit getRetrofitInstance(){
		if (instance == null){
			instance = new Retrofit.Builder()
					.baseUrl(Constants.BASE_URL)
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			return instance;
		}
		else {
			return instance;
		}
	}

	//Return API for lectures which can be used to make network requests
	public static LectureApi getLectureApi(){
		return getRetrofitInstance().create(LectureApi.class);
	}

	//Return API for authorization which can be used to make network requests
	public static AuthApi getAuthApi(){
		return getRetrofitInstance().create(AuthApi.class);
	}
}
