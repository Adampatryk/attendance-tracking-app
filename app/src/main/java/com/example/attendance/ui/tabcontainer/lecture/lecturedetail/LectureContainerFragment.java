package com.example.attendance.ui.tabcontainer.lecture.lecturedetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.DeleteLectureModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.ui.tabcontainer.TabAdapter;
import com.example.attendance.ui.tabcontainer.AppViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

public class LectureContainerFragment extends Fragment {

	private static final String TAG = "LectureTabFragment";

	private AppViewModel viewModel;
	private Toolbar toolbar;
	private ProgressBar lecture_deleting_progress;

	DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
		switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				deleteLecture();
				break;
		}
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lecture_tab_fragment, container, false);

		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

		findViews(v);
		setupViews(v);

		return v;
	}

	public void findViews(View v){
		toolbar = v.findViewById(R.id.lecture_toolbar);
		lecture_deleting_progress = v.findViewById(R.id.lecture_deleting_progress);
	}

	public void setupViews(View v) {


		ViewPager viewPager = v.findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);

		//Only setup tabs for this page if the user is an authenticated lecturer
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){
			// Set Tabs inside Toolbar
			tabs.setupWithViewPager(viewPager);
		} else {
			tabs.setVisibility(View.GONE);
		}


		//Set up user toolbar
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
		toolbar.setNavigationOnClickListener(view -> {
			Navigation.findNavController(getView()).popBackStack();
		});

		//If the user is a lecturer give full menu, else only provide with logout item
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){
			toolbar.inflateMenu(R.menu.lecture_menu);
		} else {
			toolbar.inflateMenu(R.menu.logout_menu);
		}

		//Setup the toolbar menu
		toolbar.setOnMenuItemClickListener(item -> {
			// Logout
			if (item.getItemId() == R.id.logout_menu_item){
				Navigation.findNavController(v).navigate(R.id.action_global_loginFragment);
				viewModel.clearAll();
				SessionManager.logout();
			}
			//Ask if the user wants to delete the lecture
			else if (item.getItemId() == R.id.delete_lecture_menu_item){
				//Create dialog box to ask if the user is sure
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setMessage("Are you sure you want to delete this lecture?")
						.setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("Cancel", dialogClickListener)
						.show();
			}
			return false;
		});
	}

	private void deleteLecture() {

		LectureModel lecture = viewModel.observeLecture().getValue();

		if (lecture == null) {
			Toast.makeText(getContext(), "Something went wrong while deleting lecture - Lecture is null", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "confirmDeleteLecture: Cannot delete, lecture is null");
			return;
		}

		DeleteLectureModel lectureToDelete = new DeleteLectureModel(lecture.getId(),
				lecture.getModule().getId());

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
		lecture_deleting_progress.setVisibility(View.VISIBLE);
		viewModel.deleteLecture(lectureToDelete);
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.observeLecture().observe(getViewLifecycleOwner(), lectureModel -> {
			if (lectureModel != null){
				toolbar.setTitle("Lecture: " + lectureModel.getTitle());
				toolbar.setSubtitle("Module: " + lectureModel.getModule().getTitle());
			} else {
				viewModel.getLecture();
			}
		});
	}

	public void setupViewPager(ViewPager viewPager) {
		TabAdapter adapter = new TabAdapter(getChildFragmentManager());
		adapter.addFragment(new LectureDetailFragment(), "Details");

		//Only add attendance page for the lecturer
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
			adapter.addFragment(new LectureStudentsFragment(), "Attendance");
		}

		viewPager.setAdapter(adapter);
	}
}
