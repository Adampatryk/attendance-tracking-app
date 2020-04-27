package com.example.attendance.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static String formatDateFromDateString(String inputDateFormat, String outputDateFormat,
												  String inputDate) throws ParseException {
		Date mParsedDate;
		String mOutputDateString;
		SimpleDateFormat mInputDateFormat =
				new SimpleDateFormat(inputDateFormat, java.util.Locale.getDefault());
		SimpleDateFormat mOutputDateFormat =
				new SimpleDateFormat(outputDateFormat, java.util.Locale.getDefault());
		mParsedDate = mInputDateFormat.parse(inputDate);
		mOutputDateString = mOutputDateFormat.format(mParsedDate);
		return mOutputDateString;
	}
}
