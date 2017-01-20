package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marananmanagement.NewsLetterList;
import com.marananmanagement.R;
import com.marananmanagement.RadioAlerts;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
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

public class NewsLetterListAdapter extends BaseAdapter {
	private static NewsLetterListAdapter mContext;
	Context ctx;
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	ArrayList<GetterSetter> listNewsLetter;
	private int count = 0;
	public ViewHolder holder;
	public static boolean isFirstTime = false;

	// RadioLectureAdapter Instance
	public static NewsLetterListAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public NewsLetterListAdapter(Context ctxx,
			ArrayList<GetterSetter> listNewsLetter) {
		ctx = ctxx;
		mContext = NewsLetterListAdapter.this;
		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();
		this.listNewsLetter = listNewsLetter;
	}

	@Override
	public int getCount() {
		return listNewsLetter.size();
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
		holder = new ViewHolder();

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
			convertView = inflater.inflate(R.layout.radio_programs_list_items,null, false);
			holder.img_radio_bottom = (ImageView) convertView.findViewById(R.id.img_radio_bottom);
			holder.tv_lecture_title_list = (TextView) convertView.findViewById(R.id.tv_lecture_title_list);
			holder.tv_lecture_title_list.setPadding(0, 20, 0, 0);
			holder.tv_count_radio = (TextView) convertView.findViewById(R.id.tv_count_radio);
			holder.tv_count_remaining = (RelativeTimeTextView) convertView.findViewById(R.id.tv_count_remaining);
			holder.swipe_check = (Button) convertView.findViewById(R.id.swipe_check);
			holder.swipe_check.setVisibility(View.VISIBLE);
			holder.swipe_uncheck = (Button) convertView.findViewById(R.id.swipe_uncheck);
			holder.swipe_uncheck.setVisibility(View.GONE);
			
			holder.swipe_delete = (Button) convertView.findViewById(R.id.swipe_delete);
			holder.swipe_delete.setVisibility(View.VISIBLE);
			holder.swipe_delete_gray = (Button) convertView.findViewById(R.id.swipe_delete_gray);
			holder.swipe_delete_gray.setVisibility(View.GONE);
			
			holder.swipe_braodcast_check = (Button) convertView.findViewById(R.id.swipe_braodcast_check);
			holder.swipe_braodcast_check.setVisibility(View.VISIBLE);
			holder.swipe_braodcast_uncheck = (Button) convertView.findViewById(R.id.swipe_braodcast_uncheck);
			holder.swipe_braodcast_uncheck.setVisibility(View.GONE);
			
			// Here To Set UI For GONE State For NewsLetter List Adapter...
			holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
			holder.tv_duration.setVisibility(View.VISIBLE);
			holder.tv_duration.setPadding(70, 0, 0, 0);
			holder.tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time);
			holder.tv_date_time.setVisibility(View.GONE);
			holder.tv_lecture_description1 = (TextView) convertView.findViewById(R.id.tv_lecture_description1);
			holder.tv_lecture_description1.setVisibility(View.VISIBLE);
			holder.tv_lecture_description1.setGravity(Gravity.RIGHT);
			holder.progress_loading_list = (ProgressBar) convertView.findViewById(R.id.progress_loading_list);
			holder.progress_loading_list.setVisibility(View.GONE);
			holder.seek_bar_list = (SeekBar) convertView.findViewById(R.id.seek_bar_list);
			holder.seek_bar_list.setVisibility(View.GONE);
			holder.btn_list_playpause = (Button) convertView.findViewById(R.id.btn_list_playpause);
			holder.btn_list_playpause.setBackgroundResource(R.drawable.play_arrow_white);
			holder.btn_list_playpause.setVisibility(View.GONE);
			holder.tv_sms_swipe = (TextView) convertView.findViewById(R.id.tv_sms_swipe);
			holder.tv_sms_swipe.setVisibility(View.GONE);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_lecture_title_list.setTag(position);
		holder.img_radio_bottom.setTag(position);
		holder.swipe_delete.setTag(position);
		holder.swipe_delete_gray.setTag(position);
		holder.swipe_check.setTag(position);
		holder.swipe_uncheck.setTag(position);
		holder.tv_count_radio.setTag(position);
		holder.swipe_braodcast_check.setTag(position);
		holder.tv_count_remaining.setTag(position);
		holder.tv_lecture_description1.setTag(position);

		holder.tv_lecture_title_list.setText(Utilities.decodeImoString(listNewsLetter.get(position).getTitle()));
		Picasso.with(ctx)
				.load(listNewsLetter.get(position).getImage_thumb())
				.placeholder(R.drawable.below_thubnails)
				.error(R.drawable.below_thubnails).into(holder.img_radio_bottom);
		holder.tv_duration.setText(listNewsLetter.get(position).getPdf_pages());
		holder.tv_lecture_description1.setText(Utilities.parseDateToddMMyyyy2(listNewsLetter.get(position).getDate()) +" | "+listNewsLetter.get(position).getTime());
		
		if (!String.valueOf(listNewsLetter.get(position).getCount()).equals("0")) {
			
			holder.tv_count_radio.setVisibility(View.VISIBLE);
			holder.tv_count_radio.setText(String.valueOf(listNewsLetter.get(position).getCount()));
		}else{
			
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
		}
		
		if (Utilities.compareDateTime(listNewsLetter.get(position).getDate()
				+ " "
				+ listNewsLetter.get(position).getTime() + ":00") == true) {
			holder.tv_count_remaining.setVisibility(View.VISIBLE);
			
			// Start Count Down Timer Here For Checking How Much Time Are Left
			// To Publish The NewsLetter...
			holder.tv_count_remaining.setReferenceTimeNews((Utilities.getToDate(listNewsLetter.get(position).getDate(), listNewsLetter.get(position).getTime())).getTime(), listNewsLetter.get(position).getId(), "true", "NewsLetter", position);

			holder.swipe_check.setBackgroundResource(R.drawable.tick_gray);
			listNewsLetter.get(position).setImgCheckUncheckRes(R.drawable.tick_gray);
			listNewsLetter.get(position).setPublish_status("false");
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
			listNewsLetter.get(position).setCount(0);
			notifyDataSetChanged();
			setPublishCounts();
			new UpdateStatusNewsLetter().execute(listNewsLetter.get(position).getId(), "false");
		
		}else{
			
			holder.tv_count_remaining.setVisibility(View.INVISIBLE);
		
		}

		holder.swipe_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					if(listNewsLetter.get(position).getImgCheckUncheckRes() == R.drawable.tick_gray){
						if (Utilities.compareDateTime(listNewsLetter.get(
										position).getDate()
										+ " "
										+ listNewsLetter.get(position).getTime()
										+ ":00") == true) {

							Toast.makeText(
									ctx,
									ctx.getResources().getString(
											R.string.alerts_message_one),
									Toast.LENGTH_SHORT).show();

						}else{
							NewsLetterList.getInstance().getSwipeList().closeAnimate(position);
							holder.swipe_check.setBackgroundResource(R.drawable.tick_icon);
							listNewsLetter.get(position).setImgCheckUncheckRes(R.drawable.tick_icon);
							listNewsLetter.get(position).setPublish_status("true");
							notifyDataSetChanged();
							setPublishCounts();
							new UpdateStatusNewsLetter().execute(listNewsLetter.get(position).getId(), "true");
						}
						
					
					}else{
						isFirstTime = false;
						NewsLetterList.getInstance().getSwipeList().closeAnimate(position);
						holder.swipe_check.setBackgroundResource(R.drawable.tick_gray);
						listNewsLetter.get(position).setImgCheckUncheckRes(R.drawable.tick_gray);
						listNewsLetter.get(position).setPublish_status("false");
						listNewsLetter.get(position).setCount(0);
						notifyDataSetChanged();
						setPublishCounts();
						new UpdateStatusNewsLetter().execute(listNewsLetter.get(position).getId(), "false");
					}
				} catch (Exception e) {
					Log.e("Exception", "Exception?? "+e.getMessage());
				}
				
			}
		});

		holder.swipe_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if(listNewsLetter.get(position).getImgCancelRes() == R.drawable.cross_gray){
						holder.swipe_delete.setBackgroundResource(R.drawable.cross_icon);
						listNewsLetter.get(position).setImgCancelRes(R.drawable.cross_icon);
						notifyDataSetChanged();
					
					}else{
						
						NewsLetterList.getInstance().getClassDelete(position);
						NewsLetterList.getInstance().getSwipeList().closeAnimate(position);
						listNewsLetter.remove(String.valueOf(position));
						listNewsLetter.remove(position);
						notifyDataSetChanged();
						setPublishCounts();
					}
				} catch (Exception e) {
					Log.e("Exception", "Exception?? "+e.getMessage());
				}
				
			}
		});

		holder.swipe_braodcast_check.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							if(listNewsLetter.get(position).getImgBroadCastRes() == R.drawable.broadcast_sms_gray){
								holder.swipe_check.setBackgroundResource(R.drawable.broadcast_sms);
								listNewsLetter.get(position).setImgBroadCastRes(R.drawable.broadcast_sms);
								NewsLetterList.getInstance().getSwipeList().closeAnimate(position);
								
								Intent radioAlerts = new Intent(ctx, RadioAlerts.class);
								radioAlerts.putExtra("id", listNewsLetter.get(position).getId());
								radioAlerts.putExtra("image", listNewsLetter.get(position).getImage());
								radioAlerts.putExtra("activity_name", "NewsLetter");
								((Activity) ctx).startActivityForResult(radioAlerts, NewsLetterList.REQUEST_CODE_NEWSLETTER_UPLOAD);
								notifyDataSetChanged();
							
							}else{
								NewsLetterList.getInstance().getSwipeList().closeAnimate(position);
							}	
						} catch (Exception e) {
							Log.e("Exception", "Exception?? "+e.getMessage());
						}
					}
				});
		
		
		holder.swipe_check.setBackgroundResource(listNewsLetter.get(position).getImgCheckUncheckRes());
		holder.swipe_braodcast_check.setBackgroundResource(listNewsLetter.get(position).getImgBroadCastRes());
		holder.swipe_delete.setBackgroundResource(listNewsLetter.get(position).getImgCancelRes());
		return convertView;
	}

	// Get Adapter ArrayList After Delete Item...
	public ArrayList<GetterSetter> getArrayList() {
		return listNewsLetter;
	}

	// Set Counts According To The Publish Status True Or False...
	public void setPublishCounts() {
		count = 0;
		if (listNewsLetter.size() > 0) {
			for (int i = 0; i < listNewsLetter.size(); i++) {
				if (listNewsLetter.get(i).getPublish_status().equals("true")) {
					GetterSetter getterSetter = listNewsLetter.get(i);
					count++;
					getterSetter.setCount(count);
					listNewsLetter.set(i, getterSetter);
					notifyDataSetChanged();
				}
			}
		}
	}

	class ViewHolder {
		Button btn_list_playpause;
		Button swipe_check, swipe_uncheck;
		Button swipe_delete, swipe_delete_gray;
		Button swipe_braodcast_check, swipe_braodcast_uncheck;
		ImageView img_radio_bottom;
		TextView tv_lecture_title_list, tv_lecture_description1, tv_date_time,
				tv_sms_swipe, tv_duration, tv_count_radio;
		RelativeTimeTextView tv_count_remaining;
		ProgressBar progress_loading_list;
		SeekBar seek_bar_list;
	}
	

	public ArrayList<GetterSetter> getNewArrayListNewsLetter() {
		return listNewsLetter;
	}


	// Declare UpdateStatus NewsLetter...
	public static class UpdateStatusNewsLetter extends AsyncTask<String, Void, JSONObject> {
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
			String url = Config.ROOT_SERVER_CLIENT + Config.UPDATE_NEWSLETTER_STATUS;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				multipartEntity.addPart("publish_status", new StringBody(params[1]));
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

//	@Override
//	public void timerFinish(int position) {
//		listNewsLetter.get(position).setImgCheckUncheckRes(R.drawable.tick_icon);
//		listNewsLetter.get(position).setPublish_status("true");
//		notifyDataSetChanged();
//		setPublishCounts();
//		new UpdateStatusNewsLetter().execute(listNewsLetter.get(position).getId(), "true");
//	}
	
	public ArrayList<GetterSetter> getList(){
		return listNewsLetter;
	}
}
