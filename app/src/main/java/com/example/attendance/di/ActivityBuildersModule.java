package com.example.attendance.di;

import com.example.attendance.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;

@Module
public abstract class ActivityBuildersModule{

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

}
