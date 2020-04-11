package com.example.attendance.ui.lecturedetail;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class LectureDetailFragment extends Fragment {
	private static final String TAG = "LectureDetailFragment";
	private LectureDetailViewModel viewModel;
	private MaterialTextView
			txt_lecture_title,
			txt_lecture_module,
			txt_lecture_prof;
	private Button btn_scan;


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lecture_detail_fragment, container, false);

		//Setup views
		txt_lecture_title = v.findViewById(R.id.lecture_title);
		txt_lecture_module = v.findViewById(R.id.lecture_module);
		txt_lecture_prof = v.findViewById(R.id.lecture_lecturer);

		btn_scan = v.findViewById(R.id.btn_scan);

		if (!SessionManager.getUser().isLecturer() && SessionManager.isAuthenticated()) {
			btn_scan.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_lectureDetailFragment_to_scanFragment));
			btn_scan.setVisibility(View.VISIBLE);
		}


		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(LectureDetailViewModel.class);

		Bundle bundle = getArguments();
		if (bundle != null && bundle.getInt("lecture)id") != -1) {
			viewModel.getLecture(bundle.getInt("lecture_id"));
		}
		else {
			Log.d(TAG, "onActivityCreated: Something went wrong getting the id of the lecture");
			Objects.requireNonNull(getActivity()).onBackPressed();
		}
		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			txt_lecture_title.setText(lectureModel.getTitle());
			txt_lecture_module.setText(lectureModel.getModule().getTitle());
			txt_lecture_prof.setText(lectureModel.getModule().getProfessors()[0].getUsername());
		});
	}
}
