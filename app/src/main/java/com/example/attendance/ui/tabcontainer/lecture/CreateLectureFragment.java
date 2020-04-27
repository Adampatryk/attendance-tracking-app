package com.example.attendance.ui.tabcontainer.lecture;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.models.CreateLectureModel;
import com.example.attendance.models.ModuleModel;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.example.attendance.util.DatePickerFragment;
import com.example.attendance.util.DateUtils;
import com.example.attendance.util.TimePickerFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class CreateLectureFragment extends Fragment implements TimePickerFragment.FragmentCallbacks,
		DatePickerFragment.DateFragmentCallbacks {

	private static final String TAG = "CreateLectureFragment";
	private AutoCompleteTextView moduleDropDown;
	private TabViewModel viewModel;
	private int selectedModuleId = -1;
	private TextInputEditText titleTextView, timeTextView, dateTextView;
	private TextInputLayout titleInputLayout, moduleInputLayout, timeInputLayout, dateInputLayout;
	private Button createLectureButton;
	private ProgressBar creatingLectureProgress;
	private String formattedDate, formattedTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_create_lecture, container, false);

		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);

		findViews(v);
		setupViews();

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void findViews(View v) {
		titleInputLayout = v.findViewById(R.id.create_lecture_title_layout);
		moduleInputLayout = v.findViewById(R.id.create_lecture_module);
		timeInputLayout = v.findViewById(R.id.time_txt_layout);
		dateInputLayout = v.findViewById(R.id.date_txt_layout);
		moduleDropDown = v.findViewById(R.id.create_lecture_module_value);
		titleTextView = v.findViewById(R.id.create_lecture_title_value);
		timeTextView = v.findViewById(R.id.create_lecture_time_value);
		dateTextView = v.findViewById(R.id.create_lecture_date_value);
		createLectureButton = v.findViewById(R.id.create_lecture_button);
		creatingLectureProgress = v.findViewById(R.id.creaing_lecture_progress);
	}

	public void setupViews(){
		ModuleDropDownAdapter adapter = new ModuleDropDownAdapter();

		moduleDropDown.setAdapter(adapter);

		timeTextView.setOnClickListener(view -> {
			new TimePickerFragment(CreateLectureFragment.this).show(getParentFragmentManager(), this.toString());
		});

		dateTextView.setOnClickListener(view -> {
			new DatePickerFragment(CreateLectureFragment.this).show(getParentFragmentManager(), this.toString());
		});

		titleTextView.onEditorAction(EditorInfo.IME_ACTION_DONE);

		createLectureButton.setOnClickListener(view -> {
			attemptCreateLecture();
		});
	}

	public void attemptCreateLecture() {
		//Validate data
		if (validData()) {
			//Set up observer
			viewModel.getCreatedLecture().observe(getViewLifecycleOwner(), lecture -> {

				creatingLectureProgress.setVisibility(View.INVISIBLE);

				if (lecture == null) {
					Toast.makeText(getContext(), "Something went wrong creating the lecture", Toast.LENGTH_LONG).show();
					Log.d(TAG, "attemptCreateLecture: Something went wrong creating the lecture");

				//On success, force update on lecture list objects and navigate back to the lecture list
				} else {
					Toast.makeText(getContext(), "Lecture created", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "attemptCreateLecture: lecture: " + lecture.toString());
					getActivity().onBackPressed();
				}
			});

			//Send request
			String datetime = formattedDate + "T" + formattedTime;

			CreateLectureModel lectureToCreate = new CreateLectureModel(titleTextView.getText().toString(),
																	selectedModuleId,
																	datetime);//Format needs to be YYYY-MM-DDThh:mm[:ss[.uuuuuu]][+HH:MM|-HH:MM|Z]


			viewModel.createLecture(lectureToCreate);
			creatingLectureProgress.setVisibility(View.VISIBLE);
		}
	}

	//Validate form data
	public boolean validData() {

		boolean valid = true;

		//Make sure title is not empty
		if (TextUtils.isEmpty(titleTextView.getText().toString().trim())) {
			titleInputLayout.setError("Title cannot be blank");
			valid = false;
		} else {
			titleInputLayout.setError(null);
		}

		//Make sure Module is selected
		if (TextUtils.isEmpty(moduleDropDown.getText().toString().trim())) {
			moduleInputLayout.setError("Choose a module");
			valid = false;
		} else {
			moduleInputLayout.setError(null);
		}

		//Make sure date is selected
		if (TextUtils.isEmpty(dateTextView.getText().toString().trim())) {
			dateInputLayout.setError("Choose a date");
			valid = false;
		} else {
			dateInputLayout.setError(null);
		}

		//Make sure time is selected
		if (TextUtils.isEmpty(timeTextView.getText().toString().trim())) {
			timeInputLayout.setError("Choose a time");
			valid = false;
		} else {
			timeInputLayout.setError(null);
		}

		return valid;
	}

	@Override
	public void TimeUpdated(int hour, int minute) {
		formattedTime = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);

		timeTextView.setText(formattedTime);
		timeInputLayout.setError(null);
	}

	@Override
	public void onDateSet(int year, int month, int day) {
		formattedDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month+1, day);

		String dateToView;

		try {
			dateToView = DateUtils.formatDateFromDateString("yyyy-MM-dd", "EEE, MMM d, ''yy", formattedDate);
		} catch (ParseException e) {
			e.printStackTrace();
			dateToView = formattedDate;
		}

		dateTextView.setText(dateToView);
		dateInputLayout.setError(null);
	}

	private class ModuleDropDownAdapter extends BaseAdapter implements Filterable {

		@Override
		public int getCount() {
			List<ModuleModel> modules = viewModel.observeModules().getValue();
			if (modules == null){
				Toast.makeText(getContext(), "Network connection failed", Toast.LENGTH_SHORT).show();
				return 0;
			} else {
				return modules.size();
			}
		}

		@Override
		public Object getItem(int i) {
			//TODO Deal with null list
			Log.d(TAG, "getItem: " + viewModel.observeModules().getValue().get(i).toString());
			return (Object) viewModel.observeModules().getValue().get(i).getTitle();
		}

		@Override
		public long getItemId(int i) {

			//TODO Deal with null list
			selectedModuleId = viewModel.observeModules().getValue().get(i).getId();
			Log.d(TAG, "getItemId: Called, ID: " + selectedModuleId);

			return selectedModuleId;
		}

		@Override
		public View getView(int i, View convertView, ViewGroup parent) {
			Log.d(TAG, "getView: " + i);
			// inflate the layout for each list row
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).
						inflate(R.layout.module_drop_down_menu, parent, false);
			}

			//Get current module to be displayed
			String moduleTitle = (String) getItem(i);

			// get the TextView for item name and item description
			TextView textViewItemName = (TextView)
					convertView.findViewById(R.id.module_dropdown_item);

			textViewItemName.setText(moduleTitle);

			return textViewItemName;
		}

		@Override
		public Filter getFilter() {
			Log.d(TAG, "getFilter: Called");
			return new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence charSequence) {
					Log.d(TAG, "performFiltering: Filtering happening");
					FilterResults results = new FilterResults();

//					//If there's nothing to filter on, return the original data for your list
//					if(charSequence == null || charSequence.length() == 0)
//					{
//						//TODO Deal with null
//						results.values = viewModel.observeModules().getValue();
//						results.count = viewModel.observeModules().getValue().size();
//					}
//					else
//					{
//						ArrayList<HashMap<String,String>> filterResultsData = new ArrayList<HashMap<String,String>>();
//
//						for(HashMap<String,String> data : viewModel.observeModules().getValue())
//						{
//							//In this loop, you'll filter through originalData and compare each item to charSequence.
//							//If you find a match, add it to your new ArrayList
//							//I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
//							if(data matches your filter criteria)
//							{
//								filterResultsData.add(data);
//							}
//						}
//
//						results.values = filterResultsData;
//						results.count = filteredResultsData.size();
//					}
					results.values = viewModel.observeModules().getValue();
					results.count = viewModel.observeModules().getValue().size();

					return results;
				}

				@Override
				protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
					Log.d(TAG, "publishResults: Called");
					notifyDataSetChanged();
				}
			};
		}
	}
}
