package com.example.attendance.network;

import com.example.attendance.util.Constants;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceProvider {
	private static Retrofit instance;

	//Create Retrofit instance if it does not exists and return it
	private static Retrofit getRetrofitInstance(){
		if (instance == null){
			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.level(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

			instance = new Retrofit.Builder()
					.baseUrl(Constants.BASE_URL)
					.client(client)
					.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
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

	//Return API for modules
	public static ModuleApi getModuleApi() {return getRetrofitInstance().create(ModuleApi.class);}

	//Return API for authorization which can be used to make network requests
	public static AuthApi getAuthApi(){
		return getRetrofitInstance().create(AuthApi.class);
	}
}
