package com.example.attendance.ui.lecturelist;

import android.util.Log;

import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
import com.example.attendance.network.WebServiceProvider;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LectureViewModel extends ViewModel {
    private static final String TAG = "LectureListViewModel";
    private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();

    public void getLectures(){

        final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
                WebServiceProvider.getLectureApi().getLectureList(SessionManager.getUser().getToken(true))
//                        .doOnError(throwable -> {
//                            SessionManager.logout();
//                        })
                        .subscribeOn(Schedulers.io())
                .onErrorComplete()

        );

        lectureList.addSource(source, lectureModels -> {
            lectureList.setValue(lectureModels);
            lectureList.removeSource(source);
        });
    }

    public LiveData<List<LectureModel>> observeLectures(){
        return lectureList;
    }
}
