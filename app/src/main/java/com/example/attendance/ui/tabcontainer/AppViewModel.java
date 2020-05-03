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
import com.example.attendance.models.UserModel;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppViewModel extends ViewModel {
	private static final String TAG = "TabViewModel";

	private int selectedLectureId = -1;
	private int selectedModuleId = -1;
	private int selectedStudentId = -1;
	private MediatorLiveData<List<LectureModel>> lectureList = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> lecture = new MediatorLiveData<>();
	private MediatorLiveData<ModuleModel> module = new MediatorLiveData<>();
	private MediatorLiveData<UserModel> student = new MediatorLiveData<>();
	private MediatorLiveData<AttendanceModel> attendance = new MediatorLiveData<>();
	private MediatorLiveData<List<ModuleModel>> moduleList = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> creatingLecture = new MediatorLiveData<>();
	private MediatorLiveData<LectureModel> deletingLecture = new MediatorLiveData<>();
	private MediatorLiveData<Bitmap> qrCodeGenerator = new MediatorLiveData<>();
	private MediatorLiveData<List<AttendanceModel>> lectureAttendance = new MediatorLiveData<>();
	private MediatorLiveData<List<LectureModel>> lecturesForModule = new MediatorLiveData<>();
	private MediatorLiveData<List<UserModel>> studentsForModule = new MediatorLiveData<>();
	private MediatorLiveData<List<LectureModel>> studentLectureAttendanceForModule = new MediatorLiveData<>();
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
		if (lectureList.getValue() != null){
			for (LectureModel l: lectureList.getValue()) {
				Log.d(TAG, "setLecture: trying lectureList");
				if (l.getId() == id){
					lecture.postValue(l);
					selectedLectureId = id;
					setModule(l.getModuleId());
					Log.d(TAG, "setLecture: id:"+id);
					return;
				}
			}
		}
		if (lecturesForModule.getValue() != null){
			for (LectureModel l: lecturesForModule.getValue()) {
				Log.d(TAG, "setLecture: trying lectureList");
				if (l.getId() == id){
					lecture.postValue(l);
					selectedLectureId = id;
					setModule(l.getModuleId());
					Log.d(TAG, "setLecture: id:"+id);
					return;
				}
			}
		}
		else if (id < 0) {
			lecture = null;
		}
		else {
			Log.d(TAG, "setLecture: No lectures");
		}
	}

	public void setModule(int id){
		Log.d(TAG, "setModule: id:" + id);
		if (moduleList.getValue() == null){
			//TODO Deal with null module list...
			Log.d(TAG, "setModule: Module list is null");
			return;
		} else if (id < 0){
			module.setValue(null);
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

	public void setStudent(int id){
		Log.d(TAG, "setStudent: id:" + id);
		if (id < 0){
			student.setValue(null);
			return;
		}

		if (lectureAttendance.getValue() != null){
			//Find correct student to set
			for (AttendanceModel m: lectureAttendance.getValue()) {
				if (m.getStudent().getId() == id){
					student.setValue(m.getStudent());
					selectedStudentId = id;
					Log.d(TAG, "setStudent: Selected from lectureAttendance: " + selectedStudentId);
					return;
				}
			}
		}
		if (studentsForModule.getValue() != null){
			for (UserModel s: studentsForModule.getValue()) {
				if (s.getId() == id){
					student.setValue(s);
					selectedStudentId = id;
					Log.d(TAG, "setStudent: Selected from studentsForModule: " + selectedStudentId);
					return;
				}
			}
		}
		student.setValue(null);
		Log.d(TAG, "setStudent: Student not found");
		Log.d(TAG, "setStudent: Selected: " + selectedStudentId);
	}

	public LiveData<ModuleModel> observeModule(){
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
					Log.d(TAG, "postAttendance: Throwable " + throwable.toString());
					Log.d(TAG, "postAttendance: Throwable " + throwable.getMessage());
					if (throwable.getMessage().contains("409")){
						error = new AttendanceModel(-1, null, null, "Device Already Used", true);
					} else if (throwable.getMessage().contains("406")) {
						error = new AttendanceModel(-1, null, null, "Invalid QR Code", true);
					} else if (throwable.getMessage().contains("403")) {
							error = new AttendanceModel(-1, null, null, "No access to this lecture", true);
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
		if (lecturesForModule.getValue() != null){
			lecturesForModule.getValue().clear();
		}
		if (moduleList.getValue() != null){
			moduleList.getValue().clear();
		}
		lecture.setValue(null);
		attendance.setValue(null);

		Log.d(TAG, "clearAll: Cleared");
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
					lectureAttendance.postValue(null);
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

	public LiveData<List<LectureModel>> observeLecturesForModule() {
		return lecturesForModule;
	}

	public LiveData<List<UserModel>> observeStudentsForModule() {
		return studentsForModule;
	}


	public void getLecturesForModule() {
		WebServiceProvider.getLectureApi().getLecturesForModule(SessionManager.getUser().getToken(true), selectedModuleId)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "getLecturesForModule: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "getLecturesForModule: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "getLecturesForModule: Error!");
				})
				.onErrorReturn(throwable -> {
					List<LectureModel> error = new ArrayList<>();

					Log.d(TAG, "getLecturesForModule: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "getLecturesForModule: Throwable: " + throwable.getMessage());
					lecturesForModule.postValue(null);
					return error;
				})
				.doOnNext(lectureModels -> {
					Log.d(TAG, "getLecturesForModule: " + lectureModels.toString());
					//lectureList.postValue(lectureModels);
					lecturesForModule.postValue(lectureModels);
				})
				.subscribe();
	}

	public void getStudentsForModule() {
		WebServiceProvider.getModuleApi().getStudentsForModule(SessionManager.getUser().getToken(true), selectedModuleId)
				.subscribeOn(Schedulers.io())
				.doOnSubscribe(action -> {
					Log.d(TAG, "getStudentsForModule: Subscribed!");
				})
				.doOnComplete(() -> {
					Log.d(TAG, "getStudentsForModule: Complete!");
				})
				.doOnError(action ->{
					Log.d(TAG, "getStudentsForModule: Error!");
				})
				.onErrorReturn(throwable -> {
					List<UserModel> error = new ArrayList<>();

					Log.d(TAG, "getStudentsForModule: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "getStudentsForModule: Throwable: " + throwable.getMessage());
					studentsForModule.postValue(null);
					return error;
				})
				.doOnNext(userModels -> {
					Log.d(TAG, "getStudentsForModule: " + userModels.toString());
					studentsForModule.postValue(userModels);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
	}

	public LiveData<List<LectureModel>> observeStudentLectureAttendanceForModule() {
		return studentLectureAttendanceForModule;
	}

	public void getStudentLectureAttendanceForModule() {
		WebServiceProvider
				.getLectureApi()
				.getLecturesForModuleWithStudentsAttendance(SessionManager.getUser().getToken(true), selectedModuleId, selectedStudentId)
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
					List<LectureModel> error = new ArrayList<>();

					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Throwable: " + throwable.getClass().getCanonicalName());
					Log.d(TAG, "getAttendedStudentsForCurrentLecture: Throwable: " + throwable.getMessage());
					studentLectureAttendanceForModule.postValue(null);
					return error;
				})
				.doOnNext(lectureModels -> {
					studentLectureAttendanceForModule.postValue(lectureModels);
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
	}

	public LiveData<UserModel> observerStudent(){
		return student;
	}

	public void clearAttendance() {
		attendance.setValue(null);
	}

	public void clearLectures() {
		if (lectureList.getValue() != null){
			lectureList.getValue().clear();
		}
	}

	public void clearLecturesForModule() {
		if (lecturesForModule.getValue() != null){
			lecturesForModule.getValue().clear();
		}
	}

	public void setCurrentLecture(LectureModel currentLecture) {
		lecture.setValue(currentLecture);
	}
}
