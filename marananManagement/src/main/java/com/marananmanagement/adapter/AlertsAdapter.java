package com.marananmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.marananmanagement.R;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;

public class AlertsAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<GetterSetter> listAlerts;

	public AlertsAdapter(Context ctxx, ArrayList<GetterSetter> listAlert) {
		ctx = ctxx;
		listAlerts = listAlert;
	}

	@Override
	public int getCount() {
		return listAlerts.size();
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
			convertView = LayoutInflater.from(ctx).inflate(R.layout.list_items, null, false);
			holder.tv_alerts = (TextView) convertView.findViewById(R.id.tv_alerts_text);
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
		
		holder.tv_alerts.setTag(position);
		holder.tv_alerts_time.setTag(position);
		holder.btn_mon.setTag(position);
		holder.btn_tues.setTag(position);
		holder.btn_wed.setTag(position);
		holder.btn_thur.setTag(position);
		holder.btn_fri.setTag(position);
		holder.btn_sat.setTag(position);
		holder.btn_sun.setTag(position);
		
		if (listAlerts.get(position).getMode().equals("Automatic")) {
			holder.tv_alerts.setText(Utilities.decodeImoString(listAlerts.get(position).getAlertMessages()));
			holder.tv_alerts_time.setVisibility(View.VISIBLE);
			holder.tv_alerts_time.setText(Utilities.decodeImoString(listAlerts.get(position).getTime_hr()+":"+listAlerts.get(position).getTime_min()));
			
			if (listAlerts.get(position).getSun().equals("Sun")) {
				holder.btn_sun.setVisibility(View.VISIBLE);
			}else{
				holder.btn_sun.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getMon().equals("Mon")) {
				holder.btn_mon.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_mon.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getTues().equals("Tue")) {
				holder.btn_tues.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_tues.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getWed().equals("Wed")) {
				holder.btn_wed.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_wed.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getThur().equals("Thu")) {
				holder.btn_thur.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_thur.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getFri().equals("Fri")) {
				holder.btn_fri.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_fri.setVisibility(View.GONE);
			}

			if (listAlerts.get(position).getSat().equals("Sat")) {
				holder.btn_sat.setVisibility(View.VISIBLE);
			
			}else{
				holder.btn_sat.setVisibility(View.GONE);
			}
			
		}else{
			holder.tv_alerts.setText(Utilities.decodeImoString(listAlerts.get(position).getAlertMessages()));
			holder.tv_alerts_time.setVisibility(View.GONE);
			holder.btn_sun.setVisibility(View.GONE);
			holder.btn_mon.setVisibility(View.GONE);
			holder.btn_tues.setVisibility(View.GONE);
			holder.btn_wed.setVisibility(View.GONE);
			holder.btn_thur.setVisibility(View.GONE);
			holder.btn_fri.setVisibility(View.GONE);
			holder.btn_sat.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_alerts, tv_alerts_time;
		Button btn_mon, btn_tues, btn_wed, btn_thur, btn_fri, btn_sat, btn_sun;
	}

}
