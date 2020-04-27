package com.example.attendance.ui.tabcontainer.module;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.models.ModuleModel;
import com.example.attendance.models.UserModel;
import com.example.attendance.ui.tabcontainer.TabViewModel;

public class ModuleDetailFragment extends Fragment {

	private TabViewModel viewModel;

	private TextView txt_module_title, txt_module_code, txt_module_year, txt_module_teacher;
	private static final String TAG = "ModuleDetailFragment";

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.module_detail_fragment, container, false);

		txt_module_title = v.findViewById(R.id.module_title);
		txt_module_code = v.findViewById(R.id.module_code);
		txt_module_year = v.findViewById(R.id.academic_year);
		txt_module_teacher = v.findViewById(R.id.teachers);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);

		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.getModule().observe(getViewLifecycleOwner(), module -> {
			if (module == null){
				Toast.makeText(getContext(), "Error fetching the module details", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onActivityCreated: Module was null");
				return;
			}
			txt_module_title.setText(module.getTitle());
			txt_module_code.setText(module.getModuleCode());
			txt_module_year.setText(module.getacademicYearStart().toString());

			String teachersText = "";

			if (module.getProfessors() != null){
				for (UserModel teacher: module.getProfessors()){
					teachersText += teacher.getUsername() + "\n";
				}
			}

			txt_module_teacher.setText(teachersText);
		});
	}

}
