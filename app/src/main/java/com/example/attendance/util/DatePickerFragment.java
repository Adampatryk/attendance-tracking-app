package com.example.attendance.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.text.ParseException;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private static final String TAG = "DatePickerFragment";

	DateFragmentCallbacks dateFragmentCallbacks;

	public DatePickerFragment(DateFragmentCallbacks listener){
		dateFragmentCallbacks = listener;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);

	}

	public interface DateFragmentCallbacks {
		void onDateSet(int i, int i1, int i2) throws ParseException;
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		try {
			dateFragmentCallbacks.onDateSet(year, month, day);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
