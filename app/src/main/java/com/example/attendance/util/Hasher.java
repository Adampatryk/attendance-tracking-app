package com.example.attendance.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

	private static String bytesToString(byte[] bytes) {
		//Bytes to string
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(b);
			if (hex.length() == 1) {
				stringBuilder.append("0");
				stringBuilder.append(hex.charAt(hex.length() - 1));
			} else {
				stringBuilder.append(hex.substring(hex.length() - 2));
			}
		}
		return stringBuilder.toString();
	}

	//Function to hash from bytes
	public static String hash(byte[] source){
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(source);

			return bytesToString(hash);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String hash(String source){
		return hash(source.getBytes(StandardCharsets.UTF_8));
	}

}
