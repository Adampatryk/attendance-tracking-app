package com.example.attendance.ui.scan;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import com.example.attendance.ui.lecturedetail.LectureDetailViewModel;
import com.example.attendance.util.Hasher;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Objects;

public class ScanFragment extends Fragment {
	private static final String TAG = "ScanFragment";
	private LectureDetailViewModel viewModel;
	private BarcodeDetector barcodeDetector;
	private CameraSource cameraSource;
	private SurfaceView surfaceView;
	private Handler barcodeHandler;

	private String secret = null;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.scan_fragment, container, false);

		surfaceView = v.findViewById(R.id.surface_scan);

		barcodeDetector = new BarcodeDetector.Builder(getContext())
				.setBarcodeFormats(Barcode.QR_CODE).build();

		cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
				.setRequestedPreviewSize(640, 480).build();

		surfaceView.setSecure(true);
		surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder surfaceHolder) {

				//Check if camera permission is granted
				if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
					Toast.makeText(getContext(), "Permission to use camera not granted", Toast.LENGTH_SHORT).show();
					return;
				}

				//Try to start the camera
				try {
					cameraSource.start(surfaceHolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
				cameraSource.stop();
			}
		});

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


		viewModel = new ViewModelProvider(requireActivity()).get(LectureDetailViewModel.class);

		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			secret = lectureModel.getSecret();
			Toast.makeText(getContext(), "secret: " + secret, Toast.LENGTH_SHORT).show();
		});

		return v;
	}

	public boolean handleBarcode(Barcode barcode){


		//Toast.makeText(getContext(), "Generated QRCode: " + QrCodeGenerator.generateCodeFromSecret(secret) +
		//		"\nScanned QRCode: " + barcode.displayValue, Toast.LENGTH_LONG).show();

		//Toast.makeText(getContext(), "QRCode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();

		//Does the scanned QR code match what it should be based on the secret
		boolean match = QrCodeGenerator.generateCodeFromSecret(secret).equals(barcode.displayValue);

		//If it matches, tell the server and update viewmodel, otherwise tell the user the QR code is invalid
		if (match) {
			//TODO Post to the server that this student is at this lecture using viewmodel
			getActivity().onBackPressed();
			Snackbar.make(getView(), "You have been scanned in", Snackbar.LENGTH_SHORT);
		}
		return match;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

}
