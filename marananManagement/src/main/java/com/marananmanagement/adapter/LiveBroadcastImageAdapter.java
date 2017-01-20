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

import com.marananmanagement.LiveBroadcastImages;
import com.marananmanagement.R;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.GetterSetter;
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

public class LiveBroadcastImageAdapter extends BaseAdapter {
	private static LiveBroadcastImageAdapter mContext;
	Context ctx;
	ArrayList<GetterSetter> live_list;
	public ViewHolder holder;
	public String imagestatus = "false";
	String image_old;
	String className;

	// ChannelPlayListAdapter Instance
	public static LiveBroadcastImageAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public LiveBroadcastImageAdapter(Context ctx,
			ArrayList<GetterSetter> live_list, String image_old, String className) {
		this.ctx = ctx;
		this.image_old = image_old;
		mContext = LiveBroadcastImageAdapter.this;
		this.live_list = live_list;
		this.className = className;
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
			convertView = inflater.inflate(R.layout.live_images_items, null,
					false);
			holder.img_Channel = (ImageView) convertView
					.findViewById(R.id.img_live_images);
			holder.swipe_check_uncheck = (Button) convertView
					.findViewById(R.id.live_check_uncheck);
			holder.swipe_delete = (Button) convertView
					.findViewById(R.id.live_delete);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.swipe_check_uncheck.setTag(position);
		holder.img_Channel.setTag(position);
		holder.swipe_delete.setTag(position);

		Picasso.with(ctx)
				.load(live_list.get(position).getImage())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_Channel);

		holder.swipe_check_uncheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(!className.equals("AlertEditPage")){
					if (live_list.get(position).getImageResource() == R.drawable.tick_icon) {
						imagestatus = "false";
						live_list.get(position).setImageResource(R.drawable.tick_gray);
						LiveBroadcastImages.getInstance().setLiveImageName("");
						LiveBroadcastImages.getInstance().setImageStatus(imagestatus);

						if (!live_list.get(position).getImage().equals("")) {
							Intent mintent = new Intent();
							mintent.putExtra("imagepaths", image_old);
							mintent.putExtra("imagename", "");
							mintent.putExtra("imagestatus", imagestatus);
							((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
							((Activity) ctx).finish();
							((Activity) ctx).overridePendingTransition(
									R.anim.activity_open_scale,
									R.anim.activity_close_translate);
						}

						LiveBroadcastImages.getInstance().getSwipeList().closeAnimate(position);
						notifyDataSetChanged();

					} else {

						String imageName = live_list.get(position).getImage()
								.substring(live_list.get(position).getImage().lastIndexOf("/") + 1);
						LiveBroadcastImages.getInstance().setLiveImageName(imageName);
						LiveBroadcastImages.getInstance().setLiveImagePath(live_list.get(position).getImage());
						imagestatus = "true";
						LiveBroadcastImages.getInstance().setImageStatus(imagestatus);

						if (!live_list.get(position).getImage().equals("") && !imageName.equals("")) {
							Intent mintent = new Intent();
							mintent.putExtra("imagepaths", live_list.get(position).getImage());
							mintent.putExtra("imagename", imageName);
							mintent.putExtra("imagestatus", imagestatus);
							((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
							((Activity) ctx).finish();
							((Activity) ctx).overridePendingTransition(
									R.anim.activity_open_scale,
									R.anim.activity_close_translate);
						}

						for (int i = 0; i < live_list.size(); i++) {
							if (position == i) {
								live_list.get(position).setImageResource(
										R.drawable.tick_icon);
							} else {
								live_list.get(i).setImageResource(
										R.drawable.tick_gray);
							}
						}

						LiveBroadcastImages.getInstance().getSwipeList().closeAnimate(position);
						notifyDataSetChanged();
					}
				}else if(className.equals("AlertEditPage")){

					if (live_list.get(position).getImageResource() == R.drawable.tick_icon) {
						live_list.get(position).setImageResource(R.drawable.tick_gray);

						if (!live_list.get(position).getImage().equals("")) {
							Intent mintent = new Intent();
							mintent.putExtra("imagepaths", live_list.get(position).getImage());
							((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
							((Activity) ctx).finish();
							((Activity) ctx).overridePendingTransition(
									R.anim.activity_open_scale,
									R.anim.activity_close_translate);
						}

						LiveBroadcastImages.getInstance().getSwipeList().closeAnimate(position);
						notifyDataSetChanged();

					} else {

						if (!live_list.get(position).getImage().equals("")) {
							Intent mintent = new Intent();
							mintent.putExtra("imagepaths", live_list.get(position).getImage());
							((Activity) ctx).setResult(Activity.RESULT_OK, mintent);
							((Activity) ctx).finish();
							((Activity) ctx).overridePendingTransition(
									R.anim.activity_open_scale,
									R.anim.activity_close_translate);
						}

						for (int i = 0; i < live_list.size(); i++) {
							if (position == i) {
								live_list.get(position).setImageResource(
										R.drawable.tick_icon);
							} else {
								live_list.get(i).setImageResource(
										R.drawable.tick_gray);
							}
						}

						LiveBroadcastImages.getInstance().getSwipeList().closeAnimate(position);
						notifyDataSetChanged();
					}
				}


			}
		});

		holder.swipe_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

					if (live_list.get(position).getImgCancelRes() == R.drawable.cross_gray) {

						live_list.get(position).setImgCancelRes(R.drawable.cross_icon);
						notifyDataSetChanged();

					} else {

						String deleteText = live_list
								.get(position)
								.getImage()
								.substring(live_list.get(position).getImage()
										.lastIndexOf("/") + 1);

						LiveBroadcastImages.getInstance().getSwipeList()
								.closeAnimate(position);
						live_list.remove(String.valueOf(position));
						live_list.remove(position);
						notifyDataSetChanged();
						new DeleteLiveBroadcastImages().execute(deleteText);
					}

			}
		});

		holder.swipe_check_uncheck.setBackgroundResource(live_list.get(position).getImageResource());
		holder.swipe_delete.setBackgroundResource(live_list.get(position).getImgCancelRes());
		return convertView;
	}

	class ViewHolder {
		Button swipe_check_uncheck, swipe_delete;
		ImageView img_Channel;
	}

	public ArrayList<GetterSetter> getList() {
		return live_list;
	}

	// Delete Live Broadcast Images...
	public class DeleteLiveBroadcastImages extends
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
			String url = Config.ROOT_SERVER_CLIENT
					+ Config.DELETE_LIVE_BROADCAST_IMAGE;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("image_name", new StringBody(params[0]));
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
			Log.e("Delete", "Image Response?? " + result);
		}
	}
}
