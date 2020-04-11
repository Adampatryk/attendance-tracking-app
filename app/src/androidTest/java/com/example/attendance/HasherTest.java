package com.example.attendance;

import com.example.attendance.util.Hasher;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HasherTest {
	@Test
	public void useSha256Test(){
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", Hasher.hash("12345"));
	}

	@Test
	public void combinedHashTest(){
		String timestamp = "12345678";
		String secret = "3fadf3afdf43fdfsa23h7k789lkolagr";

		String timestampHash = Hasher.hash(timestamp);
		String secretHash = Hasher.hash(secret);

		assertEquals("ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f", timestampHash);
		assertEquals("cf861b63393fa93155a8ae11eee8a6b5548f0d58e047cdc24a195600a0c008e6", secretHash);

		String combinedHash = Hasher.hash(timestampHash + "-" + secretHash);

		assertEquals("4160991a3438230e2af6214feeae5d1299f348f8acf80da81efce42718bf29ed", combinedHash);

	}
}
