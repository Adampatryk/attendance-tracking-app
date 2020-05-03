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
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.example.attendance.ui.tabcontainer.LectureRecyclerViewAdapter;
import com.example.attendance.util.Constants;

public class ModuleLecturesFragment extends Fragment {
	private static final String TAG = "ModuleLecturesFragment";

	private AppViewModel viewModel;

	private SwipeRefreshLayout refreshLecturesForModuleLayout;
	private RecyclerView lecturesForModuleRecyclerView;
	private TextView noModulesForLectureTextView;

	final private LectureRecyclerViewAdapter lectureRecyclerViewAdapter = new LectureRecyclerViewAdapter(true, true, true, true);


	public ModuleLecturesFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_module_lectures, container, false);

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
		viewModel.observeLecturesForModule().observe(getViewLifecycleOwner() , lecturesForModule -> {
			Log.d(TAG, "subscribeObserver: IMPORTANT: " + lecturesForModule.size());

			//Has the call been successful?
			if (lecturesForModule != null){
				Log.d(TAG, "subscribeObserver: Lectures returned");

				//Update adapter for recycler view
				lectureRecyclerViewAdapter.submitList(lecturesForModule);
				if(lecturesForModule.size() == 0) {
					Log.d(TAG, "subscribeObservers: no lectures for this user");
					noModulesForLectureTextView.setVisibility(View.VISIBLE);
				} else {
					noModulesForLectureTextView.setVisibility(View.GONE);

				}
			} else {
				Log.d(TAG, "subscribeObservers: lectureModels returned null");
				Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
			}
			//Stop the refresh animation
			Log.d(TAG, "subscribeObserver: Stop refresh animation");
			refreshLecturesForModuleLayout.setRefreshing(false);
			lecturesForModuleRecyclerView.setVisibility(View.VISIBLE);
		});
	}

	public void findViews(View v){
		refreshLecturesForModuleLayout = v.findViewById(R.id.pull_to_refresh_lectures_for_module);
		noModulesForLectureTextView = v.findViewById(R.id.no_lectures_for_module_text_view);
		lecturesForModuleRecyclerView = v.findViewById(R.id.recycler_view_lecture_list_for_module);
	}

	public void setupViews(View v){
		//Set up on refresh listener for swipe refresh layout
		refreshLecturesForModuleLayout.setOnRefreshListener(() -> {
			Log.d(TAG, "setupViews: Refreshing lectures for module");
			viewModel.getLecturesForModule();
		});

		//Set up recycler view and its adapter

		lectureRecyclerViewAdapter.setOnItemClickListener(lectureModel -> {
			Log.d(TAG, "setupViews: " + lectureModel.toString());
			viewModel.setLecture(lectureModel.getId());
			Navigation.findNavController(v).navigate(R.id.action_moduleTabFragment_to_lectureTabFragment);
		});

		lecturesForModuleRecyclerView.setHasFixedSize(true);
		lecturesForModuleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		lecturesForModuleRecyclerView.setAdapter(lectureRecyclerViewAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		lecturesForModuleRecyclerView.setVisibility(View.INVISIBLE);
		refreshLecturesForModuleLayout.setRefreshing(true);
		viewModel.getLecturesForModule();
	}
}
