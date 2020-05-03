package com.example.attendance.ui.tabcontainer;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.ui.tabcontainer.lecture.LectureListFragment;
import com.example.attendance.ui.tabcontainer.module.ModuleListFragment;
import com.google.android.material.tabs.TabLayout;

public class LecturesModulesContainerFragment extends Fragment {

	private static final String TAG = "LecturesModulesTabFragm";

	private AppViewModel viewModel;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lectures_modules_tab_fragment, container, false);

		setupViews(v);

		return v;
	}

	public void findViews(View v){

	}

	public void setupViews(View v){

		//Set up user toolbar
		Toolbar toolbar = v.findViewById(R.id.user_toolbar);
		toolbar.setTitle("Attendance");
		toolbar.setSubtitle(SessionManager.getLoggedInAs());

		//If the user is a lecturer give full menu, else only provide with logout item
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){
			toolbar.inflateMenu(R.menu.main_menu);
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
			//Create a new lecture
			else if (item.getItemId() == R.id.create_lecture_menu_item){
				//Don't navigate unless modules are loaded
				if (viewModel.observeModules().getValue() == null){
					Toast.makeText(getContext(), "Loading modules...", Toast.LENGTH_SHORT).show();
					viewModel.getModules();
				} else {
					Navigation.findNavController(v).navigate(R.id.action_tabFragment_to_createLectureFragment);
				}
			}
			return false;
		});

		ViewPager viewPager = v.findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		// Set Tabs inside Toolbar
		TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
	}

	public void setupViewPager(ViewPager viewPager) {
		TabAdapter adapter = new TabAdapter(getChildFragmentManager());
		adapter.addFragment(new LectureListFragment(), "Lectures Today");
		adapter.addFragment(new ModuleListFragment(), "Modules");

		viewPager.setAdapter(adapter);
	}

}
