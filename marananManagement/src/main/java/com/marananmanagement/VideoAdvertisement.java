package com.marananmanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.VideoAdvertisementAdapter;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class VideoAdvertisement extends Activity {
	private static VideoAdvertisement mContext;
	private SwipeListView lv_video_add;
	private VideoAdvertisementAdapter adapter;
	private ArrayList<GetterSetter> list_video_add;
	private ProgressBar pDialog;

	// VideoAdvertisement Instance
	public static VideoAdvertisement getInstance() {
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
		setContentView(R.layout.video_advertisement);
		mContext = VideoAdvertisement.this;
		initializeView();
	}

	private void initializeView() {
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		lv_video_add = (SwipeListView) findViewById(R.id.lv_video_add);
		lv_video_add.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		lv_video_add.setOffsetRight(Utilities.convertDpToPixel(this, 80f));
		lv_video_add.setAnimationTime(500);
		lv_video_add.setSwipeOpenOnLongPress(false);

		lv_video_add.setSwipeListViewListener(new BaseSwipeListViewListener() {
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
				lv_video_add.closeAnimate(position);
				if (list_video_add != null && !list_video_add.get(position).getVid_Id().equals("")) {
					Intent advertisement = new Intent(mContext, VideoPlayer.class);
					advertisement.putExtra("video_id", list_video_add.get(position).getVid_Id());
					startActivity(advertisement);
				} 

			}

			@Override
			public void onClickBackView(int position) {
				lv_video_add.closeAnimate(position);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {

			}

		});
		
		/* Get Async Request Video Advertisement */
		AsyncRequest.getVideoAdvertisement(mContext, this.getClass().getSimpleName(), pDialog);

	}
	
	/* Set Adapter For Video Advertisement */
	public void setAdapter(ArrayList<GetterSetter> list_video_add) {
		if (list_video_add != null) {
			if (list_video_add.size() > 0) {
				this.list_video_add = list_video_add;
				adapter = new VideoAdvertisementAdapter(VideoAdvertisement.this, list_video_add, getIntent().getStringExtra("name_class"));
				lv_video_add.setAdapter(adapter);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		mintent.putExtra("imagepaths", "");
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}

}
