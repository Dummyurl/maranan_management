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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.RadioLectureAdapter;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;

public class RadioPrograms extends Activity implements OnClickListener {
	private static RadioPrograms mContext;
	private SwipeListView lv_radio_lectures;
	private ImageButton img_pencil_icon;
	public static int REQUEST_CODE = 100;
	private RadioLectureAdapter adapter;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private ProgressBar pDialog;
	private ArrayList<GetterSetter> listRadioPrograms;
	private int count = 0;
	@SuppressWarnings("unused")
	private int pos;

	// RadioPrograms Instance
	public static RadioPrograms getInstance() {
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
		setContentView(R.layout.radio_lecture_list);
		mContext = RadioPrograms.this;
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		initializeView();
	}

	// initialize View Here...
	private void initializeView() {
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		lv_radio_lectures = (SwipeListView) findViewById(R.id.lv_radio_lectures);
		lv_radio_lectures.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		lv_radio_lectures.setOffsetRight(convertDpToPixel(80f));
		lv_radio_lectures.setAnimationTime(500);
		lv_radio_lectures.setSwipeOpenOnLongPress(false);

		img_pencil_icon = (ImageButton) findViewById(R.id.img_pencil_icon);
		img_pencil_icon.setOnClickListener(this);

		lv_radio_lectures.setSwipeListViewListener(new BaseSwipeListViewListener() {
					@Override
					public void onOpened(int position, boolean toRight) {
					pos = position;
					}

					@Override
					public void onClosed(int position, boolean fromRight) {
						pos = position;
					}

					@Override
					public void onListChanged() {
					}

					@Override
					public void onMove(int position, float x) {
					}

					@Override
					public void onStartOpen(int position, int action,
							boolean right) {
						pos = position;
					}

					@Override
					public void onStartClose(int position, boolean right) {
						pos = position;
					}

					@Override
					public void onClickFrontView(int position) {

						// swipelistview.openAnimate(position); //when you touch
						// front view it will open
						if (RadioLectureAdapter.getInstance().getMdPlayer() != null) {
							if (RadioLectureAdapter.getInstance().getMdPlayer().isPlaying()) {
								RadioLectureAdapter.getInstance().getMdPlayer().stop();
								RadioLectureAdapter.getInstance().getMdPlayer().release();
							}
						}
						if (listRadioPrograms != null)
							if (listRadioPrograms.size() > 0) {
								RadioLectureAdapter.getInstance().getRefreshUi();
							}
						pos = position;
						lv_radio_lectures.closeAnimate(position);
						Intent radio = new Intent(RadioPrograms.this, RadioProgramUpload.class);
						radio.putExtra("id", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getId());
						radio.putExtra("title", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getTitle());
						radio.putExtra("description", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getDescriptions());
						radio.putExtra("image", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getImage());
						radio.putExtra("radio_program", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getRadio_programs());
						radio.putExtra("time", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getTime());
						radio.putExtra("date", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getDate());
						radio.putExtra("status", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getStatus());
						radio.putExtra("duration", RadioLectureAdapter.getInstance().getNewArrayList().get(position).getDuration());
						radio.putExtra("size", listRadioPrograms.size());
						startActivityForResult(radio, REQUEST_CODE);
					}

					@Override
					public void onClickBackView(int position) {

						lv_radio_lectures.closeAnimate(position);
					}

					@Override
					public void onDismiss(int[] reverseSortedPositions) {

					}

				});

		if (isInternetPresent) {

			new GetAllRadioPrograms().execute();

		} else {
			Utilities.showAlertDialog(RadioPrograms.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_pencil_icon:
			//lv_radio_lectures.closeAnimate(pos);
			if (RadioLectureAdapter.getInstance().getMdPlayer() != null) {
				if (RadioLectureAdapter.getInstance().getMdPlayer().isPlaying()) {
					RadioLectureAdapter.getInstance().getMdPlayer().stop();
					RadioLectureAdapter.getInstance().getMdPlayer().release();
				}
			}
			
			if (listRadioPrograms != null)
				if (listRadioPrograms.size() > 0) {
					RadioLectureAdapter.getInstance().getRefreshUi();
				}
			
			Intent radio = new Intent(RadioPrograms.this, RadioProgramUpload.class);
			if (listRadioPrograms != null) 
			if (listRadioPrograms.size() > 0) {
				radio.putExtra("size", listRadioPrograms.size());
			}
			startActivityForResult(radio, REQUEST_CODE);
			break;

		default:
			break;
		}

	}

	// OnActivity Result To Get Result From Radio ProgramUpload Activity and
	// Refresh Here With New values...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				if (isInternetPresent) {

					new GetAllRadioPrograms().execute();

				} else {
					Utilities.showAlertDialog(RadioPrograms.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
			}
		}
	}

	// Get All Dedications for Administrator who control all the data over
	// server
	public class GetAllRadioPrograms extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jObj = null;
			listRadioPrograms = new ArrayList<GetterSetter>();
			ServiceHandler sd = new ServiceHandler();
			String responce = sd.makeServiceCall(Config.ROOT_SERVER_CLIENT + Config.GET_RADIO_PROGRAMS, ServiceHandler.GET);
			try {
				JSONObject json = new JSONObject(responce);
				JSONArray jArry = json.getJSONArray("value");
				for (int i = 0; i < jArry.length(); i++) {
					JSONObject jsonObj = jArry.getJSONObject(i);
					GetterSetter getset = new GetterSetter();
					getset.setId(jsonObj.getString("id"));

					if (!jsonObj.getString("title").toString().equals("")) {
						getset.setTitle((jsonObj.getString("title")));
					} else {
						getset.setTitle("");
					}

					if (!jsonObj.getString("description").toString().equals("")) {
						getset.setDescriptions((jsonObj.getString("description")));
					} else {
						getset.setDescriptions("");
					}

					if (!jsonObj.getString("image").toString().equals("")) {
						getset.setImage(jsonObj.getString("image"));

					} else {
						getset.setImage("");
					}

					if (!jsonObj.getString("radio_program").toString().equals("")) {
						getset.setRadio_programs(jsonObj.getString("radio_program"));
					} else {
						getset.setRadio_programs("");
					}

					if (!jsonObj.getString("duration").toString().equals("")) {
						getset.setDuration(jsonObj.getString("duration"));
					} else {
						getset.setDuration("");
					}

					if (!jsonObj.getString("time").toString().equals("")) {
						getset.setTime(jsonObj.getString("time"));
					} else {
						getset.setTime("");
					}

					if (!jsonObj.getString("date").toString().equals("")) {
						getset.setDate(jsonObj.getString("date"));
					} else {
						getset.setDate("");
					}

					if (!jsonObj.getString("status").toString().equals("")) {
						getset.setStatus(jsonObj.getString("status"));
					} else {
						getset.setStatus("");
					}
					
					if (!jsonObj.getString("publish_status").toString().equals("")) {
						getset.setPublish_status(jsonObj.getString("publish_status"));
					} else {
						getset.setPublish_status("");
					}
					
					if (!jsonObj.getString("publish_notification").toString().equals("")) {
						getset.setPublish_notification(jsonObj.getString("publish_notification"));
					} else {
						getset.setPublish_notification("");
					}

					if (!jsonObj.getString("duration").toString().equals("")) {
						getset.setDuration(jsonObj.getString("duration"));
					} else {
						getset.setDuration("");
					}
					
					getset.setImageResource(R.drawable.play_arrow_white);
					
					if (!jsonObj.getString("publish_status").toString().equals("")) {
						if (jsonObj.getString("publish_status").equals("true")) {
							count++;
							getset.setCount(count);	
						}
					} 
					
					listRadioPrograms.add(getset);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return jObj;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.setVisibility(View.GONE);
			if(listRadioPrograms != null) 
			if (listRadioPrograms.size() > 0) {
				count = 0;
				adapter = new RadioLectureAdapter(RadioPrograms.this, listRadioPrograms);
				lv_radio_lectures.setAdapter(adapter);
			} else {
				Utilities.showToast(RadioPrograms.this, "No Programs Found");
			}
		}
	}

	// Declare Delete Program...
	class DeleteProgram extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject jObj = null;
			HttpParams params2 = new BasicHttpParams();
			params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
			String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_PROGRAM;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("program_id", new StringBody(params[0]));
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
		}
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	// Call DeleteProgram Class here in the adapter...
	public void getClassDelete(int position) {
		new DeleteProgram().execute(RadioLectureAdapter.getInstance().getArrayList().get(position).getId());
	}

	// Get Swipe List View
	public SwipeListView getSwipeList() {
		return lv_radio_lectures;

	}
	
	// Get Adapter To Refresh List View When Delete Item...
	public void getRefreshAdapter(){
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		if (RadioLectureAdapter.getInstance().getMdPlayer() != null) {
			if (RadioLectureAdapter.getInstance().getMdPlayer().isPlaying()) {
				RadioLectureAdapter.getInstance().getMdPlayer().stop();
				RadioLectureAdapter.getInstance().getMdPlayer().release();
				getRefreshAdapter();
			}
		}
		
		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		try {
			if (((ApplicationSingleton) getApplicationContext()).player != null) {
				if (((ApplicationSingleton) getApplicationContext()).player
						.isPlaying()) {
				}
			}

			if (Utilities.getCountDownTimer() != null) {
				Utilities.getCountDownTimer().cancel();
			}
			finish();

		} catch (Exception e) {
			if (Utilities.getCountDownTimer() != null) {
				Utilities.getCountDownTimer().cancel();
			}
		}
		super.onDestroy();
		// if (RadioLectureAdapter.getInstance().getMdPlayer() != null) {
		// RadioLectureAdapter.getInstance().getMdPlayer().release();
		// }
	}
	
	
}
