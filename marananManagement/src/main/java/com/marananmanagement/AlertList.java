package com.marananmanagement;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
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

import com.marananmanagement.adapter.AlertsAdapter;
import com.marananmanagement.database.MarananDB;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

public class AlertList extends Activity {
	private static AlertList mContext;
	ImageView img_edit;
	DynamicListView lst;
	ArrayList<String> list;
	ProgressBar pDialog;
	ArrayList<GetterSetter> listAlerts;
	int REQUEST_CODE = 200;
	public AlertsAdapter madapter;
	SharedPreferences prefMode;
	MarananDB db;
	SQLiteDatabase sqlitedb;
	
	
	// AlertList Instance
	public static AlertList getInstance() {
		return mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		// Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
		setContentView(R.layout.activity_list);
		mContext = AlertList.this;
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.GONE);
		img_edit = (ImageView) findViewById(R.id.img_edit);
		lst = (DynamicListView) findViewById(R.id.lst);
		prefMode = getSharedPreferences("ModePrefrence", MODE_PRIVATE);
		Utilities.setListViewHeightBasedOnChildren(lst);
		db = new MarananDB(this);
		
		lst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				
				Intent in = new Intent(mContext, AlertEditPage.class);
				in.putExtra("id", listAlerts.get(position).getId());
				in.putExtra("description", listAlerts.get(position).getDescriptions());
				in.putExtra("image", listAlerts.get(position).getListImage());
				in.putExtra("audio_alert", listAlerts.get(position).getAlert_audio());
				in.putExtra("phone", listAlerts.get(position).getPhone());
				startActivityForResult(in, REQUEST_CODE);
			}
		});

		img_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent in = new Intent(AlertList.this, AlertEditPage.class);
				startActivityForResult(in, REQUEST_CODE);
			}
		});

		// Swipe to dismiss list view item
		lst.enableSwipeToDismiss(new OnDismissCallback() {
			@Override
			public void onDismiss(final ViewGroup listView,
					final int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {

					if (listAlerts.size() > 0)
					/* Declare Delete Alerts */
					AsyncRequest.deleteAlerts(this, this.getClass().getSimpleName(), listAlerts.get(position).getId());
					db.deleteSingleRecord(listAlerts.get(position).getId());
					listAlerts.remove(String.valueOf(position));
					listAlerts.remove(position);
					madapter.notifyDataSetChanged();
				}
			}
		});
		
		/* Get All Alerts */
		AsyncRequest.getAllAlerts(mContext, this.getClass().getSimpleName(),
				db, sqlitedb, pDialog);

	}

	// OnActivity Result To Get Result Here..
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {

				if (listAlerts.size() > 0) {
					listAlerts.clear();
				}
				/* Get All Alerts */
				AsyncRequest.getAllAlerts(mContext, this.getClass()
						.getSimpleName(), db, sqlitedb, pDialog);
			}
		}
	}
	
	/* Set Adapter Alert List */
	public void setAdapter(ArrayList<GetterSetter> listAlerts) {

		if (listAlerts.size() > 0) {
			this.listAlerts = listAlerts;
			madapter = new AlertsAdapter(AlertList.this, listAlerts);
			lst.setAdapter(madapter);
			madapter.notifyDataSetChanged();
		}
	}
	
	/* Set Delete Response */
	public void setDeleteResponse(JSONObject result) {
		if (result != null) {
			try {
				if (result.get("success").equals(true)) {
					Utilities.showToast(AlertList.this, result
							.get("value").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
