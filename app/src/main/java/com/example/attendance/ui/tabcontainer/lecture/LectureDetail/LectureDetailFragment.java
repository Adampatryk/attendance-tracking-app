package com.example.attendance.ui.tabcontainer.lecture.LectureDetail;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.DeleteLectureModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;


public class LectureDetailFragment extends Fragment {
	private static final String TAG = "LectureDetailFragment";
	private TabViewModel viewModel;
	private MaterialTextView
			txt_lecture_title,
			txt_lecture_module,
			txt_lecture_prof;
	private Button btn_scan;
	private ImageView qr_code_image_view;
	private FloatingActionButton delete_button;
	private ProgressBar lecture_deleting_progress;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lecture_detail_fragment, container, false);

		//Setup views
		txt_lecture_title = v.findViewById(R.id.lecture_title);
		txt_lecture_module = v.findViewById(R.id.lecture_module);
		txt_lecture_prof = v.findViewById(R.id.lecture_lecturer);
		lecture_deleting_progress = v.findViewById(R.id.lecture_deleting_progress);

		//Make sure the user is authenticated
		if (SessionManager.isAuthenticated()) {
			//If the user is a lecturer, show the QR code, otherwise show the QR code
			if (SessionManager.getUser().isLecturer()) {
				qr_code_image_view = v.findViewById(R.id.lecture_qr_code);
				qr_code_image_view.setVisibility(View.VISIBLE);

				delete_button = v.findViewById(R.id.delete_lecture_button);
				delete_button.setVisibility(View.VISIBLE);
				delete_button.setOnClickListener(view -> {
					confirmDeleteLecture();
				});
			} else {
				btn_scan = v.findViewById(R.id.btn_scan);
			}
		}

		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);

		subscribeObservers();
		viewModel.getLecture();

		return v;
	}

	private void confirmDeleteLecture() {

		LectureModel lecture = viewModel.observeLecture().getValue();

		if (lecture == null) {
			Toast.makeText(getContext(), "Something went wrong while deleting lecture - Lecture is null", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "confirmDeleteLecture: Cannot delete, lecture is null");
			return;
		}

		DeleteLectureModel lectureToDelete = new DeleteLectureModel(lecture.getId(),
						lecture.getModule().getId());

		//TODO Create dialog box to ask if the user is sure

		//On positive button pressed


		//Observe the deleted lecture
		viewModel.getDeletedLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			//Set progress bar to stop turning
			lecture_deleting_progress.setVisibility(View.GONE);

			Log.d(TAG, "confirmDeleteLecture: lectureModel: " + lectureModel.toString());
			//If id returns as -1, this was a success
			if (lectureModel != null && lectureModel.getId() == -1 && lectureModel.getTitle() != null){
				Toast.makeText(getContext(), "Lecture Deleted", Toast.LENGTH_LONG).show();
				getActivity().onBackPressed();
			} else {
				Toast.makeText(getContext(), "Something went wrong while deleting lecture", Toast.LENGTH_SHORT).show();
			}

			viewModel.getDeletedLecture().removeObservers(getViewLifecycleOwner());
		});

		//Delete lecture
		viewModel.deleteLecture(lectureToDelete);
		lecture_deleting_progress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void subscribeObservers(){
		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			Log.d(TAG, "subscribeObservers: Lecture Received");
			txt_lecture_title.setText(lectureModel.getTitle());
			txt_lecture_module.setText(lectureModel.getModule().getTitle());
			txt_lecture_prof.setText(lectureModel.getModule().getProfessors()[0].getUsername());

			//If the user is authenticated
			if (SessionManager.isAuthenticated()){
				//If the user is a lecturer, start generating the qr code
				if (SessionManager.getUser().isLecturer()){
					viewModel.startGenerating(lectureModel.getSecret());
				}
				//if the user is a student, and the user is supposed to be at the lecture but isn't yet
				// then show the scan-in button
				else if (!SessionManager.getUser().isLecturer() && lectureModel.isPresent() == 0) {
					btn_scan.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_lectureTabFragment_to_scanFragment));
					btn_scan.setVisibility(View.VISIBLE);
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
		viewModel.observeLecture().removeObservers(getViewLifecycleOwner());

		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
			viewModel.stopGenerating();
			viewModel.getQrCodeGenerator().removeObservers(getViewLifecycleOwner());
		}
	}
}
