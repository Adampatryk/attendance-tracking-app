package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import gr.net.maroulis.library.EasySplashScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
				.withFullScreen()
				.withTargetActivity(MainActivity.class)
				.withSplashTimeOut(3000)
				.withBackgroundColor(getResources().getColor(R.color.colorPrimary))
				.withFooterText("Copyright 2020")
				.withBeforeLogoText("Attendance")
				.withLogo(R.drawable.logo);

		config.getBeforeLogoTextView().setTextColor(Color.WHITE);
		config.getBeforeLogoTextView().setTextSize(30);

		View splashScreen = config.create();
		setContentView(splashScreen);
	}
}
