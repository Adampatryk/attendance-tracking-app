package com.example.attendance.ui.tabcontainer.module.moduledetail;

import androidx.lifecycle.ViewModelProvider;

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
import com.example.attendance.models.UserModel;
import com.example.attendance.ui.tabcontainer.AppViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ModuleDetailFragment extends Fragment {

	private AppViewModel viewModel;

	private TextView txt_module_title,
			txt_module_code,
			txt_module_year,
			txt_module_teacher,
			txt_module_credits,
			txt_module_info;
	private static final String TAG = "ModuleDetailFragment";

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.module_detail_fragment, container, false);

		txt_module_title = v.findViewById(R.id.module_title);
		txt_module_code = v.findViewById(R.id.module_code);
		txt_module_year = v.findViewById(R.id.academic_year);
		txt_module_teacher = v.findViewById(R.id.module_lecturer);
		txt_module_credits = v.findViewById(R.id.module_credits);
		txt_module_info = v.findViewById(R.id.module_info);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

		subscribeObservers();
	}

	public void subscribeObservers(){
		viewModel.observeModule().observe(getViewLifecycleOwner(), module -> {
			if (module == null){
				Toast.makeText(getContext(), "Error fetching the module details", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onActivityCreated: Module was null");
				return;
			}
			txt_module_title.setText(module.getTitle());
			txt_module_code.setText(module.getModuleCode());
			txt_module_credits.setText(module.getCredits() + " Credits");
			Log.d(TAG, "info: " + module.getInfo());
			txt_module_info.setText(module.getInfo());

			String academicYear;

			SimpleDateFormat fullYearFormatter = new SimpleDateFormat("YYYY", Locale.ENGLISH);
			int yearStart = Integer.parseInt(fullYearFormatter.format(module.getAcademicYearStart()));

			academicYear = yearStart + "/" + String.valueOf(yearStart+1).substring(2);

			txt_module_year.setText(academicYear);


			String professors = "";

			for (UserModel user : module.getProfessors()){
				professors += user.getFirstName() + " " + user.getLastName() +
						" (" + user.getUsername() + ")\n";
			}

			txt_module_teacher.setText(professors);
		});
	}

}
