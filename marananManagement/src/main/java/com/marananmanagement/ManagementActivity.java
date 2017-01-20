package com.marananmanagement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.util.Config;
import com.marananmanagement.util.Utilities;

public class ManagementActivity extends Activity implements OnClickListener {
	Button btn_first_name, btn_thereIs, btn_last_name, btn_submit;
	TextView title, tv_first_name, tv_thereIs, tv_last_name, tv_time, tv_email;
	ProgressBar pDialog;
	String publish;
	boolean isButtonVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
		// Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
		setContentView(R.layout.activity_management);
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		btn_first_name = (Button) findViewById(R.id.btn_first_name);
		btn_first_name.setTag(R.drawable.gray_new);
		btn_first_name.setOnClickListener(this);
		btn_thereIs = (Button) findViewById(R.id.btn_thereIs);
		btn_thereIs.setTag(R.drawable.gray_new);
		btn_thereIs.setOnClickListener(this);
		btn_last_name = (Button) findViewById(R.id.btn_last_name);
		btn_last_name.setTag(R.drawable.gray_new);
		btn_last_name.setOnClickListener(this);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("Dedication Received" + " " + getIntent().getStringExtra("size"));
		tv_first_name = (TextView) findViewById(R.id.tv_first_name);
		tv_first_name.setText(getIntent().getStringExtra("first_name"));
		tv_thereIs = (TextView) findViewById(R.id.tv_thereIs);
		tv_thereIs.setText(getIntent().getStringExtra("there_Is"));
		tv_last_name = (TextView) findViewById(R.id.tv_last_name);
		tv_last_name.setText(getIntent().getStringExtra("last_name"));
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setText(getIntent().getStringExtra("time"));
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_email.setText(getIntent().getStringExtra("email"));

		if (getIntent().getStringExtra("last_name").equals("")) {
			btn_last_name.setVisibility(View.GONE);
			isButtonVisible = true;
		}
       
		// OLD CHECK FOR GETTING EXISTING VALUES AND HANDLE BUTTON ACCORDINGLY
		/*// Get intent value to handle buttons drawables
		if (!getIntent().getStringExtra("f_status").equals("")) {
			if (getIntent().getStringExtra("f_status").equals("f_accept")) {
				btn_first_name.setBackgroundResource(R.drawable.right);
				btn_first_name.setTag(R.drawable.right);
				btn_first_name.setClickable(false);

			} else if (getIntent().getStringExtra("f_status")
					.equals("f_reject")) {
				btn_first_name.setBackgroundResource(R.drawable.cross);
				btn_first_name.setTag(R.drawable.cross);
				btn_first_name.setClickable(false);
			}
		} else {
			btn_first_name.setBackgroundResource(R.drawable.gray_new);
			btn_first_name.setTag(R.drawable.gray_new);
		}

		if (!getIntent().getStringExtra("l_status").equals("")) {
			if (getIntent().getStringExtra("l_status").equals("l_accept")) {
				btn_last_name.setBackgroundResource(R.drawable.right);
				btn_last_name.setTag(R.drawable.right);
				btn_last_name.setClickable(false);

			} else if (getIntent().getStringExtra("l_status")
					.equals("l_reject")) {
				btn_last_name.setBackgroundResource(R.drawable.cross);
				btn_last_name.setTag(R.drawable.cross);
				btn_last_name.setClickable(false);
			}
		} else {
			btn_last_name.setBackgroundResource(R.drawable.gray_new);
			btn_last_name.setTag(R.drawable.gray_new);
		}
		if (!getIntent().getStringExtra("m_status").equals("")) {
			if (getIntent().getStringExtra("m_status").equals("m_accept")) {
				btn_thereIs.setBackgroundResource(R.drawable.right);
				btn_thereIs.setTag(R.drawable.right);
				btn_thereIs.setClickable(false);

			} else if (getIntent().getStringExtra("m_status")
					.equals("m_reject")) {
				btn_thereIs.setBackgroundResource(R.drawable.cross);
				btn_thereIs.setTag(R.drawable.cross);
				btn_thereIs.setClickable(false);
			}
		} else {
			btn_thereIs.setBackgroundResource(R.drawable.gray_new);
			btn_thereIs.setTag(R.drawable.gray_new);
		}*/
		
		// NEW CHECK FOR GETTING VALUES AND HANDLE BUTTONS ACCORDINGLY
		if (!getIntent().getStringExtra("f_status").equals("")) {
			 
			if (getIntent().getStringExtra("f_status").equals("f_accept") && getIntent().getStringExtra("f_sex_status").equals("f_male")) {
				btn_first_name.setBackgroundResource(R.drawable.male_new);
				btn_first_name.setTag(R.drawable.male_new);
				btn_first_name.setClickable(false);

			} else if (getIntent().getStringExtra("f_status").equals("f_accept") && getIntent().getStringExtra("f_sex_status").equals("f_female")) {
				btn_first_name.setBackgroundResource(R.drawable.female_new);
				btn_first_name.setTag(R.drawable.female_new);
				btn_first_name.setClickable(false);

			}else if (getIntent().getStringExtra("f_status").equals("f_reject")) {
				btn_first_name.setBackgroundResource(R.drawable.cancel_new);
				btn_first_name.setTag(R.drawable.cancel_new);
				btn_first_name.setClickable(false);
			}
		
		} else {
			btn_first_name.setBackgroundResource(R.drawable.gray_new);
			btn_first_name.setTag(R.drawable.gray_new);
		}
		
		if (!getIntent().getStringExtra("m_status").equals("")) {
			
			if (getIntent().getStringExtra("m_status").equals("m_accept") && getIntent().getStringExtra("m_sex_status").equals("m_male")) {
				btn_thereIs.setBackgroundResource(R.drawable.male_new);
				btn_thereIs.setTag(R.drawable.male_new);
				btn_thereIs.setClickable(false);

			} else if (getIntent().getStringExtra("m_status").equals("m_accept") && getIntent().getStringExtra("m_sex_status").equals("m_female")) {
				btn_thereIs.setBackgroundResource(R.drawable.female_new);
				btn_thereIs.setTag(R.drawable.female_new);
				btn_thereIs.setClickable(false);

			}else if (getIntent().getStringExtra("m_status").equals("m_reject")) {
				btn_thereIs.setBackgroundResource(R.drawable.cancel_new);
				btn_thereIs.setTag(R.drawable.cancel_new);
				btn_thereIs.setClickable(false);
			}
		
		} else {
			btn_thereIs.setBackgroundResource(R.drawable.gray_new);
			btn_thereIs.setTag(R.drawable.gray_new);
		}
		
		if (!getIntent().getStringExtra("l_status").equals("")) {
			if (getIntent().getStringExtra("l_status").equals("l_accept")) {
				btn_last_name.setBackgroundResource(R.drawable.lastname_new);
				btn_last_name.setTag(R.drawable.lastname_new);
				btn_last_name.setClickable(false);

			} else if (getIntent().getStringExtra("l_status").equals("l_reject")) {
				btn_last_name.setBackgroundResource(R.drawable.cancel_new);
				btn_last_name.setTag(R.drawable.cancel_new);
				btn_last_name.setClickable(false);
			}
		} else {
			btn_last_name.setBackgroundResource(R.drawable.gray_new);
			btn_last_name.setTag(R.drawable.gray_new);
		}
	}

	// Get All Dedications for Administrator who control all the data over
	// server
	public class SendResponse extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		} 

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jObj = null;
			HttpParams params2 = new BasicHttpParams();
			params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
			HttpPost httppost = new HttpPost(Config.ROOT_SERVER_CLIENT + Config.UPDATE_STATUS);

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				multipartEntity.addPart("publish", new StringBody(params[1]));
				if (!params[2].equals(""))
					multipartEntity.addPart("admin_status", new StringBody(params[2]));
				else
					multipartEntity.addPart("admin_status", new StringBody(""));

				multipartEntity.addPart("name_status", new StringBody(params[3]));
				multipartEntity.addPart("thereis_status", new StringBody(params[4]));
				
				if (!params[5].equals(""))
					multipartEntity.addPart("nameopt_status", new StringBody(params[5]));
				else
					multipartEntity.addPart("nameopt_status", new StringBody(""));
				
				if (!params[6].equals(""))
					multipartEntity.addPart("f_sex_status", new StringBody(params[6]));
				else
					multipartEntity.addPart("f_sex_status", new StringBody(""));
				
				if (!params[7].equals(""))
					multipartEntity.addPart("m_sex_status", new StringBody(params[7]));
				else
					multipartEntity.addPart("m_sex_status", new StringBody(""));
				
				if (!params[8].equals(""))
					multipartEntity.addPart("l_sex_status", new StringBody(params[8]));
				else
					multipartEntity.addPart("l_sex_status", new StringBody(""));
				
				multipartEntity.addPart("time", new StringBody(Utilities.getIsraelTime()));
				multipartEntity.addPart("date", new StringBody(Utilities.getIsraelDate()));
				
				httppost.setEntity(multipartEntity);
				HttpResponse response = mHttpClient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				String strResponse = EntityUtils.toString(r_entity);
				Log.e("Response GET Dedication>> ", ">> " + strResponse);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return jObj;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.setVisibility(View.GONE);
			closeAcitivity();
		}
	}

	// onBackPressed
	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_first_name) {
			if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.gray_new) {
				btn_first_name.setBackgroundResource(R.drawable.male_new);
				btn_first_name.setTag(R.drawable.male_new);

			} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new) {
				btn_first_name.setBackgroundResource(R.drawable.female_new);
				btn_first_name.setTag(R.drawable.female_new);

			} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new) {
				btn_first_name.setBackgroundResource(R.drawable.cancel_new);
				btn_first_name.setTag(R.drawable.cancel_new);
			
			}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new) {
				btn_first_name.setBackgroundResource(R.drawable.gray_new);
				btn_first_name.setTag(R.drawable.gray_new);
			}

		} else if (id == R.id.btn_thereIs) {
			if (Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.gray_new) {
				btn_thereIs.setBackgroundResource(R.drawable.male_new);
				btn_thereIs.setTag(R.drawable.male_new);

			} else if (Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new) {
				btn_thereIs.setBackgroundResource(R.drawable.female_new);
				btn_thereIs.setTag(R.drawable.female_new);

			} else if (Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new) {
				btn_thereIs.setBackgroundResource(R.drawable.cancel_new);
				btn_thereIs.setTag(R.drawable.cancel_new);
			
			} else if (Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new) {
				btn_thereIs.setBackgroundResource(R.drawable.gray_new);
				btn_thereIs.setTag(R.drawable.gray_new);
			}

		} else if (id == R.id.btn_last_name) {
			if (Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.gray_new) {
				btn_last_name.setBackgroundResource(R.drawable.lastname_new);
				btn_last_name.setTag(R.drawable.lastname_new);

			} else if (Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new) {
				btn_last_name.setBackgroundResource(R.drawable.cancel_new);
				btn_last_name.setTag(R.drawable.cancel_new);

			} else if (Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new) {
				btn_last_name.setBackgroundResource(R.drawable.gray_new);
				btn_last_name.setTag(R.drawable.gray_new);
			}

		} else if (id == R.id.btn_submit) {
			if (!isButtonVisible) {

				dedicationWithThreeButtonCheck();

			} else {

				dedicationWithTwoButtonCheck();

			}
		}

	}

	// Dedication Set Using Three Buttons Are Visible
	private void dedicationWithThreeButtonCheck() {
		
		/*// OLD CHECK DEFINE HERE...
		if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.right) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "l_accept");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.right) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "l_accept");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cross) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_reject", "l_reject");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cross) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_accept", "l_reject");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.right) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_accept");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cross) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_reject");

		} else {

			Utilities.showToast(ManagementActivity.this, "Please Select");

		}*/
		
		//NEW CHECK DEFINE HERE...
		if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "l_accept", "f_male", "m_male", "l_group");

		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "l_accept", "f_female", "m_female", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "l_accept", "f_male", "m_female", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "l_accept", "f_female", "m_male", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_accept", "l_reject", "f_male", "m_male", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_accept", "l_reject", "f_female", "m_female", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_accept", "l_reject", "f_male", "m_female", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_accept", "l_reject", "f_female", "m_male", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "l_reject", "f_male", "m_cancel", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "l_reject", "f_female", "m_cancel", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_reject", "f_cancel", "m_male", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_reject", "f_cancel", "m_female", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "l_accept", "f_male", "m_cancel", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "l_accept", "f_female", "m_cancel", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_accept", "f_cancel", "m_male", "l_group");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "l_accept", "f_cancel", "m_female", "l_group");
	
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.cancel_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_reject", "l_reject", "f_cancel", "m_cancel", "l_group_cancel");
		
		}else if(Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_last_name.getTag().toString()) == R.drawable.lastname_new){
			
			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_reject", "l_accept", "f_cancel", "m_cancel", "l_group");
		}else {

			Utilities.showToast(ManagementActivity.this, "Please Select");

		}

	}

	// Dedication Set Using Two Buttons Are Visible
	private void dedicationWithTwoButtonCheck() {
        /*// OLD CHECK WITH TWO BUTTON FOUND
		if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cross) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.right
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cross) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_reject", "");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cross
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.right) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "");

		} else {

			Utilities.showToast(ManagementActivity.this, "Please Select");

		}*/
		
		// NEW CHECK WITH NEW VALUES
		if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "", "f_male", "m_female", "");

		} else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "", "f_male", "m_male", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "", "f_female", "m_male", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"accept", "admin", "f_accept", "m_accept", "", "f_female", "m_female", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.male_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "", "f_male", "m_cancel", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.female_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_accept", "m_reject", "", "f_female", "m_cancel", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.male_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "", "f_cancel", "m_male", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.female_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_accept", "", "f_cancel", "m_female", "");

		}else if (Integer.parseInt(btn_first_name.getTag().toString()) == R.drawable.cancel_new
				&& Integer.parseInt(btn_thereIs.getTag().toString()) == R.drawable.cancel_new) {

			new SendResponse().execute(getIntent().getStringExtra("id"),
					"reject", "admin", "f_reject", "m_reject", "", "f_cancel", "m_cancel", "");

		}
	}

	private void closeAcitivity() {
		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

}
