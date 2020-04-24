package com.example.attendance.ui.tabcontainer;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import com.example.attendance.ui.tabcontainer.lecture.lecturelist.LectureListFragment;
import com.example.attendance.ui.tabcontainer.module.modulelist.ModuleListFragment;
import com.example.attendance.util.Constants;
import com.google.android.material.tabs.TabLayout;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment {

	private static final String TAG = "TabFragment";

	private TabViewModel viewModel;

	public static TabFragment newInstance() {
		return new TabFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_fragment, container, false);

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

		Toast.makeText(getContext(), Constants.DEVICE_ID, Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onCreateView: device_id: " + Constants.DEVICE_ID);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);
	}

	public void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getChildFragmentManager());
		adapter.addFragment(new LectureListFragment(), "Lectures");
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
