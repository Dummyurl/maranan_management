package com.marananmanagement;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.SmsAdapter;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.UncaughtExceptionHandler;
import com.marananmanagement.util.Utilities;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

public class SmsSuscribersActivity extends Activity implements OnClickListener {
	private static SmsSuscribersActivity mContext;
	private DynamicListView list_sms;
	private ImageView img_edit_sms;
	private SmsAdapter adapter;
	private ArrayList<GetterSetter> listSms;
	private int REQUEST_CODE = 100;
	private ProgressBar pDialog;

	// SmsSuscribersActivity Instance
	public static SmsSuscribersActivity getInstance() {
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
		mContext = SmsSuscribersActivity.this;
		pDialog = (ProgressBar) findViewById(R.id.progressBar);
		pDialog.setVisibility(View.INVISIBLE);
		list_sms = (DynamicListView) findViewById(R.id.list_sms);
		img_edit_sms = (ImageView) findViewById(R.id.img_edit_sms);
		img_edit_sms.setOnClickListener(this);

		list_sms.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent sub = new Intent(SmsSuscribersActivity.this, AddSubscribersActivity.class);
				sub.putExtra("intent", "intent_values");
				sub.putExtra("id", listSms.get(position).getId());
				sub.putExtra("firstname", listSms.get(position).getName_suscriber());
				sub.putExtra("city", listSms.get(position).getCity_suscriber());
				sub.putExtra("family", listSms.get(position).getFamily_suscriber());
				sub.putExtra("mobileno1", listSms.get(position).getMobile_one_suscriber());
				sub.putExtra("mobileno2", listSms.get(position).getMobile_two_suscriber());
				sub.putExtra("mobileno3", listSms.get(position).getMobile_three_suscriber());
				sub.putExtra("mobileno4", listSms.get(position).getMobile_four_suscriber());
				sub.putExtra("landline1", listSms.get(position).getLine_one_suscriber());
				sub.putExtra("landline2", listSms.get(position).getLine_two_suscriber());
				sub.putExtra("country_code", listSms.get(position).getCountry_code_suscriber());
				startActivityForResult(sub, REQUEST_CODE);

			}
		});

		// Swipe to dismiss list view item
		list_sms.enableSwipeToDismiss(new OnDismissCallback() {
			@Override
			public void onDismiss(final ViewGroup listView,
					final int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {

					if (listSms.size() > 0)
						/* Declare Delete SMS Subscribers */
					    AsyncRequest.deleteSmsSubscribers(mContext, listSms.get(position).getId());
						listSms.remove(String.valueOf(position));
					listSms.remove(position);
					adapter.notifyDataSetChanged();
				}
			}
		});
			
		AsyncRequest.getSmsSubscribers(mContext, this.getClass()
				.getSimpleName(), pDialog);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_edit_sms:
			// showCustomDialog();
			Intent sub = new Intent(SmsSuscribersActivity.this,
					AddSubscribersActivity.class);
			startActivityForResult(sub, REQUEST_CODE);
			break;

		default:
			break;
		}

	}

	// Show Custom dialog to send message direct to the subscriber...
	@SuppressWarnings("unused")
	private void showCustomDialog() {
		final Dialog dialog = new Dialog(SmsSuscribersActivity.this);
		dialog.setContentView(R.layout.custom_dialog_message);
		dialog.setTitle("Send Sms...");

		final EditText edt_message = (EditText) dialog
				.findViewById(R.id.edt_message);
		Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edt_message.length() == 0) {

					Utilities.showToast(SmsSuscribersActivity.this,
							"Please Enter Message");

				} else if (SmsAdapter.getInstance().getSubIds() == null) {
					Utilities.showToast(SmsSuscribersActivity.this,
							"Please Select Subscribers For Sending Message");

				} else {
					
					/*
					 * Send Alert Message Notification To Users Who Use The
					 * Maranan App
					 */
					AsyncRequest.sendMessagesNotification(mContext, this
							.getClass().getSimpleName(), null, edt_message
							.getText().toString(), "Manual", SmsAdapter
							.getInstance().getSubIds(), "", "", "", "", "", "",
							"", "", "", pDialog);
					dialog.dismiss();
				}

			}

		});

		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {

				if (listSms != null)
					if (listSms.size() > 0) {
						listSms.clear();
					}
				AsyncRequest.getSmsSubscribers(mContext, this.getClass()
						.getSimpleName(),pDialog);

			}
		}
	}
	
	/* Set adapter sms subscribers */
	public void setAdapter(ArrayList<GetterSetter> listSms) {
		if (listSms != null && listSms.size() > 0) {
			this.listSms= listSms;
			adapter = new SmsAdapter(this, listSms,
					this.getClass().getSimpleName(), pDialog);
			list_sms.setAdapter(adapter);
		}
	}
	
	/* Set response delete subscribers */
	public void setDeleteResponse(JSONObject result) {
		try {
			if(result != null && result.length() > 0){
				if (result.get("success").equals(true)) {
					Utilities.showToast(SmsSuscribersActivity.this, result
							.get("value").toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/* Set response message */
	public void setResponseForSmsMessage(JSONObject result) {
		if (result != null) {
			try {
				if (result.get("success").equals(true)) {
					Utilities.showToast(SmsSuscribersActivity.this, result
							.get("value").toString());
					Intent mintent = new Intent();
					mintent.putExtra("sub_id", SmsAdapter.getInstance()
							.getSubIds());
					setResult(RESULT_OK, mintent);
					finish();
					overridePendingTransition(R.anim.activity_open_scale,
							R.anim.activity_close_translate);

				} else if (result.get("success").equals(false)) {
					Utilities.showToast(SmsSuscribersActivity.this, result
							.get("value").toString());
					Intent mintent = new Intent();
					mintent.putExtra("sub_id", SmsAdapter.getInstance()
							.getSubIds());
					setResult(RESULT_OK, mintent);
					finish();
					overridePendingTransition(R.anim.activity_open_scale,
							R.anim.activity_close_translate);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onBackPressed() {
		Intent mintent = new Intent();
		mintent.putExtra("sub_id", SmsAdapter.getInstance().getSubIds());
		setResult(RESULT_OK, mintent);
		finish();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);

		super.onBackPressed();
	}
	
}
