package com.example.attendance.ui.lecturelist;

import android.util.Log;

import com.example.attendance.WebService;
import com.example.attendance.network.LecturesApi;
import com.example.attendance.util.Constants;

import java.util.List;

import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//Repository is created only once
@Singleton
public class LectureRepository {

    private static final String TAG = "LectureRepository";
    private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();

    public MutableLiveData<List<LectureModel>> getLectures() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
                retrofit.create(LecturesApi.class).getLectureList("Token 8f448d13a46a86211e5ebdb5d335a87ab4d841d8")
                .subscribeOn(Schedulers.io())
        );



        return lectureList;
    }
}
