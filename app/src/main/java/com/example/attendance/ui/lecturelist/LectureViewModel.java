package com.example.attendance.ui.lecturelist;

import android.app.Application;
import android.util.Log;

import com.example.attendance.network.LecturesApi;
import com.example.attendance.util.Constants;

import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LectureViewModel extends ViewModel {
    private static final String TAG = "LectureListViewModel";
    private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();
    //private LectureRepository lectureListRepository;

    @Inject
    public LectureViewModel() {
        //Make a new repository CHANGE
        //lectureListRepository = new LectureRepository();
    }
//
//    public LectureViewModel(@NonNull Application application) {
//        Log.d(TAG, "LectureListViewModel: Constructor called");
//        lectureListRepository = new LectureRepository();
//    }
    
//    public LiveData<List<LectureModel>> getLectures() {
////        if (lectureList == null) {
////            lectureList = new MutableLiveData<List<LectureModel>>();
////            loadLectures();
////        }
////        return lectureList;
////    }
////
////    private void loadLectures() {
////        lectureList = lectureListRepository.getLectures();
////    }

    public void getLectures(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LecturesApi lecturesApi = retrofit.create(LecturesApi.class);

        final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
                lecturesApi.getLectureList("Token 8f448d13a46a86211e5ebdb5d335a87ab4d841d8")
                        .subscribeOn(Schedulers.io())
        );

        lectureList.addSource(source, new Observer<List<LectureModel>>() {
            @Override
            public void onChanged(List<LectureModel> lectureModels) {
                lectureList.setValue(lectureModels);
                lectureList.removeSource(source);
            }
        });
    }

    public LiveData<List<LectureModel>> observeLectures(){
        return lectureList;
    }
}
