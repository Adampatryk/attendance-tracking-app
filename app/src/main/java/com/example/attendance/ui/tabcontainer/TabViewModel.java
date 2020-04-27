package com.example.attendance.ui.tabcontainer;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.attendance.auth.QrCodeGenerator;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.CreateLectureModel;
import com.example.attendance.models.DeleteLectureModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.models.ModuleModel;
import com.example.attendance.network.WebServiceProvider;
import com.google.zxing.WriterException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TabViewModel extends ViewModel {
	private static final String TAG = "TabViewModel";

	private int selectedLectureId = -1;
	private int selectedModuleId = -1;
	private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> lecture = new MediatorLiveData<>();
	private MediatorLiveData<ModuleModel> module = new MediatorLiveData<>();
	private MediatorLiveData<AttendanceModel> attendance = new MediatorLiveData<>();
	private MediatorLiveData<List<ModuleModel>> moduleList = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> creatingLecture = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> deletingLecture = new MediatorLiveData<>();
	private MediatorLiveData<Bitmap> qrCodeGenerator = new MediatorLiveData<>();
	private MediatorLiveData<List<AttendanceModel>> lectureAttendance = new MediatorLiveData<>();
	private Disposable poolingDisposable = null;

	public void getLecturesForToday(){

		String dateToday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

		makeLecturesCall(WebServiceProvider
				.getLectureApi()
				.getLecturesForDate(SessionManager.getUser().getToken(true), dateToday));
	}

	public void getLectures(){
		makeLecturesCall(WebServiceProvider
				.getLectureApi()
				.getLectureList(SessionManager.getUser().getToken(true)));
	}

	private void makeLecturesCall(io.reactivex.rxjava3.core.Observable<List<LectureModel>> observable){
		observable.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "getLectures: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "getLectures: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "getLectures: Error!");
				})
				.onErrorReturn(throwable -> {
					List<LectureModel> error = new ArrayList<>();

					Log.d(TAG, "getLectures: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "getLectures: Throwable: " + throwable.getMessage());
					lectureList.postValue(null);
					return error;
				})
				.doOnNext(lectureModels -> {
					Log.d(TAG, "getLectures: " + lectureModels.toString());
					lectureList.postValue(lectureModels);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
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

	public void setModule(int id){
		if (moduleList.getValue() == null){
			//TODO Deal with null module list...
			Log.d(TAG, "setModule: Module list is null");
			return;
		} else if (id < 0){
			module = null;
		}

		//Find correct module to set
		for (ModuleModel m: moduleList.getValue()) {
			if (m.getId() == id){
				module.setValue(m);
				selectedModuleId = id;
				Log.d(TAG, "setModule: id:"+id);
				return;
			}
		}
	}

	public LiveData<ModuleModel> getModule(){
		setModule(selectedModuleId);
		return module;
	}

	public void getLecture(){
		Log.d(TAG, "getLecture: Triggering lecture update");
		setLecture(selectedLectureId);
	}

	public LiveData<LectureModel> observeLecture() {
		return lecture;
	}

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

		WebServiceProvider.getLectureApi()
				.postAttendance(SessionManager.getUser().getToken(true), attendanceModel)
				.subscribeOn(Schedulers.io())
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

	public void createLecture(CreateLectureModel lectureToCreate) {

		WebServiceProvider.getLectureApi()
				.createLecture(SessionManager.getUser().getToken(true), lectureToCreate)
				.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "createLecture: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "createLecture: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "createLecture: Error!");
				})
				.onErrorReturn(throwable -> {
					LectureModel error = new LectureModel(-1, null, null, null, null);

					Log.d(TAG, "createLecture: Throwable: " + throwable.getClass().getCanonicalName());
					creatingLecture.postValue(null);
					return error;
				})
				.doOnNext(lectureModel -> {
					Log.d(TAG, "createLecture: " + lectureModel.toString());
					creatingLecture.postValue(lectureModel);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();

	}

	public void deleteLecture(DeleteLectureModel lectureToDelete){
		WebServiceProvider.getLectureApi().deleteLecture(SessionManager.getUser().getToken(true), lectureToDelete)
				.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "deleteLecture: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "deleteLecture: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "deleteLecture: Error!");
				})
				.onErrorReturn(throwable -> {
					LectureModel error = new LectureModel(-2, null, null, null, null);

					Log.d(TAG, "deleteLecture: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "deleteLecture: Throwable: " + throwable.getMessage());
					deletingLecture.postValue(null);
					return error;
				})
				.doOnNext(lectureModel -> {
					Log.d(TAG, "deleteLecture: " + lectureModel.toString());
					deletingLecture.postValue(lectureModel);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
	}

	public LiveData<LectureModel> getCreatedLecture() {
		return creatingLecture;
	}

	public LiveData<LectureModel> getDeletedLecture(){
		return deletingLecture;
	}

	public void startGenerating(String secret) {
		Log.d(TAG, "startGenerating: called");

		if(poolingDisposable == null){
			Log.d(TAG, "startGenerating: starting");
			poolingDisposable = Observable.interval(1, TimeUnit.SECONDS)
					.subscribeOn(io.reactivex.schedulers.Schedulers.computation())
					.observeOn(io.reactivex.schedulers.Schedulers.io())
					.doOnNext(n -> generateQrCode(secret))
					.subscribe();

		}

	}

	private void generateQrCode(String secret) {
		Log.d(TAG, "startGenerating: Attempting to generate");

		String qrCodeString = QrCodeGenerator.generateCodeFromSecret(secret);
		QRGEncoder qrgEncoder = new QRGEncoder(qrCodeString, null, QRGContents.Type.TEXT, 300);

		try{
			// Getting QR-Code as Bitmap
			Bitmap bitmap = qrgEncoder.encodeAsBitmap();
			qrCodeGenerator.postValue(bitmap);
		}
		catch (WriterException e) {
			Log.v(TAG, e.toString());
		}
	}

	public LiveData<Bitmap> getQrCodeGenerator() {
		return qrCodeGenerator;
	}


	public void stopGenerating() {
		Log.d(TAG, "stopGenerating: called");
		if(poolingDisposable != null){
			poolingDisposable.dispose();
			poolingDisposable = null;
			Log.d(TAG, "stopGenerating: stopped");
		}
	}

	public void getAttendedStudentsForCurrentLecture() {
		WebServiceProvider.getLectureApi().getLectureAttendance(SessionManager.getUser().getToken(true), selectedLectureId)
				.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Error!");
				})
				.onErrorReturn(throwable -> {
					List<AttendanceModel> error = new ArrayList<>();

					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Throwable: " + throwable.getMessage());
					lectureList.postValue(null);
					return error;
				})
				.doOnNext(attendanceModels -> {
					Log.d(TAG, "getLectures: " + attendanceModels.toString());
					lectureAttendance.postValue(attendanceModels);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
	}

	public LiveData<List<AttendanceModel>> observeAttendanceForLecture() {
		return lectureAttendance;
	}
}
