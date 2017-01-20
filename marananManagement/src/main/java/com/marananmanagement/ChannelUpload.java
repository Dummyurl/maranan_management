package com.marananmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

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

public class ChannelUpload extends Activity implements OnClickListener {
	private ImageView img_cam, img_record_title, img_record_des, img_thumbnail;
	private EditText edt_title_radio, edt_des_radio;
	private Button btn_upload, btn_play_pause, btn_edit_pdf, btn_camera;
	private Button btn_input_output;
	private ProgressBar progress_loading;
	private ToggleButton toggle_btn;
	private DatePicker date_picker;
	private TimePicker time_picker;
	private ProgressBar seek_bar_radio;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
	private final int REQ_CODE_SPEECH_INPUT_TWO = 200;
	private final int REQ_CODE_LIVE_IMAGES = 300;
	private final int REQ_CODE_DATE_TIMES = 400;
	private final int REQ_CODE_CLOCK = 500;
	private RelativeLayout relative_datetimecurrent;
	private TextView tv_dateTimeCurrent;
	public String date = "";
	public String time = "";
	private HttpEntity resEntity;
	public String response_str;
	// Progress Dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;
	private Boolean isUploading = false;
	private long totalSize = 0;
	private RelativeLayout relate_des;
	private String liveImagePath = "";
	private String liveImageName = "";
	private String imagestatus = "false";
	private String days_select = "";

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
		relate_des = (RelativeLayout) findViewById(R.id.relate_des);
		relate_des.setVisibility(View.VISIBLE);
		relative_datetimecurrent = (RelativeLayout) findViewById(R.id.relative_datetimecurrent);
		relative_datetimecurrent.setVisibility(View.VISIBLE);
		relative_datetimecurrent.setOnClickListener(this);
		tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
		img_cam = (ImageView) findViewById(R.id.img_cam);
		img_cam.setVisibility(View.INVISIBLE);
		img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
		img_thumbnail.setVisibility(View.VISIBLE);
		img_thumbnail.setOnClickListener(this);
		img_record_title = (ImageView) findViewById(R.id.img_record_title);
		img_record_title.setOnClickListener(this);
		edt_title_radio = (EditText) findViewById(R.id.edt_title_radio);
		edt_title_radio.setText(Utilities.decodeImoString(getIntent()
				.getStringExtra("title")));
		edt_des_radio = (EditText) findViewById(R.id.edt_des_radio);
		edt_des_radio.setText(Utilities.decodeImoString(getIntent()
				.getStringExtra("description")));
		img_record_des = (ImageView) findViewById(R.id.img_record_des);
		img_record_des.setOnClickListener(this);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		btn_upload.setOnClickListener(this);
		btn_upload.setBackgroundResource(R.drawable.upload_btn_new);
		btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
		btn_play_pause.setVisibility(View.INVISIBLE);
		toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
		toggle_btn.setVisibility(View.INVISIBLE);
		btn_edit_pdf = (Button) findViewById(R.id.btn_edit_pdf);
		btn_edit_pdf.setVisibility(View.GONE);
		date_picker = (DatePicker) findViewById(R.id.date_picker);
		date_picker.setVisibility(View.GONE);
		time_picker = (TimePicker) findViewById(R.id.time_picker);
		time_picker.setIs24HourView(true);
		time_picker.setVisibility(View.GONE);
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
		progress_loading.setVisibility(View.INVISIBLE);
		seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
		seek_bar_radio.setProgress(0);
		btn_camera = (Button) findViewById(R.id.btn_camera);
		btn_camera.setVisibility(View.VISIBLE);
		btn_camera.setOnClickListener(this);
		btn_input_output = (Button) findViewById(R.id.btn_input_output);
		btn_input_output.setVisibility(View.VISIBLE);
		btn_input_output.setOnClickListener(this);

		if (getIntent().getStringExtra("image") != null) {
			img_thumbnail.setBackgroundResource(0);
			Picasso.with(this)
					.load(getIntent().getStringExtra("image"))
					.placeholder(R.drawable.cam_icon)
					.error(R.drawable.cam_icon).into(img_thumbnail);
		}

		if (getIntent().getStringExtra("date") != null
				&& !getIntent().getStringExtra("date").equals("")
				&& getIntent().getStringExtra("time") != null
				&& !getIntent().getStringExtra("time").equals("")) {

			date = getIntent().getStringExtra("date");
			String[] items1 = date.split("-");
			String y1 = items1[0];
			String m1 = items1[1];
			String d1 = items1[2];

			time = getIntent().getStringExtra("time");
			String[] items2 = time.split(":");
			String h = items2[0];
			String m = items2[1];

			tv_dateTimeCurrent.setText(d1 + ":" + m1 + ":" + y1 + " | " + h
					+ ":" + m);
		} else {
			tv_dateTimeCurrent.setText(Utilities.getCurrentDate() + " | "
					+ Utilities.getIsraelTime());
		}

		if (getIntent().getStringExtra("image_status").equals("true")) {

			imagestatus = getIntent().getStringExtra("image_status");
			liveImageName = getIntent().getStringExtra("image").substring(
					getIntent().getStringExtra("image").lastIndexOf("/") + 1);

		} else {
			if (getIntent().getStringExtra("image") != null) {
				imagestatus = "false";
				liveImageName = "";
				liveImagePath = "";
			}
		}
		
		if(getIntent().getStringExtra("days_select") != null && !getIntent().getStringExtra("days_select").equals("")){
			days_select = getIntent().getStringExtra("days_select");
		}else{
			days_select = getResources().getString(R.string.no_pause);
		}

	}

	// onclick Listener...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
			if (isUploading == false) {

				if (isInternetPresent) {

					Utilities.showToast(ChannelUpload.this, "Uploading...");
					new UpdateVideo().execute(
							getIntent().getStringExtra("playlist_id"),
							getIntent().getStringExtra("unique_video_id"),
							Utilities.encodeImoString(edt_title_radio.getText()
									.toString().trim()),
							Utilities.encodeImoString(edt_des_radio.getText()
									.toString().trim()), time, date,
							liveImagePath, liveImageName, imagestatus, days_select);

				} else {
					Utilities.showAlertDialog(ChannelUpload.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}

			} else {
				Utilities.showToast(ChannelUpload.this,
						"Uploading...Please wait");
			}

			break;

		case R.id.img_thumbnail:
			if (isUploading == false) {
				Intent videoIntent = new Intent(this, VideoPlayer.class);
				videoIntent.putExtra("video_id",
						getIntent().getStringExtra("video_id"));
				startActivity(videoIntent);

			} else {
				Utilities.showToast(ChannelUpload.this,
						"Uploading...Please wait");
			}

			break;

		case R.id.img_record_title:
			promptSpeechInput("title");
			break;

		case R.id.img_record_des:
			promptSpeechInput("description");
			break;

		case R.id.btn_camera:
			if (isInternetPresent) {

				if (isUploading == false) {

					Intent liveImages = new Intent(this, LiveBroadcastImages.class);
					liveImages.putExtra("id", getIntent().getStringExtra("unique_video_id"));
					liveImages.putExtra("name_class", "VideoChannel");
					liveImages.putExtra("image_old", getIntent().getStringExtra("image_old"));
					startActivityForResult(liveImages, REQ_CODE_LIVE_IMAGES);

				} else {
					Utilities.showToast(this, "Uploading...Please wait");
				}

			} else {
				Utilities.showAlertDialog(this, "Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.relative_datetimecurrent:
			if (isInternetPresent) {

				if (isUploading == false) {

					if (getIntent().getStringExtra("image_status").equals(
							"true")) {

						imagestatus = getIntent()
								.getStringExtra("image_status");
						liveImageName = getIntent().getStringExtra("image")
								.substring(
										getIntent().getStringExtra("image")
												.lastIndexOf("/") + 1);

					} else {
						if (getIntent().getStringExtra("image") != null) {
							imagestatus = "false";
							liveImageName = "";
							liveImagePath = "";
						}
					}

					Intent liveImages = new Intent(this, UpdateDateTime.class);
					liveImages.putExtra("time", time);
					liveImages.putExtra("date", date);
					liveImages.putExtra("days_select", days_select);
					startActivityForResult(liveImages, REQ_CODE_DATE_TIMES);

				} else {
					Utilities.showToast(this, "Uploading...Please wait");
				}

			} else {
				Utilities.showAlertDialog(this, "Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.btn_input_output:

			if (isInternetPresent) {

				if (isUploading == false) {

					if (getIntent().getStringExtra("image_status").equals(
							"true")) {

						imagestatus = getIntent()
								.getStringExtra("image_status");
						liveImageName = getIntent().getStringExtra("image")
								.substring(
										getIntent().getStringExtra("image")
												.lastIndexOf("/") + 1);

					} else {
						if (getIntent().getStringExtra("image") != null) {
							imagestatus = "false";
							liveImageName = "";
							liveImagePath = "";
						}
					}

					Intent liveImages = new Intent(this, InputOutputClock.class);
					startActivityForResult(liveImages, REQ_CODE_CLOCK);

				} else {
					Utilities.showToast(this, "Uploading...Please wait");
				}

			} else {
				Utilities.showAlertDialog(this, "Internet Connection Error",
						"Please connect to working Internet connection", false);
			}

			break;

		default:
			break;
		}
	}

	// onActivity Result To Get Image From Gallery...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_title_radio.setText(result.get(0));

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_SPEECH_INPUT_TWO) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_des_radio.setText(result.get(0));

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_LIVE_IMAGES) {

				try {

					// Get the file path and server image name from the URI
					liveImagePath = data.getStringExtra("imagepaths");
					liveImageName = data.getStringExtra("imagename");
					imagestatus = data.getStringExtra("imagestatus");

					if (liveImagePath != null) {
						if (liveImagePath.startsWith("http://")
								|| liveImagePath.startsWith("https://")) {
							img_thumbnail.setImageResource(0);
							Picasso.with(this)
									.load(liveImagePath)
									.placeholder(R.drawable.cam_icon)
									.error(R.drawable.cam_icon).into(img_thumbnail);
							liveImagePath = "";

						} else {
							img_thumbnail.setImageResource(0);
							img_thumbnail.setImageBitmap(BitmapFactory
									.decodeFile(liveImagePath));
						}
					}

				} catch (Exception e) {
					Log.e("FileTestActivity", "File select error", e);
				}

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_DATE_TIMES) {

				date = data.getStringExtra("date");
				time = data.getStringExtra("time");
				days_select = data.getStringExtra("days_select");
				Log.e("days_select", "days_select?? "+days_select);

				if (date != null && !date.equals("") && time != null
						&& !time.equals("")) {

					String[] items1 = date.split("-");
					String y1 = items1[0];
					String m1 = items1[1];
					String d1 = items1[2];

					String[] items2 = time.split(":");
					String h = items2[0];
					String m = items2[1];

					tv_dateTimeCurrent.setText(d1 + ":" + m1 + ":" + y1 + " | "
							+ h + ":" + m);
				} else {
					tv_dateTimeCurrent.setText(Utilities.getCurrentDate()
							+ " | " + Utilities.getIsraelTime());
				}

			} else if (resultCode == RESULT_OK && null != data
					&& requestCode == REQ_CODE_CLOCK) {

			}

		} catch (Exception e) {
			Log.e("Exception", "Exception?? " + e.getMessage());
			Utilities.showToast(this, "Something went wrong");
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
				Utilities.showToast(this,
						getString(R.string.speech_not_supported));

			}
		} else {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_TWO);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this,
						getString(R.string.speech_not_supported));

			}
		}

	}

	// Update Video..
	class UpdateVideo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			seek_bar_radio.setProgress(0);
		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT
					+ Config.UPLOAD_VIDEO);

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
					reqEntity.addPart("playlist_id", new StringBody(params[0],
							Charset.forName("UTF-8")));
				} else {
					reqEntity.addPart("playlist_id", new StringBody(""));

				}

				if (params[1] != null) {
					if (!params[1].equals("")) {
						reqEntity.addPart("unique_video_id", new StringBody(
								params[1], Charset.forName("UTF-8")));
					} else {
						reqEntity
								.addPart("unique_video_id", new StringBody(""));
					}
				} else {
					reqEntity.addPart("unique_video_id", new StringBody(""));
				}

				if (params[2] != null) {
					if (!params[2].equals("")) {
						reqEntity.addPart("title", new StringBody(params[2],
								Charset.forName("UTF-8")));
					} else {
						reqEntity.addPart("title", new StringBody(""));
					}
				} else {
					reqEntity.addPart("title", new StringBody(""));
				}

				if (params[3] != null) {
					if (!params[3].equals("")) {

						reqEntity.addPart("description", new StringBody(
								params[3], Charset.forName("UTF-8")));

					} else {
						reqEntity.addPart("description", new StringBody(""));
					}
				} else {
					reqEntity.addPart("description", new StringBody(""));
				}

				reqEntity.addPart("time",
						new StringBody(params[4], Charset.forName("UTF-8")));
				reqEntity.addPart("date",
						new StringBody(params[5], Charset.forName("UTF-8")));

				if (params[6] != null) {
					if (!params[6].equals("")) {

						File imageFile = new File(params[6]);
						FileBody bodyImage = new FileBody(imageFile, "",
								"UTF-8");
						reqEntity.addPart("image", bodyImage);
					} else {
						reqEntity.addPart("image",
								new StringBody("", Charset.forName("UTF-8")));
					}
				} else {
					reqEntity.addPart("image",
							new StringBody("", Charset.forName("UTF-8")));
				}

				if (params[7] != null) {
					if (!params[7].equals("")) {

						reqEntity.addPart("image_name", new StringBody(
								params[7], Charset.forName("UTF-8")));

					} else {
						reqEntity.addPart("image_name", new StringBody(""));
					}
				} else {
					reqEntity.addPart("image_name", new StringBody(""));
				}

				if (params[8] != null) {
					if (!params[8].equals("")) {

						reqEntity.addPart("image_status", new StringBody(
								params[8], Charset.forName("UTF-8")));

					} else {
						reqEntity.addPart("image_status", new StringBody(""));
					}
				} else {
					reqEntity.addPart("image_status", new StringBody(""));
				}
				
				if (params[9] != null) {
					if (!params[9].equals("")) {

						reqEntity.addPart("days_select", new StringBody(
								params[9], Charset.forName("UTF-8")));

					} else {
						reqEntity.addPart("days_select", new StringBody(""));
					}
				} else {
					reqEntity.addPart("days_select", new StringBody(""));
				}

				totalSize = reqEntity.getContentLength();
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				resEntity = response.getEntity();
				response_str = EntityUtils.toString(resEntity);

			} catch (final ClientProtocolException e) {
				Log.e("ClientException??", "ClientProtocolException?? "
						+ e.getMessage());
				Utilities.showRunUiThread(ChannelUpload.this, e);

			} catch (final IOException e) {
				Log.e("IOException??", "IOException?? " + e.getMessage());
				Utilities.showRunUiThread(ChannelUpload.this, e);
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
			seek_bar_radio.setProgress(0);
			isUploading = false;
			if (result != null) {
				try {
					JSONObject jsonObj = new JSONObject(result);
					if (jsonObj.get("success").equals(true)) {

						Utilities.showToast(ChannelUpload.this,
								jsonObj.getString("value"));

					} else {

						Utilities.showToast(ChannelUpload.this,
								jsonObj.getString("value"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Change Long To Integer...
	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l
					+ " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

	// OnBack Press...
	@Override
	public void onBackPressed() {
		if (isUploading) {

			Utilities.showToast(ChannelUpload.this, "Uploading...Please wait");

		} else {
			Intent mintent = new Intent();
			setResult(RESULT_OK, mintent);
			finish();
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
			super.onBackPressed();
		}}
}
