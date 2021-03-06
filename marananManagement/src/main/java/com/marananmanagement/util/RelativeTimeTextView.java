package com.marananmanagement.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.marananmanagement.LiveBroadcastChannel;
import com.marananmanagement.R;
import com.marananmanagement.adapter.ChannelPlayListAdapter;
import com.marananmanagement.adapter.ChannelPlayListAdapter.UpdateVideoStatus;
import com.marananmanagement.adapter.NewsLetterListAdapter;
import com.marananmanagement.adapter.NewsLetterListAdapter.UpdateStatusNewsLetter;

/**
 * A {@code TextView} that, given a reference time, renders that time as a time
 * period relative to the current time.
 * 
 * @author Kiran Rao
 * @see (long)
 * 
 */
public class RelativeTimeTextView extends TextView {

	private long mReferenceTime;
	private String mText;
	private String mPrefix;
	private String mSuffix;
	private Handler mHandler = new Handler();
	private UpdateTimeRunnable mUpdateTimeTask;
	private boolean isUpdateTaskRunning = false;
	private Context context;
	private String playlist_id;
	private String video_id;
	private String status;
	private String activityName, textCompare;
	private boolean isRun;
	private int position;

	public RelativeTimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context, attrs);
	}

	public RelativeTimeTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.RelativeTimeTextView, 0, 0);
		try {
			mText = a
					.getString(R.styleable.RelativeTimeTextView_reference_time);
			mPrefix = a
					.getString(R.styleable.RelativeTimeTextView_relative_time_prefix);
			mSuffix = a
					.getString(R.styleable.RelativeTimeTextView_relative_time_suffix);

			mPrefix = mPrefix == null ? "" : mPrefix;
			mSuffix = mSuffix == null ? "" : mSuffix;
		} finally {
			a.recycle();
		}

		try {
			mReferenceTime = Long.valueOf(mText);

		} catch (NumberFormatException nfe) {
			/*
			 * TODO: Better exception handling
			 */
			mReferenceTime = -1L;

		}

		setReferenceTime(mReferenceTime, "", "", "", "", -1);
		setReferenceTimeNews(mReferenceTime, "", "", "", -1);
		setReferenceTimeRadio(mReferenceTime, "", "", "", -1);
		setReferenceTimeForStartTime(mReferenceTime, "", "");
	}

	/**
	 * Returns prefix
	 * 
	 * @return
	 */
	public String getPrefix() {
		return this.mPrefix;
	}

	/**
	 * String to be attached before the reference time
	 * 
	 * @param prefix
	 * 
	 *            Example: [prefix] in XX minutes
	 */
	public void setPrefix(String prefix) {
		this.mPrefix = prefix;
		if (activityName.equals("ChannelPlaylist")) {
			updateTextDisplay();

		} else if (activityName.equals("NewsLetter")) {
			updateTextDisplayForNewsLetter();

		} else if (activityName.equals("Radio")) {
			updateTextDisplayForRadio();

		} else if (activityName.equals("LiveBroadcast")) {
			updateTextDisplayForLiveBroadcast();

		}

	}

	/**
	 * Returns suffix
	 * 
	 * @return
	 */
	public String getSuffix() {
		return this.mSuffix;
	}

	/**
	 * String to be attached after the reference time
	 * 
	 * @param suffix
	 * 
	 *            Example: in XX minutes [suffix]
	 */
	public void setSuffix(String suffix) {
		this.mSuffix = suffix;
		if (activityName.equals("ChannelPlaylist")) {
			updateTextDisplay();

		} else if (activityName.equals("NewsLetter")) {
			updateTextDisplayForNewsLetter();

		} else if (activityName.equals("Radio")) {
			updateTextDisplayForRadio();

		} else if (activityName.equals("LiveBroadcast")) {
			updateTextDisplayForLiveBroadcast();
		}
	}

	/**
	 * Sets the reference time for this view. At any moment, the view will
	 * render a relative time period relative to the time set here.
	 * <p/>
	 * This value can also be set with the XML attribute {@code reference_time}
	 * 
	 * @param referenceTime
	 *            The timestamp (in milliseconds since epoch) that will be the
	 *            reference point for this view.
	 */
	public void setReferenceTime(long referenceTime, String playlist_id,
			String video_id, String status, String activityName, int position) {
		this.playlist_id = playlist_id;
		this.video_id = video_id;
		this.status = status;
		this.position = position;
		this.activityName = activityName;
		this.mReferenceTime = referenceTime;
		isRun = true;
		/*
		 * Note that this method could be called when a row in a ListView is
		 * recycled. Hence, we need to first stop any currently running
		 * schedules (for example from the recycled view.
		 */
		stopTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Instantiate a new runnable with the new reference time
		 */
		mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);

		/*
		 * Start a new schedule.
		 */
		startTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Finally, update the text display.
		 */
		updateTextDisplay();
	}

	public void setReferenceTimeNews(long referenceTime, String playlist_id,
			String status, String activityName, int position) {
		this.position = position;
		this.status = status;
		this.playlist_id = playlist_id;
		this.activityName = activityName;
		this.mReferenceTime = referenceTime;
		isRun = true;
		/*
		 * Note that this method could be called when a row in a ListView is
		 * recycled. Hence, we need to first stop any currently running
		 * schedules (for example from the recycled view.
		 */
		stopTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Instantiate a new runnable with the new reference time
		 */
		mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);

		/*
		 * Start a new schedule.
		 */
		startTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Finally, update the text display.
		 */
		updateTextDisplayForNewsLetter();

	}

	public void setReferenceTimeRadio(long referenceTime, String playlist_id,
			String status, String activityName, int position) {
		this.position = position;
		this.status = status;
		this.playlist_id = playlist_id;
		this.activityName = activityName;
		this.mReferenceTime = referenceTime;
		isRun = true;
		/*
		 * Note that this method could be called when a row in a ListView is
		 * recycled. Hence, we need to first stop any currently running
		 * schedules (for example from the recycled view.
		 */
		stopTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Instantiate a new runnable with the new reference time
		 */
		mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);

		/*
		 * Start a new schedule.
		 */
		startTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Finally, update the text display.
		 */
		updateTextDisplayForRadio();

	}

	// public void setReferenceTime(long referenceTime, boolean wallet, String
	// playlist_id, String video_id, String status) {
	//
	// this.mReferenceTime = referenceTime;
	// /*
	// * Note that this method could be called when a row in a ListView is
	// * recycled. Hence, we need to first stop any currently running
	// * schedules (for example from the recycled view.
	// */
	// stopTaskForPeriodicallyUpdatingRelativeTime();
	//
	// /*
	// * Instantiate a new runnable with the new reference time
	// */
	// mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);
	//
	// /*
	// * Start a new schedule.
	// */
	// startTaskForPeriodicallyUpdatingRelativeTime();
	//
	// /*
	// * Finally, update the text display.
	// */
	// updateTextDisplay();
	// }

	public void setReferenceTimeForStartTime(long referenceTimeStart,
			String activityName, String textCompare) {

		this.activityName = activityName;
		this.mReferenceTime = referenceTimeStart;
		this.textCompare = textCompare;
		isRun = true;
		/*
		 * Note that this method could be called when a row in a ListView is
		 * recycled. Hence, we need to first stop any currently running
		 * schedules (for example from the recycled view.
		 */
		stopTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Instantiate a new runnable with the new reference time
		 */
		mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);

		/*
		 * Start a new schedule.
		 */
		startTaskForPeriodicallyUpdatingRelativeTime();

		/*
		 * Finally, update the text display.
		 */
		updateTextDisplayForLiveBroadcast();
	}

	private void updateTextDisplay() {
		/*
		 * TODO: Validation, Better handling of negative cases
		 */
		if (this.mReferenceTime == -1L)
			return;

		setText(getRelativeTimeDisplayString().toString());
	}

	private void updateTextDisplayForNewsLetter() {
		/*
		 * TODO: Validation, Better handling of negative cases
		 */
		if (this.mReferenceTime == -1L)
			return;

		setText(getRelativeTimeDisplayStringForNewsLetter().toString());
	}

	private void updateTextDisplayForRadio() {
		/*
		 * TODO: Validation, Better handling of negative cases
		 */
		if (this.mReferenceTime == -1L)
			return;

		setText(getRelativeTimeDisplayForRadio().toString());
	}

	private void updateTextDisplayForLiveBroadcast() {
		/*
		 * TODO: Validation, Better handling of negative cases
		 */
		if (this.mReferenceTime == -1L)
			return;

		setText(getRelativeTimeDisplayForLiveBroadcast().toString());
	}

	private CharSequence getRelativeTimeDisplayString() {
		TimeAgo timeago = new TimeAgo(context);
		long timeDiff = mReferenceTime - System.currentTimeMillis();

		if (timeDiff > 0) {

			return timeago.timeAgo(mReferenceTime);

		} else {
			if (isRun) {
				isRun = false;
				ChannelPlayListAdapter.getInstance().getList().get(position)
						.setImageResource(R.drawable.tick_icon);
				ChannelPlayListAdapter.getInstance().getList().get(position)
						.setPublish("true");
				ChannelPlayListAdapter.getInstance().notifyDataSetChanged();
				new UpdateVideoStatus().execute(playlist_id, video_id, status);
			}
			return "";
		}
	}

	private CharSequence getRelativeTimeDisplayStringForNewsLetter() {
		TimeAgo timeago = new TimeAgo(context);
		long timeDiff = mReferenceTime - System.currentTimeMillis();
		if (timeDiff > 0) {
			return timeago.timeAgo(mReferenceTime);
		} else {
			if (isRun) {
				isRun = false;
				NewsLetterListAdapter.getInstance().getList().get(position)
						.setImgCheckUncheckRes(R.drawable.tick_icon);
				NewsLetterListAdapter.getInstance().getList().get(position)
						.setPublish_status("true");
				NewsLetterListAdapter.getInstance().setPublishCounts();
				NewsLetterListAdapter.getInstance().notifyDataSetChanged();
				new UpdateStatusNewsLetter().execute(playlist_id, status);
			}
			return "";
		}
	}

	private CharSequence getRelativeTimeDisplayForRadio() {
		TimeAgo timeago = new TimeAgo(context);
		long timeDiff = mReferenceTime - System.currentTimeMillis();
		if (timeDiff > 0) {
			return timeago.timeAgo(mReferenceTime);
		} else {
			if (isRun) {
				isRun = false;
				// RadioLectureAdapter.getInstance().notifyDataSetChanged();
				// new UpdateStatusRadioProgram().execute(playlist_id, status);
			}
			return "";
		}
	}

	private CharSequence getRelativeTimeDisplayForLiveBroadcast() {
		TimeAgo timeago = new TimeAgo(context);
		long timeDiff = mReferenceTime - System.currentTimeMillis();
		if (timeDiff > 0) {
			return timeago.timeAgoLiveBroadcast(mReferenceTime);
		
		} else {
			if (isRun) {
				isRun = false;
				if(textCompare.equals("startText")){
					
					LiveBroadcastChannel.getInstance().settextToEndTime();
				}else{
					
					LiveBroadcastChannel.getInstance().setVisibilityGone();
				}
			}
			return "";
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startTaskForPeriodicallyUpdatingRelativeTime();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopTaskForPeriodicallyUpdatingRelativeTime();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == GONE || visibility == INVISIBLE) {
			stopTaskForPeriodicallyUpdatingRelativeTime();
		} else {
			startTaskForPeriodicallyUpdatingRelativeTime();
		}
	}

	private void startTaskForPeriodicallyUpdatingRelativeTime() {
		mHandler.post(mUpdateTimeTask);
		isUpdateTaskRunning = true;
	}

	private void stopTaskForPeriodicallyUpdatingRelativeTime() {
		if (isUpdateTaskRunning) {
			mHandler.removeCallbacks(mUpdateTimeTask);
			isUpdateTaskRunning = false;
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.referenceTime = mReferenceTime;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState ss = (SavedState) state;
		mReferenceTime = ss.referenceTime;
		super.onRestoreInstanceState(ss.getSuperState());
	}

	public static class SavedState extends BaseSavedState {

		private long referenceTime;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeLong(referenceTime);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		private SavedState(Parcel in) {
			super(in);
			referenceTime = in.readLong();
		}
	}

	public class UpdateTimeRunnable implements Runnable {

		@SuppressWarnings("unused")
		private long mRefTime;

		UpdateTimeRunnable(long refTime) {

			this.mRefTime = refTime;
		}

		@Override
		public void run() {
			if (activityName.equals("ChannelPlaylist")) {
				// Log.e("activityName", "activityName?? "+activityName);
				updateTextDisplay();

			} else if (activityName.equals("NewsLetter")) {
				// Log.e("activityName", "activityName?? "+activityName);
				updateTextDisplayForNewsLetter();

			} else if (activityName.equals("Radio")) {
				// Log.e("activityName", "activityName?? "+activityName);
				updateTextDisplayForRadio();

			} else if (activityName.equals("LiveBroadcast")) {
				// Log.e("activityName", "activityName?? "+activityName);
				updateTextDisplayForLiveBroadcast();

			}
			mHandler.postDelayed(this, 1000);
		}

	}

}
