package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marananmanagement.ChannelPlaylist;
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

public class ChannelPlayListAdapter extends BaseAdapter {
	private static ChannelPlayListAdapter mContext;
	Context ctx;
	ArrayList<GetterSetter> listPlaylist;
	public ViewHolder holder;

	// ChannelPlayListAdapter Instance
	public static ChannelPlayListAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public ChannelPlayListAdapter(Context ctx,
			ArrayList<GetterSetter> listPlaylist) {
		this.ctx = ctx;
		this.listPlaylist = listPlaylist;
		mContext = ChannelPlayListAdapter.this;
	}

	@Override
	public int getCount() {
		return listPlaylist.size();
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
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(ctx).inflate(R.layout.channel_playlist_items, null, false);
			holder.tv_title_video_playlist = (TextView) convertView.findViewById(R.id.tv_title_video_playlist);
			holder.tv_description_video_playlist = (TextView) convertView.findViewById(R.id.tv_description_video_playlist);
			holder.img_video_playlist = (ImageView) convertView.findViewById(R.id.img_video_playlist);
			holder.swipe_check_uncheck_playlist = (Button) convertView.findViewById(R.id.swipe_check_uncheck_playlist);
			holder.swipe_broadcast_playlist = (Button) convertView.findViewById(R.id.swipe_broadcast_playlist);
			holder.swipe_add = (Button) convertView.findViewById(R.id.swipe_add);
			holder.tv_time = (RelativeTimeTextView) convertView.findViewById(R.id.tv_time);
			holder.front_channel_playlist = (RelativeLayout) convertView.findViewById(R.id.front_channel_playlist);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_title_video_playlist.setTag(position);
		holder.tv_description_video_playlist.setTag(position);
		holder.img_video_playlist.setTag(position);
		holder.swipe_check_uncheck_playlist.setTag(position);
		
		holder.swipe_broadcast_playlist.setTag(position);
		holder.tv_time.setTag(position);
		holder.front_channel_playlist.setTag(position);
		
		if(listPlaylist.get(position).getPublish_status().equals("true")){
			holder.front_channel_playlist.setBackgroundColor(Color.parseColor("#CAEBFC"));
		}else{
			holder.front_channel_playlist.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}

		if (!listPlaylist.get(position).getTitle().equals(""))
			holder.tv_title_video_playlist.setText(Utilities.decodeImoString(listPlaylist.get(position).getTitle()));

		if (!listPlaylist.get(position).getDescriptions().equals(""))
			holder.tv_description_video_playlist.setText(Utilities.decodeImoString(listPlaylist.get(position).getDescriptions()));
		
		if (!listPlaylist.get(position).getImage().equals("")) 

		Picasso.with(ctx)
				.load(listPlaylist.get(position).getImage())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_video_playlist);

		if (Utilities.compareDateTime(listPlaylist.get(position).getDate()
				+ " "
				+ listPlaylist.get(position).getTime() + ":00") == true) {
			
			holder.tv_time.setVisibility(View.VISIBLE);
			holder.tv_time.setReferenceTime((Utilities.getToDate(listPlaylist.get(position).getDate(), listPlaylist.get(position).getTime())).getTime(), listPlaylist.get(position).getPlayListId(), listPlaylist.get(position).getVid_Id(), "true", "ChannelPlaylist", position);
			
			listPlaylist.get(position).setImageResource(R.drawable.tick_gray);
			listPlaylist.get(position).setPublish_status("false");
			notifyDataSetChanged();
			new UpdateVideoStatus().execute(listPlaylist.get(position).getPlayListId(), listPlaylist.get(position).getVid_Id(), "false");

		}else{
			
			holder.tv_time.setVisibility(View.GONE);
		
		}
	   
		
	    holder.swipe_check_uncheck_playlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listPlaylist.get(position).getImageResource() == R.drawable.tick_icon){
					listPlaylist.get(position).setImageResource(R.drawable.tick_gray);
					listPlaylist.get(position).setPublish_status("false");
					notifyDataSetChanged();
					new UpdateVideoStatus().execute(listPlaylist.get(position).getPlayListId(), listPlaylist.get(position).getVid_Id(), "false");

				}else{
							if (Utilities.compareDateTime(listPlaylist.get(
									position).getDate()
									+ " "
									+ listPlaylist.get(position).getTime()
									+ ":00") == true) {

								Toast.makeText(ctx, ctx.getResources().getString(
												R.string.alerts_message_one),
										Toast.LENGTH_SHORT).show();

			}else{
				listPlaylist.get(position).setImageResource(R.drawable.tick_icon);
				listPlaylist.get(position).setPublish_status("true");
				notifyDataSetChanged();
				new UpdateVideoStatus().execute(listPlaylist.get(position).getPlayListId(), listPlaylist.get(position).getVid_Id(), "true");

			 }	
		     }
			}
		});
		
        holder.swipe_broadcast_playlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listPlaylist.get(position).getImgBroadCastRes() == R.drawable.broadcast_sms_gray){
					ChannelPlaylist.getInstance().getSwipeList().closeAnimate(position);
					Intent radioAlerts = new Intent(ctx, RadioAlerts.class);
					radioAlerts.putExtra("id", listPlaylist.get(position).getPlayListId());
					radioAlerts.putExtra("video_id", listPlaylist.get(position).getVid_Id());
					radioAlerts.putExtra("image", listPlaylist.get(position).getImage());
					radioAlerts.putExtra("activity_name", "ChannelPlaylist");
					((Activity) ctx).startActivityForResult(radioAlerts, ChannelPlaylist.REQUEST_CODE_CHANNEL_UPLOAD);
					notifyDataSetChanged();	
				}
			}
		});
        
       holder.swipe_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listPlaylist.get(position).getImgAddRes() == R.drawable.gray_no_add){
					listPlaylist.get(position).setImgAddRes(R.drawable.green_before_add);
					notifyDataSetChanged();
					new UpdateAdvertisementStatus().execute(listPlaylist.get(position).getUnique_video_id(), "beforeAdd");

				
				}else if(listPlaylist.get(position).getImgAddRes() == R.drawable.green_before_add){
					listPlaylist.get(position).setImgAddRes(R.drawable.blue_after_add);
					notifyDataSetChanged();	
					new UpdateAdvertisementStatus().execute(listPlaylist.get(position).getUnique_video_id(), "afterAdd");

				
				}else if(listPlaylist.get(position).getImgAddRes() == R.drawable.blue_after_add){
					listPlaylist.get(position).setImgAddRes(R.drawable.both_add);
					notifyDataSetChanged();
					new UpdateAdvertisementStatus().execute(listPlaylist.get(position).getUnique_video_id(),  "bothAdd");

				}else if(listPlaylist.get(position).getImgAddRes() == R.drawable.both_add){
					listPlaylist.get(position).setImgAddRes(R.drawable.gray_no_add);
					notifyDataSetChanged();
					new UpdateAdvertisementStatus().execute(listPlaylist.get(position).getUnique_video_id(), "noAdd");
				
				}else{
					listPlaylist.get(position).setImgAddRes(R.drawable.gray_no_add);
					notifyDataSetChanged();
					new UpdateAdvertisementStatus().execute(listPlaylist.get(position).getUnique_video_id(), "noAdd");

				}
			}
		});
		
		holder.swipe_broadcast_playlist.setBackgroundResource(listPlaylist.get(position).getImgBroadCastRes());
		holder.swipe_check_uncheck_playlist.setBackgroundResource(listPlaylist.get(position).getImageResource());
		holder.swipe_add.setBackgroundResource(listPlaylist.get(position).getImgAddRes());
		return convertView;
	}

	class ViewHolder {
		TextView tv_title_video_playlist, tv_description_video_playlist;
		ImageView img_video_playlist;
		RelativeTimeTextView tv_time;
		RelativeLayout front_channel_playlist;
		Button swipe_check_uncheck_playlist, swipe_broadcast_playlist, swipe_add;
	}
	
	// Declare UpdateStatus NewsLetter...
	public static class UpdateVideoStatus extends AsyncTask<String, Void, JSONObject> {
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
				String url = Config.ROOT_SERVER_CLIENT + Config.UPDATE_VIDEO_STATUS;

				HttpPost httppost = new HttpPost(url);

				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					multipartEntity.addPart("playlist_id", new StringBody(params[0]));
					multipartEntity.addPart("video_id", new StringBody(params[1]));
					multipartEntity.addPart("single_status", new StringBody(params[2]));
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
				//Log.e("result", "result?? "+result);
			}
		}
	
	// Declare UpdateStatus Advertisement...
	public static class UpdateAdvertisementStatus extends
			AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT + Config.UPDATE_ADVERTISEMENT_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("unique_video_id", new StringBody(
						params[0]));
				multipartEntity
						.addPart("add_status", new StringBody(params[1]));
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
	
	
	public ArrayList<GetterSetter> getList(){
		return listPlaylist;
	}

}
