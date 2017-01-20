package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class FinancialManagement extends Activity implements OnClickListener {
	private static FinancialManagement mContext;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private ImageView img_videoAds;
	private ImageView img_classified;
	private ImageView img_alarmsystem;
	private ImageView img_location;
	private ImageView img_financial_Info;
	private ImageView img_customer_management;
	private int REQUEST_CODE_VIDEO_ADD = 100;

	// FinancialManagement Instance
	public static FinancialManagement getInstance() {
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
		setContentView(R.layout.financialmanagement);
		mContext = FinancialManagement.this;
		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();
		initializeView();
	}

	// Initialize View...
	private void initializeView() {
		img_videoAds = (ImageView) findViewById(R.id.img_videoAds);
		img_videoAds.setOnClickListener(this);
		img_classified = (ImageView) findViewById(R.id.img_classified);
		img_classified.setOnClickListener(this);
		img_alarmsystem = (ImageView) findViewById(R.id.img_alarmsystem);
		img_alarmsystem.setOnClickListener(this);
		img_location = (ImageView) findViewById(R.id.img_location);
		img_location.setOnClickListener(this);
		img_financial_Info = (ImageView) findViewById(R.id.img_financial_Info);
		img_financial_Info.setOnClickListener(this);
		img_customer_management = (ImageView) findViewById(R.id.img_customer_management);
		img_customer_management.setOnClickListener(this);
	}

	// Click Events...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_videoAds:
			if (isInternetPresent) {
				
				Intent intent = new Intent(this, VideoAdvertisement.class);
				startActivityForResult(intent, REQUEST_CODE_VIDEO_ADD);

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.img_classified:
			if (isInternetPresent) {

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.img_alarmsystem:
			if (isInternetPresent) {

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.img_location:
			if (isInternetPresent) {

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.img_financial_Info:
			if (isInternetPresent) {

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		case R.id.img_customer_management:
			if (isInternetPresent) {

			} else {
				Utilities.showAlertDialog(FinancialManagement.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			break;

		default:
			break;
		}

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
