package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.R;
import com.marananmanagement.RadioAlerts;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SmsAdapter extends BaseAdapter  {

	Context ctx;
	private static SmsAdapter mContext;
	ArrayList<GetterSetter> listSms;
	ArrayList<InfoRowdata> infodata;
	String name;
	String subs_id;
	StringBuilder commaSepValueBuilder;
	public static SparseBooleanArray mCheckStates;
	private int mPosition;
	private ProgressBar pDialog;

	// SmsAdapter Instance
	public static SmsAdapter getInstance() {
		return mContext;
	}

	public SmsAdapter(Context ctx, ArrayList<GetterSetter> listSms, String name, ProgressBar pDialog) {
		this.ctx = ctx;
		mContext = SmsAdapter.this;
		this.pDialog=pDialog;
		this.listSms = listSms;
		this.name = name;
		if (name.equals("SmsSuscribersActivity")) {
			commaSepValueBuilder = new StringBuilder();
			infodata = new ArrayList<InfoRowdata>();
			for (int i = 0; i < listSms.size(); i++) {
				infodata.add(new InfoRowdata(true, i));
				if (infodata.get(i).isclicked) {

					commaSepValueBuilder.append(listSms.get(i).getId());
					if (i != listSms.size() - 1) {
						commaSepValueBuilder.append(",");
					}
				}
			}
			subs_id = commaSepValueBuilder.toString();
		
		}else if(name.equals("RadioAlerts")){
			mCheckStates = new SparseBooleanArray();
		}
	}

	@Override
	public int getCount() {
		return listSms.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(ctx).inflate(R.layout.sms_lits_items, null, false);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.check_subscribers = (CheckBox) convertView.findViewById(R.id.check_subscribers);
			holder.tv_alerts_time = (TextView) convertView.findViewById(R.id.tv_alerts_time);
			holder.btn_mon = (Button) convertView.findViewById(R.id.btn_mon);
			holder.btn_tues = (Button) convertView.findViewById(R.id.btn_tues);
			holder.btn_wed = (Button) convertView.findViewById(R.id.btn_wed);
			holder.btn_thur = (Button) convertView.findViewById(R.id.btn_thur);
			holder.btn_fri = (Button) convertView.findViewById(R.id.btn_fri);
			holder.btn_sat = (Button) convertView.findViewById(R.id.btn_sat);
			holder.btn_sun = (Button) convertView.findViewById(R.id.btn_sun);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (name.equals("SmsSuscribersActivity")) {
			holder.check_subscribers.setVisibility(View.VISIBLE);
			holder.check_subscribers.setTag(position);
			holder.check_subscribers.setChecked(true);
			holder.tv_name.setText(Utilities.decodeImoString(listSms.get(position).getName_suscriber()));
			holder.check_subscribers.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (infodata.get(position).isclicked) {
						infodata.get(position).isclicked = false;

					} else {
						infodata.get(position).isclicked = true;
					}

					StringBuilder commaSepValueBuilder = new StringBuilder();

					for (int i = 0; i < infodata.size(); i++) {
						if (infodata.get(i).isclicked) {

							commaSepValueBuilder.append(listSms.get(i).getId());
							if (i != listSms.size() - 1) {
								commaSepValueBuilder.append(",");
							}
						}
					}

					subs_id = commaSepValueBuilder.toString();
				}
			});

			if (infodata.get(position).isclicked) {

				holder.check_subscribers.setChecked(true);
			} else {
				holder.check_subscribers.setChecked(false);
			}

		} else if (name.equals("RadioAlerts")) {
			holder.check_subscribers.setVisibility(View.VISIBLE);
			holder.check_subscribers.setTag(position);
			//holder.check_subscribers.setChecked(false);
			holder.tv_name.setText(Utilities.decodeImoString(listSms.get(position).getTitle()));
			holder.check_subscribers.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								mCheckStates.delete(mPosition);
								mCheckStates.put(position, isChecked);
								mPosition = position;
								notifyDataSetChanged();
								if (RadioAlerts.getInstance().getActivityName().equals("RadioProgram")) {
									
									AsyncRequest.sendRadioAlertsNotification(ctx, this.getClass().getSimpleName(), 
											RadioAlerts.getInstance().getNotiId(), listSms.get(position).getId(), 
											RadioAlerts.getInstance().getActivityName(),pDialog);
								
								}else if(RadioAlerts.getInstance().getActivityName().equals("NewsLetter")){
									
									AsyncRequest.sendRadioAlertsNotification(ctx, this.getClass().getSimpleName(), 
											RadioAlerts.getInstance().getNotiId(), listSms.get(position).getId(), 
											RadioAlerts.getInstance().getActivityName(),pDialog);
								
								}else if(RadioAlerts.getInstance().getActivityName().equals("ChannelPlaylist")){
									
									AsyncRequest.sendChannelAlertsNotification(ctx, this.getClass().getSimpleName(), 
											RadioAlerts.getInstance().getNotiId(), 
											RadioAlerts.getInstance().getVideoID(), 
											listSms.get(position).getId(), pDialog);
								
								
								}else if(RadioAlerts.getInstance().getActivityName().equals("ChannelManagement")){
									
									AsyncRequest.sendRadioAlertsNotification(ctx, this.getClass().getSimpleName(), 
											RadioAlerts.getInstance().getNotiId(), listSms.get(position).getId(), 
											RadioAlerts.getInstance().getActivityName(), pDialog);
								}
                               
							} else {
								mCheckStates.delete(position);
							}

						}
					});
			holder.check_subscribers.setChecked((mCheckStates.get(position) == true ? true : false));

		} else {
			holder.tv_alerts_time.setTag(position);
			holder.btn_mon.setTag(position);
			holder.btn_tues.setTag(position);
			holder.btn_wed.setTag(position);
			holder.btn_thur.setTag(position);
			holder.btn_fri.setTag(position);
			holder.btn_sat.setTag(position);
			holder.btn_sun.setTag(position);
			
			holder.check_subscribers.setVisibility(View.GONE);
			
			if (listSms.get(position).getMode().equals("Automatic")) {
				holder.tv_name.setText(Utilities.decodeImoString(listSms.get(position).getMessage()));
				holder.tv_alerts_time.setVisibility(View.VISIBLE);
				holder.tv_alerts_time.setText(listSms.get(position)
						.getTime_hr()
						+ ":"
						+ listSms.get(position).getTime_min());

				if (listSms.get(position).getSun().equals("Sun")) {
					holder.btn_sun.setVisibility(View.VISIBLE);

				} else {
					holder.btn_sun.setVisibility(View.GONE);
				}

				if (listSms.get(position).getMon().equals("Mon")) {
					holder.btn_mon.setVisibility(View.VISIBLE);

				} else {
					holder.btn_mon.setVisibility(View.GONE);
				}

				if (listSms.get(position).getTues().equals("Tue")) {
					holder.btn_tues.setVisibility(View.VISIBLE);

				} else {
					holder.btn_tues.setVisibility(View.GONE);
				}

				if (listSms.get(position).getWed().equals("Wed")) {
					holder.btn_wed.setVisibility(View.VISIBLE);

				} else {
					holder.btn_wed.setVisibility(View.GONE);
				}

				if (listSms.get(position).getThur().equals("Thu")) {
					holder.btn_thur.setVisibility(View.VISIBLE);

				} else {
					holder.btn_thur.setVisibility(View.GONE);
				}

				if (listSms.get(position).getFri().equals("Fri")) {
					holder.btn_fri.setVisibility(View.VISIBLE);

				} else {
					holder.btn_fri.setVisibility(View.GONE);
				}

				if (listSms.get(position).getSat().equals("Sat")) {
					holder.btn_sat.setVisibility(View.VISIBLE);

				} else {
					holder.btn_sat.setVisibility(View.GONE);
				}
			}else{
				holder.tv_name.setText(Utilities.decodeImoString(listSms.get(position).getMessage()));
				holder.tv_alerts_time.setVisibility(View.GONE);
				holder.btn_sun.setVisibility(View.GONE);
				holder.btn_mon.setVisibility(View.GONE);
				holder.btn_tues.setVisibility(View.GONE);
				holder.btn_wed.setVisibility(View.GONE);
				holder.btn_thur.setVisibility(View.GONE);
				holder.btn_fri.setVisibility(View.GONE);
				holder.btn_sat.setVisibility(View.GONE);
			}
		}

		return convertView;
	}
	
	

	class ViewHolder {
		TextView tv_name, tv_alerts_time;
		CheckBox check_subscribers;
		Button btn_mon, btn_tues, btn_wed, btn_thur, btn_fri, btn_sat, btn_sun;

	}
	

	public class InfoRowdata {

		public boolean isclicked = false;
		public int index;

		/*
		 * public String fanId; public String strAmount;
		 */

		public InfoRowdata(boolean isclicked, int index/*
														 * ,String fanId,String
														 * strAmount
														 */) {
			this.index = index;
			this.isclicked = isclicked;
			/*
			 * this.fanId=fanId; this.strAmount=strAmount;
			 */
		}

	}

	// Here to Get Subscribers ID's
	public String getSubIds() {
		return subs_id;
	}
    
	/* Set Radio Alerts Notification Response */
	public void setRadioAlertsNotificationResponse(String result) {
		if (result.length() > 0) {
			try {
				JSONObject json = new JSONObject(result);
				if (json.get("success").equals(true)) {
					Utilities.showToast(ctx, json.get("value").toString());
					Intent mintent = new Intent();
					((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
					((Activity) ctx).finish();
					((Activity) ctx).overridePendingTransition(
							R.anim.activity_open_scale,
							R.anim.activity_close_translate);
				} else {
					Utilities.showToast(ctx, json.get("value").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/* Set Channel Alerts Notification Response */
	public void setChannelAlertsNotificationResponse(String result) {

		if (result.length() > 0) {
			try {
				JSONObject json = new JSONObject(result);
				if (json.get("success").equals(true)) {
					Utilities.showToast(ctx, json.get("value").toString());
					Intent mintent = new Intent();
					((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
					((Activity) ctx).finish();
					((Activity) ctx).overridePendingTransition(
							R.anim.activity_open_scale,
							R.anim.activity_close_translate);
				} else {
					Utilities.showToast(ctx, json.get("value").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
