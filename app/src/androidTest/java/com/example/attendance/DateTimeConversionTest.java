package com.example.attendance;

import com.example.attendance.util.DateTimeConversion;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DateTimeConversionTest {

	@Test
	public void millisToSecTest(){
		assertEquals(1L,DateTimeConversion.millisToSec(1000L));
		assertEquals(0L, DateTimeConversion.millisToSec(0L));
		assertEquals(1586622293L, DateTimeConversion.millisToSec(1586622293000L));
		assertEquals(1586622293L, DateTimeConversion.millisToSec(1586622293999L));
		assertEquals(1586622293L, DateTimeConversion.millisToSec(1586622293500L));
	}
}
