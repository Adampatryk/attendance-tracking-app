package com.example.attendance.ui.scan;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.example.attendance.util.Constants;
import com.example.attendance.util.DateTimeConversion;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanFragment extends Fragment {
	private static final String TAG = "ScanFragment";
	private AppViewModel viewModel;
	private BarcodeDetector barcodeDetector;
	private CameraSource cameraSource;
	private SurfaceView surfaceView;
	private Handler barcodeHandler;
	SurfaceHolder.Callback cameraCallback;
	private ProgressBar scanLoading;
	private int lecture_id;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.scan_fragment, container, false);

		findViews(v);
		setupViews(v);

		return v;
	}

	public void findViews(View v){
		surfaceView = v.findViewById(R.id.surface_scan);
		scanLoading = v.findViewById(R.id.scan_loading);
	}

	public void setupViews(View v){
		barcodeDetector = new BarcodeDetector.Builder(getContext())
				.setBarcodeFormats(Barcode.QR_CODE).build();

		cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
				.setRequestedPreviewSize(640, 480).build();

		//Camera Callback
		cameraCallback = new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder surfaceHolder) {
				Log.d(TAG, "surfaceCreated: called");
				//Check if camera permission is granted
				if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
					Toast.makeText(getContext(), "Permission to use camera not granted", Toast.LENGTH_SHORT).show();
					// Permission is not granted, ask for it
					requestPermissions(new String[]{Manifest.permission.CAMERA},
							Constants.PERMISSIONS_REQUEST_CAMERA);

				} else {
					Log.d(TAG, "surfaceCreated: Starting camera");
					startCamera(surfaceHolder);
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
				Log.d(TAG, "surfaceChanged: called");
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
				cameraSource.stop();
			}
		};

		surfaceView.getHolder().addCallback(cameraCallback);

		//Prevents the user from taking a screenshot or recording screen
		// (had to turn off for dissertation write up)
		surfaceView.setSecure(true);

		barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
			@Override
			public void release() {
			}

			@Override
			public void receiveDetections(Detector.Detections<Barcode> detections) {
				SparseArray<Barcode> qrCodes = detections.getDetectedItems();
				if (qrCodes.size() != 0){
					Log.d(TAG, "receiveDetections: Something detected: " + qrCodes.valueAt(0).displayValue);
					if (barcodeHandler.post(() -> handleBarcode(qrCodes.valueAt(0)))){
						this.release();
					}
				}else {
					Log.d(TAG, "receiveDetections: Nothing detected: " + qrCodes.toString());
				}
			}
		});

		barcodeHandler = new Handler();
	}

	private void setupServerResponse(){
		//What to do when the server responds
		viewModel.observeAttendance().observe(getViewLifecycleOwner(), attendanceModel -> {
			if (attendanceModel != null){
				Log.d(TAG, "setupServerResponse: " + attendanceModel.toString());
				if (attendanceModel.getLectureId() == -1){
					Toast.makeText(getContext(), "Error: " + attendanceModel.getError() + "\nCheck lecture and try again.", Toast.LENGTH_LONG).show();
				} else {
					//Server accepted
					Toast.makeText(getContext(), "You have been signed in! ", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "setupServerResponse: Accepted!: " + attendanceModel.toString());
					LectureModel currentLecture = viewModel.observeLecture().getValue();
					if (currentLecture!= null){
						currentLecture.setPresent(1);
						viewModel.setCurrentLecture(currentLecture);
					} else {
						Log.d(TAG, "setupServerResponse: Current lecture is null");
					}
				}
				Log.d(TAG, "setupServerResponse: Lecture model: " + viewModel.observeAttendance().getValue());
				viewModel.getLecturesForModule();
				Navigation.findNavController(getView()).popBackStack();
			} else{
				Log.d(TAG, "setupServerResponse: something went wrong - response was null");
			}
			//Stop progress bar
			scanLoading.setVisibility(View.INVISIBLE);

		});
	}

	private boolean handleBarcode(Barcode barcode){

		long qrTimestamp = DateTimeConversion.millisToSec(System.currentTimeMillis()) / Constants.TIMESTAMP_VALID_FOR;
		//Setup request model
		AttendanceModel attendance = new AttendanceModel(lecture_id, barcode.displayValue, Constants.DEVICE_ID, qrTimestamp);


		if (getView() == null) {
			return false;
		}

		//Ask the server if this is a valid code
		viewModel.postAttendance(attendance);

		//Stop the camera
		cameraSource.stop();

		//Start progress bar animation
		scanLoading.setVisibility(View.VISIBLE);
		return true;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

		setupServerResponse();

		//Make sure the secret and lecture_id is up to date
		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			if (lectureModel != null) {
				lecture_id = lectureModel.getId();
			} else {
				lecture_id = -1;
				viewModel.getLecture();
			}
		});
	}

	private boolean startCamera(SurfaceHolder surfaceHolder){
		//Try to start the camera
		try {
			cameraSource.start(surfaceHolder);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case Constants.PERMISSIONS_REQUEST_CAMERA: {
				Log.d(TAG, "onRequestPermissionsResult: Callback");
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted
					Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
					startCamera(surfaceView.getHolder());
				} else {
					// permission denied
					Log.d(TAG, "onRequestPermissionsResult: Permission Denied");
					Toast.makeText(getContext(),
							"You must enable the camera permission to scan yourself in",
							Toast.LENGTH_SHORT).show();
					Navigation.findNavController(getView()).popBackStack();
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Cleaning up");
		cameraSource.release();
		viewModel.clearAttendance();
	}
}
