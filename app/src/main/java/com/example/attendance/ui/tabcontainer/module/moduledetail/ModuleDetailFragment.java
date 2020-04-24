package com.example.attendance.ui.tabcontainer.module.moduledetail;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.ui.tabcontainer.TabViewModel;

public class ModuleDetailFragment extends Fragment {

	private TabViewModel mViewModel;

	public static ModuleDetailFragment newInstance() {
		return new ModuleDetailFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.module_detail_fragment, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(TabViewModel.class);
		// TODO: Use the ViewModel
	}

}
