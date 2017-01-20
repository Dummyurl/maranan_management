package com.marananmanagement.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;

import com.marananmanagement.BugReportActivity;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public UncaughtExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);

		Intent intent = new Intent(
			"android.fbreader.action.CRASH",
			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		try {
			myContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			intent = new Intent(myContext, BugReportActivity.class);
			intent.putExtra(BugReportActivity.STACKTRACE, stackTrace.toString());
			myContext.startActivity(intent);
		}

		if (myContext instanceof Activity) {
			((Activity)myContext).finish();
		}

		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
