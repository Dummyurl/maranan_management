package com.marananmanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.adapter.LiveBroadcastImageAdapter;
import com.marananmanagement.interfaces.Constant;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class LiveBroadcastImages extends Activity implements OnClickListener , Constant{
	private static LiveBroadcastImages mContext;
	private ConnectionDetector cd;
	private Boolean isInternetPresent = false;
	private SwipeListView lv_live;
	private ArrayList<GetterSetter> list_live_images;
	private LiveBroadcastImageAdapter adapter;
	private ServiceHandler handle;
	private final int GET = 1;
	public String response;
	private String liveImageName = "";
	public String liveImagePath = "";
	private static final int SELECT_LIVE_IMAGE_GALLERY = 500;
	private static final int SELECT_LIVE_IMAGE_CAMERA = 700;
	private ProgressBar pDialog;
	public String imagestatus = "false";

	// LiveBroadcastImages Instance
	public static LiveBroadcastImages getInstance() {
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
		setContentView(R.layout.live_broadcast_pick_image);
		mContext = LiveBroadcastImages.this;
		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();
		initializeView();

		if (isInternetPresent) {

			// Get Server Images...
			list_live_images = new ArrayList<GetterSetter>();
			new GetLiveBroadcastImages().execute();

		} else {
			Utilities.showAlertDialog(this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		}

	}

	private void initializeView() {
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		TextView tv_gallery = (TextView) findViewById(R.id.tv_gallery);
		tv_gallery.setOnClickListener(this);
		TextView tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_camera.setOnClickListener(this);

		lv_live = (SwipeListView) findViewById(R.id.lv_live_images);
		lv_live.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		lv_live.setOffsetRight(convertDpToPixel(80f));
		lv_live.setAnimationTime(500);
		lv_live.setSwipeOpenOnLongPress(false);
		lv_live.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
			}

			@Override
			public void onStartClose(int position, boolean right) {
			}

			@Override
			public void onClickFrontView(int position) {
				lv_live.closeAnimate(position);
			}

			@Override
			public void onClickBackView(int position) {
				lv_live.closeAnimate(position);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
			}

		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_camera:
			if(!getIntent().getStringExtra("name_class").equals("AlertEditPage")){
				liveImagePath = "";
				liveImageName = "";
				Intent intentCamera = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intentCamera, SELECT_LIVE_IMAGE_CAMERA);

			}else if(getIntent().getStringExtra("name_class").equals("AlertEditPage")){

				Intent intentCamera = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intentCamera, SELECT_LIVE_IMAGE_CAMERA_ALERT_EDIT);
			}

			break;

		case R.id.tv_gallery:
			if(!getIntent().getStringExtra("name_class").equals("AlertEditPage")){
				liveImagePath = "";
				liveImageName = "";

				// Create intent to Open Image applications like Gallery, Google
				// Photos
				Intent galleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				galleryIntent.setType("image/*");
				startActivityForResult(galleryIntent, SELECT_LIVE_IMAGE_GALLERY);

			}else if(getIntent().getStringExtra("name_class").equals("AlertEditPage")){
				// Create intent to Open Image applications like Gallery, Google
				// Photos
				Intent galleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				galleryIntent.setType("image/*");
				startActivityForResult(galleryIntent, SELECT_LIVE_IMAGE_GALLERY_ALERT_EDIT);
			}

			break;

		default:
			break;
		}
	}

	// Get Live Broadcast Images...
	class GetLiveBroadcastImages extends
			AsyncTask<Void, Void, ArrayList<GetterSetter>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<GetterSetter> doInBackground(Void... arg0) {

			handle = new ServiceHandler();
			response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT
					+ Config.GET_LIVE_BROADCAST_IMAGES
					+ Config.PUT_ID+getIntent().getStringExtra("id")
					+ Config.PUT_NAME+getIntent().getStringExtra("name_class"), GET);
			

			if (response != null) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArrayItem = jsonObject.getJSONArray("value");

					if (jsonArrayItem.length() > 0) {

						for (int i = 0; i < jsonArrayItem.length(); i++) {
							JSONObject jsonObj = jsonArrayItem.getJSONObject(i);
							GetterSetter getset = new GetterSetter();
							getset.setImage(jsonObj.getString("images")
									.replaceAll(" ", "%20"));
							
							if(jsonObj.getString("image_status").equals("true")){
								
								getset.setImageResource(R.drawable.tick_icon);
								setImageStatus("true");
							
							}else{
								
								getset.setImageResource(R.drawable.tick_gray);
							}
							
							getset.setImgCancelRes(R.drawable.cross_gray);
							
							list_live_images.add(getset);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Utilities.showAlertDialog(LiveBroadcastImages.this,
								"Internet Connection Error",
								"Your internet connection is too slow...",
								false);
					}
				});
			}
			return list_live_images;
		}

		@Override
		protected void onPostExecute(ArrayList<GetterSetter> result) {
			super.onPostExecute(result);
			pDialog.setVisibility(View.GONE);
			if (result != null) {
				if (result.size() > 0) {
					setAdapterLiveImages(result);
				}
			}
		}
	}

	// Set Adapter For Getting Live Broadcast Images From Server...
	private void setAdapterLiveImages(ArrayList<GetterSetter> result) {
		adapter = new LiveBroadcastImageAdapter(LiveBroadcastImages.this,
				result, getIntent().getStringExtra("image_old"), getIntent().getStringExtra("name_class"));
		lv_live.setAdapter(adapter);
	}

	// Get Swipe List View
	public SwipeListView getSwipeList() {
		return lv_live;
	}

	// Set Live Name Image From Selection In Live Broadcast Image Adapter
	public void setLiveImageName(String liveImageName) {
		liveImagePath = "";
		this.liveImageName = liveImageName;
	}

	// Set Live Name Image Path From Selection In Live Broadcast Image Adapter
	public void setLiveImagePath(String liveImagePath) {
		this.liveImagePath = liveImagePath;
	}
	
	// Set imageStatus
	public void setImageStatus(String imagestatus){
		this.imagestatus = imagestatus;
	}
	



	// onActivityResult Call...
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
            if(!getIntent().getStringExtra("name_class").equals("AlertEditPage")){

				if (resultCode == RESULT_OK && null != data
						&& requestCode == SELECT_LIVE_IMAGE_GALLERY) {
					Uri uri = data.getData();

					// try {

					// Get the file path from the URI
					liveImageName = "";
					// liveImagePath = FileUtils.getPath(this, uri);
					liveImagePath = getPath(uri);
					imagestatus = "true";

					if (liveImagePath != null && !liveImagePath.equals("")) {
						Intent mintent = new Intent();
						mintent.putExtra("imagepaths", liveImagePath);
						mintent.putExtra("imagename", liveImageName);
						mintent.putExtra("imagestatus", imagestatus);
						setResult(RESULT_OK, mintent);
						finish();
						overridePendingTransition(R.anim.activity_open_scale,
								R.anim.activity_close_translate);
					}

					// } catch (Exception e) {
					// Log.e("FileSelectorTestActivity", "File select error", e);
					// }
				} else if (resultCode == RESULT_OK && null != data
						&& requestCode == SELECT_LIVE_IMAGE_CAMERA) {

					Bitmap photo = (Bitmap) data.getExtras().get("data");

					// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
					Uri tempUri = getImageUri(getApplicationContext(), photo);

					// CALL THIS METHOD TO GET THE ACTUAL PATH
					liveImageName = "";
					liveImagePath = getRealPathFromURI(tempUri);
					imagestatus = "true";

					if (!liveImagePath.equals("")) {
						Intent mintent = new Intent();
						mintent.putExtra("imagepaths", liveImagePath);
						mintent.putExtra("imagename", liveImageName);
						mintent.putExtra("imagestatus", imagestatus);
						setResult(RESULT_OK, mintent);
						finish();
						overridePendingTransition(R.anim.activity_open_scale,
								R.anim.activity_close_translate);
					}
				}
			}else if(getIntent().getStringExtra("name_class").equals("AlertEditPage")){

				if (resultCode == RESULT_OK && null != data
						&& requestCode == SELECT_LIVE_IMAGE_GALLERY_ALERT_EDIT) {
					Uri uri = data.getData();

					// try {

					// Get the file path from the URI
					// liveImagePath = FileUtils.getPath(this, uri);
					liveImagePath = getPath(uri);

					if (liveImagePath != null && !liveImagePath.equals("")) {
						Intent mintent = new Intent();
						mintent.putExtra("imagepaths", liveImagePath);
						setResult(RESULT_OK, mintent);
						finish();
						overridePendingTransition(R.anim.activity_open_scale,
								R.anim.activity_close_translate);
					}

					// } catch (Exception e) {
					// Log.e("FileSelectorTestActivity", "File select error", e);
					// }
				} else if (resultCode == RESULT_OK && null != data
						&& requestCode == SELECT_LIVE_IMAGE_CAMERA_ALERT_EDIT) {

					Bitmap photo = (Bitmap) data.getExtras().get("data");

					// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
					Uri tempUri = getImageUri(getApplicationContext(), photo);

					// CALL THIS METHOD TO GET THE ACTUAL PATH
					liveImagePath = getRealPathFromURI(tempUri);

					if (!liveImagePath.equals("")) {
						Intent mintent = new Intent();
						mintent.putExtra("imagepaths", liveImagePath);
						setResult(RESULT_OK, mintent);
						finish();
						overridePendingTransition(R.anim.activity_open_scale,
								R.anim.activity_close_translate);
					}
				}
			}

		} catch (Exception e) {
			Utilities.showToast(this, "Something went wrong");

		}
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		int column_index = cursor.getColumnIndex(projection[0]);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	@Override
	public void onBackPressed() {
		if(!getIntent().getStringExtra("name_class").equals("AlertEditPage")){

			Intent mintent = new Intent();
			mintent.putExtra("imagestatus", imagestatus);
			setResult(RESULT_OK, mintent);

		}else if(getIntent().getStringExtra("name_class").equals("AlertEditPage")){
			Intent mintent = new Intent();
			mintent.putExtra("imagepaths", liveImagePath);
			setResult(RESULT_OK, mintent);
		}

		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onBackPressed();

	}
}
