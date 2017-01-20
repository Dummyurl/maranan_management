package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.marananmanagement.R;
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

public class VideoAdvertisementAdapter extends BaseAdapter {
	private static VideoAdvertisementAdapter mContext;
	Context ctx;
	ArrayList<GetterSetter> listPlaylist;
	public ViewHolder holder;
    private String className;

	// ChannelPlayListAdapter Instance
	public static VideoAdvertisementAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public VideoAdvertisementAdapter(Context ctx,
			ArrayList<GetterSetter> listPlaylist, String className) {
		this.ctx = ctx;
		this.listPlaylist = listPlaylist;
		this.className=className;
		mContext = VideoAdvertisementAdapter.this;
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
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.video_advertisement_items, null, false);
			holder.tv_title_video_add = (TextView) convertView.findViewById(R.id.tv_title_video_add);
			holder.tv_video_count = (TextView) convertView.findViewById(R.id.tv_video_count);
			holder.tv_des_video_add = (TextView) convertView.findViewById(R.id.tv_des_video_add);
			holder.img_video_add = (ImageView) convertView.findViewById(R.id.img_video_add);
			holder.swipe_check_uncheck = (Button) convertView.findViewById(R.id.swipe_check_uncheck);
			holder.front_video_add = (RelativeLayout) convertView.findViewById(R.id.front_video_add);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_title_video_add.setTag(position);
		holder.tv_des_video_add.setTag(position);
		holder.img_video_add.setTag(position);
		holder.swipe_check_uncheck.setTag(position);
		holder.front_video_add.setTag(position);
		holder.tv_video_count.setTag(position);
		
		holder.tv_video_count.setText(String.valueOf(position+1));

		if (!listPlaylist.get(position).getStatus().equals("") && listPlaylist.get(position).getStatus().equals("true")) {
			holder.front_video_add.setBackgroundColor(Color
					.parseColor("#CAEBFC"));
		} else {
			holder.front_video_add.setBackgroundColor(Color
					.parseColor("#FFFFFF"));
		}
		
		if (!listPlaylist.get(position).getTitle().equals(""))
			holder.tv_title_video_add.setText(Utilities.decodeImoString(listPlaylist.get(position).getTitle()));

		if (!listPlaylist.get(position).getDescriptions().equals(""))
			holder.tv_des_video_add.setText(Utilities.decodeImoString(listPlaylist.get(position).getDescriptions()));
		

		if (!listPlaylist.get(position).getImage().equals(""))
		Picasso.with(ctx)
				.load(listPlaylist.get(position).getImage())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_video_add);

		holder.swipe_check_uncheck
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(!className.equals("AlertEditPage")){
							if (listPlaylist.get(position).getImageResource() == R.drawable.tick_icon) {
								listPlaylist.get(position).setImageResource(
										R.drawable.tick_gray);
								listPlaylist.get(position).setStatus("false");
								notifyDataSetChanged();
								new UpdateAdvertisementVideoStatus().execute(listPlaylist
										.get(position).getUnique_video_id(), "false");

							} else {

								listPlaylist.get(position).setImageResource(
										R.drawable.tick_icon);
								listPlaylist.get(position).setStatus("true");
								notifyDataSetChanged();
								new UpdateAdvertisementVideoStatus().execute(listPlaylist
										.get(position).getUnique_video_id(), "true");

							}
						}else if(className.equals("AlertEditPage")){
							if (listPlaylist.get(position).getImageResource() == R.drawable.tick_icon) {
								listPlaylist.get(position).setImageResource(
										R.drawable.tick_gray);
								listPlaylist.get(position).setStatus("false");
								notifyDataSetChanged();
								if (!listPlaylist.get(position).getImage().equals("")) {
									Intent mintent = new Intent();
									mintent.putExtra("imagepaths", listPlaylist.get(position).getImage());
									((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
									((Activity) ctx).finish();
									((Activity) ctx).overridePendingTransition(
											R.anim.activity_open_scale,
											R.anim.activity_close_translate);
								}

							} else {

								listPlaylist.get(position).setImageResource(
										R.drawable.tick_icon);
								listPlaylist.get(position).setStatus("true");
								notifyDataSetChanged();
								if (!listPlaylist.get(position).getImage().equals("")) {
									Intent mintent = new Intent();
									mintent.putExtra("imagepaths", listPlaylist.get(position).getImage());
									((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
									((Activity) ctx).finish();
									((Activity) ctx).overridePendingTransition(
											R.anim.activity_open_scale,
											R.anim.activity_close_translate);
								}

							}
						}

					}
				});

		holder.swipe_check_uncheck.setBackgroundResource(listPlaylist
				.get(position).getImageResource());
		return convertView;
	}

	class ViewHolder {
		TextView tv_title_video_add, tv_des_video_add, tv_video_count;
		ImageView img_video_add;
		RelativeLayout front_video_add;
		Button swipe_check_uncheck;
	}

	// Declare UpdateAdvertisementVideoStatus
	public static class UpdateAdvertisementVideoStatus extends
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
			String url = Config.ROOT_SERVER_CLIENT + Config.UPDATE_VIDEO_ADVERTISEMENT_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("unique_video_id",new StringBody(params[0]));
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
			Log.e("result", "result?? "+result);
		}
	}

}
