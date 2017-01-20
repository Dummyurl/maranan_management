package com.marananmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marananmanagement.R;
import com.marananmanagement.util.GetterSetter;

public class AccountListAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<GetterSetter> listAccounts;

	public AccountListAdapter(Context ctx, ArrayList<GetterSetter> listAccounts) {
		this.ctx = ctx;
		this.listAccounts = listAccounts;
	}

	@Override
	public int getCount() {
		return listAccounts.size();
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
			convertView = LayoutInflater.from(ctx).inflate(R.layout.accounts_items, null, false);
			holder.tv_account_name = (TextView) convertView.findViewById(R.id.tv_account_name);
			holder.tv_account_email = (TextView) convertView.findViewById(R.id.tv_account_email);
			holder.img_profile_pic = (ImageView) convertView.findViewById(R.id.img_profile_pic);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_account_name.setTag(position);
		holder.tv_account_email.setTag(position);
		holder.img_profile_pic.setTag(position);
		
		holder.tv_account_name.setText(listAccounts.get(position).getFirstname().toString()+" "+listAccounts.get(position).getLastname().toString());
		holder.tv_account_email.setText(listAccounts.get(position).getEmail().toString());
		return convertView;
	}

	class ViewHolder {
		TextView tv_account_name, tv_account_email;
		ImageView img_profile_pic;
		
	}

}
