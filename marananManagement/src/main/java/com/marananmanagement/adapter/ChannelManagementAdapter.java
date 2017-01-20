package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marananmanagement.ChannelManagement;
import com.marananmanagement.R;
import com.marananmanagement.RadioAlerts;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.GetterSetter;
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

public class ChannelManagementAdapter extends BaseAdapter {
	private static ChannelManagementAdapter mContext;
	Context ctx;
	ArrayList<GetterSetter> channelList;
	public ViewHolder holder;

	// ChannelPlayListAdapter Instance
	public static ChannelManagementAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public ChannelManagementAdapter(Context ctx,
			ArrayList<GetterSetter> channelList) {
		this.ctx = ctx;
		this.channelList = channelList;
		mContext = ChannelManagementAdapter.this;
	}

	@Override
	public int getCount() {
		return channelList.size();
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
			convertView = inflater.inflate(R.layout.channelmanagement_list_items, null, false);
			holder.img_Channel = (ImageView) convertView.findViewById(R.id.img_Channel);
			holder.btn_down = (Button) convertView.findViewById(R.id.btn_down);
			holder.btn_up = (Button) convertView.findViewById(R.id.btn_up);
			holder.broadcast_check_uncheck = (Button) convertView.findViewById(R.id.broadcast_check_uncheck);
			holder.tv_Count_Channel = (TextView) convertView.findViewById(R.id.tv_Count_Channel);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title_channel);
			holder.tv_time_left = (TextView) convertView.findViewById(R.id.tv_time_left);
			holder.swipe_check_uncheck = (Button) convertView.findViewById(R.id.swipe_check_uncheck);
			holder.front_channel = (RelativeLayout) convertView.findViewById(R.id.front_channel);
			holder.btn_sequence = (Button) convertView.findViewById(R.id.btn_sequence);
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
		holder.btn_sequence.setTag(position);
		
		holder.front_channel.setBackgroundColor(channelList.get(position).getColor());
		holder.tv_title.setTextColor(channelList.get(position).getText_color());
		
		holder.tv_title.setText(Utilities.decodeImoString(channelList.get(position).getTitle()));

		Picasso.with(ctx)
				.load(channelList.get(position).getImage())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_Channel);

		if(channelList.get(position).getTag().equals("video")){
			
			holder.btn_down.setVisibility(View.VISIBLE);
			holder.btn_up.setVisibility(View.VISIBLE);
			holder.tv_Count_Channel.setVisibility(View.VISIBLE);
			holder.tv_Count_Channel.setText(channelList.get(position).getContentDetails());
			holder.broadcast_check_uncheck.setVisibility(View.GONE);
			holder.btn_sequence.setVisibility(View.VISIBLE);
		
		}else if(channelList.get(position).getTag().equals("pdf")){
			
			holder.btn_down.setVisibility(View.VISIBLE);
			holder.btn_up.setVisibility(View.VISIBLE);
			holder.tv_Count_Channel.setVisibility(View.VISIBLE);
			holder.tv_Count_Channel.setText(channelList.get(position).getContentDetails());
			holder.broadcast_check_uncheck.setVisibility(View.GONE);
			holder.btn_sequence.setVisibility(View.GONE);
		
		}else if(channelList.get(position).getTag().equals("live") && channelList.get(position).getTitle().equals("Live Broadcast not available")){
			
			holder.btn_down.setVisibility(View.GONE);
			holder.btn_up.setVisibility(View.GONE);
			holder.tv_Count_Channel.setVisibility(View.INVISIBLE);
			holder.broadcast_check_uncheck.setVisibility(View.INVISIBLE);
			holder.swipe_check_uncheck.setVisibility(View.INVISIBLE);
			holder.img_Channel.setVisibility(View.GONE);
			holder.tv_title.setGravity(Gravity.CENTER);
			holder.btn_sequence.setVisibility(View.GONE);
		
		}else{
			holder.btn_down.setVisibility(View.INVISIBLE);
			holder.btn_up.setVisibility(View.INVISIBLE);
			holder.tv_Count_Channel.setVisibility(View.INVISIBLE);
			holder.broadcast_check_uncheck.setVisibility(View.GONE);
		}
		
		holder.swipe_check_uncheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!channelList.get(position).getTag().equals("live")){
					if(channelList.get(position).getImageResource() == R.drawable.tick_icon){
						channelList.get(position).setImageResource(R.drawable.tick_gray);
						notifyDataSetChanged();
						new UpdateChannelStatus().execute(channelList.get(position).getPlayListId(), channelList.get(position).getTag(), "false");
					
					}else{
						
						channelList.get(position).setImageResource(R.drawable.tick_icon);
						notifyDataSetChanged();
						new UpdateChannelStatus().execute(channelList.get(position).getPlayListId(), channelList.get(position).getTag(), "true");
					}
				}else{
					
					if(channelList.get(position).getImageResource() == R.drawable.tick_icon){
						channelList.get(position).setImageResource(R.drawable.tick_gray);
						notifyDataSetChanged();
						new UpdateLiveBroadcastStatus().execute(channelList.get(position).getId(), "false");

					
					}else{
						channelList.get(position).setImageResource(R.drawable.tick_icon);
						notifyDataSetChanged();
						new UpdateLiveBroadcastStatus().execute(channelList.get(position).getId(), "true");

					}
				}
				
			}
		});
		
		holder.btn_sequence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
					if (channelList.get(position).getImgSeq() == R.drawable.sequence_icon_green) {
						channelList.get(position).setImgSeq(R.drawable.sequence_icon_gray);
						notifyDataSetChanged();
						new UpdateSequenceStatus().execute(channelList.get(position).getPlayListId(), "false");


					} else {

						channelList.get(position).setImgSeq(
								R.drawable.sequence_icon_green);
						notifyDataSetChanged();
						new UpdateSequenceStatus().execute(channelList.get(position).getPlayListId(),  "true");

						
					}
			}
		});

		holder.broadcast_check_uncheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (channelList.get(position).getImgBroadCastRes() == R.drawable.broadcast_sms_gray) {
					channelList.get(position).setImgBroadCastRes(R.drawable.broadcast_sms);
					
					Intent radioAlerts = new Intent(ctx, RadioAlerts.class);
					radioAlerts.putExtra("id", channelList.get(position).getId());
					radioAlerts.putExtra("video_id", channelList.get(position).getVid_Id());
					radioAlerts.putExtra("image", channelList.get(position).getImage());
					radioAlerts.putExtra("activity_name", "ChannelManagement");
					((Activity) ctx).startActivityForResult(radioAlerts, ChannelManagement.REQUEST_CODE_CHANNEL_UPLOAD);
					
					notifyDataSetChanged();
				} 
			}
		});
		
		holder.btn_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                if(!channelList.get(position).getTag().equals("pdf")){
                	if (position != channelList.size() - 1 && !channelList.get(position+1).getTag().equals("pdf")) {
                		
                		// Update Priority Of Channel...
    					new UpdateChannelPriority().execute(channelList.get(position).getId(), 
    							String.valueOf(position+2), channelList.get(position+1).getId(), 
    							String.valueOf(position+1));
    					
						GetterSetter get = channelList.get(position);
    					GetterSetter get2 = channelList.get(position + 1);
    					channelList.set(position + 1, get);
    					channelList.set(position, get2);
    					notifyDataSetChanged();
    				}
                }else{
                	holder.btn_down.setClickable(false);
                }
			}
		});

		holder.btn_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!channelList.get(position).getTag().equals("pdf")){
					
					if (position != 0) {
						
						if(!channelList.get(position-1).getTag().equals("live")){
							
							int temp = (position+1);
							// Update Priority Of Channel...
							new UpdateChannelPriority().execute(channelList.get(position).getId(), 
									String.valueOf(temp-1), channelList.get(position-1).getId(), 
									String.valueOf(temp));
							
							GetterSetter get = channelList.get(position);
							GetterSetter get2 = channelList.get(position - 1);
							channelList.set(position - 1, get);
							channelList.set(position, get2);
							notifyDataSetChanged();
						}
					}
				}else{
					holder.btn_up.setClickable(false);
				}
			}
		});
		
		holder.swipe_check_uncheck.setBackgroundResource(channelList.get(position).getImageResource());
		holder.broadcast_check_uncheck.setBackgroundResource(channelList.get(position).getImgBroadCastRes());
		holder.btn_sequence.setBackgroundResource(channelList.get(position).getImgSeq());
		return convertView;
	}
	
	class ViewHolder {
		Button swipe_check_uncheck, btn_down, btn_up, broadcast_check_uncheck, btn_sequence;
		ImageView img_Channel;
		TextView tv_Count_Channel, tv_title, tv_time_left;
		RelativeLayout front_channel;
	}
	
	// Declare UpdateStatus NewsLetter...
	class UpdateChannelStatus extends AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT + Config.CHANNEL_MANAGEMENT;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("playlist_id", new StringBody(params[0]));
				multipartEntity.addPart("tag", new StringBody(params[1]));
				multipartEntity.addPart("status", new StringBody(params[2]));
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
	
	// Declare Update Live Broadcast Status...
	class UpdateLiveBroadcastStatus extends AsyncTask<String, Void, JSONObject> {
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
					+ Config.LIVE_BROADCAST_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				multipartEntity.addPart("status", new StringBody(params[1]));
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
			Log.e("Rse", "res?? "+result);
		}
	}
	
	// Update Channel Priority...
	class UpdateChannelPriority extends AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT + Config.CHANNEL_PRIORITY;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id1", new StringBody(params[0]));
				multipartEntity.addPart("priority1", new StringBody(params[1]));
				multipartEntity.addPart("id2", new StringBody(params[2]));
				multipartEntity.addPart("priority2", new StringBody(params[3]));
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
			Log.e("Result", "Result??? "+result);
		}
	}
	
	// Declare Update Sequence Status...
	class UpdateSequenceStatus extends AsyncTask<String, Void, JSONObject> {
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
					+ Config.VIDEO_SEQUENCE_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("playlist_id", new StringBody(params[0]));
				multipartEntity.addPart("sequence_status", new StringBody(params[1]));
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
	
	// Get PlayList From Adapter...
	public ArrayList<GetterSetter> getPlaylist(){
		return channelList;
	}

}
