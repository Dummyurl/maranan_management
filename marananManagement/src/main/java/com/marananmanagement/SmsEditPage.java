package com.marananmanagement;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.marananmanagement.database.SmsDB;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class SmsEditPage extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private static SmsEditPage mContext;
	EditText edit_page;
	TextView tv_time;
	ImageView img_record_sms;
	Button btn_saturday, btn_wednesday, btn_thursday, btn_friday, btn_Sunday,
			btn_Monday, btn_tuesday;
	ToggleButton toggle_btn;
	Spinner spinner_time_Hr;
	Spinner spinner_time_min;
	Button btn_save, btn_save_publish, btn_broadcast, btn_enter, btn_disk,
			btn_sms, btn_whatsapp, btn_mail;
	boolean check;
	LinearLayout ll_edit_page_btn;
	LinearLayout ll_button_up;
	String alertMessage, sun, mond, tues, wed, thur, fri, sat, mode, time_Hr,
			time_min;
	String subs_id = null;
	String[] array_min, array_Hr;
	ProgressBar pDialog;
	Editor editor;
	SmsDB db;
	SQLiteDatabase sqlitedb;
	int REQUEST_CODE_SMS_EDIT = 200;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;

	// SmsEditPage Instance
	public static SmsEditPage getInstance() {
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
		setContentView(R.layout.activity_edit_page);
		mContext = SmsEditPage.this;
		initializeView();

	}

	// IniTialize View...
	private void initializeView() {
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		db = new SmsDB(this);
		edit_page = (EditText) findViewById(R.id.edit_page);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setVisibility(View.VISIBLE);
		Utilities.startTime(this, tv_time);
		ll_edit_page_btn = (LinearLayout) findViewById(R.id.ll_edit_page_btn);
		ll_button_up = (LinearLayout) findViewById(R.id.ll_button_up);
		ll_button_up.setVisibility(View.VISIBLE);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setVisibility(View.GONE);
		btn_save_publish = (Button) findViewById(R.id.btn_save_publish);
		btn_save_publish.setVisibility(View.GONE);
		btn_broadcast = (Button) findViewById(R.id.btn_broadcast);
		btn_broadcast.setVisibility(View.VISIBLE);
		btn_broadcast.setOnClickListener(this);
		btn_enter = (Button) findViewById(R.id.btn_enter);
		btn_enter.setVisibility(View.VISIBLE);
		btn_enter.setOnClickListener(this);
		btn_disk = (Button) findViewById(R.id.btn_disk);
		btn_disk.setVisibility(View.VISIBLE);
		btn_disk.setOnClickListener(this);
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		btn_sms.setBackgroundResource(R.drawable.sms_gray);
		btn_sms.setTag(R.drawable.sms_gray);
		btn_whatsapp = (Button) findViewById(R.id.btn_whatsapp);
		btn_whatsapp.setOnClickListener(this);
		btn_whatsapp.setBackgroundResource(R.drawable.wats_app_gray);
		btn_whatsapp.setTag(R.drawable.wats_app_gray);
		btn_mail = (Button) findViewById(R.id.btn_mail);
		btn_mail.setOnClickListener(this);
		btn_mail.setBackgroundResource(R.drawable.mail_gray);
		btn_mail.setTag(R.drawable.mail_gray);
		img_record_sms = (ImageView) findViewById(R.id.img_record_sms);
		img_record_sms.setVisibility(View.VISIBLE);
		img_record_sms.setOnClickListener(this);
		btn_saturday = (Button) findViewById(R.id.btn_saturday);
		btn_saturday.setOnClickListener(this);
		btn_saturday.setTag(R.drawable.bg_hollow);
		btn_wednesday = (Button) findViewById(R.id.btn_wednesday);
		btn_wednesday.setOnClickListener(this);
		btn_wednesday.setTag(R.drawable.bg_hollow);
		btn_thursday = (Button) findViewById(R.id.btn_thursday);
		btn_thursday.setOnClickListener(this);
		btn_thursday.setTag(R.drawable.bg_hollow);
		btn_friday = (Button) findViewById(R.id.btn_friday);
		btn_friday.setOnClickListener(this);
		btn_friday.setTag(R.drawable.bg_hollow);
		btn_Sunday = (Button) findViewById(R.id.btn_Sunday);
		btn_Sunday.setOnClickListener(this);
		btn_Sunday.setTag(R.drawable.bg_hollow);
		btn_Monday = (Button) findViewById(R.id.btn_Monday);
		btn_Monday.setOnClickListener(this);
		btn_Monday.setTag(R.drawable.bg_hollow);
		btn_tuesday = (Button) findViewById(R.id.btn_tuesday);
		btn_tuesday.setOnClickListener(this);
		btn_tuesday.setTag(R.drawable.bg_hollow);
		toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
		toggle_btn.setOnCheckedChangeListener(this);
		toggle_btn.setChecked(false);
		check = false;
		spinner_time_Hr = (Spinner) findViewById(R.id.spinner_time_Hr);
		spinner_time_min = (Spinner) findViewById(R.id.spinner_time_min);
		spinner_time_Hr.setVisibility(View.INVISIBLE);
		spinner_time_min.setVisibility(View.INVISIBLE);
		ll_edit_page_btn.setVisibility(View.INVISIBLE);

		array_Hr = getResources().getStringArray(R.array.array_Hr);
		ArrayAdapter<String> aryadapter_Hr = new ArrayAdapter<String>(
				SmsEditPage.this, R.layout.spinner_background, array_Hr);
		aryadapter_Hr.setDropDownViewResource(R.layout.spinner_item_background);

		spinner_time_Hr = (Spinner) findViewById(R.id.spinner_time_Hr);
		spinner_time_Hr.setAdapter(aryadapter_Hr);

		array_min = getResources().getStringArray(R.array.array_min);

		ArrayAdapter<String> aryadapter_min = new ArrayAdapter<String>(
				SmsEditPage.this, R.layout.spinner_background, array_min);
		aryadapter_min
				.setDropDownViewResource(R.layout.spinner_item_background);
		spinner_time_min = (Spinner) findViewById(R.id.spinner_time_min);
		spinner_time_min.setAdapter(aryadapter_min);
		spinner_time_Hr
				.setOnItemSelectedListener(new CustomOnItemSelectedListenerHR());
		spinner_time_min
				.setOnItemSelectedListener(new CustomOnItemSelectedListenerMin());

		// set database methos call...
		setDataBaseValues();

	}

	// Set DataBase Values When Exists...
	private void setDataBaseValues() {
		if (Utilities.doesDatabaseExist(SmsEditPage.this, "SmsDatabase")) {

			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				GetterSetter getset = new GetterSetter();
				getset = db.getAlertValues(getIntent().getStringExtra("id"));
				edit_page.setText(Utilities.decodeImoString(getset.getMessage()));

				if (getset.getMode().equals("Manual")) {
					toggle_btn.setChecked(false);
					check = false;
					btn_save.setVisibility(View.GONE);
					btn_disk.setVisibility(View.VISIBLE);
					spinner_time_Hr.setVisibility(View.INVISIBLE);
					spinner_time_min.setVisibility(View.INVISIBLE);
					ll_edit_page_btn.setVisibility(View.INVISIBLE);

				} else if (getset.getMode().equals("Automatic")) {
					toggle_btn.setChecked(true);
					check = true;
					btn_save.setVisibility(View.GONE);
					btn_disk.setVisibility(View.INVISIBLE);
					spinner_time_Hr.setVisibility(View.VISIBLE);
					spinner_time_Hr.setSelection(Integer.valueOf(getset
							.getTime_hr()));
					spinner_time_min.setVisibility(View.VISIBLE);
					spinner_time_min.setSelection(Integer.valueOf(getset
							.getTime_min()));
					ll_edit_page_btn.setVisibility(View.VISIBLE);

					if (getset.getSun().equals("sunday")) {
						btn_Sunday.setBackgroundResource(R.drawable.bg_solid);
						btn_Sunday.setTag(R.drawable.bg_solid);
						btn_Sunday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_Sunday.setBackgroundResource(R.drawable.bg_hollow);
						btn_Sunday.setTag(R.drawable.bg_hollow);
						btn_Sunday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getMon().equals("monday")) {
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

					if (getset.getTues().equals("tuesday")) {
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

					if (getset.getWed().equals("wednesday")) {
						btn_wednesday
								.setBackgroundResource(R.drawable.bg_solid);
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

					if (getset.getThur().equals("thursday")) {
						btn_thursday.setBackgroundResource(R.drawable.bg_solid);
						btn_thursday.setTag(R.drawable.bg_solid);
						btn_thursday.setTextColor(getResources().getColor(
								R.color.white));
					} else {
						btn_thursday
								.setBackgroundResource(R.drawable.bg_hollow);
						btn_thursday.setTag(R.drawable.bg_hollow);
						btn_thursday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}

					if (getset.getFri().equals("friday")) {
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

					if (getset.getSat().equals("saturday")) {
						btn_saturday.setBackgroundResource(R.drawable.bg_solid);
						btn_saturday.setTag(R.drawable.bg_solid);
						btn_saturday.setTextColor(getResources().getColor(R.color.white));
					} else {
						btn_saturday.setBackgroundResource(R.drawable.bg_hollow);
						btn_saturday.setTag(R.drawable.bg_hollow);
						btn_saturday.setTextColor(getResources().getColor(
								R.color.DeepSkyBlue));
					}
				
				}else if(getset.getMode().equals("Manual-Save")){
					toggle_btn.setChecked(false);
					check = false;
					btn_save.setVisibility(View.GONE);
					btn_disk.setVisibility(View.VISIBLE);
					spinner_time_Hr.setVisibility(View.INVISIBLE);
					spinner_time_min.setVisibility(View.INVISIBLE);
					ll_edit_page_btn.setVisibility(View.INVISIBLE);
				}

			} else {

				edit_page.setText("");
			}

		}

	}

	// click Listener on views...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Sunday:
			if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_hollow) {
				btn_Sunday.setBackgroundResource(R.drawable.bg_solid);
				btn_Sunday.setTag(R.drawable.bg_solid);
				btn_Sunday.setTextColor(getResources().getColor(R.color.white));
				if (db.isIdExist(getIntent().getStringExtra("id"))) {
					db.updateSun(getIntent().getStringExtra("id"), "sunday");
				}

			} else if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
				btn_Sunday.setBackgroundResource(R.drawable.bg_hollow);
				btn_Sunday.setTag(R.drawable.bg_hollow);
				btn_Sunday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
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
					db.updateMon(getIntent().getStringExtra("id"), "monday");
				}

			} else if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
				btn_Monday.setBackgroundResource(R.drawable.bg_hollow);
				btn_Monday.setTag(R.drawable.bg_hollow);
				btn_Monday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
				if (db.isIdExist(getIntent().getStringExtra("id"))) {
					db.updateMon(getIntent().getStringExtra("id"), "");
				}
			}
			break;

		case R.id.btn_tuesday:

			if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_hollow) {
				btn_tuesday.setBackgroundResource(R.drawable.bg_solid);
				btn_tuesday.setTag(R.drawable.bg_solid);
				btn_tuesday
						.setTextColor(getResources().getColor(R.color.white));
				if (db.isIdExist(getIntent().getStringExtra("id"))) {
					db.updateTues(getIntent().getStringExtra("id"), "tuesday");
				}

			} else if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_solid) {
				btn_tuesday.setBackgroundResource(R.drawable.bg_hollow);
				btn_tuesday.setTag(R.drawable.bg_hollow);
				btn_tuesday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
				if (db.isIdExist(getIntent().getStringExtra("id"))) {
					db.updateTues(getIntent().getStringExtra("id"), "");
				}

			}
			break;

		case R.id.btn_wednesday:
			if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_hollow) {
				btn_wednesday.setBackgroundResource(R.drawable.bg_solid);
				btn_wednesday.setTag(R.drawable.bg_solid);
				btn_wednesday.setTextColor(getResources().getColor(
						R.color.white));

				if (db.isIdExist(getIntent().getStringExtra("id"))) {
					db.updateWed(getIntent().getStringExtra("id"), "wednesday");
				}

			} else if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_solid) {
				btn_wednesday.setBackgroundResource(R.drawable.bg_hollow);
				btn_wednesday.setTag(R.drawable.bg_hollow);
				btn_wednesday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
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
					db.updateThur(getIntent().getStringExtra("id"), "thursday");
				}

			} else if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_solid) {
				btn_thursday.setBackgroundResource(R.drawable.bg_hollow);
				btn_thursday.setTag(R.drawable.bg_hollow);
				btn_thursday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
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
					db.updateFri(getIntent().getStringExtra("id"), "friday");
				}

			} else if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
				btn_friday.setBackgroundResource(R.drawable.bg_hollow);
				btn_friday.setTag(R.drawable.bg_hollow);
				btn_friday.setTextColor(getResources().getColor(
						R.color.DeepSkyBlue));
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
					db.updateSat(getIntent().getStringExtra("id"), "saturday");
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

		case R.id.btn_broadcast:
			if (check == true) {
				mode = "Automatic";

				if (edit_page.length() == 0) {
					Utilities
					.showToast(SmsEditPage.this,
							"Please enter message");
				} else {

					if (Integer.parseInt(btn_saturday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_Sunday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_Monday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_friday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_tuesday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_wednesday.getTag().toString()) != R.drawable.bg_hollow
							|| Integer.parseInt(btn_thursday.getTag().toString()) != R.drawable.bg_hollow) {

						if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
							// sun = btn_Sunday.getText().toString();
							sun = "Sun";
						} else {
							sun = "";
						}

						if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
							// mond = btn_Monday.getText().toString();
							mond = "Mon";
						} else {
							mond = "";
						}

						if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_solid) {
							// tues = btn_tuesday.getText().toString();
							tues = "Tue";
						} else {
							tues = "";
						}

						if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_solid) {
							// wed = btn_wednesday.getText().toString();
							wed = "Wed";
						} else {
							wed = "";
						}

						if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_solid) {
							// thur = btn_thursday.getText().toString();
							thur = "Thu";
						} else {
							thur = "";
						}

						if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
							// fri = btn_friday.getText().toString();
							fri = "Fri";
						} else {
							fri = "";
						}

						if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_solid) {
							// sat = btn_saturday.getText().toString();
							sat = "Sat";
						} else {
							sat = "";
						}

						alertMessage = edit_page.getText().toString();

						time_Hr = spinner_time_Hr.getSelectedItem().toString();
						time_min = spinner_time_min.getSelectedItem()
								.toString();

						if (subs_id != null) {
							
							/* Send Alert Message Notification To Users Who Use The Maranan App */
							AsyncRequest.sendMessages(this, this.getClass().getSimpleName(), 
									getIntent()
									.getStringExtra("id"), Utilities.encodeImoString(alertMessage), mode, subs_id, time_Hr, time_min, sun, mond, tues, wed, thur, fri, sat, pDialog);
						} else {
							Utilities
									.showToast(SmsEditPage.this,
											"Please Select Subscribers For Sending Message");
						}

					} else {
						Utilities.showToast(SmsEditPage.this, "Select Days");
					}
				}

			} else {
				check = toggle_btn.isChecked();
				mode = "Manual";

				if (edit_page.length() != 0) {

					alertMessage = edit_page.getText().toString();

					if (subs_id != null) {
						
						/* Send Alert Message Notification To Users Who Use The Maranan App */
						AsyncRequest.sendMessages(this, this.getClass().getSimpleName(), 
								getIntent()
								.getStringExtra("id"), Utilities.encodeImoString(alertMessage), mode, subs_id, "", "", "", "", "", "", "", "",
								"",pDialog);

					} else {
						Utilities
								.showToast(SmsEditPage.this,
										"Please Select Subscribers For Sending Message");
					}

				} else {
					Utilities
					.showToast(SmsEditPage.this, "Please enter message");
				}
			}
			break;

		case R.id.btn_enter:
			Intent intent = new Intent(SmsEditPage.this,
					SmsSuscribersActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SMS_EDIT);
			break;

		case R.id.btn_disk:
			check = toggle_btn.isChecked();
			mode = "Manual-Save";

			if (edit_page.length() != 0) {

				alertMessage = edit_page.getText().toString();
				
				/* Send Alert Message Notification To Users Who Use The Maranan App */
				AsyncRequest.sendMessages(this, this.getClass().getSimpleName(), 
						getIntent()
						.getStringExtra("id"), Utilities.encodeImoString(alertMessage), mode, subs_id, "", "", "", "", "", "", "", "",
						"", pDialog);

			} else {

				Utilities.showToast(this, "Please enter message");

			}
			break;

		case R.id.img_record_sms:
			promptSpeechInput();
			break;

		case R.id.btn_sms:
			if (Integer.parseInt(btn_sms.getTag().toString()) == R.drawable.sms_gray) {
				btn_sms.setBackgroundResource(R.drawable.sms_blue);
				btn_sms.setTag(R.drawable.sms_blue);
				mode = "Manual";

				if (edit_page.length() != 0) {

					alertMessage = edit_page.getText().toString();

					if (subs_id != null) {
						
						/* Send Alert Message Notification To Users Who Use The Maranan App */
						AsyncRequest.sendMessages(this, this.getClass().getSimpleName(), 
								getIntent()
								.getStringExtra("id"), Utilities.encodeImoString(alertMessage), mode, subs_id, "", "", "", "", "", "", "", "",
								"", pDialog);

					} else {
						btn_sms.setBackgroundResource(R.drawable.sms_gray);
						btn_sms.setTag(R.drawable.sms_gray);
						Utilities.showToast(SmsEditPage.this,
										"Please Select Subscribers For Sending Message");
					}

				} else {
					btn_sms.setBackgroundResource(R.drawable.sms_gray);
					btn_sms.setTag(R.drawable.sms_gray);
					Utilities.showToast(this, "Please enter message");

				}

			} else if (Integer.parseInt(btn_sms.getTag().toString()) == R.drawable.sms_blue) {
				btn_sms.setBackgroundResource(R.drawable.sms_gray);
				btn_sms.setTag(R.drawable.sms_gray);
			}
			break;

		case R.id.btn_whatsapp:
			if (Integer.parseInt(btn_whatsapp.getTag().toString()) == R.drawable.wats_app_gray) {
				btn_whatsapp.setBackgroundResource(R.drawable.whats_app);
				btn_whatsapp.setTag(R.drawable.whats_app);
				if (edit_page.length() != 0) {

					sendMessageOnWhatsApp(Utilities.encodeImoString(edit_page.getText().toString()));

				} else {
					btn_whatsapp.setBackgroundResource(R.drawable.wats_app_gray);
					btn_whatsapp.setTag(R.drawable.wats_app_gray);
					Utilities.showToast(this, "Please enter message");
				}

			} else if (Integer.parseInt(btn_whatsapp.getTag().toString()) == R.drawable.whats_app) {
				btn_whatsapp.setBackgroundResource(R.drawable.wats_app_gray);
				btn_whatsapp.setTag(R.drawable.wats_app_gray);
			}
			break;

		case R.id.btn_mail:
			if (Integer.parseInt(btn_mail.getTag().toString()) == R.drawable.mail_gray) {
				btn_mail.setBackgroundResource(R.drawable.mail_red);
				btn_mail.setTag(R.drawable.mail_red);
				if (edit_page.length() != 0) {

					sendEmail(edit_page.getText().toString());

				} else {
					btn_mail.setBackgroundResource(R.drawable.mail_gray);
					btn_mail.setTag(R.drawable.mail_gray);
					Utilities.showToast(this, "Please enter message");
				}

			} else if (Integer.parseInt(btn_mail.getTag().toString()) == R.drawable.mail_red) {
				btn_mail.setBackgroundResource(R.drawable.mail_gray);
				btn_mail.setTag(R.drawable.mail_gray);
			}
			break;

		default:
			break;
		}

	}

	// Send Message On Whats App Contacts...
	private void sendMessageOnWhatsApp(String message) {
		PackageManager pm = getPackageManager();
		try {

			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");
			String text = message;

			@SuppressWarnings("unused")
			PackageInfo info = pm.getPackageInfo("com.whatsapp",
					PackageManager.GET_META_DATA);
			// Check if package exists or not. If not then code
			// in catch block will be called
			waIntent.setPackage("com.whatsapp");

			waIntent.putExtra(Intent.EXTRA_TEXT, text);
			startActivity(Intent.createChooser(waIntent, "Share with"));
			btn_whatsapp.setBackgroundResource(R.drawable.wats_app_gray);
			btn_whatsapp.setTag(R.drawable.wats_app_gray);

		} catch (NameNotFoundException e) {
			btn_whatsapp.setBackgroundResource(R.drawable.wats_app_gray);
			btn_whatsapp.setTag(R.drawable.wats_app_gray);
			Utilities.showToast(this, "WhatsApp not Installed");
		}

	}
    
	// Send Email here...
	protected void sendEmail(String message) {
		String[] TO = { "" };
		String[] CC = { "" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);

		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
		emailIntent.putExtra(Intent.EXTRA_TEXT, message);

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			btn_mail.setBackgroundResource(R.drawable.mail_gray);
			btn_mail.setTag(R.drawable.mail_gray);
		} catch (android.content.ActivityNotFoundException ex) {
			btn_mail.setBackgroundResource(R.drawable.mail_gray);
			btn_mail.setTag(R.drawable.mail_gray);
			Utilities.showToast(this, "There is no email client installed.");

		}
	}

	// Toggle Button Check Change Listener...
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			check = isChecked;
			btn_save.setVisibility(View.GONE);
			btn_disk.setVisibility(View.INVISIBLE);
			spinner_time_Hr.setVisibility(View.VISIBLE);
			spinner_time_min.setVisibility(View.VISIBLE);
			ll_edit_page_btn.setVisibility(View.VISIBLE);

			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				db.updateMode(getIntent().getStringExtra("id"), "Automatic");
			}

		} else {
			check = isChecked;
			btn_save.setVisibility(View.GONE);
			btn_disk.setVisibility(View.VISIBLE);
			spinner_time_Hr.setVisibility(View.INVISIBLE);
			spinner_time_min.setVisibility(View.INVISIBLE);
			ll_edit_page_btn.setVisibility(View.INVISIBLE);
			if (db.isIdExist(getIntent().getStringExtra("id"))) {
				db.updateMode(getIntent().getStringExtra("id"), "Manual");
			}

		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View view = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (view instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

	public class CustomOnItemSelectedListenerHR implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			db.updatetime_hr(getIntent().getStringExtra("id"),
					String.valueOf(pos));

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	public class CustomOnItemSelectedListenerMin implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			db.updatetime_min(getIntent().getStringExtra("id"),
					String.valueOf(pos));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	// OnActivityResult Declare Here...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SMS_EDIT
				&& data != null) {
			subs_id = data.getStringExtra("sub_id");

		} else if (resultCode == RESULT_OK && null != data
				&& requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			edit_page.setText(result.get(0));

		}

	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));

		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ONE);
		} catch (ActivityNotFoundException a) {
			Utilities.showToast(SmsEditPage.this,
					getString(R.string.speech_not_supported));
		}
	}
	
	/* Set Adapter For Sms Edit Page */
	public void setAdapter(JSONObject result) {
		btn_sms.setBackgroundResource(R.drawable.sms_gray);
		btn_sms.setTag(R.drawable.sms_gray);
		if (result != null) {
			try {
				if (result.get("success").equals(true)) {
					Utilities.showToast(SmsEditPage.this,
							result.get("value").toString());
					Intent mintent = new Intent();
					setResult(RESULT_OK, mintent);
					finish();
					overridePendingTransition(R.anim.activity_open_scale,
							R.anim.activity_close_translate);

				} else if (result.get("success").equals(false)) {
					Utilities.showToast(SmsEditPage.this,
							result.get("value").toString());
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

	@Override
	public void onBackPressed() {
		if (db.isIdExist(getIntent().getStringExtra("id"))) {
			db.updateMessage(getIntent().getStringExtra("id"), edit_page
					.getText().toString());
		}

		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}

}
