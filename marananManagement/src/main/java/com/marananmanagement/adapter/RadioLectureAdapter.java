package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.marananmanagement.R;
import com.marananmanagement.RadioAlerts;
import com.marananmanagement.RadioPrograms;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.RelativeTimeTextView;
import com.marananmanagement.util.Utilities;
import com.marananmanagement.util.Utilities.TimerPosition;
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
import java.util.List;

public class RadioLectureAdapter extends BaseAdapter implements TimerPosition,
		OnCompletionListener, OnPreparedListener, OnErrorListener,
		OnSeekBarChangeListener {
	private static RadioLectureAdapter mContext;
	private MediaPlayer mediaPlayer;
	Context ctx;
	// ViewHolder holder;
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	Boolean isHolderFull = false;
	public static boolean isFirstTime = false;
	Boolean isClickEnable = true;
	ArrayList<GetterSetter> listRadioPrograms;
	int pos;
	public int currentPosition = 0;
	private int count = 0;
	private View view;
	private ViewHolder vie;
	public ViewHolder holder;
	private String[] str;

	// RadioLectureAdapter Instance
	public static RadioLectureAdapter getInstance() {
		return mContext;
	}

	@SuppressWarnings("deprecation")
	public RadioLectureAdapter(Context ctxx,
			ArrayList<GetterSetter> listRadioProgram) {
		ctx = ctxx;
		mContext = RadioLectureAdapter.this;
		cd = new ConnectionDetector(ctx);
		mediaPlayer = new MediaPlayer();
		isInternetPresent = cd.isConnectingToInternet();
		listRadioPrograms = listRadioProgram;
	}

	@Override
	public int getCount() {
		return listRadioPrograms.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	List<ViewHolder> dd = new ArrayList<RadioLectureAdapter.ViewHolder>();
	Integer topPosition = null;

	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = new ViewHolder();

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
			convertView = inflater.inflate(R.layout.radio_programs_list_items, null, false);
			holder.btn_list_playpause = (Button) convertView.findViewById(R.id.btn_list_playpause);
			holder.btn_list_playpause.setBackgroundResource(R.drawable.play_arrow_white);
			holder.img_radio_bottom = (ImageView) convertView.findViewById(R.id.img_radio_bottom);
			holder.tv_lecture_title_list = (TextView) convertView.findViewById(R.id.tv_lecture_title_list);
			holder.tv_lecture_title_list.setSelected(false);
			// holder.tv_lecture_title_list.setEllipsize(TruncateAt.MARQUEE);
			holder.tv_lecture_description1 = (TextView) convertView.findViewById(R.id.tv_lecture_description1);
			holder.tv_lecture_description1.setSelected(false);
			// holder.tv_lecture_description1.setEllipsize(TruncateAt.MARQUEE);
			holder.progress_loading_list = (ProgressBar) convertView.findViewById(R.id.progress_loading_list);
			holder.seek_bar_list = (SeekBar) convertView.findViewById(R.id.seek_bar_list);
			holder.seek_bar_list.setOnSeekBarChangeListener(seekBarChangeListener);
			holder.tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time);
			holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
			holder.tv_count_radio = (TextView) convertView.findViewById(R.id.tv_count_radio);
			holder.swipe_check = (Button) convertView.findViewById(R.id.swipe_check);
			holder.swipe_uncheck = (Button) convertView.findViewById(R.id.swipe_uncheck);
			holder.swipe_delete = (Button) convertView.findViewById(R.id.swipe_delete);
			holder.swipe_delete_gray = (Button) convertView.findViewById(R.id.swipe_delete_gray);
			holder.swipe_braodcast_check = (Button) convertView.findViewById(R.id.swipe_braodcast_check);
			holder.swipe_braodcast_uncheck = (Button) convertView.findViewById(R.id.swipe_braodcast_uncheck);
			holder.tv_sms_swipe = (TextView) convertView.findViewById(R.id.tv_sms_swipe);
			holder.tv_count_remaining = (RelativeTimeTextView) convertView.findViewById(R.id.tv_count_remaining);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.btn_list_playpause.setTag(position);
		holder.tv_lecture_title_list.setTag(position);
		holder.tv_lecture_description1.setTag(position);
		holder.img_radio_bottom.setTag(position);
		holder.progress_loading_list.setTag(position);
		holder.seek_bar_list.setTag(position);
		holder.tv_date_time.setTag(position);
		holder.tv_duration.setTag(position);
		holder.swipe_delete.setTag(position);
		holder.swipe_delete_gray.setTag(position);
		holder.swipe_check.setTag(position);
		holder.swipe_uncheck.setTag(position);
		holder.tv_count_radio.setTag(position);
		holder.swipe_braodcast_check.setTag(position);
		holder.tv_sms_swipe.setTag(position);
		holder.tv_count_remaining.setTag(position);
		setButttonUiChanges(position, holder);

		// Set View Holder Position According To ArrayList Response...
		if (isHolderFull == false) {
			if (dd.size() == listRadioPrograms.size()) {

				isHolderFull = true;

			} else {
				if (!dd.contains(holder)) {
					dd.add(position, holder);
				}
			}
		} else {
			isHolderFull = false;
		}

		holder.tv_lecture_title_list.setText(Utilities.decodeImoString(listRadioPrograms.get(position).getTitle()));
		holder.tv_lecture_description1.setText(Utilities.decodeImoString(listRadioPrograms.get(position).getDescriptions()));
		holder.tv_date_time.setText(Utilities.parseDateToddMMyyyy(listRadioPrograms.get(
						position).getDate())
						+ " "
						+ " | "
						+ " "
						+ listRadioPrograms.get(position).getTime());
		holder.tv_duration.setText(listRadioPrograms.get(position).getDuration());

		if(listRadioPrograms.get(position).getImage()!=null && !listRadioPrograms.get(position).getImage().equals("")){
			Picasso.with(ctx)
					.load(listRadioPrograms.get(position).getImage())
					.placeholder(R.drawable.below_thubnails)
					.error(R.drawable.below_thubnails).into(holder.img_radio_bottom);
		}

		holder.tv_count_radio.setText(String.valueOf(listRadioPrograms.get(position).getCount()));

		holder.btn_list_playpause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isInternetPresent) {
					pos = position;

					if (listRadioPrograms.get(position).getImageResource() == R.drawable.play_arrow_white) {

						enableRadioProgram(arg0, position);

					} else if (listRadioPrograms.get(position)
							.getImageResource() == R.drawable.pause_arrow_white) {
						if (isClickEnable) {
							ViewHolder vie2 = dd.get(position);
							vie2.seek_bar_list.setProgress(0);
							vie2.seek_bar_list.setVisibility(View.GONE);
							vie2.tv_date_time.setVisibility(View.GONE);
							vie2.tv_duration.setVisibility(View.VISIBLE);
							vie2.tv_lecture_title_list.setSelected(false);
							vie2.tv_lecture_description1.setSelected(false);
							arg0.setBackgroundResource(R.drawable.play_arrow_white);
							listRadioPrograms.get(position).setImageResource(R.drawable.play_arrow_white);
							notifyDataSetChanged();
							if (mediaPlayer != null && mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
							}
						} else {
							Toast.makeText(ctx,
									"Program Loading Please Wait...",
									Toast.LENGTH_LONG).show();
						}
					}

				} else {
					Utilities.showAlertDialog(ctx, "Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
			}
		});

		holder.swipe_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFirstTime = false;
				ViewHolder vie2 = dd.get(position);
				vie2.swipe_check.setVisibility(View.GONE);
				vie2.swipe_uncheck.setVisibility(View.VISIBLE);
				vie2.tv_count_radio.setVisibility(View.INVISIBLE);
				vie2.tv_count_remaining.setVisibility(View.VISIBLE);
				GetterSetter getset = listRadioPrograms.get(position);
				getset.setPublish_status("false");
				listRadioPrograms.set(position, getset);
				notifyDataSetChanged();
				setPublishCounts();
				new UpdateStatusRadioProgram().execute(
						listRadioPrograms.get(position).getId(), "false");
			}
		});

		holder.swipe_uncheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listRadioPrograms.get(position).getStatus().equals("true")
						&& Utilities.compareDateTime(listRadioPrograms.get(
								position).getDate()
								+ " "
								+ listRadioPrograms.get(position).getTime()
								+ ":00") == true) {

					Toast.makeText(
							ctx,
							ctx.getResources().getString(
									R.string.alerts_message_one),
							Toast.LENGTH_SHORT).show();

				} else if (listRadioPrograms.get(position).getStatus()
						.equals("true")
						&& Utilities.compareDateTime(listRadioPrograms.get(
								position).getDate()
								+ " "
								+ listRadioPrograms.get(position).getTime()
								+ ":00") == false) {
					ViewHolder vie2 = dd.get(position);
					vie2.swipe_check.setVisibility(View.VISIBLE);
					vie2.swipe_uncheck.setVisibility(View.GONE);
					vie2.tv_count_radio.setVisibility(View.VISIBLE);
					vie2.tv_count_remaining.setVisibility(View.INVISIBLE);
					GetterSetter getset = listRadioPrograms.get(position);
					getset.setPublish_status("true");
					listRadioPrograms.set(position, getset);
					notifyDataSetChanged();
					setPublishCounts();
					new UpdateStatusRadioProgram().execute(listRadioPrograms
							.get(position).getId(), "true");

				} else if (listRadioPrograms.get(position).getStatus()
						.equals("false")) {

					Toast.makeText(
							ctx,
							ctx.getResources().getString(
									R.string.alerts_message_two),
							Toast.LENGTH_SHORT).show();

				}
			}
		});

		holder.swipe_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listRadioPrograms.size() > 0)
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							mediaPlayer.release();
						}
					}
				RadioPrograms.getInstance().getClassDelete(position);
				RadioPrograms.getInstance().getSwipeList().closeAnimate(position);
				listRadioPrograms.remove(String.valueOf(position));
				listRadioPrograms.remove(position);
				notifyDataSetChanged();
				isHolderFull = false;
				dd.clear();
				setPublishCounts();

			}
		});

		holder.swipe_delete_gray.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder vie = dd.get(position);
				vie.swipe_delete_gray.setVisibility(View.GONE);
				vie.swipe_delete.setVisibility(View.VISIBLE);
			}
		});

		holder.swipe_braodcast_check
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						RadioPrograms.getInstance().getSwipeList()
								.closeAnimate(position);
					}
				});

		holder.swipe_braodcast_uncheck
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
								mediaPlayer.release();
								holder.btn_list_playpause
										.setBackgroundResource(R.drawable.play_arrow_white);
							}
						}
						
						RadioPrograms.getInstance().getSwipeList().closeAnimate(position);
						Intent radioAlerts = new Intent(ctx, RadioAlerts.class);
						radioAlerts.putExtra("id", listRadioPrograms.get(position).getId());
						radioAlerts.putExtra("image", listRadioPrograms.get(position).getImage());
						radioAlerts.putExtra("activity_name", "RadioProgram");
						((Activity) ctx).startActivityForResult(radioAlerts, RadioPrograms.REQUEST_CODE);
						notifyDataSetChanged();

					}
				});

		holder.btn_list_playpause.setBackgroundResource(listRadioPrograms.get(position).getImageResource());
		return convertView;
	}

	private void setButttonUiChanges(int position, ViewHolder holder) {
		if (listRadioPrograms.get(position).getStatus().equals("true")
				&& Utilities.compareDateTime(listRadioPrograms.get(position)
						.getDate()
						+ " "
						+ listRadioPrograms.get(position).getTime() + ":00") == true) {
			isFirstTime = false;
			holder.swipe_check.setVisibility(View.GONE);
			holder.swipe_uncheck.setVisibility(View.VISIBLE);
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
			holder.tv_count_remaining.setVisibility(View.VISIBLE);

			// Start Count Down Timer Here For Checking How Much Time Are Left
			// To Publish The Program...
			Utilities.getCutTimerClass(ctx, Utilities
					.getDateDiiferece(listRadioPrograms.get(position).getDate()
							+ " " + listRadioPrograms.get(position).getTime()
							+ ":00"), 1000, holder.tv_count_remaining,
					position, this, isFirstTime);
			
			//holder.tv_count_remaining.setReferenceTimeRadio((Utilities.getToDate(listRadioPrograms.get(position).getDate(), listRadioPrograms.get(position).getTime())).getTime(), listRadioPrograms.get(position).getId(), "true", "Radio", position);


		} else if (listRadioPrograms.get(position).getStatus().equals("true")
				&& Utilities.compareDateTime(listRadioPrograms.get(position)
						.getDate()
						+ " "
						+ listRadioPrograms.get(position).getTime() + ":00") == false) {
			holder.swipe_check.setVisibility(View.GONE);
			holder.swipe_uncheck.setVisibility(View.VISIBLE);
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
			holder.tv_count_remaining.setVisibility(View.INVISIBLE);

		} else if (listRadioPrograms.get(position).getStatus().equals("false")) {
			holder.swipe_check.setVisibility(View.GONE);
			holder.swipe_uncheck.setVisibility(View.VISIBLE);
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
			holder.tv_count_remaining.setVisibility(View.INVISIBLE);

		} else {
			holder.swipe_check.setVisibility(View.GONE);
			holder.swipe_uncheck.setVisibility(View.VISIBLE);
			holder.tv_count_radio.setVisibility(View.INVISIBLE);
			holder.tv_count_remaining.setVisibility(View.INVISIBLE);
		}

		if (listRadioPrograms.get(position).getPublish_status().equals("true")) {
			holder.swipe_check.setVisibility(View.VISIBLE);
			holder.swipe_uncheck.setVisibility(View.GONE);
			holder.tv_count_radio.setVisibility(View.VISIBLE);
			holder.tv_count_remaining.setVisibility(View.INVISIBLE);
		}

		if (listRadioPrograms.get(position).getPublish_notification()
				.equals("true")) {
			holder.swipe_braodcast_check.setVisibility(View.VISIBLE);
			holder.swipe_braodcast_uncheck.setVisibility(View.GONE);
		}

	}

	// Get Adapter ArrayList After Delete Item...
	public ArrayList<GetterSetter> getArrayList() {
		return listRadioPrograms;
	}

	// Set Counts According To The Publish Status True Or False...
	public void setPublishCounts() {
		count = 0;
		if (listRadioPrograms.size() > 0) {
			for (int i = 0; i < listRadioPrograms.size(); i++) {
				if (listRadioPrograms.get(i).getPublish_status().equals("true")) {
					GetterSetter getterSetter = listRadioPrograms.get(i);
					count++;
					getterSetter.setCount(count);
					listRadioPrograms.set(i, getterSetter);
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

	// Enable Radio Program to play...
	private void enableRadioProgram(View arg0, int position) {

		if (!mediaPlayer.isPlaying()) {

			if (isClickEnable) {

				vie = dd.get(position);

				if (!listRadioPrograms.get(position).getRadio_programs().equals("")) {
					view = arg0;
					isClickEnable = false;
					vie.progress_loading_list.setVisibility(View.VISIBLE);
					vie.seek_bar_list.setOnSeekBarChangeListener(seekBarChangeListener);
					vie.seek_bar_list.setProgress(0);
					vie.seek_bar_list.setVisibility(View.VISIBLE);
					vie.tv_date_time.setVisibility(View.VISIBLE);
					vie.tv_duration.setVisibility(View.VISIBLE);
					arg0.setVisibility(View.INVISIBLE);
					startFromUrl(listRadioPrograms.get(position).getRadio_programs());

					// Add Values For Notification According To Playing Radio
					// Lecture...
					addNotificationData(listRadioPrograms.get(position)
							.getTitle(), listRadioPrograms.get(position)
							.getDescriptions(), listRadioPrograms.get(position)
							.getDuration(), listRadioPrograms.get(position)
							.getImage(),
							listRadioPrograms.get(position).getTime_hr()
									+ ":"
									+ listRadioPrograms.get(position)
											.getTime_min());
					notifyDataSetChanged();
				} else {

					Toast.makeText(ctx, "No file selected", Toast.LENGTH_LONG)
							.show();
				}

			} else {
				Toast.makeText(ctx, "Program Loading Please Wait...",
						Toast.LENGTH_LONG).show();
			}

		} else {
			isClickEnable = true;
			vie.seek_bar_list.setProgress(0);
			vie.seek_bar_list.setVisibility(View.GONE);
			vie.tv_date_time.setVisibility(View.GONE);
			vie.tv_duration.setVisibility(View.VISIBLE);
			// vie.btn_list_playpause.setBackgroundResource(R.drawable.play_arrow_white);
			listRadioPrograms.get(topPosition).setImageResource(
					R.drawable.play_arrow_white);
			notifyDataSetChanged();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				enableRadioProgram(arg0, position);
			}
		}

	}

	// get Media Player...
	public MediaPlayer getMdPlayer() {
		return mediaPlayer;
	}

	public ArrayList<GetterSetter> getNewArrayList() {
		return listRadioPrograms;

	}

	// Here is the method to start mp3 from url...
	public void startFromUrl(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// String radioUrl = getStreamingURL(URL);
				try {
					mediaPlayer.reset();
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setOnPreparedListener(RadioLectureAdapter.this);
					mediaPlayer.setOnCompletionListener(RadioLectureAdapter.this);
					mediaPlayer.setOnErrorListener(RadioLectureAdapter.this);
					mediaPlayer.setDataSource(ctx, Uri.parse(url));
					mediaPlayer.prepareAsync();

				} catch (IllegalArgumentException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();

				} catch (IllegalStateException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();

				} catch (IOException e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
			}

		}).start();
	}

	// Prepare Media player to play...
	@Override
	public void onPrepared(MediaPlayer mp) {
		isClickEnable = true;
		ViewHolder vi = dd.get(pos);
		topPosition = pos;
		vi.progress_loading_list.setVisibility(View.INVISIBLE);
		view.setVisibility(View.VISIBLE);
		view.setBackgroundResource(R.drawable.pause_arrow_white);
		listRadioPrograms.get(pos).setImageResource(R.drawable.pause_arrow_white);

		vi.tv_lecture_title_list.setSelected(true);
		vi.tv_lecture_title_list.setEllipsize(TruncateAt.MARQUEE);
		vi.tv_lecture_description1.setSelected(true);
		vi.tv_lecture_description1.setEllipsize(TruncateAt.MARQUEE);

		if (!mp.isPlaying()) {
			mp.start();
			updateSeekBar(mediaPlayer, vi.seek_bar_list);
		}
	}

	// Oncompletion Listener To Listen Media Player
	@Override
	public void onCompletion(MediaPlayer mp) {
		isClickEnable = true;
		vie.seek_bar_list.setProgress(0);
		vie.seek_bar_list.setVisibility(View.GONE);
		vie.tv_date_time.setVisibility(View.GONE);
		vie.tv_duration.setVisibility(View.VISIBLE);
		view.setBackgroundResource(R.drawable.play_arrow_white);
		listRadioPrograms.get(pos).setImageResource(R.drawable.play_arrow_white);
		notifyDataSetChanged();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return true;
	}

	// Update Seek Bar
	public void updateSeekBar(final MediaPlayer mediaPlayer,
			final SeekBar seekBar2) {

		// Worker thread that will update the seekbar as each song is playing
		Thread t = new Thread() {
			@Override
			public void run() {

				currentPosition = 0;
				int total = (int) mediaPlayer.getDuration();

				seekBar2.setMax(total);
				while (mediaPlayer != null
						&& mediaPlayer.getCurrentPosition() < total) {
					try {

						Thread.sleep(1000);
						currentPosition = mediaPlayer.getCurrentPosition();
					} catch (InterruptedException e) {
						return;
					} catch (Exception e) {
						return;
					}

					seekBar2.setProgress(mediaPlayer.getCurrentPosition());
				}

			}

		};
		t.start();

	}

	// Declare UpdateStatus Radio Program...
	public static class UpdateStatusRadioProgram extends AsyncTask<String, Void, JSONObject> {
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
					+ Config.UPDATE_STATUS_RADIO_PROGRAM;

			HttpPost httppost = new HttpPost(url);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multipartEntity.addPart("id", new StringBody(params[0]));
				multipartEntity.addPart("publish_status", new StringBody(
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
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	// Set Notification Data
	public String[] addNotificationData(String title, String descriptions,
			String duration, String image, String time) {
		str = new String[5];
		str[0] = title;
		str[1] = descriptions;
		str[2] = duration;
		str[3] = image;
		str[4] = time;
		return str;
	}

	// Get Notification Data
	public String[] getNotificationData() {
		return str;
	}

	@Override
	public void timerFinish(int position) {
		ViewHolder vie2 = dd.get(position);
		vie2.swipe_check.setVisibility(View.VISIBLE);
		vie2.swipe_uncheck.setVisibility(View.GONE);
		vie2.tv_count_radio.setVisibility(View.VISIBLE);
		vie2.tv_count_remaining.setVisibility(View.INVISIBLE);
		GetterSetter getset = listRadioPrograms.get(position);
		getset.setPublish_status("true");
		listRadioPrograms.set(position, getset);
		notifyDataSetChanged();
		setPublishCounts();
		new UpdateStatusRadioProgram().execute(listRadioPrograms.get(position).getId(), "true");
	}

	// Get Button to handle playPause Button...
	public Button getplayPauseButton() {
		return holder.btn_list_playpause;
	}

	// Get Refresh Ui...
	public void getRefreshUi() {
		holder.btn_list_playpause.setBackgroundResource(R.drawable.play_arrow_white);
		notifyDataSetChanged();
	}
	
	public ArrayList<GetterSetter> getList(){
		return listRadioPrograms;
	}

	// SeekBar Change Listener Call Here...
	private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (mediaPlayer != null && fromUser) {
				mediaPlayer.seekTo(progress);
				seekBar.setProgress(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

}
