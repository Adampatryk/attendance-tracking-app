package com.example.attendance.ui.scan;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
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
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.QrCodeGenerator;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.example.attendance.util.Constants;
import com.example.attendance.util.DateTimeConversion;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class ScanFragment extends Fragment {
	private static final String TAG = "ScanFragment";
	private TabViewModel viewModel;
	private BarcodeDetector barcodeDetector;
	private CameraSource cameraSource;
	private SurfaceView surfaceView;
	private Handler barcodeHandler;
	SurfaceHolder.Callback cameraCallback;

	private int lecture_id;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.scan_fragment, container, false);

		surfaceView = v.findViewById(R.id.surface_scan);

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
		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);

		//Make sure the secret and lecture_id is up to date
		viewModel.observeLecture().observeForever(lectureModel -> {
			lecture_id = lectureModel.getId();
		});

		return v;
	}

	private void setupServerResponse(){
		//What to do when the server responds
		viewModel.observeAttendance().observe(getViewLifecycleOwner(), attendanceModel -> {
			if (attendanceModel != null){
				Log.d(TAG, "setupServerResponse: " + attendanceModel.toString());
				if (attendanceModel.getLectureId() == -1){
					Toast.makeText(getContext(), "Error: " + attendanceModel.getError(), Toast.LENGTH_SHORT).show();
				} else {
					//Server accepted
					Toast.makeText(getContext(), "Accepted!: " + attendanceModel.toString(), Toast.LENGTH_SHORT).show();
					Log.d(TAG, "setupServerResponse: Accepted!: " + attendanceModel.toString());
				}
				Log.d(TAG, "setupServerResponse: Lecture model: " + viewModel.observeAttendance().getValue());
				getActivity().onBackPressed();
			} else{
				Toast.makeText(getContext(), "response was null", Toast.LENGTH_LONG).show();
				Log.d(TAG, "setupServerResponse: response was null");
			}
			viewModel.observeAttendance().removeObservers(getViewLifecycleOwner());
		});
	}

	private boolean handleBarcode(Barcode barcode){

		long qrTimestamp = DateTimeConversion.millisToSec(System.currentTimeMillis()) / Constants.TIMESTAMP_VALID_FOR;
		//Setup request model
		AttendanceModel attendance = new AttendanceModel(lecture_id, barcode.displayValue, Constants.DEVICE_ID, qrTimestamp);


		//Ask the server if this is a valid code
		setupServerResponse();
		viewModel.postAttendance(attendance);

		//Stop the camera
		cameraSource.stop();

		//TODO: Show dialogue box with refresh

//		//Does the scanned QR code match what it should be based on the secret
//		boolean match = QrCodeGenerator.generateCodeFromSecret(secret).equals(barcode.displayValue);
//
//		//If it matches, tell the server and update viewmodel, otherwise tell the user the QR code is invalid
//		if (match) {
//			AttendanceModel attendance = new AttendanceModel(lecture_id, secret, Constants.DEVICE_ID, null, false);
//
//			//What to do when the server responds after scanning QR code
//			viewModel.observeAttendance().observe(getViewLifecycleOwner(), attendanceModel -> {
//				if (attendanceModel != null){
//
//					if (attendanceModel.getLectureId() == -1){
//						Toast.makeText(getContext(), "Error: " + attendanceModel.getError(), Toast.LENGTH_SHORT).show();
//					} else {
//						//Server accepted
//						Toast.makeText(getContext(), "Accepted!: " + attendanceModel.toString(), Toast.LENGTH_SHORT).show();
//						Log.d(TAG, "onCreateView: Accepted!: " + attendanceModel.toString());
//					}
//					Log.d(TAG, "onActivityCreated: Lecture model: " + viewModel.observeAttendance().getValue());
//					getActivity().onBackPressed();
//				} else {
//					Log.d(TAG, "onCreateView: Response was null");
//				}
//				viewModel.observeAttendance().removeObservers(getViewLifecycleOwner());
//
//				Log.d(TAG, "OnResult: lecture observers? " + viewModel.observeAttendance().hasObservers());
//			});
//			viewModel.postAttendance(attendance);
//
//			cameraSource.stop();
//
//			Log.d(TAG, "handleBarcode: Posting attendance..." + attendance.toString());
//		}
//		return match;
		return true;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

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
					Navigation.findNavController(getView()).navigate(R.id.action_scanFragment_to_lectureDetailFragment);
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Cleaning up");
		cameraSource.release();
		viewModel.observeAttendance().removeObservers(getViewLifecycleOwner());
		viewModel.observeLecture().removeObservers(getViewLifecycleOwner());
	}
}
