package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.ChannelPlayListAdapter;
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

public class ChannelPlaylist extends Activity {
    private static ChannelPlaylist mContext;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SwipeListView lv_channel_playlist;
    private ServiceHandler handle;
    private final int GET = 1;
    private ChannelPlayListAdapter adapter;
    private ProgressBar pDialog;
    public static int REQUEST_CODE_CHANNEL_UPLOAD = 100;
    private ArrayList<GetterSetter> list_youtubeVidID;
    private GetterSetter getset;

    // ChannelPlaylist Instance
    public static ChannelPlaylist getInstance() {
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
        setContentView(R.layout.channelplaylist);
        mContext = ChannelPlaylist.this;
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        initializeView();
    }

    // initialize View Here...
    private void initializeView() {
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        lv_channel_playlist = (SwipeListView) findViewById(R.id.lv_channel_playlist);
        lv_channel_playlist.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        lv_channel_playlist.setOffsetRight(convertDpToPixel(80f));
        lv_channel_playlist.setAnimationTime(500);
        lv_channel_playlist.setSwipeOpenOnLongPress(false);

        lv_channel_playlist
                .setSwipeListViewListener(new BaseSwipeListViewListener() {
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
                        lv_channel_playlist.closeAnimate(position);
                        Intent channelIntent = new Intent(ChannelPlaylist.this, ChannelUpload.class);
                        channelIntent.putExtra("playlist_id", list_youtubeVidID.get(position).getPlayListId());
                        channelIntent.putExtra("video_id", list_youtubeVidID.get(position).getVid_Id());
                        channelIntent.putExtra("title", list_youtubeVidID.get(position).getTitle());
                        channelIntent.putExtra("description", list_youtubeVidID.get(position).getDescriptions());
                        channelIntent.putExtra("image", list_youtubeVidID.get(position).getImage());
                        channelIntent.putExtra("image_old", list_youtubeVidID.get(position).getImage_old());
                        channelIntent.putExtra("image_status", list_youtubeVidID.get(position).getImage_status());
                        channelIntent.putExtra("time", list_youtubeVidID.get(position).getTime());
                        channelIntent.putExtra("date", list_youtubeVidID.get(position).getDate());
                        channelIntent.putExtra("days_select", list_youtubeVidID.get(position).getDays_select());
                        channelIntent.putExtra("unique_video_id", list_youtubeVidID.get(position).getUnique_video_id());
                        startActivityForResult(channelIntent, REQUEST_CODE_CHANNEL_UPLOAD);
                    }

                    @Override
                    public void onClickBackView(int position) {
                        lv_channel_playlist.closeAnimate(position);
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {

                    }

                });

        if (isInternetPresent) {
            if (getIntent().getStringExtra("playlist_id") != null && !getIntent().getStringExtra("playlist_id").equals("")) {


                new GetYouTubeVideosID().execute(getIntent().getStringExtra("playlist_id"));

            }

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

    // Get You Tube Video ID
    public class GetYouTubeVideosID extends AsyncTask<String, String, ArrayList<GetterSetter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<GetterSetter> doInBackground(String... playlistID) {
            handle = new ServiceHandler();
            list_youtubeVidID = new ArrayList<GetterSetter>();

            String response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT + Config.GET_CHANNEL_PLAYLIST
                    + Config.KEY_PLAYLIST + playlistID[0], GET);
            if (response != null) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayItem = jsonObject.getJSONArray("value");

                    if (jsonArrayItem.length() > 0) {
                        for (int i = 0; i < jsonArrayItem.length(); i++) {
                            getset = new GetterSetter();
                            JSONObject jsonObj = jsonArrayItem.getJSONObject(i);
                            getset.setPlayListId(jsonObj.getString("playlist_id"));
                            getset.setTitle(jsonObj.getString("title"));
                            getset.setDescriptions(jsonObj.getString("description"));
                            getset.setVid_Id(jsonObj.getString("video_id"));
                            getset.setImage(jsonObj.getString("image"));
                            getset.setImage_old(jsonObj.getString("image_old"));
                            getset.setImage_status(jsonObj.getString("image_status"));
                            getset.setContentDetails(jsonObj.getString("itemcounts"));
                            getset.setStatus(jsonObj.getString("status"));
                            getset.setUnique_video_id(jsonObj.getString("unique_video_id"));
                            getset.setDate(jsonObj.getString("date"));
                            getset.setTime(jsonObj.getString("time"));
                            getset.setDays_select(jsonObj.getString("days_select"));

                            if (jsonObj.getString("status_single").equals("true")) {
                                getset.setImageResource(R.drawable.tick_icon);
                                getset.setPublish_status(jsonObj.getString("status_single"));
                            } else {
                                getset.setImageResource(R.drawable.tick_gray);
                                getset.setPublish_status(jsonObj.getString("status_single"));
                            }

                            if (jsonObj.getString("broadcast_status").equals("true")) {
                                getset.setImgBroadCastRes(R.drawable.broadcast_sms);
                                getset.setPublish_notification(jsonObj.getString("broadcast_status"));
                            } else {
                                getset.setImgBroadCastRes(R.drawable.broadcast_sms_gray);
                                getset.setPublish_notification(jsonObj.getString("broadcast_status"));
                            }

                            if (jsonObj.has("advertisment_status")) {
                                if (jsonObj.getString("advertisment_status").equals("noAdd")) {
                                    getset.setImgAddRes(R.drawable.gray_no_add);

                                } else if (jsonObj.getString("advertisment_status").equals("beforeAdd")) {
                                    getset.setImgAddRes(R.drawable.green_before_add);

                                } else if (jsonObj.getString("advertisment_status").equals("afterAdd")) {
                                    getset.setImgAddRes(R.drawable.blue_after_add);

                                } else if (jsonObj.getString("advertisment_status").equals("bothAdd")) {
                                    getset.setImgAddRes(R.drawable.both_add);

                                } else {
                                    getset.setImgAddRes(R.drawable.gray_no_add);

                                }
                            } else {
                                getset.setImgAddRes(R.drawable.gray_no_add);

                            }

                            list_youtubeVidID.add(getset);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Utilities.showAlertDialog(ChannelPlaylist.this,
                                "Internet Connection Error",
                                "Your internet connection is too slow...",
                                false);
                    }
                });
            }
            return list_youtubeVidID;
        }

        @Override
        protected void onPostExecute(ArrayList<GetterSetter> result) {
            super.onPostExecute(result);
            pDialog.setVisibility(View.GONE);
            if (result != null) {
                if (result.size() > 0) {
                    adapter = new ChannelPlayListAdapter(ChannelPlaylist.this, result);
                    lv_channel_playlist.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANNEL_UPLOAD) {
            if (isInternetPresent) {
                if (getIntent().getStringExtra("playlist_id") != null
                        && !getIntent().getStringExtra("playlist_id")
                        .equals("")) {
                    if (list_youtubeVidID != null)
                        if (list_youtubeVidID.size() > 0)
                            list_youtubeVidID.clear();

                    new GetYouTubeVideosID().execute(getIntent().getStringExtra("playlist_id"));

                }

            } else {
                Utilities.showAlertDialog(this, "Internet Connection Error",
                        "Please connect to working Internet connection", false);
            }
        }

    }

    // Get Swipe List View
    public SwipeListView getSwipeList() {
        return lv_channel_playlist;

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        super.onBackPressed();
    }

}
