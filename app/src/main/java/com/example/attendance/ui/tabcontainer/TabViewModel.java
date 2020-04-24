package com.example.attendance.ui.tabcontainer;

import android.util.Log;

import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.models.ModuleModel;
import com.example.attendance.network.WebServiceProvider;

import org.reactivestreams.Publisher;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Flowable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class TabViewModel extends ViewModel {
	private static final String TAG = "TabViewModel";

	private int selectedLectureId = -1;
	private int selectedModuleId = -1;
	private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> lecture = new MediatorLiveData<>();
	private MediatorLiveData<AttendanceModel> attendance = new MediatorLiveData<>();

	public void getLectures(){

		final LiveData<List<LectureModel>> source = LiveDataReactiveStreams.fromPublisher(
				WebServiceProvider.getLectureApi().getLectureList(SessionManager.getUser().getToken(true))
						.onErrorResumeNext(throwable -> {
							//Notify observer that there was an error by posting null
							lectureList.postValue(null);
							return Flowable.empty();
						})
						.subscribeOn(Schedulers.io())

		);

		Log.d(TAG, "getLectures: source acquired");
		lectureList.addSource(source, lectureModels -> {
			lectureList.setValue(lectureModels);
			lectureList.removeSource(source);
		});
	}

	public LiveData<List<LectureModel>> observeLectures(){
		return lectureList;
	}


	public void setLecture(int id){
		if (lectureList.getValue() == null){
			//TODO Deal with null lecture list
			//This will involve making a call to fetch the data
			Log.d(TAG, "setLecture: lecturelist is null");
			return;
		} else if (id < 0) {
			lecture = null;
		}

		for (LectureModel l: lectureList.getValue()) {
			if (l.getId() == id){
				lecture.postValue(l);
				selectedLectureId = id;
				Log.d(TAG, "setLecture: id:"+id);
				return;
			}
		}
	}

	public void getLecture(){
		Log.d(TAG, "getLecture: Triggering lecture update");
		setLecture(selectedLectureId);
	}

	public LiveData<LectureModel> observeLecture() {
		return lecture;
	}

	private MediatorLiveData<List<ModuleModel>> moduleList = new MediatorLiveData<>();

	public void getModules(){

		final LiveData<List<ModuleModel>> source = LiveDataReactiveStreams.fromPublisher(
				WebServiceProvider.getModuleApi().getModuleList(SessionManager.getUser().getToken(true))
						.onErrorResumeNext(throwable -> {
							//Notify observer that there was an error by posting null
							moduleList.postValue(null);
							return Flowable.empty();
						})
						.subscribeOn(Schedulers.io())

		);

		Log.d(TAG, "getLectures: source acquired");
		moduleList.addSource(source, moduleModels -> {
			moduleList.setValue(moduleModels);
			moduleList.removeSource(source);
		});
	}

	public LiveData<List<ModuleModel>> observeModules(){
		return moduleList;
	}

	public void postAttendance(AttendanceModel attendanceModel){

		Log.d(TAG, "postAttendance: Posting attendance");
		
//		final LiveData<AttendanceModel> source = LiveDataReactiveStreams.fromPublisher(
//			WebServiceProvider.getLectureApi().postAttendance(SessionManager.getUser().getToken(true), attendanceModel)
//				.onErrorReturn(throwable -> {
//
//					AttendanceModel error;
//
//					Log.d(TAG, "postAttendance: Throwable: " + throwable.getClass().getCanonicalName());
//					if (throwable.getMessage().contains("409")){
//						error = new AttendanceModel(-1, null, null, "Already signed in", true);
//					} else if (throwable.getMessage().contains("406")) {
//						error = new AttendanceModel(-1, null, null, "Device already used", true);
//					} else {
//						error = new AttendanceModel(-1, null, null, "Something went wrong", true);
//					}
//					attendance.postValue(error);
//					return error;
//				})
//				.subscribeOn(Schedulers.io())
//				.doOnSubscribe(action -> {
//					Log.d(TAG, "postAttendance: Subscribed!");
//				})
//				.doOnComplete(() -> {
//					Log.d(TAG, "postAttendance: Completed!");
//				})
//				.doOnError(action ->{
//					Log.d(TAG, "postAttendance: Error!");
//				})
//		);
//
//		attendance.addSource(source, attendanceModel1 -> {
//			attendance.setValue(attendanceModel);
//			attendance.removeSource(source);
//		});

		Observable<AttendanceModel> postAttendanceObservable = WebServiceProvider.getLectureApi()
				.postAttendance(SessionManager.getUser().getToken(true), attendanceModel);

		postAttendanceObservable.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(action -> {
					Log.d(TAG, "postAttendance: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "postAttendance: Complete!");
				})
				.onErrorReturn(throwable -> {
					AttendanceModel error;

					Log.d(TAG, "postAttendance: Throwable: " + throwable.getClass().getCanonicalName());
					if (throwable.getMessage().contains("409")){
						error = new AttendanceModel(-1, null, null, "Already signed in", true);
					} else if (throwable.getMessage().contains("406")) {
						error = new AttendanceModel(-1, null, null, "Device already used", true);
					} else {
						error = new AttendanceModel(-1, null, null, "Something went wrong", true);
					}
					attendance.postValue(error);
					return error;
				})
				.doOnError(action ->{
					Log.d(TAG, "postAttendance: Error!");
				})
				.doOnNext(attendanceModel1 -> {
					Log.d(TAG, "postAttendance: " + attendanceModel1.toString());
					attendance.postValue(attendanceModel1);
				})
				.subscribe();
	}


	public LiveData<AttendanceModel> observeAttendance() { return attendance; }

	public void clearAttendance(){
		attendance.setValue(null);
	}

	public void clearAll(){
		if (lectureList.getValue() != null){
			lectureList.getValue().clear();
		}
		lecture.setValue(null);
		moduleList.setValue(null);
		attendance.setValue(null);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		clearAll();
	}
}
