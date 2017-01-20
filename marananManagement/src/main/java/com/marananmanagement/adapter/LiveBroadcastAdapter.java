package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marananmanagement.LiveBroadcastList;
import com.marananmanagement.R;
import com.marananmanagement.RadioAlerts;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.RelativeTimeTextView;
import com.marananmanagement.util.Utilities;
import com.squareup.picasso.Picasso;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LiveBroadcastAdapter extends BaseAdapter {
	private static LiveBroadcastAdapter mContext;
	Context ctx;
	ArrayList<GetterSetter> live_list;
	public ViewHolder holder;

	// ChannelPlayListAdapter Instance
	public static LiveBroadcastAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public LiveBroadcastAdapter(Context ctx, ArrayList<GetterSetter> live_list) {
		this.ctx = ctx;
		mContext = LiveBroadcastAdapter.this;
		this.live_list = live_list;
	}

	@Override
	public int getCount() {
		return live_list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = new ViewHolder();

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
			convertView = inflater.inflate(
					R.layout.channelmanagement_list_items, null, false);
			holder.img_Channel = (ImageView) convertView
					.findViewById(R.id.img_Channel);
			holder.btn_down = (Button) convertView.findViewById(R.id.btn_down);
			holder.btn_up = (Button) convertView.findViewById(R.id.btn_up);
			holder.broadcast_check_uncheck = (Button) convertView.findViewById(R.id.broadcast_check_uncheck);
			holder.tv_Count_Channel = (TextView) convertView.findViewById(R.id.tv_Count_Channel);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title_channel);
			holder.tv_time_left = (RelativeTimeTextView) convertView.findViewById(R.id.tv_time_left);
			holder.swipe_check_uncheck = (Button) convertView.findViewById(R.id.swipe_check_uncheck);
			holder.swipe_delete = (Button) convertView.findViewById(R.id.swipe_delete);
			holder.front_channel = (RelativeLayout) convertView.findViewById(R.id.front_channel);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.swipe_check_uncheck.setTag(position);
		holder.btn_down.setTag(position);
		holder.btn_up.setTag(position);
		holder.broadcast_check_uncheck.setTag(position);
		holder.img_Channel.setTag(position);
		holder.tv_Count_Channel.setTag(position);
		holder.tv_title.setTag(position);
		holder.tv_time_left.setTag(position);
		holder.swipe_delete.setTag(position);

		holder.btn_down.setVisibility(View.INVISIBLE);
		holder.btn_up.setVisibility(View.INVISIBLE);
		holder.tv_Count_Channel.setVisibility(View.INVISIBLE);
		holder.broadcast_check_uncheck.setVisibility(View.VISIBLE);
		holder.swipe_delete.setVisibility(View.VISIBLE);
		holder.tv_time_left.setVisibility(View.GONE);
		
		holder.tv_title.setTextColor(live_list.get(position).getText_color());
		holder.tv_title.setText(Utilities.decodeImoString(live_list.get(position).getTitle()));

		Picasso.with(ctx)
				.load(live_list.get(position).getImage())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_Channel);

		holder.front_channel.setBackgroundColor(live_list.get(position).getColor());
		
		if(live_list.get(position).getMode().equals("Automatic") && live_list.get(position).getPublish_notification().equals("true")
				&& live_list.get(position).getPublish_status().equals("true")){
			
			if (Utilities.compareDateTimeLive(Utilities.getCurrentDateNew()
					+ " " + live_list.get(0).getTime_hr() + ":"
					+ live_list.get(0).getTime_min() + ":00")) {

				live_list.get(position).setColor(
						ctx.getResources().getColor(R.color.list_blue));
				notifyDataSetChanged();

			} else if (Utilities.compareDateTimeLive(Utilities
					.getCurrentDateNew()
					+ " "
					+ live_list.get(0).getTime_hr_two()
					+ ":"
					+ live_list.get(0).getTime_min_two() + ":00")) {

				live_list.get(position).setColor(
						ctx.getResources().getColor(R.color.list_green));
				notifyDataSetChanged();
			}
		}

		holder.swipe_check_uncheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (live_list.get(position).getImageResource() == R.drawable.tick_icon) {
                  
					if(live_list.get(position).getMode().equals("Automatic") 
							&& live_list.get(position).getPublish_notification().equals("true")
							&& live_list.get(position).getPublish_status().equals("true")){
						
						if (Utilities.compareDateTimeLive(Utilities.getCurrentDateNew()
								+ " " + live_list.get(0).getTime_hr() + ":"
								+ live_list.get(0).getTime_min() + ":00")) {

							live_list.get(position).setImageResource(R.drawable.tick_gray);
							LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
							live_list.get(position).setPublish_status("false");
							notifyDataSetChanged();
							new UpdateLiveChannelSingleStatus().execute(live_list.get(position).getId(), "false");

						} else if (Utilities.compareDateTimeLive(Utilities
								.getCurrentDateNew()
								+ " "
								+ live_list.get(0).getTime_hr_two()
								+ ":"
								+ live_list.get(0).getTime_min_two() + ":00")) {

							Utilities.showToast(ctx, ctx.getResources().getString(
									R.string.alerts_message_one));
						}
					
					}else{
						live_list.get(position).setImageResource(R.drawable.tick_gray);
						LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
						live_list.get(position).setPublish_status("false");
						notifyDataSetChanged();
						new UpdateLiveChannelSingleStatus().execute(live_list.get(position).getId(), "false");
					}

				} else {
					
					live_list.get(position).setImageResource(R.drawable.tick_icon);
					LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
					live_list.get(position).setPublish_status("true");
					notifyDataSetChanged();
					new UpdateLiveChannelSingleStatus().execute(live_list.get(position).getId(), "true");

				}
			}
		});

		holder.broadcast_check_uncheck
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (live_list.get(position).getImgBroadCastRes() == R.drawable.broadcast_sms_gray) {
							
							//live_list.get(position).setImgBroadCastRes(R.drawable.broadcast_sms);
							LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
							
							Intent radioAlerts = new Intent(ctx, RadioAlerts.class);
							radioAlerts.putExtra("id", live_list.get(position).getId());
							radioAlerts.putExtra("video_id", live_list.get(position).getVid_Id());
							radioAlerts.putExtra("image", live_list.get(position).getImage());
							radioAlerts.putExtra("activity_name", "ChannelManagement");
							((Activity) ctx).startActivityForResult(radioAlerts,
									LiveBroadcastList.REQUEST_CODE_CHANNEL_UPLOAD);
							notifyDataSetChanged();
						
						}else{
							LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
						}
					}
				});
		
		holder.swipe_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (live_list.get(position).getImgCancelRes() == R.drawable.cross_gray) {
					
					live_list.get(position).setImgCancelRes(R.drawable.cross_icon);
					notifyDataSetChanged();
				
				}else{
					
					LiveBroadcastList.getInstance().getClassDelete(live_list.get(position).getId());
					LiveBroadcastList.getInstance().getSwipeList().closeAnimate(position);
					live_list.remove(String.valueOf(position));
					live_list.remove(position);
					notifyDataSetChanged();
				}
			}
		});
		
		holder.front_channel.setBackgroundColor(live_list.get(position).getColor());
		holder.swipe_check_uncheck.setBackgroundResource(live_list.get(position).getImageResource());
		holder.broadcast_check_uncheck.setBackgroundResource(live_list.get(position).getImgBroadCastRes());
		holder.swipe_delete.setBackgroundResource(live_list.get(position).getImgCancelRes());
		return convertView;
	}

	class ViewHolder {
		Button swipe_check_uncheck, btn_down, btn_up, broadcast_check_uncheck, swipe_delete;
		ImageView img_Channel;
		TextView tv_Count_Channel, tv_title;
		RelativeTimeTextView tv_time_left;
		RelativeLayout front_channel;
	}
	
	public ArrayList<GetterSetter> getList(){
		return live_list;
	}
	
	// Declare Update Live Broadcast Status...
	public static class UpdateLiveChannelSingleStatus extends AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT
					+ Config.LIVE_BROADCAST_SINGLE_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				multipartEntity.addPart("single_status", new StringBody(
						params[1]));
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
			Log.e("Rse", "res?? " + result);
		}
	}

}
