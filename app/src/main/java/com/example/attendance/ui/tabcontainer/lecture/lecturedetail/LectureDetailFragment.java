package com.example.attendance.ui.tabcontainer.lecture.lecturedetail;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.UserModel;
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.example.attendance.util.DateTimeConversion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;


public class LectureDetailFragment extends Fragment {
	private static final String TAG = "LectureDetailFragment";
	private AppViewModel viewModel;
	private MaterialTextView
			txt_lecture_title,
			txt_lecture_module,
			txt_lecture_prof,
			txt_lecture_info,
			txt_lecture_time,
			txt_lecture_date,
			txt_lecture_present,
			txt_lecture_absent;
	private FloatingActionButton btn_scan;
	private ImageView qr_code_image_view;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lecture_detail_fragment, container, false);

		findViews(v);
		setupViews(v);

		return v;
	}

	public void findViews(View v){
		txt_lecture_title = v.findViewById(R.id.lecture_title);
		txt_lecture_module = v.findViewById(R.id.lecture_module);
		txt_lecture_prof = v.findViewById(R.id.lecture_lecturer);
		txt_lecture_info = v.findViewById(R.id.lecture_info);
		txt_lecture_date = v.findViewById(R.id.lecture_date);
		txt_lecture_time = v.findViewById(R.id.lecture_time);
		txt_lecture_present = v.findViewById(R.id.lecture_present);
		txt_lecture_absent = v.findViewById(R.id.lecture_absent);
		qr_code_image_view = v.findViewById(R.id.lecture_qr_code);
		btn_scan = v.findViewById(R.id.btn_scan);
	}

	public void setupViews(View v){
		//Make sure the user is authenticated
		if (SessionManager.isAuthenticated()) {
			//If the user is a lecturer, show the QR code
			if (SessionManager.getUser().isLecturer()) {
				qr_code_image_view.setVisibility(View.VISIBLE);
			}
		}
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			if (lectureModel != null) {
				//Set properties of lecture to the text views
				txt_lecture_title.setText(lectureModel.getTitle());
				txt_lecture_module.setText(lectureModel.getModule().getTitle());

				String professors = "";

				for (UserModel user : lectureModel.getModule().getProfessors()) {
					professors += user.getFirstName() + " " + user.getLastName() +
							" (" + user.getUsername() + ")\n";
				}

				txt_lecture_prof.setText(professors);
				txt_lecture_info.setText(lectureModel.getInfo());

				String date = DateTimeConversion.getLongDateFromDate(lectureModel.getDate());
				String time = DateTimeConversion.getTimeFromDate(lectureModel.getDate());

				txt_lecture_date.setText(date);
				txt_lecture_time.setText(time);

				//If the user is authenticated
				if (SessionManager.isAuthenticated()) {
					//If the user is a lecturer, start generating the qr code
					if (SessionManager.getUser().isLecturer()) {
						viewModel.startGenerating(lectureModel.getSecret());
					}
					//if the user is a student, and the user is supposed to be at the lecture but isn't yet
					// then show the scan-in button, and show that they are not yet present
					else if (!SessionManager.getUser().isLecturer()) {
						if (lectureModel.isPresent() == 0) {
							btn_scan.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_lectureTabFragment_to_scanFragment));
							btn_scan.setVisibility(View.VISIBLE);
							txt_lecture_absent.setVisibility(View.VISIBLE);
						} else if (lectureModel.isPresent() == 1) {
							btn_scan.setVisibility(View.INVISIBLE);
							txt_lecture_present.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});

		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
			viewModel.getQrCodeGenerator().observe(getViewLifecycleOwner(), qrCodeBitmap -> {
				// Setting Bitmap to ImageView
				qr_code_image_view.setImageBitmap(qrCodeBitmap);
				Log.d(TAG, "subscribeObservers: Getting qrCodeBitmap " + qrCodeBitmap);
				Log.d(TAG, "subscribeObservers: Getting what is on image " + qr_code_image_view);
			});
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		//viewModel.observeLecture().removeObservers(getViewLifecycleOwner());

		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
			viewModel.stopGenerating();
			viewModel.getQrCodeGenerator().removeObservers(getViewLifecycleOwner());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		viewModel.getLecture();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewModel.clearLectures();
	}
}
