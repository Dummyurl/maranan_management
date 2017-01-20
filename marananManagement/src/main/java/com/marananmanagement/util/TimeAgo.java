package com.marananmanagement.util;

import android.content.Context;

import com.marananmanagement.R;

public class TimeAgo {

	private long mDays = 0;
	private long mHours = 0;
	private long mMinutes = 0;
	private long mSeconds = 0;
	protected Context context;

	public TimeAgo(Context context) {
		this.context = context;
	}

	public String timeAgo(long millis) {
		long timeDiff = millis - System.currentTimeMillis();
		String words = "";

		if (timeDiff > 0) {
			mSeconds = (timeDiff / 1000);
			mMinutes = mSeconds / 60;
			mSeconds = mSeconds % 60;
			mHours = mMinutes / 60;
			mMinutes = mMinutes % 60;
			mDays = mHours / 24;
			mHours = mHours % 24;
			words = context.getResources().getString(R.string.left) + " "
					+ mDays + " "
					+ context.getResources().getString(R.string.days) + " "
					+ mHours + " "
					+ context.getResources().getString(R.string.hours) + " "
					+ context.getResources().getString(R.string.and) + " "
					+ mMinutes + " "
					+ context.getResources().getString(R.string.minutes);
		}
		return words;
	}
	
	public String timeAgoLiveBroadcast(long millis) {
		long timeDiff = millis - System.currentTimeMillis();
		String words = "";

		if (timeDiff > 0) {
			mSeconds = (timeDiff / 1000);
			mMinutes = mSeconds / 60;
			mSeconds = mSeconds % 60;
			mHours = mMinutes / 60;
			mMinutes = mMinutes % 60;
			mDays = mHours / 24;
			mHours = mHours % 24;
			words = mHours+":"+mMinutes+":"+mSeconds;
		}
		return words;
	}

	@SuppressWarnings("unused")
	private String getTwoDigitNumber(long number) {
		if (number >= 0 && number < 10) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

}
