package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.adapter.ChannelManagementAdapter;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelManagement extends Activity {
    private static ChannelManagement mContext;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SwipeListView lv_radio_lectures;
    private ImageButton img_pencil_icon;
    private TextView tv_channels_title;
    private View views_titile_channel;
    private ArrayList<GetterSetter> list_getAllChannels;
    private ServiceHandler handle;
    private final int GET = 1;
    private String response;
    private ProgressBar pDialog;
    private ChannelManagementAdapter adapter;
    public static int REQUEST_CODE_CHANNEL_UPLOAD = 900;
    public static int REQUEST_CODE_LIVE_BROADCAST = 100;

    // ChannelManagement Instance
    public static ChannelManagement getInstance() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.activity_open_translate,
                R.anim.activity_close_scale);
        // Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
        setContentView(R.layout.channelmanagement);
        mContext = ChannelManagement.this;
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        initializeView();
    }

    // initialize View Here...
    private void initializeView() {
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        lv_radio_lectures = (SwipeListView) findViewById(R.id.lv_channels);
        lv_radio_lectures.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        lv_radio_lectures.setOffsetRight(convertDpToPixel(80f));
        lv_radio_lectures.setAnimationTime(500);
        lv_radio_lectures.setSwipeOpenOnLongPress(false);
        img_pencil_icon = (ImageButton) findViewById(R.id.img_pencil_icon);
        img_pencil_icon.setVisibility(View.GONE);
        tv_channels_title = (TextView) findViewById(R.id.tv_channels_title);
        tv_channels_title.setVisibility(View.VISIBLE);
        tv_channels_title.setText(getResources().getString(R.string.management_endowment));
        views_titile_channel = (View) findViewById(R.id.views_titile_channel);
        views_titile_channel.setVisibility(View.VISIBLE);

        lv_radio_lectures.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action,
                                    boolean right) {
            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {
                if (ChannelManagementAdapter.getInstance().getPlaylist().get(position).getTag().equals("video")) {

                    Intent channelIntent = new Intent(ChannelManagement.this, ChannelPlaylist.class);
                    channelIntent.putExtra("playlist_id", ChannelManagementAdapter.getInstance().getPlaylist().get(position).getPlayListId());
                    startActivity(channelIntent);

                } else if (ChannelManagementAdapter.getInstance().getPlaylist().get(position).getTag().equals("live")) {

                    Intent channelIntent = new Intent(ChannelManagement.this, LiveBroadcastList.class);
                    startActivityForResult(channelIntent, REQUEST_CODE_LIVE_BROADCAST);

                }
            }

            @Override
            public void onClickBackView(int position) {
                lv_radio_lectures.closeAnimate(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        if (isInternetPresent) {

            list_getAllChannels = new ArrayList<GetterSetter>();
            new GetAllChannels().execute();

        } else {
            Utilities.showAlertDialog(this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    // Get All Channels For Channel Management Page
    class GetAllChannels extends AsyncTask<Void, Void, ArrayList<GetterSetter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<GetterSetter> doInBackground(Void... arg0) {

            handle = new ServiceHandler();
            response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT
                    + Config.GET_YOUTUBE_CHANNELS, GET);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayItem = jsonObject.getJSONArray("value");

                    if (jsonArrayItem.length() > 0) {

                        for (int i = 0; i < jsonArrayItem.length(); i++) {
                            JSONObject jsonObj = jsonArrayItem.getJSONObject(i);
                            GetterSetter getset = new GetterSetter();
                            getset.setId(jsonObj.getString("id"));
                            getset.setPlayListId(jsonObj.getString("playlist_id"));
                            getset.setStatus(jsonObj.getString("status"));
                            getset.setImage(jsonObj.getString("image"));
                            getset.setTitle(jsonObj.getString("title"));
                            getset.setContentDetails(jsonObj.getString("itemcount"));
                            getset.setPriority(jsonObj.getString("priority"));

                            if (jsonObj.getString("tag").equals("video")) {
                                getset.setTag(jsonObj.getString("tag"));
                                getset.setColor(getResources().getColor(R.color.list_marron));
                                getset.setText_color(getResources().getColor(R.color.White));
                                getset.setTime_hr(jsonObj.getString("time_hr1"));
                                getset.setTime_min(jsonObj.getString("time_min1"));
                                getset.setTime_hr_two(jsonObj.getString("time_hr2"));
                                getset.setTime_min_two(jsonObj.getString("time_min2"));

                            } else if (jsonObj.getString("tag").equals("pdf")) {
                                getset.setTag(jsonObj.getString("tag"));
                                getset.setColor(getResources().getColor(R.color.list_yellow));
                                getset.setText_color(getResources().getColor(R.color.Black));
                                getset.setTime_hr(jsonObj.getString("time_hr1"));
                                getset.setTime_min(jsonObj.getString("time_min1"));
                                getset.setTime_hr_two(jsonObj.getString("time_hr2"));
                                getset.setTime_min_two(jsonObj.getString("time_min2"));

                            } else if (jsonObj.getString("tag").equals("live")) {
                                getset.setTag(jsonObj.getString("tag"));
                                getset.setColor(getResources().getColor(R.color.list_blue));
                                getset.setText_color(getResources().getColor(R.color.White));
                                getset.setTime_hr(jsonObj.getString("time_hr1"));
                                getset.setTime_min(jsonObj.getString("time_min1"));
                                getset.setTime_hr_two(jsonObj.getString("time_hr2"));
                                getset.setTime_min_two(jsonObj.getString("time_min2"));
                            }

                            if (jsonObj.has("broadcast_status")) {
                                getset.setPublish_status(jsonObj.getString("broadcast_status"));

                                if (jsonObj.getString("broadcast_status").equals("true")) {

                                    getset.setImgBroadCastRes(R.drawable.broadcast_sms);

                                } else {

                                    getset.setImgBroadCastRes(R.drawable.broadcast_sms_gray);

                                }

                            } else {
                                getset.setPublish_status("");
                            }


                            if (jsonObj.getString("status").equals("true")) {

                                getset.setImageResource(R.drawable.tick_icon);

                            } else {
                                getset.setImageResource(R.drawable.tick_gray);

                            }

                            if (jsonObj.has("sequence_status")) {
                                if (jsonObj.getString("sequence_status").equals("true")) {

                                    getset.setImgSeq(R.drawable.sequence_icon_green);

                                } else {

                                    getset.setImgSeq(R.drawable.sequence_icon_gray);

                                }
                            } else {
                                getset.setImgSeq(R.drawable.sequence_icon_gray);
                            }

                            list_getAllChannels.add(getset);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Utilities.showAlertDialog(ChannelManagement.this,
                                "Internet Connection Error",
                                "Your internet connection is too slow...",
                                false);
                    }
                });
            }
            return list_getAllChannels;
        }

        @Override
        protected void onPostExecute(ArrayList<GetterSetter> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.size() > 0) {
                    adapter = new ChannelManagementAdapter(
                            ChannelManagement.this, result);
                    lv_radio_lectures.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            pDialog.setVisibility(View.GONE);
        }
    }


    // Get Swipe List View
    public SwipeListView getSwipeList() {
        return lv_radio_lectures;
    }

    // onActivity Result Called Here...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_LIVE_BROADCAST) {
            if (isInternetPresent) {

                if (list_getAllChannels != null) {
                    list_getAllChannels.clear();
                    list_getAllChannels = new ArrayList<GetterSetter>();
                    new GetAllChannels().execute();
                }

            } else {
                Utilities.showAlertDialog(this, "Internet Connection Error",
                        "Please connect to working Internet connection", false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent mintent = new Intent();
        setResult(RESULT_OK, mintent);
        finish();
        overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        super.onBackPressed();
    }

}
