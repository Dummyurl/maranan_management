package com.marananmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.marananmanagement.R;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;

public class ManagementAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<GetterSetter> listDedicationss;
	
	public ManagementAdapter(Context ctxx, ArrayList<GetterSetter> listDedications) {
		ctx = ctxx;
		listDedicationss = listDedications;
	}

	@Override
	public int getCount() {
		return listDedicationss.size();
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
			convertView = LayoutInflater.from(ctx).inflate(R.layout.activity_list_items, null, false);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_sucess = (TextView) convertView.findViewById(R.id.tv_sucess);
		holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		
		holder.tv_sucess.setText(Utilities.decodeImoString(listDedicationss.get(position).getNature()));
		holder.tv_time.setText(Utilities.decodeImoString(listDedicationss.get(position).getTime()));
		
       if (listDedicationss.get(position).getName_Optional().equals("")){
		    
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getName().trim()+" "
					+ listDedicationss.get(position).getSex().trim()+" "
					+ listDedicationss.get(position).getThere_Is().trim()+" "
					+ listDedicationss.get(position).getBlessing().trim()));
		
		}else if(listDedicationss.get(position).getName().equals("")){
			
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getSex().trim()+" "
					+ listDedicationss.get(position).getThere_Is().trim()+" "
					+ listDedicationss.get(position).getName_Optional().trim()+" "
					+ listDedicationss.get(position).getBlessing().trim()));
		
		}else if(listDedicationss.get(position).getSex().equals("")){
			
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getName().trim()+" "
					+ listDedicationss.get(position).getThere_Is().trim()+" "
					+ listDedicationss.get(position).getName_Optional().trim()+" "
					+ listDedicationss.get(position).getBlessing().trim()));
		
		}else if(listDedicationss.get(position).getThere_Is().equals("")){
			
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getName().trim()+" "
					+ listDedicationss.get(position).getSex().trim()+" "
					+ listDedicationss.get(position).getName_Optional().trim()+" "
					+ listDedicationss.get(position).getBlessing().trim()));
		
		}else if(listDedicationss.get(position).getBlessing().equals("")){
			
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getName().trim()+" "
					+ listDedicationss.get(position).getSex().trim()+" "
					+ listDedicationss.get(position).getThere_Is().trim()+" "
					+ listDedicationss.get(position).getName_Optional().trim()));
		
		}else{
			holder.tv_name.setText(Utilities.decodeImoString(listDedicationss.get(position).getName().trim()+" "
					+ listDedicationss.get(position).getSex().trim()+" "
					+ listDedicationss.get(position).getThere_Is().trim()+" "
					+ listDedicationss.get(position).getName_Optional().trim()+" "
					+ listDedicationss.get(position).getBlessing().trim()));
		}
		
		return convertView;
	}

	class ViewHolder {
		TextView tv_name, tv_sucess, tv_time;
	}

}
