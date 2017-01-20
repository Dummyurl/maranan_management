package com.marananmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.itextpdf.text.pdf.PdfReader;
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

public class NewsLetterUpload extends Activity implements OnClickListener{
	private ImageView img_cam, img_record_title;
	private EditText edt_title_radio;
	private Button btn_upload, btn_play_pause, btn_edit_pdf;
	private ProgressBar progress_loading;
	private ToggleButton toggle_btn;
	private DatePicker date_picker;
	private TimePicker time_picker;
	private ProgressBar seek_bar_radio;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
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
	public  String date;
	public  String time;
	private String pdfPath = null;
	private String pages_count;
	private HttpEntity resEntity;
	public String response_str;
	// Progress Dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;
	private Boolean isUploading = false;
	private long totalSize = 0;
	private RelativeLayout relate_des;
	private static final int SELECT_PDF = 1;

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
		relate_des.setVisibility(View.GONE);
		relative_datetimecurrent = (RelativeLayout) findViewById(R.id.relative_datetimecurrent);
		relative_datetimecurrent.setVisibility(View.VISIBLE);
		tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
		tv_dateTimeCurrent.setText(Utilities.getCurrentDate()+" | "+Utilities.getIsraelTime());
		img_cam = (ImageView) findViewById(R.id.img_cam);
		img_cam.setBackgroundResource(R.drawable.cam_icon_pdf);
		img_cam.setOnClickListener(this);
		img_record_title = (ImageView) findViewById(R.id.img_record_title);
		img_record_title.setOnClickListener(this);
		edt_title_radio = (EditText) findViewById(R.id.edt_title_radio);
		edt_title_radio.setText(Utilities.decodeImoString(getIntent().getStringExtra("title")));
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
		time_picker = (TimePicker) findViewById(R.id.time_picker);
		time_picker.setIs24HourView(true);
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
		progress_loading.setVisibility(View.INVISIBLE);
		seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
		seek_bar_radio.setProgress(0);

		if (getIntent().getStringExtra("image") != null) {
			pdfPath = "";
			img_cam.setBackgroundResource(0);
			Picasso.with(this)
					.load(getIntent().getStringExtra("image"))
					.placeholder(R.drawable.cam_icon_pdf)
					.error(R.drawable.cam_icon_pdf).into(img_cam);
		}

		if (getIntent().getStringExtra("date") != null) {
			String insertDate = getIntent().getStringExtra("date");
			String[] items1 = insertDate.split("-");
			String y1 = items1[0];
			String m1 = items1[1];
			String d1 = items1[2];
			int y = Integer.parseInt(y1);
			int m = Integer.parseInt(m1);
			int d = Integer.parseInt(d1);

			date_picker.init(y, (m - 1), d, null);
			NewsLetterList.getInstance().setdate(y1);
		}

		if (getIntent().getStringExtra("time") != null) {
			String insertTime = getIntent().getStringExtra("time");
			String[] items1 = insertTime.split(":");
			String h = items1[0];
			int hh = Integer.parseInt(h);
			String m = items1[1];
			int mm = Integer.parseInt(m);
			time_picker.setCurrentHour(hh);
			time_picker.setCurrentMinute(mm);
		}

		if (getIntent().getStringExtra("id") != null) {
			id = getIntent().getStringExtra("id");
		}

	}

	// onclick Listener...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
            	if (isUploading == false) {
            		
            		if (isInternetPresent) {
						
						if (pdfPath != null) {
							
							Utilities.showToast(NewsLetterUpload.this, "Uploading Newsletter...");
							new UploadNewsLetter().execute(id,
									Utilities.encodeImoString(edt_title_radio.getText().toString()), 
									pdfPath, pages_count,
									getTimeFromTimePicker(),
									getDateFromDatePicker());
    						
						}else{
							Utilities.showToast(NewsLetterUpload.this,
		    						"Please select PDF");
						}

					} else {
						Utilities.showAlertDialog(
								NewsLetterUpload.this,
										"Internet Connection Error",
										"Please connect to working Internet connection",
										false);
					}

    			} else {
    				Utilities.showToast(NewsLetterUpload.this,
    						"Uploading...Please wait");
    			}

			break;

		case R.id.img_cam:
			if (isUploading == false) {
				//if (!isComingIntent) {
					
					pdfPath = null;
			        Intent target = FileUtils.createGetContentIntent();
			        Intent intent = Intent.createChooser(target, getString(R.string.chooser_title));
			        intent.setType("application/pdf");
			        intent.setAction(Intent.ACTION_GET_CONTENT);
			        try {
			            startActivityForResult(intent, SELECT_PDF);
			        } catch (ActivityNotFoundException e) {
			            // The reason for the existence of aFileChooser
			        }
				//}
			} else {
				Utilities.showToast(NewsLetterUpload.this,
						"Uploading...Please wait");
			}

			break;

		case R.id.img_record_title:
			promptSpeechInput("title");
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
		NewsLetterList.getInstance().setdate(String.valueOf(year));
		return date;
	}

	// onActivity Result To Get Image From Gallery...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK && null != data && requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				edt_title_radio.setText(result.get(0));

			}else if (resultCode == RESULT_OK && null != data && requestCode == SELECT_PDF) {
				
				isUploading = false;
				Uri selectedpdf = data.getData();
				if(selectedpdf.getLastPathSegment().endsWith("pdf")){
					
					try {
						// Get the file path from the URI
						pdfPath = FileUtils.getPath(this, selectedpdf);
						
						Log.e("pdfPath", "pdfPath?? " + pdfPath);
						Log.e("PDfName", "PDfName?? "+pdfPath.substring(pdfPath.lastIndexOf("/")+1).replaceAll(".pdf", ""));
                        
						// Get PDF Name From SDCard And set On The Edit Text...
						if (pdfPath.substring(pdfPath.lastIndexOf("/")+1) != null && !pdfPath.substring(pdfPath.lastIndexOf("/")+1).equals("")) {
							edt_title_radio.setText(pdfPath.substring(pdfPath.lastIndexOf("/")+1).replaceAll(".pdf", ""));
						}
					
					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error", e);
					}

					try {

						PdfReader reader = new PdfReader(pdfPath);
						pages_count = String.valueOf(reader.getNumberOfPages());
						Log.e("Pdf", "Page?? " + pages_count);
						
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Exception",
								"ExceptionPDF Page Count?? " + e.getMessage());
					}
				}else{
					 Utilities.showToast(this, "Invalid file type");
				}
			}

		} catch (Exception e) {
			Log.e("Exception", "Exception?? "+e.getMessage());
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
				Utilities.showToast(this, getString(R.string.speech_not_supported));
				
			}
		}

	}

	// Send Radio Programs To Server...
	class UploadNewsLetter extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			seek_bar_radio.setProgress(0);
		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT + Config.UPLOAD_NEWSLETTERS);

			try {

				CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress(""+ (int) ((num / (float) totalSize) * 100));
							}
						}, Charset.forName("UTF-8"));

				if (!params[0].equals("")) {
					reqEntity.addPart("id", new StringBody(params[0], Charset.forName("UTF-8")));
				} else {
					reqEntity.addPart("id", new StringBody(""));
				}

				if (params[1] != null) {
					if (!params[1].equals("")) {
						reqEntity.addPart("title", new StringBody(params[1], Charset.forName("UTF-8")));
					} else {
						reqEntity.addPart("title", new StringBody(""));
					}
				} else {
					reqEntity.addPart("title", new StringBody(""));
				}

				if (params[2] != null) {
					if (!params[2].equals("")) {

						File pdfFile = new File(params[2]);
						FileBody bodypdf = new FileBody(pdfFile, "", "UTF-8");
						reqEntity.addPart("pdf", bodypdf);

					} else {
						reqEntity.addPart("pdf", new StringBody("", Charset.forName("UTF-8")));
					}
				} else {
					reqEntity.addPart("pdf", new StringBody("", Charset.forName("UTF-8")));
				}
				
				if (params[3] != null) {
					if (!params[3].equals("")) {
						reqEntity.addPart("pdf_pages", new StringBody(params[3], Charset.forName("UTF-8")));
					} else {
						reqEntity.addPart("pdf_pages", new StringBody(""));
					}
				} else {
					reqEntity.addPart("pdf_pages", new StringBody(""));
				}

				reqEntity.addPart("time", new StringBody(params[4], Charset.forName("UTF-8")));
				reqEntity.addPart("date", new StringBody(params[5], Charset.forName("UTF-8")));
				totalSize = reqEntity.getContentLength();
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				resEntity = response.getEntity();
				response_str = EntityUtils.toString(resEntity);

			} catch (final ClientProtocolException e) {
				Log.e("ClientProtocolException??", "ClientProtocolException?? "+ e.getMessage());
				Utilities.showRunUiThread(NewsLetterUpload.this, e);
			
			} catch (final IOException e) {
				Log.e("IOException??", "IOException?? " + e.getMessage());
				Utilities.showRunUiThread(NewsLetterUpload.this, e);
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
						img_cam.setBackgroundResource(0);
						Picasso.with(NewsLetterUpload.this)
								.load(jsonObj.getString("image"))
								.placeholder(R.drawable.cam_icon_pdf)
								.error(R.drawable.cam_icon_pdf).into(img_cam);
						Utilities.showToast(NewsLetterUpload.this, jsonObj.getString("value"));
						
					} else {
						img_cam.setImageResource(R.drawable.cam_icon_pdf);
						Utilities.showToast(NewsLetterUpload.this, jsonObj.getString("value"));
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
			
			Utilities.showToast(NewsLetterUpload.this,
					"Uploading...Please wait");
		
		} else {
			Intent mintent = new Intent();
			setResult(RESULT_OK, mintent);
			finish();
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
			super.onBackPressed();
		}
	}

}
