package com.example.attendance.ui.tabcontainer.student;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import com.example.attendance.models.ModuleModel;
import com.example.attendance.models.UserModel;
import com.example.attendance.ui.tabcontainer.LectureRecyclerViewAdapter;
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.example.attendance.util.Constants;

public class StudentFragment extends Fragment {

	private static final String TAG = "StudentFragment";

	private RecyclerView lecturesForModuleAttendanceForStudentRecyclerView;
	private SwipeRefreshLayout refreshLayoutLecturesForModuleAttendanceForStudent;
	private TextView noLecturesTextView;

	final private LectureRecyclerViewAdapter adapter = new LectureRecyclerViewAdapter(true, true, false, false);

	private AppViewModel viewModel;

	private ModuleModel currentModule;
	private UserModel currentStudent;
	private Toolbar toolbar;

	public StudentFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_student, container, false);

		findViews(v);
		setupViews(v);

		return v;
	}

	public void findViews(View v){
		lecturesForModuleAttendanceForStudentRecyclerView = v.findViewById(R.id.recycler_view_student_attendance_lecture_list_for_module);
		refreshLayoutLecturesForModuleAttendanceForStudent = v.findViewById(R.id.pull_to_refresh_student_attendance_for_lectures_for_module);
		noLecturesTextView = v.findViewById(R.id.no_attendance_lectures_for_module_text_view);
		toolbar = v.findViewById(R.id.student_toolbar);
	}

	public void setupViews(View v){
		//What to do on refresh
		refreshLayoutLecturesForModuleAttendanceForStudent.setOnRefreshListener(() -> {
			viewModel.getStudentLectureAttendanceForModule();
		});

		//Setup recycler view
		lecturesForModuleAttendanceForStudentRecyclerView.setHasFixedSize(true);
		lecturesForModuleAttendanceForStudentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		lecturesForModuleAttendanceForStudentRecyclerView.setAdapter(adapter);

		//Set up user toolbar
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
		toolbar.setNavigationOnClickListener(view -> {
			Navigation.findNavController(getView()).popBackStack();
		});

		//Can only logout from here
		toolbar.inflateMenu(R.menu.logout_menu);
		toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.logout_menu_item){
				Navigation.findNavController(v).navigate(R.id.action_global_loginFragment);
				viewModel.clearAll();
				SessionManager.logout();
			}
			return false;
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated: called");

		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
		subscribeObservers();
	}

	public void subscribeObservers(){
		//Rect to changes in lecture attendance
		viewModel.observeStudentLectureAttendanceForModule().observe(getViewLifecycleOwner(),  lectureModels -> {
			Log.d(TAG, "refreshLectures: observer notified");
			//Has the call been successful?
			if (lectureModels != null){
				Log.d(TAG, "refreshLectures: Lectures returned");
				adapter.submitList(lectureModels);
				if(lectureModels.size() == 0) {
					Log.d(TAG, "subscribeObservers: no lectures for this user");
					noLecturesTextView.setVisibility(View.VISIBLE);
				} else {
					noLecturesTextView.setVisibility(View.GONE);

				}
			} else {
				Log.d(TAG, "subscribeObservers: lectureModels returned null");
				Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
			}
			//Stop the refresh animation
			Log.d(TAG, "refreshLectures: Stop refresh animation");
			refreshLayoutLecturesForModuleAttendanceForStudent.setRefreshing(false);
			lecturesForModuleAttendanceForStudentRecyclerView.setVisibility(View.VISIBLE);
		});

		//React to the student changing
		viewModel.observerStudent().observe(getViewLifecycleOwner(), userModel -> {
			if (userModel != null){
				currentStudent = userModel;
			}
			setTitle();
		});

		//React to module changes
		viewModel.observeModule().observe(getViewLifecycleOwner(), moduleModel -> {
			if (moduleModel != null){
				currentModule = moduleModel;
			}
			setTitle();
		});
	}

	public void setTitle(){
		if (currentModule != null && currentStudent != null){
			String title = currentModule.getModuleCode() + ": " + currentStudent.getFirstName() + " (" + currentStudent.getUsername() + ")";
			toolbar.setTitle("Attendance: " + currentStudent.getFirstName() + " (" + currentStudent.getUsername() + ")");
			toolbar.setSubtitle("Module: " + currentModule.getTitle());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		lecturesForModuleAttendanceForStudentRecyclerView.setVisibility(View.INVISIBLE);
		refreshLayoutLecturesForModuleAttendanceForStudent.setRefreshing(true);
		viewModel.getStudentLectureAttendanceForModule();
	}

}
