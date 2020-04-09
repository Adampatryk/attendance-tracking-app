package com.example.attendance.ui.lecturelist;

import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
import com.example.attendance.network.WebServiceProvider;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LectureViewModel extends ViewModel {
    private static final String TAG = "LectureListViewModel";
    private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();
    //private LectureRepository lectureListRepository;

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

        final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
                WebServiceProvider.getLectureApi().getLectureList("Token " + SessionManager.getUser().getToken())
                        .subscribeOn(Schedulers.io())
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
