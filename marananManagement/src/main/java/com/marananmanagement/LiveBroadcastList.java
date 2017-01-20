package com.marananmanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marananmanagement.adapter.AccountListAdapter;
import com.marananmanagement.adapter.LiveBroadcastAdapter;
import com.marananmanagement.swipetodismiss.BaseSwipeListViewListener;
import com.marananmanagement.swipetodismiss.SwipeListView;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LiveBroadcastList extends Activity implements OnClickListener {
    private static LiveBroadcastList mContext;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SwipeListView lv_live;
    private ImageButton img_pencil_icon;
    private TextView tv_channels_title;
    private View views_titile_channel;
    private ArrayList<GetterSetter> list_liveChannel;
    private ArrayList<GetterSetter> list_AccUser;
    private ServiceHandler handle;
    private final int GET = 1;
    private String response;
    private ProgressBar pDialog;
    private LiveBroadcastAdapter adapter;
    public static int REQUEST_CODE_LIVE_BROADCAST = 500;
    public static int REQUEST_CODE_CHANNEL_UPLOAD = 900;

    // ChannelManagement Instance
    public static LiveBroadcastList getInstance() {
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
        mContext = LiveBroadcastList.this;
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        initializeView();
    }

    // initialize View Here...
    private void initializeView() {
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        lv_live = (SwipeListView) findViewById(R.id.lv_channels);
        lv_live.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        lv_live.setOffsetRight(convertDpToPixel(80f));
        lv_live.setAnimationTime(500);
        lv_live.setSwipeOpenOnLongPress(false);
        img_pencil_icon = (ImageButton) findViewById(R.id.img_pencil_icon);
        img_pencil_icon.setVisibility(View.VISIBLE);
        img_pencil_icon.setOnClickListener(this);
        tv_channels_title = (TextView) findViewById(R.id.tv_channels_title);
        tv_channels_title.setVisibility(View.VISIBLE);
        tv_channels_title.setText(getResources().getString(R.string.management_endowment));
        views_titile_channel = (View) findViewById(R.id.views_titile_channel);
        views_titile_channel.setVisibility(View.VISIBLE);

        lv_live.setSwipeListViewListener(new BaseSwipeListViewListener() {
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
                lv_live.closeAnimate(position);
                Intent channelIntent = new Intent(LiveBroadcastList.this, LiveBroadcastChannel.class);
                channelIntent.putExtra("id", list_liveChannel.get(position).getId());
                channelIntent.putExtra("image_old", list_liveChannel.get(position).getImage_old());
                startActivityForResult(channelIntent, REQUEST_CODE_LIVE_BROADCAST);
            }

            @Override
            public void onClickBackView(int position) {
                lv_live.closeAnimate(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        if (isInternetPresent) {

            list_liveChannel = new ArrayList<GetterSetter>();
            list_AccUser = new ArrayList<GetterSetter>();
            new GetAllLiveBroadcast().execute();
            new GetYouTubeAccountUser().execute();

        } else {
            Utilities.showAlertDialog(this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_pencil_icon:

                showAccountsDialog();

                break;

            default:
                break;
        }

    }

    // Click on pencil icon to show all accounts register in the list...
    private void showAccountsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_accounts_dialog);

        if (list_AccUser != null && list_AccUser.size() > 0) {
            ListView lv_accounts = (ListView) dialog.findViewById(R.id.lv_accounts);
            TextView tv_title_accounts = (TextView) dialog.findViewById(R.id.tv_title_accounts);
            tv_title_accounts.setText(getString(R.string.add_account_title));
            tv_title_accounts.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent channelIntent = new Intent(LiveBroadcastList.this, LiveBroadcastChannel.class);
                    channelIntent.putExtra("id", "");
                    startActivityForResult(channelIntent, REQUEST_CODE_LIVE_BROADCAST);
                    dialog.dismiss();

                }
            });
            AccountListAdapter adapter = new AccountListAdapter(mContext, list_AccUser);
            lv_accounts.setAdapter(adapter);
            dialog.show();

        } else {
            Utilities.showToast(mContext, getString(R.string.user_not_found));
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    // Get All Channels For Channel Management Page
    class GetAllLiveBroadcast extends AsyncTask<Void, Void, ArrayList<GetterSetter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<GetterSetter> doInBackground(Void... arg0) {

            handle = new ServiceHandler();
            response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT
                    + Config.GET_LIVE_BROADCAST_LIST, GET);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayItem = jsonObject.getJSONArray("value");

                    if (jsonArrayItem.length() > 0) {

                        for (int i = 0; i < jsonArrayItem.length(); i++) {
                            JSONObject jsonObj = jsonArrayItem.getJSONObject(i);
                            GetterSetter getset = new GetterSetter();
                            getset.setId(jsonObj.getString("id"));
                            getset.setTitle(jsonObj.getString("title"));
                            getset.setDescriptions(jsonObj.getString("description"));
                            getset.setStatus(jsonObj.getString("status"));
                            getset.setPublish_status(jsonObj.getString("single_status"));
                            getset.setPublish_notification(jsonObj.getString("broadcast_status"));
                            getset.setImage(jsonObj.getString("image"));
                            getset.setImage_old(jsonObj.getString("image_old"));
                            getset.setMode(jsonObj.getString("mode"));
                            getset.setTime_hr(jsonObj.getString("time_hr1"));
                            getset.setTime_min(jsonObj.getString("time_min1"));
                            getset.setTime_hr_two(jsonObj.getString("time_hr2"));
                            getset.setTime_min_two(jsonObj.getString("time_min2"));
                            getset.setSun(jsonObj.getString("sun"));
                            getset.setMon(jsonObj.getString("mon"));
                            getset.setTues(jsonObj.getString("tue"));
                            getset.setWed(jsonObj.getString("wed"));
                            getset.setThur(jsonObj.getString("thu"));
                            getset.setFri(jsonObj.getString("fri"));
                            getset.setSat(jsonObj.getString("sat"));
                            getset.setColor(getResources().getColor(R.color.list_blue));
                            getset.setText_color(getResources().getColor(R.color.White));
                            getset.setImgCancelRes(R.drawable.cross_gray);

                            if (jsonObj.getString("single_status").equals("true")) {

                                getset.setImageResource(R.drawable.tick_icon);

                            } else {
                                getset.setImageResource(R.drawable.tick_gray);

                            }

                            if (jsonObj.getString("broadcast_status").equals("true")) {

                                getset.setImgBroadCastRes(R.drawable.broadcast_sms);

                            } else {

                                getset.setImgBroadCastRes(R.drawable.broadcast_sms_gray);

                            }

                            list_liveChannel.add(getset);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Utilities.showAlertDialog(LiveBroadcastList.this,
                                "Internet Connection Error",
                                "Your internet connection is too slow...",
                                false);
                    }
                });
            }
            return list_liveChannel;
        }

        @Override
        protected void onPostExecute(ArrayList<GetterSetter> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.size() > 0) {
                    adapter = new LiveBroadcastAdapter(LiveBroadcastList.this, result);
                    lv_live.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            pDialog.setVisibility(View.GONE);
        }
    }

    // Get All Youtube Users....
    class GetYouTubeAccountUser extends AsyncTask<Void, Void, ArrayList<GetterSetter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<GetterSetter> doInBackground(Void... arg0) {

            handle = new ServiceHandler();
            response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT
                    + Config.GET_YOUTUBE_ACCOUNT_USERS, GET);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayItem = jsonObject.getJSONArray("value");

                    if (jsonArrayItem.length() > 0) {

                        for (int i = 0; i < jsonArrayItem.length(); i++) {
                            JSONObject jsonObj = jsonArrayItem.getJSONObject(i);
                            GetterSetter getset = new GetterSetter();
                            getset.setId(jsonObj.getString("id"));
                            getset.setFirstname(jsonObj.getString("firstname"));
                            getset.setLastname(jsonObj.getString("lastname"));
                            getset.setEmail(jsonObj.getString("email"));
                            list_AccUser.add(getset);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Utilities.showAlertDialog(LiveBroadcastList.this,
                                "Internet Connection Error",
                                "Your internet connection is too slow...",
                                false);
                    }
                });
            }
            return list_AccUser;
        }
    }


    // Get Swipe List View
    public SwipeListView getSwipeList() {
        return lv_live;
    }

    // onActivity Result Called Here...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_LIVE_BROADCAST) {
            if (isInternetPresent) {

                if (list_liveChannel != null) {
                    list_liveChannel.clear();
                    list_liveChannel = new ArrayList<GetterSetter>();
                    new GetAllLiveBroadcast().execute();
                }

            } else {
                Utilities.showAlertDialog(this, "Internet Connection Error",
                        "Please connect to working Internet connection", false);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANNEL_UPLOAD) {
            if (isInternetPresent) {

                if (list_liveChannel != null) {
                    list_liveChannel.clear();
                    list_liveChannel = new ArrayList<GetterSetter>();
                    new GetAllLiveBroadcast().execute();
                }

            } else {
                Utilities.showAlertDialog(this, "Internet Connection Error",
                        "Please connect to working Internet connection", false);
            }
        }
    }

    // Call DeleteProgram Class here in the adapter...
    public void getClassDelete(String id) {
        new DeleteLiveBroadcast().execute(id);
    }

    // Declare Delete Live Broadcast...
    class DeleteLiveBroadcast extends AsyncTask<String, Void, JSONObject> {
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
            String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_LIVE_BROADCAST;

            HttpPost httppost = new HttpPost(url);

            MultipartEntity multipartEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                multipartEntity.addPart("id", new StringBody(params[0]));
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
    public void onBackPressed() {
        Intent mintent = new Intent();
        setResult(RESULT_OK, mintent);
        finish();
        overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        super.onBackPressed();
    }

}
