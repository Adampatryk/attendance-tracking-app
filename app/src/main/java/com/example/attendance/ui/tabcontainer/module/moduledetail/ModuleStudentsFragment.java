package com.example.attendance.ui.tabcontainer.module.moduledetail;

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

public class ModuleStudentsFragment extends Fragment {
	private static final String TAG = "ModuleStudentsFragment";

	private AppViewModel viewModel;

	private SwipeRefreshLayout refreshStudentsForModuleLayout;
	private RecyclerView studentsForModuleRecyclerView;
	private TextView noStudentsForModuleTextView;

	final private StudentForModuleRecyclerViewAdapter studentRecyclerViewAdapter = new StudentForModuleRecyclerViewAdapter();


	public ModuleStudentsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_module_students, container, false);

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

	public void subscribeObserver(){
		viewModel.observeStudentsForModule().observe(getViewLifecycleOwner() , studentsForModule -> {
			//Has the call been successful?
			if (studentsForModule != null){
				Log.d(TAG, "subscribeObserver: Lectures returned");

				//Update adapter for recycler view
				studentRecyclerViewAdapter.submitList(studentsForModule);
				if(studentsForModule.size() == 0) {
					Log.d(TAG, "subscribeObservers: no lectures for this user");
					noStudentsForModuleTextView.setVisibility(View.VISIBLE);
				} else {
					noStudentsForModuleTextView.setVisibility(View.GONE);

				}
			} else {
				Log.d(TAG, "subscribeObservers: studentsForModule returned null");
				Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
			}
			//Stop the refresh animation
			Log.d(TAG, "subscribeObserver: Stop refresh animation");
			refreshStudentsForModuleLayout.setRefreshing(false);
			studentsForModuleRecyclerView.setVisibility(View.VISIBLE);
		});
	}

	public void findViews(View v){
		refreshStudentsForModuleLayout = v.findViewById(R.id.pull_to_refresh_students_for_module);
		noStudentsForModuleTextView = v.findViewById(R.id.no_students_for_module_text_view);
		studentsForModuleRecyclerView = v.findViewById(R.id.recycler_view_student_list_for_module);
	}

	public void setupViews(View v){
		//Set up on refresh listener for swipe refresh layout
		refreshStudentsForModuleLayout.setOnRefreshListener(() -> {
			Log.d(TAG, "setupViews: Refreshing students for module");
			viewModel.getStudentsForModule();
		});

		//Set up recycler view and its adapter

		//Only let lecturers navigate to see other students detail
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){

			studentRecyclerViewAdapter.setOnItemClickListener(userModel -> {
				Log.d(TAG, "setupViews: Student id: " + userModel.getId());
				if (userModel == null || userModel.getId() == -1) {
					Toast.makeText(getContext(), "Something wrong with this student...", Toast.LENGTH_SHORT).show();
				} else {
					viewModel.setStudent(userModel.getId());
					Navigation.findNavController(v).navigate(R.id.action_moduleTabFragment_to_studentFragment2);
				}
			});
		}

		studentsForModuleRecyclerView.setHasFixedSize(true);
		studentsForModuleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		studentsForModuleRecyclerView.setAdapter(studentRecyclerViewAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		studentsForModuleRecyclerView.setVisibility(View.INVISIBLE);
		refreshStudentsForModuleLayout.setRefreshing(true);
		viewModel.getStudentsForModule();
	}
}
