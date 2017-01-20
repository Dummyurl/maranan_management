package com.marananmanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.marananmanagement.adapter.AlertEditAutoScrollAdapter;
import com.marananmanagement.database.MarananDB;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.infiniteviewpager.OnPageChangeListenerForInfiniteIndicator;
import com.marananmanagement.interfaces.Constant;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class AlertEditPage extends Activity implements OnClickListener, OnCheckedChangeListener, OnCompletionListener, OnPreparedListener, Constant {
	private static AlertEditPage mContext;
	private TextView tv_time_alert;
	private TextView tv_dateTimeCurrent;
	private EditText edt_title_alert, edt_des_alert;
	private ImageView img_cam_alert, img_record_title, img_record_dess;
	private LinearLayout ll_edit_page_btn;
	private Button btn_saturday, btn_friday, btn_thursday, btn_wednesday, btn_tuesday, btn_Monday, btn_Sunday;
	private ToggleButton toggle_btn;
	private Spinner spinner_time_Hr, spinner_time_min;
	private ProgressBar seek_bar_radio, progress_loading;
	private Button btn_wifi, btn_disc, btn_upload, btn_play_white, btn_phone;
	public boolean check;
	private Boolean isUploading = false;
	private Boolean isComingIntent = false;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private String title, sun, mon, tues, wed, thur, fri, sat, mode, state, phone;
	private String imgDecodingString;
	private String[] array_min, array_Hr;
	private MarananDB db;
	private int pos_TimeHr;
	private int pos_TimeMin;
	private String srcPath = null;
	private Uri audioFileUri = null;
	private String[] STAR = { "*" };
	private MediaPlayer mediaPlayer;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
	private final int REQ_CODE_SPEECH_INPUT_TWO = 200;
	private static int RESULT_LOAD_IMG = 300;
	private static int RQS_OPEN_AUDIO_MP3 = 400;

	enum MP_State {
		Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted, End, Error, Preparing
	}

	MP_State mediaPlayerState;

	private ViewPager pager_infinite;
	private AlertEditAutoScrollAdapter adapter_auto;
	private RelativeLayout InfiniteScrollLayout;
	private RelativeLayout relative_dateTimeCurrent;
	private int size;
	private int imageCount = 0;
	private ArrayList<String>  listImages = new ArrayList<String>();

	// AlertEditPage Instance
	public static AlertEditPage getInstance() {
		return mContext;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		// Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
		setContentView(R.layout.activity_edit_alert_page);
		mContext = AlertEditPage.this;
		db = new MarananDB(this);
		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();
		initializeView();
	}

	// initialize UI view
	private void initializeView() {
		tv_time_alert = (TextView) findViewById(R.id.tv_time_alert);
		Utilities.startTime(this, tv_time_alert);
		tv_time_alert.setVisibility(View.GONE);
		edt_title_alert = (EditText) findViewById(R.id.edt_title_alert);
		edt_des_alert = (EditText) findViewById(R.id.edt_des_alert);
		img_record_title = (ImageView) findViewById(R.id.img_record_title);
		img_record_title.setOnClickListener(this);
		img_record_dess = (ImageView) findViewById(R.id.img_record_dess);
		img_record_dess.setOnClickListener(this);
		img_cam_alert = (ImageView) findViewById(R.id.img_cam_alert);
		img_cam_alert.setOnClickListener(this);
		img_cam_alert.setVisibility(View.GONE);
		ll_edit_page_btn = (LinearLayout) findViewById(R.id.ll_edit_page_btn);

		relative_dateTimeCurrent = (RelativeLayout) findViewById(R.id.relative_dateTimeCurrent);
		relative_dateTimeCurrent.setVisibility(View.VISIBLE);
		tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
		tv_dateTimeCurrent.setText(Utilities.getCurrentDate() + " | "
				+ Utilities.getIsraelTime());

		// Set Button Ui For Weekdays...
		btn_Sunday = (Button) findViewById(R.id.btn_Sunday);
		btn_Sunday.setTag(R.drawable.bg_hollow);
		btn_Sunday.setOnClickListener(this);
		btn_Monday = (Button) findViewById(R.id.btn_Monday);
		btn_Monday.setTag(R.drawable.bg_hollow);
		btn_Monday.setOnClickListener(this);
		btn_tuesday = (Button) findViewById(R.id.btn_tuesday);
		btn_tuesday.setTag(R.drawable.bg_hollow);
		btn_tuesday.setOnClickListener(this);
		btn_wednesday = (Button) findViewById(R.id.btn_wednesday);
		btn_wednesday.setTag(R.drawable.bg_hollow);
		btn_wednesday.setOnClickListener(this);
		btn_thursday = (Button) findViewById(R.id.btn_thursday);
		btn_thursday.setTag(R.drawable.bg_hollow);
		btn_thursday.setOnClickListener(this);
		btn_friday = (Button) findViewById(R.id.btn_friday);
		btn_friday.setTag(R.drawable.bg_hollow);
		btn_friday.setOnClickListener(this);
		btn_saturday = (Button) findViewById(R.id.btn_saturday);
		btn_saturday.setTag(R.drawable.bg_hollow);
		btn_saturday.setOnClickListener(this);

		// Set Normal Button Ui
		btn_wifi = (Button) findViewById(R.id.btn_wifi);
		btn_wifi.setOnClickListener(this);
		btn_disc = (Button) findViewById(R.id.btn_disc);
		btn_disc.setOnClickListener(this);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		btn_upload.setOnClickListener(this);
		btn_play_white = (Button) findViewById(R.id.btn_play_white);
		btn_play_white.setOnClickListener(this);
		btn_play_white.setTag(R.drawable.play_white);
		btn_phone = (Button) findViewById(R.id.btn_phone);
		btn_phone.setOnClickListener(this);
		pager_infinite = (ViewPager) findViewById(R.id.pager_auto);
		InfiniteScrollLayout=(RelativeLayout)findViewById(R.id.InfiniteScrollLayout);
		InfiniteScrollLayout.setVisibility(View.VISIBLE);
		//pager_infinite.startAutoScroll();
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
		seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
		toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
		toggle_btn.setOnCheckedChangeListener(this);
		toggle_btn.setChecked(false);
		check = false;
		spinner_time_Hr = (Spinner) findViewById(R.id.spinner_time_Hr);
		spinner_time_min = (Spinner) findViewById(R.id.spinner_time_min);
		spinner_time_Hr.setVisibility(View.INVISIBLE);
		spinner_time_min.setVisibility(View.INVISIBLE);
		ll_edit_page_btn.setVisibility(View.INVISIBLE);
		setValuesOnSpinners();
		setValuesFromDataBase();

		// Get Phone Number Form Intent...
		if(getIntent().getStringExtra("phone") != null) {
			phone = getIntent().getStringExtra("phone");
		}else{
			phone = "";
		}

		listImages = (ArrayList<String>) getIntent().getSerializableExtra("image");

		if(listImages != null && listImages.size() > 0){
			imageCount = listImages.size();
		}
		setViewpagerAdapter(listImages);
	}

	/* Set View Pager Adapter */
	public void setViewpagerAdapter(ArrayList<String> listImages) {
		this.listImages =  listImages;
		adapter_auto = new AlertEditAutoScrollAdapter(this, listImages, getIntent().getStringExtra("id"), pager_infinite);
		pager_infinite.setAdapter(adapter_auto);

		if (listImages != null && listImages.size() > 0) {
			size = listImages.size()+1;
			pager_infinite.setOnPageChangeListener(new OnPageChangeListenerForInfiniteIndicator(this, pager_infinite.getCurrentItem(), size));
		}
	}

	// Set Values From DataBase...
	private void setValuesFromDataBase() {
		if (Utilities.doesDatabaseExist(AlertEditPage.this, "MarananManagement")) {

			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				GetterSetter getset = new GetterSetter();
				getset = db.getAlertValues(getIntent().getStringExtra("id"));

				if(getset.getAlertMessages().contains("*")){

					edt_title_alert.setText(Utilities.decodeImoString(getset.getAlertMessages().replace("*", "'").trim()));

				}else{

					edt_title_alert.setText(Utilities.decodeImoString(getset.getAlertMessages()));
				}

				edt_des_alert.setText(Utilities.decodeImoString(getIntent().getStringExtra("description")));

				if (getIntent().getStringExtra("audio_alert") != null) {
					isComingIntent = true;
				}

				if (getset.getMode().equals("Manual")) {
					toggle_btn.setChecked(false);
					check = false;
					btn_disc.setVisibility(View.VISIBLE);
					spinner_time_Hr.setVisibility(View.INVISIBLE);
					spinner_time_min.setVisibility(View.INVISIBLE);
					ll_edit_page_btn.setVisibility(View.INVISIBLE);

				} else if (getset.getMode().equals("Automatic")) {
					toggle_btn.setChecked(true);
					check = true;
					btn_disc.setVisibility(View.INVISIBLE);
					spinner_time_Hr.setVisibility(View.VISIBLE);
					for (int i = 0; i < array_Hr.length; i++) {
						if (getset.getTime_hr().equals(array_Hr[i])) {
							pos_TimeHr = i;
						}
					}

					for (int i = 0; i < array_min.length; i++) {
						if (getset.getTime_min().equals(array_min[i])) {
							pos_TimeMin = i;
						}
					}
					spinner_time_Hr.setSelection(Integer.valueOf(pos_TimeHr));
					spinner_time_min.setVisibility(View.VISIBLE);
					spinner_time_min.setSelection(Integer.valueOf(pos_TimeMin));
					ll_edit_page_btn.setVisibility(View.VISIBLE);

					if (getset.getSun().equals("Sun")) {
						btn_Sunday.setBackgroundResource(R.drawable.bg_solid);
						btn_Sunday.setTag(R.drawable.bg_solid);
						btn_Sunday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_Sunday.setBackgroundResource(R.drawable.bg_hollow);
						btn_Sunday.setTag(R.drawable.bg_hollow);
						btn_Sunday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					}

					if (getset.getMon().equals("Mon")) {
						btn_Monday.setBackgroundResource(R.drawable.bg_solid);
						btn_Monday.setTag(R.drawable.bg_solid);
						btn_Monday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_Monday.setBackgroundResource(R.drawable.bg_hollow);
						btn_Monday.setTag(R.drawable.bg_hollow);
						btn_Monday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getTues().equals("Tue")) {
						btn_tuesday.setBackgroundResource(R.drawable.bg_solid);
						btn_tuesday.setTag(R.drawable.bg_solid);
						btn_tuesday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_tuesday.setBackgroundResource(R.drawable.bg_hollow);
						btn_tuesday.setTag(R.drawable.bg_hollow);
						btn_tuesday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getWed().equals("Wed")) {
						btn_wednesday.setBackgroundResource(R.drawable.bg_solid);
						btn_wednesday.setTag(R.drawable.bg_solid);
						btn_wednesday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_wednesday
								.setBackgroundResource(R.drawable.bg_hollow);
						btn_wednesday.setTag(R.drawable.bg_hollow);
						btn_wednesday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getThur().equals("Thu")) {
						btn_thursday.setBackgroundResource(R.drawable.bg_solid);
						btn_thursday.setTag(R.drawable.bg_solid);
						btn_thursday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_thursday.setBackgroundResource(R.drawable.bg_hollow);
						btn_thursday.setTag(R.drawable.bg_hollow);
						btn_thursday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getFri().equals("Fri")) {
						btn_friday.setBackgroundResource(R.drawable.bg_solid);
						btn_friday.setTag(R.drawable.bg_solid);
						btn_friday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_friday.setBackgroundResource(R.drawable.bg_hollow);
						btn_friday.setTag(R.drawable.bg_hollow);
						btn_friday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getSat().equals("Sat")) {
						btn_saturday.setBackgroundResource(R.drawable.bg_solid);
						btn_saturday.setTag(R.drawable.bg_solid);
						btn_saturday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_saturday.setBackgroundResource(R.drawable.bg_hollow);
						btn_saturday.setTag(R.drawable.bg_hollow);
						btn_saturday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}
				}

			} else {

				edt_title_alert.setText("");
				edt_des_alert.setText("");
			}

		}


	}

	// OnClick Listener Initialize
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_cam_alert:
				if (isInternetPresent) {
					if (isUploading == false) {
						if (mediaPlayer != null)
							if (mediaPlayer.isPlaying()) {
								btn_play_white.setTag(R.drawable.play_white);
								btn_play_white.setBackgroundResource(R.drawable.play_white);
								cmdStop();
							}else{
								btn_play_white.setTag(R.drawable.play_white);
								btn_play_white.setBackgroundResource(R.drawable.play_white);
							}
						Intent galleryIntent = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

					}else {
						Utilities.showToast(AlertEditPage.this,
								"Uploading...Please wait");
					}
				}else{
					Utilities.showAlertDialog(
							AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
				break;

			case R.id.btn_wifi:
				if (isInternetPresent) {
					if (isUploading == false) {

						if (check == true) {
							mode = "Automatic";
							state = "Save_And_Publish";

							if (edt_title_alert.length() == 0) {
								Utilities.showToast(this, "Please enter alert");

							} else {

								if (Integer.parseInt(btn_saturday.getTag().toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_Sunday.getTag()
										.toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_Monday.getTag()
										.toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_friday.getTag()
										.toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_tuesday.getTag()
										.toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_wednesday.getTag()
										.toString()) != R.drawable.bg_hollow
										|| Integer.parseInt(btn_thursday.getTag()
										.toString()) != R.drawable.bg_hollow) {

									if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
										sun = "Sun";
									} else {
										sun = "";
									}

									if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
										mon = "Mon";
									} else {
										mon = "";
									}

									if (Integer.parseInt(btn_tuesday.getTag()
											.toString()) == R.drawable.bg_solid) {
										tues = "Tue";
									} else {
										tues = "";
									}

									if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_solid) {
										wed = "Wed";
									} else {
										wed = "";
									}

									if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_solid) {
										thur = "Thu";
									} else {
										thur = "";
									}

									if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
										fri = "Fri";
									} else {
										fri = "";
									}

									if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_solid) {
										sat = "Sat";
									} else {
										sat = "";
									}
								
								   /* Send Alert Automatic */
									AsyncRequest.sendAlert(this, this.getClass().getSimpleName(),
											getIntent().getStringExtra("id"),
											Utilities.encodeImoString(edt_title_alert.getText().toString()),
											Utilities.encodeImoString(edt_des_alert.getText().toString()),
											spinner_time_Hr.getSelectedItem().toString(),
											spinner_time_min.getSelectedItem().toString(),
											listImages, srcPath, mode, state, phone, sun, mon, tues, wed, thur, fri, sat, seek_bar_radio, imageCount);

								} else {
									Utilities.showToast(AlertEditPage.this,
											"Select Days");
								}
							}

						}else{
							mode = "Manual";
							state = "Save_And_Publish";

							if (edt_title_alert.length() != 0) {
							
							   /* Send Alert Manual */
								AsyncRequest.sendAlert(this, this.getClass().getSimpleName(),getIntent().getStringExtra("id"),
										Utilities.encodeImoString(edt_title_alert.getText().toString()),
										Utilities.encodeImoString(edt_des_alert.getText().toString()),
										"",
										"",
										listImages, srcPath, mode, state, phone, "", "", "", "", "", "", "", seek_bar_radio, imageCount);

							} else {
								Utilities.showToast(this, "Please enter alert");

							}
						}

					}else {
						Utilities.showToast(AlertEditPage.this,
								"Uploading...Please wait");
					}
				}else {
					Utilities.showAlertDialog(
							AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
				break;

			case R.id.btn_disc:
				if (isInternetPresent) {
					if (isUploading == false) {

						mode = "Manual";
						state = "Save";

						if (edt_title_alert.length() != 0) {
						
						   /* Send Alert Save */
							AsyncRequest.sendAlert(this, this.getClass().getSimpleName(), getIntent().getStringExtra("id"),
									Utilities.encodeImoString(edt_title_alert.getText().toString()),
									Utilities.encodeImoString(edt_des_alert.getText().toString()),
									"",
									"",
									listImages, srcPath, mode, state, phone, "", "", "", "", "", "", "", seek_bar_radio, imageCount);

						} else {
							Utilities.showToast(this, "Please enter alert");
						}

					}else {
						Utilities.showToast(AlertEditPage.this,
								"Uploading...Please wait");
					}
				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}
				break;

			case R.id.btn_upload:
				if (isInternetPresent) {
					if (isUploading == false) {

						if (mediaPlayer != null)
							if (mediaPlayer.isPlaying()) {
								btn_play_white.setTag(R.drawable.play_white);
								btn_play_white.setBackgroundResource(R.drawable.play_white);
								cmdStop();
							}else{
								btn_play_white.setTag(R.drawable.play_white);
								btn_play_white.setBackgroundResource(R.drawable.play_white);
							}

						// Create Intent to choose audio files from sdcard using media store external content uri...
						Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(Intent.createChooser(galleryIntent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);

					}else {
						Utilities.showToast(AlertEditPage.this,
								"Uploading...Please wait");
					}
				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}

				break;

			case R.id.btn_play_white:
				if (isInternetPresent) {
					if (Integer.parseInt(btn_play_white.getTag().toString()) == R.drawable.play_white) {

						if (isComingIntent) {
							if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
								btn_play_white.setTag(R.drawable.pause_white_new);
								btn_play_white.setBackgroundResource(R.drawable.pause_white_new);
								mediaPlayer.start();

							}else{
								if (getIntent().getStringExtra("audio_alert") != null) {
									if (!getIntent().getStringExtra("audio_alert").equals("")) {
										progress_loading.setVisibility(View.VISIBLE);
										btn_play_white.setVisibility(View.GONE);
										startFromUrl(getIntent().getStringExtra("audio_alert"));

									}else{

										Utilities.showToast(this, "No file selected");

									}

								}else{

									Utilities.showToast(this, "No file selected");

								}
							}

						} else {
							if (srcPath == null) {

								Utilities.showToast(this, "No file selected");

							} else {
								btn_play_white.setTag(R.drawable.pause_white_new);
								btn_play_white.setBackgroundResource(R.drawable.pause_white_new);
								cmdPrepare();
								cmdStart();
							}
						}

					} else if (Integer.parseInt(btn_play_white.getTag().toString()) == R.drawable.pause_white_new) {
						if (isComingIntent) {
							btn_play_white.setTag(R.drawable.play_white);
							btn_play_white.setBackgroundResource(R.drawable.play_white);
							mediaPlayer.pause();

						}else{
							if (mediaPlayer.isPlaying()) {
								btn_play_white.setTag(R.drawable.play_white);
								btn_play_white.setBackgroundResource(R.drawable.play_white);
								cmdPause();
							} else {
								cmdPause();
							}
						}
					}

				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}
				break;

			case R.id.btn_phone:
				if (isInternetPresent) {
					showCustomDialog();
				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}
				break;


			case R.id.img_record_title:
				if (isInternetPresent) {
					promptSpeechInput("title");
				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}

				break;

			case R.id.img_record_dess:
				if (isInternetPresent) {
					promptSpeechInput("description");
				} else {
					Utilities.showAlertDialog(AlertEditPage.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}

				break;

			case R.id.btn_Sunday:
				if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_Sunday.setBackgroundResource(R.drawable.bg_solid);
					btn_Sunday.setTag(R.drawable.bg_solid);
					btn_Sunday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateSun(getIntent().getStringExtra("id"), "Sun");
					}

				} else if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
					btn_Sunday.setBackgroundResource(R.drawable.bg_hollow);
					btn_Sunday.setTag(R.drawable.bg_hollow);
					btn_Sunday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateSun(getIntent().getStringExtra("id"), "");
					}

				}
				break;

			case R.id.btn_Monday:
				if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_Monday.setBackgroundResource(R.drawable.bg_solid);
					btn_Monday.setTag(R.drawable.bg_solid);
					btn_Monday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateMon(getIntent().getStringExtra("id"), "Mon");
					}

				} else if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
					btn_Monday.setBackgroundResource(R.drawable.bg_hollow);
					btn_Monday.setTag(R.drawable.bg_hollow);
					btn_Monday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateMon(getIntent().getStringExtra("id"), "");
					}
				}
				break;

			case R.id.btn_tuesday:
				if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_tuesday.setBackgroundResource(R.drawable.bg_solid);
					btn_tuesday.setTag(R.drawable.bg_solid);
					btn_tuesday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateTues(getIntent().getStringExtra("id"),
								"Tue");
					}

				} else if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_solid) {
					btn_tuesday.setBackgroundResource(R.drawable.bg_hollow);
					btn_tuesday.setTag(R.drawable.bg_hollow);
					btn_tuesday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateTues(getIntent().getStringExtra("id"), "");
					}

				}
				break;

			case R.id.btn_wednesday:
				if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_wednesday.setBackgroundResource(R.drawable.bg_solid);
					btn_wednesday.setTag(R.drawable.bg_solid);
					btn_wednesday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateWed(getIntent().getStringExtra("id"), "Wed");
					}

				} else if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_solid) {
					btn_wednesday.setBackgroundResource(R.drawable.bg_hollow);
					btn_wednesday.setTag(R.drawable.bg_hollow);
					btn_wednesday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateWed(getIntent().getStringExtra("id"), "");
					}
				}
				break;

			case R.id.btn_thursday:
				if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_thursday.setBackgroundResource(R.drawable.bg_solid);
					btn_thursday.setTag(R.drawable.bg_solid);
					btn_thursday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateThur(getIntent().getStringExtra("id"),"Thu");
					}

				} else if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_solid) {
					btn_thursday.setBackgroundResource(R.drawable.bg_hollow);
					btn_thursday.setTag(R.drawable.bg_hollow);
					btn_thursday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateThur(getIntent().getStringExtra("id"), "");
					}

				}
				break;

			case R.id.btn_friday:
				if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_friday.setBackgroundResource(R.drawable.bg_solid);
					btn_friday.setTag(R.drawable.bg_solid);
					btn_friday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateFri(getIntent().getStringExtra("id"), "Fri");
					}

				} else if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
					btn_friday.setBackgroundResource(R.drawable.bg_hollow);
					btn_friday.setTag(R.drawable.bg_hollow);
					btn_friday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateFri(getIntent().getStringExtra("id"), "");
					}

				}
				break;

			case R.id.btn_saturday:
				if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_hollow) {
					btn_saturday.setBackgroundResource(R.drawable.bg_solid);
					btn_saturday.setTag(R.drawable.bg_solid);
					btn_saturday.setTextColor(getResources().getColor(R.color.white));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateSat(getIntent().getStringExtra("id"), "Sat");
					}

				} else if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_solid) {
					btn_saturday.setBackgroundResource(R.drawable.bg_hollow);
					btn_saturday.setTag(R.drawable.bg_hollow);
					btn_saturday.setTextColor(getResources().getColor(R.color.DeepSkyBlue));
					if (db.isIdExist(getIntent().getStringExtra("id"))) {
						db.updateSat(getIntent().getStringExtra("id"), "");
					}
				}
				break;

			default:
				break;
		}
	}

	// onActivityResult Call...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG && null != data) {
				isUploading = false;
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodingString = cursor.getString(columnIndex);
				cursor.close();

				imgDecodingString = Utilities.compressImage(this, imgDecodingString);

				img_cam_alert.setImageBitmap(BitmapFactory.decodeFile(imgDecodingString));

			}else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_title_alert.setText(result.get(0));


			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_TWO) {

				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_des_alert.setText(result.get(0));

			}else if (resultCode == RESULT_OK && requestCode == RQS_OPEN_AUDIO_MP3 && null != data) {

				audioFileUri = data.getData();
				srcPath = Utilities.getPathNew(audioFileUri, AlertEditPage.this);

				if (!srcPath.equals("null")) {
					if (Utilities.getExtension(srcPath).equals("mp3")) {
						isComingIntent = false;
						cmdReset();
						cmdSetDataSource(srcPath);

						// Get Audio Title From SdCard and set on edit text...
						if (Utilities.isSdPresent()) {
							Cursor cursor;
							String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
							cursor = getContentResolver().query(audioFileUri, STAR, selection, null, null);

							if (cursor != null) {
								if (cursor.moveToFirst()) {
									do {
										title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
									} while (cursor.moveToNext());
								}
								cursor.close();
							}
						}

						// Get Audio Title From SdCard and set on edit text...
						//MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
						//mediaMetadataRetriever.setDataSource(RadioProgramUpload.this, audioFileUri);
						//title = (String) mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
						if (edt_title_alert.getText().toString().equals("")) {
							if (title != null) {
								edt_title_alert.setText(title);
							}
						}

					}else{
						srcPath = null;
						Utilities.showToast(this, "Invalid Format Extension");

					}
				} else {
					srcPath = null;
					Utilities.showToast(this, "Something went wrong");

				}

			}

			if (resultCode == RESULT_OK && null != data && requestCode == REQ_CODE_LIVE_IMAGES) {

				if (data.getStringExtra("imagepaths") != null && !data.getStringExtra("imagepaths").equals("")) {

					ArrayList<String> listImage = AlertEditAutoScrollAdapter.getInstance().getImageList();

					if (listImage != null) {
						if (!listImage.contains(data.getStringExtra("imagepaths"))) {
							listImage.add(data.getStringExtra("imagepaths"));
							setViewpagerAdapter(listImage);
						} else {
							Utilities.showToast(this, "Already Added In the Queue");
						}
					} else {
						listImage = new ArrayList<String>();
						listImage.add(data.getStringExtra("imagepaths"));
						setViewpagerAdapter(listImage);
					}

				}

			}

			if (resultCode == RESULT_OK && null != data && requestCode == REQ_CODE_YOUTUBE) {

				if(data.getStringExtra("imagepaths")!=null && !data.getStringExtra("imagepaths").equals("")){
					ArrayList<String> listImage = AlertEditAutoScrollAdapter.getInstance().getImageList();

					if (listImage != null) {
						if (!listImage.contains(data.getStringExtra("imagepaths"))) {
							listImage.add(data.getStringExtra("imagepaths"));
							setViewpagerAdapter(listImage);
						} else {
							Utilities.showToast(this, "Already Added In the Queue");
						}
					} else {
						listImage = new ArrayList<String>();
						listImage.add(data.getStringExtra("imagepaths"));
						setViewpagerAdapter(listImage);
					}
				}


			}
		} catch (Exception e) {
			srcPath = null;
			Utilities.showToast(this, "Something went wrong");

		}
	}

	// Set Spinner Values And Adapters
	private void setValuesOnSpinners() {
		array_Hr = getResources().getStringArray(R.array.array_Hr);
		ArrayAdapter<String> arrayAdapter_Hr = new ArrayAdapter<String>(
				AlertEditPage.this, R.layout.spinner_background, array_Hr);
		arrayAdapter_Hr.setDropDownViewResource(R.layout.spinner_item_background);
		spinner_time_Hr.setAdapter(arrayAdapter_Hr);

		array_min = getResources().getStringArray(R.array.array_min);
		ArrayAdapter<String> arrayAdapter_min = new ArrayAdapter<String>(
				AlertEditPage.this, R.layout.spinner_background, array_min);
		arrayAdapter_min.setDropDownViewResource(R.layout.spinner_item_background);
		spinner_time_min.setAdapter(arrayAdapter_min);

		spinner_time_Hr.setOnItemSelectedListener(new CustomOnItemSelectedListenerHR());
		spinner_time_min.setOnItemSelectedListener(new CustomOnItemSelectedListenerMin());

	}

	// Set Spinner Items Selected Listener For Hour
	public class CustomOnItemSelectedListenerHR implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
								   long id) {

			db.updatetime_hr(getIntent().getStringExtra("id"), parent
					.getItemAtPosition(pos).toString());

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	// Set Spinner Items Selected Listener For Minutes
	public class CustomOnItemSelectedListenerMin implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
								   long id) {

			db.updatetime_min(getIntent().getStringExtra("id"), parent
					.getItemAtPosition(pos).toString());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	// Set Toggle Button Check Chamge Listener...
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			check = isChecked;
			btn_disc.setVisibility(View.INVISIBLE);
			spinner_time_Hr.setVisibility(View.VISIBLE);
			spinner_time_min.setVisibility(View.VISIBLE);
			ll_edit_page_btn.setVisibility(View.VISIBLE);

			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				db.updateMode(getIntent().getStringExtra("id"), "Automatic");
			}

		} else {
			check = false;
			btn_disc.setVisibility(View.VISIBLE);
			spinner_time_Hr.setVisibility(View.INVISIBLE);
			spinner_time_min.setVisibility(View.INVISIBLE);
			ll_edit_page_btn.setVisibility(View.INVISIBLE);

			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				db.updateMode(getIntent().getStringExtra("id"), "Manual");
			}

		}

	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput(String values) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		if (values.equals("title")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ONE);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));
			}
		} else if (values.equals("description")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_TWO);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));

			}
		}

	}

	// Show Custom dialog to set phone number...
	private void showCustomDialog() {
		final Dialog dialog = new Dialog(AlertEditPage.this);
		dialog.setContentView(R.layout.custom_dialog_message);
		dialog.setTitle("Enter Number...");

		final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
		edt_message.setVisibility(View.GONE);
		final EditText edt_number = (EditText) dialog.findViewById(R.id.edt_number);
		edt_number.setVisibility(View.VISIBLE);
		Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
		btn_send.setVisibility(View.GONE);
		Button btn_save = (Button) dialog.findViewById(R.id.btn_save_no);
		btn_save.setVisibility(View.VISIBLE);
		edt_number.setText(phone);


		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edt_number.length() == 0) {

					Utilities.showToast(AlertEditPage.this,
							"Please Enter Number");

				} else {

					phone = edt_number.getText().toString();
					dialog.dismiss();
				}
			}
		});
		dialog.show();

	}

	// Here is to reset media player...
	private void cmdReset() {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
		}
		mediaPlayer.reset();
		mediaPlayerState = MP_State.Idle;
	}

	OnErrorListener mediaPlayerOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mediaPlayerState = MP_State.Error;
			return false;
		}
	};

	// Here is to prepare media player...
	private void cmdPrepare() {

		if (mediaPlayerState == MP_State.Initialized
				|| mediaPlayerState == MP_State.Stopped) {
			try {
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.prepare();
				mediaPlayerState = MP_State.Prepared;
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {

		}
	}

	private void cmdSetDataSource(String path) {
		if (mediaPlayerState == MP_State.Idle) {
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayerState = MP_State.Initialized;
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {

		}
	}

	private void cmdStart() {
		if (mediaPlayerState == MP_State.Prepared
				|| mediaPlayerState == MP_State.Started
				|| mediaPlayerState == MP_State.Paused
				|| mediaPlayerState == MP_State.PlaybackCompleted) {
			mediaPlayer.start();
			mediaPlayerState = MP_State.Started;
		} else {

		}
	}

	private void cmdPause() {
		if (mediaPlayerState == MP_State.Started
				|| mediaPlayerState == MP_State.Paused) {
			mediaPlayer.pause();
			mediaPlayerState = MP_State.Paused;
		} else {

		}
	}

	private void cmdStop() {

		if (mediaPlayerState == MP_State.Prepared
				|| mediaPlayerState == MP_State.Started
				|| mediaPlayerState == MP_State.Stopped
				|| mediaPlayerState == MP_State.Paused
				|| mediaPlayerState == MP_State.PlaybackCompleted) {
			mediaPlayer.stop();
			mediaPlayerState = MP_State.Stopped;
		} else {

		}
	}

	// Here is the method to start mp3 from url...
	public void startFromUrl(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// String radioUrl = getStreamingURL(URL);
				try {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
					mediaPlayer.setDataSource(AlertEditPage.this, Uri.parse(url));
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setOnPreparedListener(AlertEditPage.this);
					mediaPlayer.setOnCompletionListener(AlertEditPage.this);
					mediaPlayer.prepareAsync();
					// mediaPlayer.start();

				} catch (IllegalArgumentException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				} catch (IllegalStateException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

			}

		}).start();
	}

	// Prepare Media player to play...
	@Override
	public void onPrepared(MediaPlayer mp) {

		if (isComingIntent) {
			progress_loading.setVisibility(View.GONE);
			btn_play_white.setVisibility(View.VISIBLE);
			btn_play_white.setTag(R.drawable.pause_white_new);
			btn_play_white.setBackgroundResource(R.drawable.pause_white_new);
		}

		if (!mp.isPlaying()) {
			mp.start();
		}
	}

	// When Media Player To complete Play
	@Override
	public void onCompletion(MediaPlayer mp) {
		btn_play_white.setTag(R.drawable.play_white);
		btn_play_white.setBackgroundResource(R.drawable.play_white);
	}

	/* is Uploading */
	public void isUploading(Boolean isUploading) {
		this.isUploading = isUploading;
	}

	/* is Uploading */
	public Boolean isGetUploading() {
		return isUploading;
	}

	/* Set Alert Page Request */
	public void setRequestResult(String result) {
		if (isComingIntent) {
			imgDecodingString = "";
			audioFileUri = null;
			srcPath = null;
			seek_bar_radio.setProgress(0);
			isUploading = false;
		}else{
			img_cam_alert.setImageResource(R.drawable.cam_icon);
			imgDecodingString = "";
			audioFileUri = null;
			srcPath = null;
			seek_bar_radio.setProgress(0);
			isUploading = false;
			Utilities.deleteCompressImageFiles();
		}

		if (result != null) {
			try {
				JSONObject jsonObj = new JSONObject(result);

				Log.e("success","success??"+jsonObj.get("success"));

				if (jsonObj.get("success").equals(true)) {

					Utilities.showToastNew(this, jsonObj.get("value").toString());
					Intent mintent = new Intent();
					setResult(RESULT_OK, mintent);
					finish();
					overridePendingTransition(R.anim.activity_open_scale,
							R.anim.activity_close_translate);

				} else if (jsonObj.get("success").equals(false)) {
					Utilities.showToastNew(this, jsonObj.get("value").toString());
					Intent mintent = new Intent();
					setResult(RESULT_OK, mintent);
					finish();
					overridePendingTransition(R.anim.activity_open_scale,
							R.anim.activity_close_translate);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// onBackPress Call...
	@Override
	public void onBackPressed() {
		if (isUploading) {
			Utilities.showToast(this, "Uploading...Please wait");
		} else {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
			}
			String message  = "";
			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				if(edt_title_alert.getText().toString().contains("'")){
					message = edt_title_alert.getText().toString().replace("'", "*").trim();
				}
				db.updateMessage(getIntent().getStringExtra("id"), message);
			}
			Intent mintent = new Intent();
			setResult(RESULT_OK, mintent);
			finish();
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
			super.onBackPressed();
		}
	}

	/* Get Image Count */
	public int getImageCount() {
		return imageCount;
	}

	/* Set Image Count */
	public void setImageCount(int imageCount) {
		this.imageCount= imageCount;
	}

	/* Set delete Image Response */
	public void setDeleteImageResponse(JSONObject result) {
		if (result != null) {
			try {
				if (result.get("success").equals(true)) {
					Log.e("Success", "Success???"+result.get("success"));
				}else{
					Log.e("Success", "Success???"+result.get("success"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
