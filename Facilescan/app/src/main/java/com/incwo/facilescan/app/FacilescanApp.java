package com.incwo.facilescan.app;

import android.app.Application;
import android.content.Context;

import com.incwo.facilescan.managers.SingleApp;

public class FacilescanApp extends Application {
	private static FacilescanApp instance;
	public static Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();

		if (instance != null) {
			throw new IllegalStateException("Not a singleton");
		}
		instance = this;

		// Initialize SingleApp singleton
		SingleApp.getInstance().toString();
		appContext = getApplicationContext();		
	}

	public void FreeAll() { 
	}

	@Override
	public void onLowMemory() {
		try {
			FreeAll();
		} finally {
			super.onLowMemory();
		}
	}

	@Override
	public void onTerminate() {
		try {
			FreeAll();
		} finally {
			super.onTerminate();
		}
	}

	public static FacilescanApp getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized");
		}
		return instance;
	}
}
