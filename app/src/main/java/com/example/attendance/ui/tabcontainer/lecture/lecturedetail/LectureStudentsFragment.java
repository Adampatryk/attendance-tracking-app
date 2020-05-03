package com.example.attendance.ui.tabcontainer.lecture.lecturedetail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.example.attendance.util.Constants;

public class LectureStudentsFragment extends Fragment {

	private static final String TAG = "LectureStudentsFragment";

	private AppViewModel viewModel;
	private SwipeRefreshLayout refreshStudentsAttendedLayout;
	private TextView no_students_assigned_text_view;
	private RecyclerView studentsAttendanceRecyclerView;

	final private StudentAttendanceForLectureRecyclerViewAdapter adapter = new StudentAttendanceForLectureRecyclerViewAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_lecture_students, container, false);

		findViews(v);
		setupViews(v);


		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
		subscribeObserver();
	}

	public void findViews(View v){
		refreshStudentsAttendedLayout = v.findViewById(R.id.pull_to_refresh_attended_students);
		no_students_assigned_text_view = v.findViewById(R.id.no_students_assigned_to_lecture_text_view);
		studentsAttendanceRecyclerView = v.findViewById(R.id.recycler_view_students_attended_list);
	}

	public void setupViews(View v){
		//Set up on refresh listener for swipe refresh layout
		refreshStudentsAttendedLayout.setOnRefreshListener(() -> {
			Log.d(TAG, "setupViews: Refreshing attendance for lecture");
			viewModel.getAttendedStudentsForCurrentLecture();
		});

		//Setup recycler view and its adapter

		//Only let lecturers find attendance for other students
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
			adapter.setOnItemClickListener(attendanceModel -> {
				viewModel.setStudent(attendanceModel.getStudent().getId());
				if (viewModel.observerStudent().getValue() == null) {
					Toast.makeText(getContext(), "Student not found", Toast.LENGTH_SHORT).show();
				} else if (viewModel.observeModule().getValue() == null) {
					Toast.makeText(getContext(), "Module not found", Toast.LENGTH_SHORT).show();
				} else {
					Navigation.findNavController(v).navigate(R.id.action_lectureTabFragment_to_studentFragment2);
				}
			});
		}

		studentsAttendanceRecyclerView.setHasFixedSize(true);
		studentsAttendanceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		studentsAttendanceRecyclerView.setAdapter(adapter);
	}

	public void subscribeObserver(){
		viewModel.observeAttendanceForLecture().observe(getViewLifecycleOwner() ,studentForLectureAttendance -> {
			//Has the call been successful?
			if (studentForLectureAttendance != null){
				Log.d(TAG, "subscribeObserver: Lectures returned");

				//Update adapter for recycler view
				adapter.submitList(studentForLectureAttendance);
				if(studentForLectureAttendance.size() == 0) {
					Log.d(TAG, "subscribeObservers: no lectures for this user");
					no_students_assigned_text_view.setVisibility(View.VISIBLE);
				} else {
					no_students_assigned_text_view.setVisibility(View.GONE);

				}
			} else {
				Log.d(TAG, "subscribeObservers: lectureModels returned null");
				Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
			}
			//Stop the refresh animation
			Log.d(TAG, "subscribeObserver: Stop refresh animation");
			refreshStudentsAttendedLayout.setRefreshing(false);
			studentsAttendanceRecyclerView.setVisibility(View.VISIBLE);
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		studentsAttendanceRecyclerView.setVisibility(View.INVISIBLE);
		refreshStudentsAttendedLayout.setRefreshing(true);
		viewModel.getAttendedStudentsForCurrentLecture();
	}

}
