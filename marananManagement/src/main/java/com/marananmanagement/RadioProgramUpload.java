package com.marananmanagement;

import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.CustomMultiPartEntity;
import com.marananmanagement.util.CustomMultiPartEntity.ProgressListener;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class RadioProgramUpload extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnCompletionListener, OnPreparedListener {
	private ImageView img_cam, img_record_title, img_record_des, img_text_change;
	private EditText edt_title_radio;
	private EditText edt_des_radio;
	private Button btn_upload, btn_play_pause;
	private ProgressBar progress_loading;
	private ToggleButton toggle_btn;
	private DatePicker date_picker;
	private TimePicker time_picker;
	private ProgressBar seek_bar_radio;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private Boolean isComingIntent = false;
	private static int RESULT_LOAD_IMG = 300;
	private static int RQS_OPEN_AUDIO_MP3 = 400;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
	private final int REQ_CODE_SPEECH_INPUT_THREE = 200;
	private RelativeLayout relative_datetimecurrent;
	private TextView tv_dateTimeCurrent;
	private int hour;
	private int minute;
	private int day;
	private int month;
	private int year;
	private String id = "";
	private String hour_str;
	private String minute_str;
	private String day_str;
	private String month_str;
	private String title;
	public String date;
	public String time;
	public String status = "true";
	private Uri audioFileUri = null;
	private MediaPlayer mediaPlayer;
	private String srcPath = null;
	private String imgDecodableString;
	private HttpEntity resEntity;
	public String response_str;
	// Progress Dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;
	private Boolean isUploading = false;
	private long totalSize = 0;
	
	enum MP_State {
		Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted, End, Error, Preparing
	}

	MP_State mediaPlayerState;

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
		setContentView(R.layout.radio_program_upload);
		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();
		initializeView();
	}



	// initializeView Here...
	private void initializeView() {
		relative_datetimecurrent = (RelativeLayout) findViewById(R.id.relative_datetimecurrent);
		relative_datetimecurrent.setVisibility(View.VISIBLE);
		tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
		tv_dateTimeCurrent.setText(Utilities.getCurrentDate()+" | "+Utilities.getIsraelTime());
		img_cam = (ImageView) findViewById(R.id.img_cam);
		img_cam.setOnClickListener(this);
		img_text_change= (ImageView) findViewById(R.id.img_text_change);
		img_text_change.setOnClickListener(this);
		img_text_change.setBackgroundResource(R.drawable.blue_move_hebrew);
		img_text_change.setTag(R.drawable.blue_move_hebrew);
		img_record_title = (ImageView) findViewById(R.id.img_record_title);
		img_record_title.setOnClickListener(this);
		img_record_des = (ImageView) findViewById(R.id.img_record_des);
		img_record_des.setOnClickListener(this);
		edt_title_radio = (EditText) findViewById(R.id.edt_title_radio);
		edt_title_radio.setText(Utilities.decodeImoString(getIntent().getStringExtra("title")));
		edt_des_radio = (EditText) findViewById(R.id.edt_des_radio);
		edt_des_radio.setText(Utilities.decodeImoString(getIntent().getStringExtra("description")));
		btn_upload = (Button) findViewById(R.id.btn_upload);
		btn_upload.setOnClickListener(this);
		btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
		btn_play_pause.setOnClickListener(this);
		btn_play_pause.setTag(R.drawable.play_arrow);
		btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
		toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
		toggle_btn.setChecked(true);
		status = "true";
		toggle_btn.setOnCheckedChangeListener(this);
		date_picker = (DatePicker) findViewById(R.id.date_picker);
		time_picker = (TimePicker) findViewById(R.id.time_picker);
		time_picker.setIs24HourView(true);
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
		seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
		seek_bar_radio.setProgress(0);

		if (getIntent().getStringExtra("status") != null) {
			isComingIntent = true;
			if (getIntent().getStringExtra("status").equals("true")) {
				toggle_btn.setChecked(true);
				status = "true";
			} else {
				toggle_btn.setChecked(false);
				status = "false";
			}
		}

		if (getIntent().getStringExtra("image") != null) {
			isComingIntent = true;
			img_cam.setBackgroundResource(0);
			Picasso.with(this)
					.load(getIntent().getStringExtra("image"))
					.placeholder(R.drawable.cam_icon)
					.error(R.drawable.cam_icon).into(img_cam);
		}

		if (getIntent().getStringExtra("date") != null) {
			isComingIntent = true;
			String insertDate = getIntent().getStringExtra("date");
			String[] items1 = insertDate.split("-");
			String y1 = items1[0];
			String m1 = items1[1];
			String d1 = items1[2];
			int y = Integer.parseInt(y1);
			int m = Integer.parseInt(m1);
			int d = Integer.parseInt(d1);

			date_picker.init(y, (m - 1), d, null);
		}

		if (getIntent().getStringExtra("time") != null) {
			isComingIntent = true;
			String insertTime = getIntent().getStringExtra("time");
			String[] items1 = insertTime.split(":");
			String h = items1[0];
			int hh = Integer.parseInt(h);
			String m = items1[1];
			int mm = Integer.parseInt(m);
			time_picker.setCurrentHour(hh);
			time_picker.setCurrentMinute(mm);
		}

		if (getIntent().getStringExtra("radio_program") != null) {
			isComingIntent = true;
		}

		if (getIntent().getStringExtra("id") != null) {
			isComingIntent = true;
			id = getIntent().getStringExtra("id");
		}

	}

	// onclick Listener...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
           // if (getIntent().getIntExtra("size", 0) < 8) {
            	if (isUploading == false) {
    				
    				if (isComingIntent) {
    					if (isInternetPresent) {
    						Utilities.showToast(RadioProgramUpload.this,
    								"Uploading...");

    						if (edt_title_radio.getText().toString().equals("")) {
    							new SendRadioPrograms().execute(id, 
    									//title,
    									//edt_des_radio.getText().toString(),
    									Utilities.encodeImoString(title),
    									Utilities.encodeImoString(edt_des_radio.getText().toString()),
    									imgDecodableString, srcPath,
    									getTimeFromTimePicker(),
    									getDateFromDatePicker(), status);
    						} else {
    							new SendRadioPrograms().execute(id, 
    									 //edt_title_radio.getText().toString(), 
    									 //edt_des_radio.getText().toString(),
    									 Utilities.encodeImoString(edt_title_radio.getText().toString()),
     									 Utilities.encodeImoString(edt_des_radio.getText().toString()),
    									 imgDecodableString,
    									srcPath, getTimeFromTimePicker(),
    									getDateFromDatePicker(), status);
    						}

    					} else {
    						Utilities.showAlertDialog(
    										RadioProgramUpload.this,
    										"Internet Connection Error",
    										"Please connect to working Internet connection",
    										false);
    					}
    				
    				}else if(srcPath == null) {
    					if (mediaPlayer != null) 
    						if (mediaPlayer.isPlaying()) {
    							btn_play_pause.setTag(R.drawable.play_arrow);
    							btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
    							cmdStop();
    						}else{
    							btn_play_pause.setTag(R.drawable.play_arrow);
    							btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
    						}
    					
    					// Create Intent to choose audio files from sdcard using media store external content uri...
//   					Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//  					startActivityForResult(Intent.createChooser(galleryIntent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
    					
    					// Create Intent to choose audio files from music player or file manager...
//    					Intent intent = new Intent();
//    					intent.setType("audio/mp3");
//    					intent.setAction(Intent.ACTION_GET_CONTENT);
//    					startActivityForResult(Intent.createChooser(intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
    					
					Intent target = FileUtils.createGetContentIntent();
					Intent intent = Intent.createChooser(target, getString(R.string.chooser_title2));
					intent.setType("audio/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					try {
						 startActivityForResult(intent, RQS_OPEN_AUDIO_MP3);
					} catch (ActivityNotFoundException e) {
						// The reason for the existence of aFileChooser
					}

    				} else {
    					if (isInternetPresent) {
    						Utilities.showToast(RadioProgramUpload.this,
    								"Uploading...");

    						if (edt_title_radio.getText().toString().equals("")) {
    							new SendRadioPrograms().execute(id, 
    									//title,
    									//edt_des_radio.getText().toString(),
    									Utilities.encodeImoString(title),
    									Utilities.encodeImoString(edt_des_radio.getText().toString()),
    									imgDecodableString, srcPath,
    									getTimeFromTimePicker(),
    									getDateFromDatePicker(), status);
    						} else {
							new SendRadioPrograms().execute(
									id,
									// edt_title_radio.getText().toString(),
									// edt_des_radio.getText().toString(),
									Utilities.encodeImoString(edt_title_radio.getText().toString()), 
									Utilities.encodeImoString(edt_des_radio.getText().toString()),
									imgDecodableString, srcPath,
									getTimeFromTimePicker(),
									getDateFromDatePicker(), status);
    						}

    					} else {
    						Utilities.showAlertDialog(
    										RadioProgramUpload.this,
    										"Internet Connection Error",
    										"Please connect to working Internet connection",
    										false);
    					}
    				}

    			} else {
    				Utilities.showToast(RadioProgramUpload.this,
    						"Uploading...Please wait");
    			}
//			}else{
//				Utilities.showToast(RadioProgramUpload.this,
//						"Yet we can not have more than 8 programs");
//			}
			

			break;

		case R.id.btn_play_pause:
			if (Integer.parseInt(btn_play_pause.getTag().toString()) == R.drawable.play_arrow) {

				if (isComingIntent) {
					if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
						btn_play_pause.setTag(R.drawable.pause_arrow);
						btn_play_pause.setBackgroundResource(R.drawable.pause_arrow);
						mediaPlayer.start();
					
					}else{
						if (getIntent().getStringExtra("radio_program") != null) {
							progress_loading.setVisibility(View.VISIBLE);
							btn_play_pause.setVisibility(View.GONE);
							startFromUrl(getIntent().getStringExtra("radio_program"));
						}else{

							Utilities.showToast(this, "No file selected");

						}
					}

				} else {
					if (srcPath == null) {

						Utilities.showToast(this, "No file selected");

					} else {
						btn_play_pause.setTag(R.drawable.pause_arrow);
						btn_play_pause.setBackgroundResource(R.drawable.pause_arrow);
						cmdPrepare();
						cmdStart();
					}
				}

			} else if (Integer.parseInt(btn_play_pause.getTag().toString()) == R.drawable.pause_arrow) {
				if (isComingIntent) {
					btn_play_pause.setTag(R.drawable.play_arrow);
					btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
					mediaPlayer.pause();
				
				}else{
					if (mediaPlayer.isPlaying()) {
						btn_play_pause.setTag(R.drawable.play_arrow);
						btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
						cmdPause();
					} else {
						cmdPause();
					}
				}
			}
			break;

		case R.id.img_cam:
			if (isUploading == false) {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						mediaPlayer.release();
					}
				}
				Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

			} else {
				Utilities.showToast(RadioProgramUpload.this,
						"Uploading...Please wait");
			}

			break;

		case R.id.img_record_title:
			promptSpeechInput("title");
			break;

		case R.id.img_record_des:
			promptSpeechInput("description");
			break;
			
		case R.id.img_text_change:
			if (Integer.parseInt(img_text_change.getTag().toString()) == R.drawable.blue_move_hebrew) {
				img_text_change
						.setBackgroundResource(R.drawable.red_move_english);
				img_text_change.setTag(R.drawable.red_move_english);

			} else if (Integer.parseInt(img_text_change.getTag().toString()) == R.drawable.red_move_english) {
				img_text_change
						.setBackgroundResource(R.drawable.blue_move_hebrew);
				img_text_change.setTag(R.drawable.blue_move_hebrew);
			}
			break;

		default:
			break;
		}
	}

	// Get Time From Time Picker
	private String getTimeFromTimePicker() {
		hour = time_picker.getCurrentHour();
		minute = time_picker.getCurrentMinute();
		if (String.valueOf(hour).length() == 1) {

			hour_str = "0" + String.valueOf(hour);

		} else {
			hour_str = String.valueOf(hour);
		}

		if (String.valueOf(minute).length() == 1) {

			minute_str = "0" + String.valueOf(minute);

		} else {
			minute_str = String.valueOf(minute);
		}

		time = hour_str + ":" + minute_str;
		return time;
	}

	// Get Date From Date Picker
	private String getDateFromDatePicker() {
		day = date_picker.getDayOfMonth();
		month = date_picker.getMonth() + 1;
		year = date_picker.getYear();

		if (String.valueOf(day).length() == 1) {
			day_str = "0" + String.valueOf(day);
		} else {
			day_str = String.valueOf(day);
		}

		if (String.valueOf(month).length() == 1) {
			month_str = "0" + String.valueOf(month);
		} else {
			month_str = String.valueOf(month);
		}

		date = new StringBuilder().append(String.valueOf(year)).append("-")
				.append(month_str).append("-").append(day_str).toString();

		return date;
	}

	// Set Toggle Button Check Change Listener...
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			status = String.valueOf(isChecked);
		} else {
			status = String.valueOf(isChecked);
		}
	}

	// onActivity Result To Get Image From Gallery...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK && requestCode == RQS_OPEN_AUDIO_MP3 && null != data) {
				audioFileUri = data.getData();
				//srcPath = getPathNew(audioFileUri);
				try {
					// Get the file path from the URI
					srcPath = FileUtils.getPath(this, audioFileUri);
					
					if (!srcPath.equals("null")) {
						if (Utilities.getExtension(srcPath) != null) {
							if (Utilities.getExtension(srcPath).equals("mp3")) {
								isComingIntent = false;
								cmdReset();
								cmdSetDataSource(srcPath);
								
								// Get Audio Title From SdCard and set on edit text...
								if (Utilities.isSdPresent()) {
//									Cursor cursor;
//							        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//							        cursor = getContentResolver().query(audioFileUri, STAR, selection, null, null);
//
//						            if (cursor != null) {
//						                if (cursor.moveToFirst()) {
//						                    do {
//						                    	title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//						                    } while (cursor.moveToNext());
//						                }
//						                cursor.close();
//						            }
									
									// Get Audio File Name From SDCard And set On The Edit Text...
									if (srcPath.substring(srcPath.lastIndexOf("/")+1) != null && !srcPath.substring(srcPath.lastIndexOf("/")+1).equals("")) {
										title = srcPath.substring(srcPath.lastIndexOf("/")+1).replaceAll(".mp3", "");
									}
								}
								
								// Get Audio Title From SdCard and set on edit text...
								//MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
								//mediaMetadataRetriever.setDataSource(RadioProgramUpload.this, audioFileUri);
								//title = (String) mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
								if (edt_title_radio.getText().toString().equals("")) {
									if (title != null) {
										edt_title_radio.setText(title);
									}
								}

							}else{
								srcPath = null;
								Utilities.showToast(this, "Invalid Format Extension");
							}
						}
					} else {
						srcPath = null;
						Utilities.showToast(this, "Something went wrong");
					}

				} catch (Exception e) {
					Log.e("FileSelectorTestActivity", "File select error", e);
				}

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_title_radio.setText(result.get(0));

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_THREE) {
				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_des_radio.setText(result.get(0));

			} else if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG && null != data) {
				isUploading = false;
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				cursor.close();

				imgDecodableString = Utilities.compressImage(this, imgDecodableString);
				img_cam.setBackgroundResource(0);
				img_cam.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
			}

		} catch (Exception e) {
			srcPath = null;
			Utilities.showToast(this, "Something went wrong");
		}
	}

	// Get Path Using URI...
	public String getPathNew(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput(String values) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
		if (values.equals("title")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ONE);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));
			}
		} else if (values.equals("description")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_THREE);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));
			}
		}
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
				Log.e("IllegalStateException", "IllegalStateException?? "+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("IOException", "IOException?? "+e.getMessage());
				e.printStackTrace();
			}
		} 
	}

	private void cmdSetDataSource(String path) {
		if (mediaPlayerState == MP_State.Idle) {
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayerState = MP_State.Initialized;
			} catch (IllegalArgumentException e) {
				Log.e("IllegalArgumentException", "IllegalArgumentException?? "+e.getMessage());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				Log.e("IllegalStateException", "IllegalStateException?? "+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("IOException", "IOException?? "+e.getMessage());
				e.printStackTrace();
			}
		} 
	}

	private void cmdStart() {
		if (mediaPlayerState == MP_State.Prepared
				|| mediaPlayerState == MP_State.Started
				|| mediaPlayerState == MP_State.Paused
				|| mediaPlayerState == MP_State.PlaybackCompleted) {
			mediaPlayer.start();
			mediaPlayerState = MP_State.Started;
		} 
	}

	private void cmdPause() {
		if (mediaPlayerState == MP_State.Started
				|| mediaPlayerState == MP_State.Paused) {
			mediaPlayer.pause();
			mediaPlayerState = MP_State.Paused;
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
		} 
	}

	// Send Radio Programs To Server...
	class SendRadioPrograms extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			seek_bar_radio.setProgress(0);
		}

		@Override
		protected String doInBackground(String... params) {
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT
					+ Config.ADD_RADIO_PROGRAMS);

			try {

				CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress(""
										+ (int) ((num / (float) totalSize) * 100));
							}
						}, Charset.forName("UTF-8"));

				if (!params[0].equals("")) {
					reqEntity.addPart("id", new StringBody(params[0], Charset
											.forName("UTF-8")));
				} else {
					reqEntity.addPart("id", new StringBody(""));
				}

				if (params[1] != null) {
					if (!params[1].equals("")) {
						reqEntity.addPart("title", new StringBody(params[1],
								Charset.forName("UTF-8")));
					} else {
						reqEntity.addPart("title", new StringBody(""));
					}
				} else {
					reqEntity.addPart("title", new StringBody(""));
				}

				if (params[2] != null) {
					if (!params[2].equals("")) {
						reqEntity.addPart("description", new StringBody(
								params[2], Charset.forName("UTF-8")));
					} else {
						reqEntity.addPart("description", new StringBody(""));
					}
				} else {
					reqEntity.addPart("description", new StringBody(""));
				}

				if (params[3] != null) {
					if (!params[3].equals("")) {
						File imageFile = new File(params[3]);
						FileBody bodyImage = new FileBody(imageFile, "", "UTF-8");
						reqEntity.addPart("image", bodyImage);
					} else {
						reqEntity.addPart("image", new StringBody("", Charset.forName("UTF-8")));
					}
				} else {
					reqEntity.addPart("image", new StringBody("", Charset.forName("UTF-8")));
				}

				if (params[4] != null) {
					if (!params[4].equals("")) {

						File audioFile = new File(params[4]);
						FileBody bodyAudio = new FileBody(audioFile, "", "UTF-8");
						reqEntity.addPart("radio_program", bodyAudio);

					} else {
						reqEntity.addPart("radio_program", new StringBody("", Charset.forName("UTF-8")));
					}
				} else {
					reqEntity.addPart("radio_program", new StringBody("", Charset.forName("UTF-8")));
				}

				reqEntity.addPart("time", new StringBody(params[5], Charset.forName("UTF-8")));
				reqEntity.addPart("date", new StringBody(params[6], Charset.forName("UTF-8")));
				reqEntity.addPart("status", new StringBody(params[7], Charset.forName("UTF-8")));
				totalSize = reqEntity.getContentLength();
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				resEntity = response.getEntity();
				response_str = EntityUtils.toString(resEntity);

			} catch (ClientProtocolException e) {
				Log.e("ClientProtocolException??", "ClientProtocolException?? "+ e.toString());
			} catch (IOException e) {
				Log.e("IOException??", "IOException?? " + e.toString());
			}
			return response_str;
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			isUploading = true;
			seek_bar_radio.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (isComingIntent) {
				imgDecodableString = "";
				audioFileUri = null;
				srcPath = null;
				seek_bar_radio.setProgress(0);
				isUploading = false;
			}else{
				img_cam.setImageResource(R.drawable.cam_icon);
				imgDecodableString = "";
				audioFileUri = null;
				srcPath = null;
				seek_bar_radio.setProgress(0);
				isUploading = false;
				Utilities.deleteCompressImageFiles();	
			}
			if (result != null) {
				try {
					JSONObject jsonObj = new JSONObject(result);
					if (jsonObj.get("success").equals(true)) {
						Utilities.showToast(RadioProgramUpload.this, jsonObj.getString("value"));
						
					} else {
						Utilities.showToast(RadioProgramUpload.this, jsonObj.getString("value"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// OnBack Press...
	@Override
	public void onBackPressed() {
		if (isUploading) {
			Utilities.showToast(RadioProgramUpload.this,
					"Uploading...Please wait");
		} else {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
			}
			Intent mintent = new Intent();
			setResult(RESULT_OK, mintent);
			finish();
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
			super.onBackPressed();
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
					mediaPlayer.setDataSource(RadioProgramUpload.this, Uri.parse(url));
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setOnPreparedListener(RadioProgramUpload.this);
					mediaPlayer.setOnCompletionListener(RadioProgramUpload.this);
					mediaPlayer.prepareAsync();
					//mediaPlayer.start();

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
			btn_play_pause.setVisibility(View.VISIBLE);
			btn_play_pause.setTag(R.drawable.pause_arrow);
			btn_play_pause.setBackgroundResource(R.drawable.pause_arrow);
		}
		if (!mp.isPlaying()) {
			mp.start();
		}
	}
	
	// When Media Player To complete Play
	@Override
	public void onCompletion(MediaPlayer mp) {
		btn_play_pause.setTag(R.drawable.play_arrow);
		btn_play_pause.setBackgroundResource(R.drawable.play_arrow);

	}

}
