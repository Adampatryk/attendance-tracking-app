package com.example.attendance.ui.lecturelist;

import com.example.attendance.models.LectureModel;

import java.util.List;


import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

//Repository is created only once
public class LectureRepository {

    private static final String TAG = "LectureRepository";
    private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();

    public MutableLiveData<List<LectureModel>> getLectures() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
//                retrofit.create(LectureApi.class).getLectureList("Token 8f448d13a46a86211e5ebdb5d335a87ab4d841d8")
//                .subscribeOn(Schedulers.io())
//        );



        return lectureList;
    }
}
