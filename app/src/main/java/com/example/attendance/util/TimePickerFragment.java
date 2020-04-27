package com.example.attendance.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class TimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

	FragmentCallbacks fragmentCallbacks;

	public TimePickerFragment(FragmentCallbacks listener){
		fragmentCallbacks = listener;
	}

	private static final String TAG = "TimePickerFragment";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.d(TAG, "onTimeSet: " + hourOfDay + ":" + minute);
		fragmentCallbacks.TimeUpdated(hourOfDay, minute);
	}

	public interface FragmentCallbacks {
		void TimeUpdated(int hour, int minute);
	}

}