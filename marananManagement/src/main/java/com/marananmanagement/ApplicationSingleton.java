package com.marananmanagement;

import android.app.Application;
import android.media.MediaPlayer;

import com.marananmanagement.util.UncaughtExceptionHandler;


public class ApplicationSingleton extends Application {
	public MediaPlayer player;

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
	}

}
