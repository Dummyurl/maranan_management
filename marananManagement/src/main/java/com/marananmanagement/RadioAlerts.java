package com.marananmanagement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.adapter.SmsAdapter;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

public class RadioAlerts extends Activity implements OnClickListener {
	private static RadioAlerts mContext;
	private DynamicListView list_RadioAlerts;
	private TextView tv_title;
	private ImageView img_edit_sms;
	private SmsAdapter adapter;
	private ArrayList<GetterSetter> listAlerts;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private ProgressBar pDialog;
	private int REQUEST_CODE = 100;

	// RadioAlerts Instance
	public static RadioAlerts getInstance() {
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
		setContentView(R.layout.activity_sms);
		mContext = RadioAlerts.this;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		list_RadioAlerts = (DynamicListView) findViewById(R.id.list_sms);
		list_RadioAlerts.setChoiceMode(DynamicListView.CHOICE_MODE_SINGLE);
		tv_title = (TextView) findViewById(R.id.tv_list);
		img_edit_sms = (ImageView) findViewById(R.id.img_edit_sms);
		img_edit_sms.setOnClickListener(this);
		list_RadioAlerts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent sub = new Intent(RadioAlerts.this, AddRadioAlarmActivity.class);
				sub.putExtra("id", listAlerts.get(position).getId());
				sub.putExtra("title", listAlerts.get(position).getTitle());
				sub.putExtra("description", listAlerts.get(position).getDescriptions());
				sub.putExtra("image", getIntent().getStringExtra("image"));
				startActivityForResult(sub, REQUEST_CODE);
			}
		});

		if (getIntent().getStringExtra("activity_name").equals("RadioProgram")) {

			tv_title.setText(getResources().getString(R.string.radio_alerts));

		} else if (getIntent().getStringExtra("activity_name").equals("NewsLetter")) {

			tv_title.setText(getResources().getString(R.string.newsletter_alerts));

		}else if(getIntent().getStringExtra("activity_name").equals( "ChannelPlaylist")){
			
			tv_title.setText(getResources().getString(R.string.video_alerts));
		
		}else if(getIntent().getStringExtra("activity_name").equals( "ChannelManagement")){
			
			tv_title.setText(getResources().getString(R.string.channel_alerts));
		}

		// Swipe to dismiss list view item
		list_RadioAlerts.enableSwipeToDismiss(new OnDismissCallback() {
			@Override
			public void onDismiss(final ViewGroup listView,
					final int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {

					if (listAlerts.size() > 0)
						new DeleteRadioAlert().execute(listAlerts.get(position)
								.getId());
					listAlerts.remove(String.valueOf(position));
					listAlerts.remove(position);
					adapter.notifyDataSetChanged();
				}
			}
		});

		if (isInternetPresent) {

			new GetRadioAlerts().execute();

		} else {
			Utilities.showAlertDialog(RadioAlerts.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
		}
	}

	// Get Radio Alerts...
	public class GetRadioAlerts extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jObj = null;
			listAlerts = new ArrayList<GetterSetter>();
			ServiceHandler sd = new ServiceHandler();
			String responce = sd.makeServiceCall(Config.ROOT_SERVER_CLIENT
					+ Config.GET_RADIO_ALERT, ServiceHandler.GET);
			if (responce != null) {
				try {
					JSONObject json = new JSONObject(responce);
					JSONArray jArry = json.getJSONArray("value");
					for (int i = 0; i < jArry.length(); i++) {
						JSONObject jsonObj = jArry.getJSONObject(i);
						GetterSetter getset = new GetterSetter();
						getset.setId(jsonObj.getString("id"));
						getset.setTitle(jsonObj.getString("title"));
						getset.setDescriptions(jsonObj.getString("description"));
						getset.setImage(jsonObj.getString("image"));
						listAlerts.add(getset);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Utilities.showToast(RadioAlerts.this,
								"Server Not Responding");
					}
				});

			}

			return jObj;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.setVisibility(View.GONE);
			if (listAlerts.size() > 0) {
				adapter = new SmsAdapter(RadioAlerts.this, listAlerts,
						"RadioAlerts", pDialog);
				list_RadioAlerts.setAdapter(adapter);
				
			}
		}
	}

	// Declare Delete SMS Subscribers...
	class DeleteRadioAlert extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject jObj = null;
			HttpParams params2 = new BasicHttpParams();
			params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
			String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_RADIO_ALERT;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				httppost.setEntity(multipartEntity);
				HttpResponse response = mHttpClient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				String strResponse = EntityUtils.toString(r_entity);
				jObj = new JSONObject(strResponse);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jObj;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					if (result.get("success").equals(true)) {
						Utilities.showToast(RadioAlerts.this,
								getIntent().getStringExtra("activity_name")+" "+getResources().getString(R.string.alert_delete));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_edit_sms:
			// showCustomDialog();
			Intent sub = new Intent(RadioAlerts.this, AddRadioAlarmActivity.class);
			sub.putExtra("image", getIntent().getStringExtra("image"));
			startActivityForResult(sub, REQUEST_CODE);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				if (isInternetPresent) {
					if (listAlerts != null)
						if (listAlerts.size() > 0) {
							listAlerts.clear();
						}
					new GetRadioAlerts().execute();

				} else {
					Utilities.showAlertDialog(RadioAlerts.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}

			}
		}
	}

	// Get Notification Broadcast id
	public String getNotiId() {
		return getIntent().getStringExtra("id");
	}
	
	// Get Video ID
	public String getVideoID() {
		return getIntent().getStringExtra("video_id");
	}
	
	// Get Activity Name...
	public String getActivityName() {
		return getIntent().getStringExtra("activity_name");
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
