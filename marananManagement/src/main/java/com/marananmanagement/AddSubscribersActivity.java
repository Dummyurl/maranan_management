package com.marananmanagement;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;

public class AddSubscribersActivity extends Activity implements OnClickListener {
	private static AddSubscribersActivity mContext;
	private EditText edt_sub_first_name, edt_sub_last_name, edt_sub_no,
			edt_sub_email;
	private ImageView img_record_first_name, img_record_last_name,
			img_record_no, img_record_email;
	private TextView tv_ids;
	private Spinner spnr_code;
	private Button btn_add_no, btn_add_email, btn_floppy_save;
	String[] array_CountryCode;
	private ViewGroup mContainerView, mContainerView2;
	private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
	private final int REQ_CODE_SPEECH_INPUT_TWO = 200;
	private final int REQ_CODE_SPEECH_INPUT_THREE = 300;
	private final int REQ_CODE_SPEECH_INPUT_FOUR = 400;
	private int pos = 0;
	private int pos2 = 0;
	private Boolean mobileOne = false;
	private Boolean mobileTwo = false;
	private Boolean mobileThree = false;
	private Boolean mobileFour = false;
	private Boolean emailOne = false;
	private Boolean emailTwo = false;
	public String mobile1, mobile2, mobile3, mobile4;
	public String code1, code2, code3, code4;
	public String email1, email2, city;
	private ProgressBar pDialog;

	// AddSubscribersActivity Instance
	public static AddSubscribersActivity getInstance() {
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
		setContentView(R.layout.add_subscriber);
		mContext = AddSubscribersActivity.this;
		initializeView();
	}

	// initialize View Here...
	private void initializeView() {
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		tv_ids = (TextView) findViewById(R.id.tv_ids);
		tv_ids.setText(getIntent().getStringExtra("id"));
		edt_sub_first_name = (EditText) findViewById(R.id.edt_sub_first_name);
		edt_sub_first_name.setText(getIntent().getStringExtra("firstname"));
		edt_sub_last_name = (EditText) findViewById(R.id.edt_sub_last_name);
		edt_sub_last_name.setText(getIntent().getStringExtra("family"));
		edt_sub_email = (EditText) findViewById(R.id.edt_sub_email);
		img_record_first_name = (ImageView) findViewById(R.id.img_record_first_name);
		img_record_first_name.setOnClickListener(this);
		img_record_last_name = (ImageView) findViewById(R.id.img_record_last_name);
		img_record_last_name.setOnClickListener(this);
		btn_add_no = (Button) findViewById(R.id.btn_add_no);
		btn_add_no.setOnClickListener(this);
		btn_add_email = (Button) findViewById(R.id.btn_add_email);
		btn_add_email.setOnClickListener(this);
		btn_floppy_save = (Button) findViewById(R.id.btn_floppy_save);
		btn_floppy_save.setOnClickListener(this);
		array_CountryCode = getResources().getStringArray(R.array.country_codes);
		mContainerView = (ViewGroup) findViewById(R.id.container);
		mContainerView2 = (ViewGroup) findViewById(R.id.container2);
		//addItem(pos);
		//addItem2(pos2);
		setIntentValueForMobileNo();
		setIntentValueForEmail();
	}
	
	// Set Intent Values For Mobile Number...
	private void setIntentValueForMobileNo() {
		if (getIntent().getStringExtra("intent") != null) {
			if (getIntent().getStringExtra("intent").equals("intent_values")) {

					if (!getIntent().getStringExtra("mobileno1").equals("")) {
						mobileOne = true;
						addItem(pos);
					
					}
					
					if (!getIntent().getStringExtra("mobileno2").equals("")) {
						mobileTwo = true;
						
					if (mobileOne == true) {

						addItem(pos + 1);

					} else {

						addItem(pos);
					}
					
					}
					
					if (!getIntent().getStringExtra("mobileno3").equals("")) {
						mobileThree = true;
						if (mobileTwo == true) {

							addItem(pos + 1);

						} else {

							addItem(pos);
						}
					
					}
					
					if (!getIntent().getStringExtra("mobileno4").equals("")) {
						mobileFour = true;
						if (mobileThree == true) {

							addItem(pos + 1);

						} else {

							addItem(pos);
						}
					
					}
					
					if (mobileOne == false && mobileTwo == false && mobileThree == false && mobileFour == false) {
						addItem(pos);
					}
				
			} else {
				addItem(pos);
			}

		} else {
			addItem(pos);
		}

	}
	
	// Set Intent Values For Email...
	private void setIntentValueForEmail() {
		if (getIntent().getStringExtra("intent") != null) {
			if (getIntent().getStringExtra("intent").equals("intent_values")) {

				
					if (!getIntent().getStringExtra("landline1").equals("")) {
						emailOne = true;
						addItem2(pos2);
					
					}
					
					if (!getIntent().getStringExtra("landline2").equals("")) {
						emailTwo = true;
						if (emailOne == true) {
							
							addItem2(pos2 + 1);
						
						}else{
							
							addItem2(pos2);
						}
					}
					
					if (emailOne == false && emailTwo == false) {
						addItem2(pos2);
					}
			} else {
				addItem2(pos2);
			}

		} else {
			addItem2(pos2);
		}

	}

	// Onclick ListenerInitialize here...
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.img_record_first_name:
			promptSpeechInput("first_name");
			break;

		case R.id.img_record_last_name:
			promptSpeechInput("last_name");
			break;

		case R.id.img_record_no:
			promptSpeechInput("no");
			break;

		case R.id.img_record_email:
			promptSpeechInput("email");
			break;

		case R.id.btn_add_no:
			if (pos < 3) {
				addItem(pos+1);
			}else{
				Utilities.showToast(this, "You did not enter more than 4 numbers");
			}
		
			break;
		
		case R.id.btn_add_email:
			if (pos2 < 1) {
				addItem2(pos2+1);
			}else{
				Utilities.showToast(this, "You did not enter more than 2 email");
			}
			
			break;
			
		case R.id.btn_floppy_save:
			if (edt_sub_first_name.getText().length() == 0) {
				
				Utilities.showToast(this, "Please Enter First Name");
			
			}else if(edt_sub_last_name.getText().length() == 0){
				
				Utilities.showToast(this, "Please Enter Family Name");
			
			}else{
				
				getValuesForFirstViewGroup();
				
				/* Send Radio Programs To Serve */
//				AsyncRequest.addSubscribers(this, this.getClass()
//						.getSimpleName(), getIntent().getStringExtra("id"),
//						edt_sub_first_name.getText().toString(),
//						edt_sub_last_name.getText().toString(), mobile1,
//						mobile2, mobile3, mobile4, email1, email2, city, code1,
//						code2, code3, code4, pDialog);
			}
			break;

		default:
			break;
		}

	}
	
	// Get Values For First View Group...
	private void getValuesForFirstViewGroup() {
		int count = mContainerView.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mContainerView.getChildAt(i);
			if (view instanceof RelativeLayout) {
				ViewGroup view1 = ((ViewGroup) view);
				for (int j = 0; j < view1.getChildCount(); j++) {
					if (view1.getChildAt(j) instanceof EditText) {
						// you got the edit text...
						EditText s = (EditText) view1.getChildAt(j);
						if (s.getText().length() != 0) {
							
							if (i == 0) {
								mobile1 = s.getText().toString();
							
							}else if(i == 1){
								
								mobile2 = s.getText().toString();
							
							}else if(i == 2){
								
								mobile3 = s.getText().toString();
							
							}else if(i == 3){
								mobile4 = s.getText().toString();
							}
							
							getValuesForSecondViewGroup();

						} else {
							Utilities.showToast(this, "Please Enter Number");
						}
					}

					if (view1.getChildAt(j) instanceof Spinner) {
						// you got the spinner here...
						final Spinner spnr = (Spinner) view1.getChildAt(j);
						
						if (i == 0) {
							code1 = spnr.getSelectedItem().toString();
						
						}else if(i == 1){
							
							code2 = spnr.getSelectedItem().toString();
						
						}else if(i == 2){
							
							code3 = spnr.getSelectedItem().toString();
						
						}else if(i == 3){
							code4 = spnr.getSelectedItem().toString();
						}
					}
				}

			}

		}

	}
	
	// Get Values For Second View Group...
	private void getValuesForSecondViewGroup() {
		int count = mContainerView2.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mContainerView2.getChildAt(i);
			if (view instanceof RelativeLayout) {
				ViewGroup view1 = ((ViewGroup) view);
				for (int j = 0; j < view1.getChildCount(); j++) {
					if (view1.getChildAt(j) instanceof EditText) {
						// you got the edit text...
						EditText s = (EditText) view1.getChildAt(j);
						if (s.getText().length() != 0) {
						
							if (i == 0) {
								
								email1 = s.getText().toString();
							
							}else if(i == 1){
								
								email2 = s.getText().toString();
							
							}
							

						} else {
							Utilities.showToast(this, "Please Enter Email");
						}
					}
				}

			}

		}

	}

	// Add View For Number...
	private void addItem(int pos) {
		final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.list_item_edt_one, mContainerView, false);
		
		img_record_no = (ImageView) newView.findViewById(R.id.img_record_no);
		spnr_code = (Spinner) newView.findViewById(R.id.spnr_code);
		edt_sub_no = (EditText) newView.findViewById(R.id.edt_sub_no);
		
		if (getIntent().getStringExtra("intent") != null) {
			if (getIntent().getStringExtra("intent").equals("intent_values")) {
				if (mobileOne == true) {
					
					edt_sub_no.setText(getIntent().getStringExtra("mobileno1"));
					setSpinnerAdapter();
				
				} 

				if (mobileTwo == true) {
					
					edt_sub_no.setText(getIntent().getStringExtra("mobileno2"));
					setSpinnerAdapter();
				}

				if (mobileThree == true) {
					
					edt_sub_no.setText(getIntent().getStringExtra("mobileno3"));
					setSpinnerAdapter();
				} 

				if (mobileFour == true) {
				
					edt_sub_no.setText(getIntent().getStringExtra("mobileno4"));
					setSpinnerAdapter();
				} 
				
			}else{
				
				edt_sub_no.setText("");
				setSpinnerAdapter();
			}
		}
		else{
			
			edt_sub_no.setText("");
			setSpinnerAdapter();
		}
		
		img_record_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput("no");
			}
		});

		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		this.pos = pos;
		mContainerView.addView(newView, pos);
	}
	
	// Add View For Email...
	private void addItem2(int pos) {
		// Instantiate a new "row" view.
		final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.list_item_edt2, mContainerView2, false);

		edt_sub_email = (EditText) newView.findViewById(R.id.edt_sub_email);
		img_record_email = (ImageView) newView.findViewById(R.id.img_record_email);

		if (getIntent().getStringExtra("intent") != null) {
			if (getIntent().getStringExtra("intent").equals("intent_values")) {
				
				if (emailOne == true) {
					
					edt_sub_email.setText(getIntent().getStringExtra("landline1"));

				} 

				if (emailTwo == true) {
					
					edt_sub_email.setText(getIntent().getStringExtra("landline2"));

				} 

			} else {
				
				edt_sub_email.setText("");
			}
		} else {
			
			edt_sub_email.setText("");
		}

		img_record_email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput("email");
			}
		});

		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		this.pos2 = pos;
		mContainerView2.addView(newView, pos);
	}

	// Showing google speech input dialog
	private void promptSpeechInput(String value) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));

		if (value.equals("first_name")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ONE);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));

			}
		} else if (value.equals("last_name")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_TWO);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));

			}
		} else if (value.equals("no")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_THREE);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));

			}
		} else if (value.equals("email")) {
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_FOUR);
			} catch (ActivityNotFoundException a) {
				Utilities.showToast(this, getString(R.string.speech_not_supported));

			}
		}
	}

	// OnActivityResult Declare Here...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && null != data
				&& requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			edt_sub_first_name.setText(result.get(0));

		} else if (resultCode == RESULT_OK && null != data
				&& requestCode == REQ_CODE_SPEECH_INPUT_TWO) {

			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			edt_sub_last_name.setText(result.get(0));

		} else if (resultCode == RESULT_OK && null != data
				&& requestCode == REQ_CODE_SPEECH_INPUT_THREE) {

			ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			edt_sub_no.setText(result.get(0));

		} else if (resultCode == RESULT_OK && null != data
				&& requestCode == REQ_CODE_SPEECH_INPUT_FOUR) {

			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			edt_sub_email.setText(result.get(0));

		}

	}
    
	// Set Spinner Adpater For Country Code...
	private void setSpinnerAdapter() {
		ArrayAdapter<String> aryadapter_Code = new ArrayAdapter<String>(
				AddSubscribersActivity.this, R.layout.spinner_new_back, array_CountryCode);
		aryadapter_Code.setDropDownViewResource(android.R.layout.simple_list_item_1);
		spnr_code.setAdapter(aryadapter_Code);
	}
	
	/* Set Response Results */
	public void setResponseResult(String result) {
		if (result.length() > 0) {
			try {
				JSONObject json = new JSONObject(result);
				if (json.get("success").equals(true)) {
					Utilities.showToast(this, json.get("value").toString());
					Intent mintent = new Intent();
					setResult(Activity.RESULT_OK, mintent);
					finish();
					overridePendingTransition(
							R.anim.activity_open_scale,
							R.anim.activity_close_translate);
				} else {
					Utilities.showToast(this, json.get("value").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		// mintent.putExtra("sub_id", SmsAdapter.getInstance().getSubIds());
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);

		super.onBackPressed();
	}

	

}
