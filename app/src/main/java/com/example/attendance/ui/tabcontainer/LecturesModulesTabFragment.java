package com.example.attendance.ui.tabcontainer;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.ui.tabcontainer.lecture.LectureListFragment;
import com.example.attendance.ui.tabcontainer.module.ModuleListFragment;
import com.example.attendance.util.Constants;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class LecturesModulesTabFragment extends Fragment {

	private static final String TAG = "LecturesModulesTabFragm";

	private TabViewModel viewModel;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lectures_modules_tab_fragment, container, false);

		ViewPager viewPager = v.findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		// Set Tabs inside Toolbar
		TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		//Setup logout button
		Button btn_logout = v.findViewById(R.id.btn_logout);
		btn_logout.setOnClickListener(v1 -> {
			Navigation.findNavController(v1).navigate(R.id.action_tabFragment_to_loginFragment);
			SessionManager.logout();

			viewModel.clearAll();
		});


		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);
	}

	public void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getChildFragmentManager());
		adapter.addFragment(new LectureListFragment(), "Lectures Today");
		adapter.addFragment(new ModuleListFragment(), "Modules");

		viewPager.setAdapter(adapter);
	}

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public Adapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

}
