package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class UpdateDateTime extends Activity implements OnClickListener {
	TextView tv_dateTimeCurrent;
	DatePicker date_picker;
	TimePicker time_picker;
	Button btn_upload;
	private int hour;
	private int minute;
	private int day;
	private int month;
	private int year;
	private String hour_str;
	private String minute_str;
	private String day_str;
	private String month_str;
	public String date;
	public String time;
	private Spinner spnr_days;
	private String days_select = "";
	private String days_select_new;

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
		setContentView(R.layout.update_datetime);
		initializeView();
	}

	private void initializeView() {
		tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
		date_picker = (DatePicker) findViewById(R.id.date_picker);
		time_picker = (TimePicker) findViewById(R.id.time_picker);
		time_picker.setIs24HourView(true);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		btn_upload.setOnClickListener(this);

		if (getIntent().getStringExtra("date") != null
				&& !getIntent().getStringExtra("date").equals("") && getIntent().getStringExtra("time") != null
						&& !getIntent().getStringExtra("time").equals("")) {
			
			String insertDate = getIntent().getStringExtra("date");
			String[] items1 = insertDate.split("-");
			String y1 = items1[0];
			String m1 = items1[1];
			String d1 = items1[2];
			int y = Integer.parseInt(y1);
			int m = Integer.parseInt(m1);
			int d = Integer.parseInt(d1);

			date_picker.init(y, (m - 1), d, null);
			
			String insertTime = getIntent().getStringExtra("time");
			String[] items2 = insertTime.split(":");
			String hh = items2[0];
			String mm = items2[1];
			int hhh = Integer.parseInt(hh);
			int mmm = Integer.parseInt(mm);
			time_picker.setCurrentHour(hhh);
			time_picker.setCurrentMinute(mmm);
			
//			tv_dateTimeCurrent.setText(d1+":"+m1+":"+y1+ " | "
//					+ hh+":"+mm);
		}else{
//			tv_dateTimeCurrent.setText(Utilities.getCurrentDate() + " | "
//					+ Utilities.getIsraelTime());
		}
		
		tv_dateTimeCurrent.setText(Utilities.getCurrentDate() + " | "
				+ Utilities.getIsraelTime());
		
		if(getIntent().getStringExtra("days_select") != null && !getIntent().getStringExtra("days_select").equals("")){
			days_select = getIntent().getStringExtra("days_select");
		}else{
			days_select = getResources().getString(R.string.no_pause);
		}
		
		spnr_days = (Spinner) findViewById(R.id.spnr_days);
		String[] some_array = getResources().getStringArray(R.array.days_sequence_array);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_tv_items, some_array);
		dataAdapter
				.setDropDownViewResource(R.layout.spinner_tv_items);
		spnr_days.setAdapter(dataAdapter);
		
		if (days_select.equals(getResources().getString(R.string.no_pause))) {
			days_select_new = getResources().getString(R.string.no_pause_heb);
		} else if (days_select.equals(getResources().getString(
				R.string.five_days))) {
			days_select_new = getResources().getString(R.string.five_days_heb);
		} else if (days_select.equals(getResources().getString(
				R.string.ten_days))) {
			days_select_new = getResources().getString(R.string.ten_days_heb);
		} else if (days_select.equals(getResources().getString(
				R.string.twenty_days))) {
			days_select_new = getResources()
					.getString(R.string.twenty_days_heb);
		} else if (days_select.equals(getResources().getString(
				R.string.thirty_days))) {
			days_select_new = getResources()
					.getString(R.string.thirty_days_heb);
		} else if (days_select.equals(getResources().getString(
				R.string.fourty_days))) {
			days_select_new = getResources()
					.getString(R.string.fourty_days_heb);
		} else {
			days_select_new = getResources().getString(R.string.no_pause_heb);
		}

		for (int i = 0; i < some_array.length; i++) {
			if (days_select_new.equals(some_array[i])) {
				spnr_days.setSelection(i);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
			if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.no_pause_heb))){
				days_select = getResources().getString(R.string.no_pause);
			}else if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.five_days_heb))){
				days_select = getResources().getString(R.string.five_days);
			}else if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.ten_days_heb))){
				days_select = getResources().getString(R.string.ten_days);
			}else if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.twenty_days_heb))){
				days_select = getResources().getString(R.string.twenty_days);
			}else if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.thirty_days_heb))){
				days_select = getResources().getString(R.string.thirty_days);
			}else if(spnr_days.getSelectedItem().toString().equals(getResources().getString(R.string.fourty_days_heb))){
				days_select = getResources().getString(R.string.fourty_days);
			}
			Intent mintent = new Intent();
			mintent.putExtra("date", getDateFromDatePicker());
			mintent.putExtra("time", getTimeFromTimePicker());
			mintent.putExtra("days_select", days_select);
			setResult(RESULT_OK, mintent);
			finish();
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
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

	// OnBack Press...
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();

	}

}
