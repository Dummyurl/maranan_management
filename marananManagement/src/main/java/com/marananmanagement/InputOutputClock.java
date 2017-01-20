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
import android.widget.Spinner;

import com.marananmanagement.util.UncaughtExceptionHandler;

public class InputOutputClock extends Activity implements OnClickListener {
	private Spinner spnr_hr_one;
	private Spinner spnr_min_one;
	private Spinner spnr_hr_two;
	private Spinner spnr_min_two;
	private Spinner spnr_hr_three;
	private Spinner spnr_min_three;
	private Button btn_set_time;

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
		setContentView(R.layout.input_output_clock);
		initializeView();

	}

	private void initializeView() {
		spnr_hr_one = (Spinner) findViewById(R.id.spnr_hr_one);
		spnr_min_one = (Spinner) findViewById(R.id.spnr_min_one);
		spnr_hr_two = (Spinner) findViewById(R.id.spnr_hr_two);
		spnr_min_two = (Spinner) findViewById(R.id.spnr_min_two);
		spnr_hr_three = (Spinner) findViewById(R.id.spnr_hr_three);
		spnr_min_three = (Spinner) findViewById(R.id.spnr_min_three);
		btn_set_time = (Button) findViewById(R.id.btn_set_time);
		btn_set_time.setOnClickListener(this);

		setSpinnerDateTime();
	}

	private void setSpinnerDateTime() {
		
		ArrayAdapter<String> adapterHr = new ArrayAdapter<String>(this,
				R.layout.clock_text_item, getResources().getStringArray(R.array.array_Hr));

		adapterHr
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> adapterMin = new ArrayAdapter<String>(this,
				R.layout.clock_text_item, getResources().getStringArray(R.array.array_min));

		adapterMin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnr_hr_one.setAdapter(adapterHr);
		spnr_hr_two.setAdapter(adapterHr);
		spnr_hr_three.setAdapter(adapterHr);
		
		spnr_min_one.setAdapter(adapterMin);
		spnr_min_two.setAdapter(adapterMin);
		spnr_min_three.setAdapter(adapterMin);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_set_time:

			break;

		default:
			break;
		}

	}

	// OnBack Press...
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
