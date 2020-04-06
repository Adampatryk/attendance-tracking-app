package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import dagger.android.support.DaggerAppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = "MainActivity";
    @Inject
    String someString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: " + someString);
    }
}
