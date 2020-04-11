package com.example.attendance.auth;


import android.util.Log;

import com.example.attendance.util.DateTimeConversion;
import com.example.attendance.util.Hasher;

public class QrCodeGenerator {
	//How often the code will change...
	private static final int TIMESTAMP_VALID_FOR = 3;

	private static final String TAG = "QrCodeGenerator";

	public static String generateCodeFromSecret(String secret){
		String secretHashed = Hasher.hash(secret);
		long timestamp = DateTimeConversion.millisToSec(System.currentTimeMillis()) / TIMESTAMP_VALID_FOR;
		Log.d(TAG, "generateCodeFromSecret: timestamp: " + timestamp);

		String timestampHashed = Hasher.hash(String.valueOf(timestamp));

		return Hasher.hash(secretHashed + "-" + timestampHashed);
		//return Hasher.hash(secretHashed + "-");
		//return secretHashed;
	}
}
