package com.example.attendance.ui.lecturedetail;

import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
import com.example.attendance.network.WebServiceProvider;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LectureDetailViewModel extends ViewModel {

	private MediatorLiveData<LectureModel> lecture = new MediatorLiveData<>();

	public void getLecture(int id){
		final LiveData<LectureModel> source = LiveDataReactiveStreams.fromPublisher(
				WebServiceProvider.getLectureApi().getLecture(SessionManager.getUser().getToken(true), id)
				.subscribeOn(Schedulers.io())
		);

		lecture.addSource(source, lectureModel -> {
			lecture.setValue(lectureModel);
			lecture.removeSource(source);
		});
	}

	public LiveData<LectureModel> observeLecture() {
		return lecture;
	}
}
