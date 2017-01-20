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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.NewsLetterListAdapter;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;

public class NewsLetterList extends Activity implements OnClickListener{
	private static NewsLetterList mContext;
	private SwipeListView lv_radio_lectures;
	private ImageButton img_pencil_icon;
	public static int REQUEST_CODE_NEWSLETTER_UPLOAD = 200;
	private NewsLetterListAdapter adapter;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private ProgressBar pDialog;
	private ArrayList<GetterSetter> listNewsLetter;
	private int count = 0;
	@SuppressWarnings("unused")
	private int pos;
	private String date = "";

	// RadioPrograms Instance
	public static NewsLetterList getInstance() {
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
		mContext = NewsLetterList.this;
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
						lv_radio_lectures.closeAnimate(position);
						Intent newsIntent = new Intent(NewsLetterList.this, NewsLetterUpload.class);
						newsIntent.putExtra("id", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getId());
						newsIntent.putExtra("title", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getTitle());
						newsIntent.putExtra("image", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getImage());
						newsIntent.putExtra("pdf", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getRadio_programs());
						newsIntent.putExtra("time", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getTime());
						newsIntent.putExtra("date", NewsLetterListAdapter.getInstance().getNewArrayListNewsLetter().get(position).getDate());
						startActivityForResult(newsIntent, REQUEST_CODE_NEWSLETTER_UPLOAD);
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
			date = getIntent().getStringExtra("date");
			setdate(date);
			if (!date.equals("")) {
				new GetAllNewsLetter().execute(getDate());
			}else{
				new GetAllNewsLetter().execute(getDate());
			}

		} else {
			Utilities.showAlertDialog(NewsLetterList.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_pencil_icon:
			
			Intent newsIntent = new Intent(NewsLetterList.this, NewsLetterUpload.class);
			startActivityForResult(newsIntent, REQUEST_CODE_NEWSLETTER_UPLOAD);
			
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
			if (requestCode == REQUEST_CODE_NEWSLETTER_UPLOAD) {
				if (isInternetPresent) {

					if (!getDate().equals("")) {
						new GetAllNewsLetter().execute(getDate());
					}else{
						new GetAllNewsLetter().execute(getDate());
					}

				} else {
					Utilities.showAlertDialog(NewsLetterList.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
			}
		}
	}

	// Get All Dedications for Administrator who control all the data over
	// server
	public class GetAllNewsLetter extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject jObj = null;
			String url = "";
			HttpParams params2 = new BasicHttpParams();
			params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
			
			if (!params[0].equals("")) {
				url = Config.ROOT_SERVER_CLIENT + Config.GET_NEWSLETTERS_BY_YEAR;
			}else{
				url = Config.ROOT_SERVER_CLIENT + Config.GET_NEWSLETTERS;
			}
			
			listNewsLetter = new ArrayList<GetterSetter>();
			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("date", new StringBody(params[0]));
				httppost.setEntity(multipartEntity);
				HttpResponse response = mHttpClient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				String strResponse = EntityUtils.toString(r_entity);
				try {
					JSONObject json = new JSONObject(strResponse);
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

						if (!jsonObj.getString("image").toString().equals("")) {
							getset.setImage(jsonObj.getString("image"));
						} else {
							getset.setImage("");
						}

						if (!jsonObj.getString("pdf").toString().equals("")) {
							getset.setPdf(jsonObj.getString("pdf"));
						} else {
							getset.setPdf("");
						}
						
						if (!jsonObj.getString("pdf_pages").toString().equals("")) {
							getset.setPdf_pages(jsonObj.getString("pdf_pages"));
						} else {
							getset.setPdf_pages("");
						}
						
						if (!jsonObj.getString("image_thumb").toString().equals("")) {
							getset.setImage_thumb(jsonObj.getString("image_thumb"));
						} else {
							getset.setImage_thumb("");
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
						
						if (!jsonObj.getString("publish_status").toString().equals("")) {
							if (jsonObj.getString("publish_status").equals("true")) {
								count++;
								getset.setCount(count);
								getset.setImgCheckUncheckRes(R.drawable.tick_icon);
							
							}else{
								getset.setImgCheckUncheckRes(R.drawable.tick_gray);
							}
						}
						
						if (!jsonObj.getString("publish_notification").toString().equals("")) {
							if (jsonObj.getString("publish_notification").equals("true")) {
							
								getset.setImgBroadCastRes(R.drawable.broadcast_sms);
							
							}else{
								
								getset.setImgBroadCastRes(R.drawable.broadcast_sms_gray);
							}
						} 
						
						getset.setImgCancelRes(R.drawable.cross_gray);
						listNewsLetter.add(getset);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			
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
			if(listNewsLetter != null) 
			if (listNewsLetter.size() > 0) {
				count = 0;
				adapter = new NewsLetterListAdapter(NewsLetterList.this, listNewsLetter);
				lv_radio_lectures.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			
			} else {
				Utilities.showToast(NewsLetterList.this, "NewsLetters Not Found");
			}
		}
	}

	// Declare Delete Program...
	class DeleteNewsLetter extends AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_NEWSLETTER;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
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
		}
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	// Call DeleteProgram Class here in the adapter...
	public void getClassDelete(int position) {
		new DeleteNewsLetter().execute(NewsLetterListAdapter.getInstance().getArrayList().get(position).getId());
	}

	// Get Swipe List View
	public SwipeListView getSwipeList() {
		return lv_radio_lectures;

	}
	
	// Get Adapter To Refresh List View When Delete Item...
	public void getRefreshAdapter(){
		adapter.notifyDataSetChanged();
	}
    
	// onBackPress 
	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();
	}
	
	public String getDate(){
		return date;
	}
	
	public void setdate(String date){
		this.date= date;
	}
	
}
