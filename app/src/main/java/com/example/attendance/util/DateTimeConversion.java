package com.example.attendance.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeConversion {

	public static long millisToSec(long millis){
		return millis / 1000;
	}

	public static String getTimeFromDate(Date date){
		return getTimeFormatter().format(date);
	}
	public static String getShortDateFromDate(Date date){
		return getShortDateFormatter().format(date);
	}
	public static String getLongDateFromDate(Date date){
		return getLongDateFormatter().format(date);
	}

	public static SimpleDateFormat getTimeFormatter(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return simpleDateFormat;
	}

	public static SimpleDateFormat getShortDateFormatter(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return simpleDateFormat;
	}

	public static SimpleDateFormat getLongDateFormatter(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM ", Locale.ENGLISH);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return simpleDateFormat;
	}
}
