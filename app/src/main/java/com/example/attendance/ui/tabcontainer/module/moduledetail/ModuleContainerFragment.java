package com.example.attendance.ui.tabcontainer.module.moduledetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
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

public class ModuleContainerFragment extends Fragment {
	private static final String TAG = "ModuleTabFragment";

	private AppViewModel viewModel;
	private Toolbar toolbar;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.module_tab_fragment, container, false);

		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

		findViews(v);
		setupViews(v);

		return v;
	}

	public void findViews(View v){
		toolbar = v.findViewById(R.id.module_toolbar);
	}

	public void setupViews(View v) {
		ViewPager viewPager = v.findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		// Set Tabs inside Toolbar
		TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		//Set up user toolbar
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
		toolbar.setNavigationOnClickListener(view -> {
			Navigation.findNavController(getView()).popBackStack();
		});
		toolbar.inflateMenu(R.menu.logout_menu);
		toolbar.setOnMenuItemClickListener(item -> {
			Navigation.findNavController(v).navigate(R.id.action_global_loginFragment);
			viewModel.clearAll();
			SessionManager.logout();
			return false;
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.observeModule().observe(getViewLifecycleOwner(), moduleModel -> {
			toolbar.setTitle("Module: " + moduleModel.getTitle());
		});
	}

	public void setupViewPager(ViewPager viewPager) {
		TabAdapter adapter = new TabAdapter(getChildFragmentManager());
		adapter.addFragment(new ModuleDetailFragment(), "Details");
		adapter.addFragment(new ModuleLecturesFragment(), "Lectures");
		adapter.addFragment(new ModuleStudentsFragment(), "Participants");

		viewPager.setAdapter(adapter);
	}

}
