package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class TelActivity extends Activity implements OnClickListener {
	private static TelActivity mContext;
	private SwipeListView lv_tel;

	// TelActivity Instance
	public static TelActivity getInstance() {
		return mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		// Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
		setContentView(R.layout.tel_activity);
		mContext = TelActivity.this;
		initializeView();
	}

	private void initializeView() {
		lv_tel = (SwipeListView) findViewById(R.id.lv_tel);
		lv_tel.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		lv_tel.setOffsetRight(Utilities.convertDpToPixel(this, 80f));
		lv_tel.setAnimationTime(500);
		lv_tel.setSwipeOpenOnLongPress(false);

		lv_tel.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {

			}

			@Override
			public void onClosed(int position, boolean fromRight) {

			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {

			}

			@Override
			public void onStartClose(int position, boolean right) {

			}

			@Override
			public void onClickFrontView(int position) {
				lv_tel.closeAnimate(position);
				

			}

			@Override
			public void onClickBackView(int position) {
				lv_tel.closeAnimate(position);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {

			}

		});
		
	}

	@Override
	public void onClick(View v) {
		

	}

	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}

}
