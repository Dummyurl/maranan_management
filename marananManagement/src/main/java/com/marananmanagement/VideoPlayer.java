package com.marananmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class VideoPlayer extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener,
		YouTubePlayer.OnFullscreenListener,
		YouTubePlayer.PlaybackEventListener,
		YouTubePlayer.PlaylistEventListener, Provider,
		YouTubePlayer.PlayerStateChangeListener {
	private YouTubePlayerView playerView;
	private static final int RECOVERY_DIALOG_REQUEST = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		// Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
		setContentView(R.layout.activity_video_play);
		initializeView();
	}

	// initializeView
	private void initializeView() {
		playerView = (YouTubePlayerView) findViewById(R.id.player_view_new);
		playerView.initialize(Config.DEVELOPER_KEY, this);

	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(getString(R.string.failed),
					errorReason.toString());
			Utilities.showToast(this, errorMessage);
		}
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			/** add listeners to YouTubePlayer instance **/
			player.setPlayerStateChangeListener(this);
			player.setPlaybackEventListener(this);
			player.setOnFullscreenListener(this);
			player.setPlaylistEventListener(this);
			player.setPlayerStyle(PlayerStyle.DEFAULT);
			player.setFullscreen(true);
			player.setShowFullscreenButton(true);
			player.setManageAudioFocus(true);
			player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
					| YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
					| YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT
					| YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
					| YouTubePlayerView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| YouTubePlayerView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| YouTubePlayerView.SYSTEM_UI_FLAG_FULLSCREEN
					| YouTubePlayerView.FOCUS_AFTER_DESCENDANTS
					| YouTubePlayerView.KEEP_SCREEN_ON);
			player.loadVideo(getIntent().getStringExtra("video_id"));

		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus) {
			Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(closeDialog);
		}
	}

	@SuppressWarnings("unused")
	private Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.player_view_new);
	}

	@Override
	public void onFullscreen(boolean arg0) {
	}

	@Override
	public void onBuffering(boolean arg0) {

	}

	@Override
	public void onPaused() {
	}

	@Override
	public void onPlaying() {
	}

	@Override
	public void onSeekTo(int arg0) {
	}

	@Override
	public void onStopped() {
	}

	@Override
	public void onAdStarted() {
	}

	@Override
	public void onError(ErrorReason reason) {

	}

	@Override
	public void onLoaded(String arg0) {
	}

	@Override
	public void onLoading() {
	}

	@Override
	public void onVideoEnded() {
		finishActivity();
	}

	@Override
	public void onVideoStarted() {
	}

	@Override
	public void initialize(String arg0, OnInitializedListener arg1) {
	}

	@Override
	public void onNext() {
	}

	@Override
	public void onPlaylistEnded() {
	}

	@Override
	public void onPrevious() {
	}

	// finishActivity Here...
	private void finishActivity() {
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}
}
