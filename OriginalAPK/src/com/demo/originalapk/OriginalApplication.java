package com.demo.originalapk;

import android.app.Application;
import android.util.Log;

public class OriginalApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("demo", "OriginalApplication's onCreate() invoked: " + this);
	}
}
