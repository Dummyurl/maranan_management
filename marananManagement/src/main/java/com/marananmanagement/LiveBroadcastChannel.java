package com.marananmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.marananmanagement.adapter.LiveBroadcastAdapter.UpdateLiveChannelSingleStatus;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.CustomMultiPartEntity;
import com.marananmanagement.util.CustomMultiPartEntity.ProgressListener;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.RelativeTimeTextView;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class LiveBroadcastChannel extends Activity implements OnClickListener,
        OnCheckedChangeListener {
    private static LiveBroadcastChannel mContext;
    private ImageView img_cam_broadcast, img_record_title_broadcast,
            img_record_des_broadcast;
    private EditText edt_title_broadcast, edt_des_broadcast;
    private TextView tv_dateTimeCurrent;
    private RelativeTimeTextView tv_duration;
    private Button btn_upload, btn_camera, btn_status;
    private ToggleButton toggle_btn;
    private Button btn_saturday, btn_friday, btn_thursday, btn_wednesday,
            btn_tuesday, btn_Monday, btn_Sunday, btn_check_uncheck;
    private Spinner spinner_time_hr_first, spinner_time_min_first,
            spinner_time_hr_second, spinner_time_min_second;
    private ProgressBar seek_bar_radio;
    private LinearLayout linear_days, linear_spinner;
    private ConnectionDetector cd;
    private String[] array_min, array_Hr;
    private Boolean isInternetPresent = false;
    private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
    private final int REQ_CODE_SPEECH_INPUT_TWO = 200;
    private final int REQ_CODE_LIVE_IMAGES = 300;
    private ServiceHandler handle;
    private final int GET = 1;
    public String response, mode, sun, mon, tue, wed, thu, fri, sat;
    private String single_status;
    private ProgressBar pDialog;
    private ArrayList<GetterSetter> list_live_broadcast;
    private boolean check;
    private Boolean isUploading = false;
    private long totalSize = 0;
    private HttpEntity resEntity;
    public String response_str;
    private String id = "";
    private String liveImagePath = "";
    private String liveImageName = "";
    private String imagestatus = "false";

    // LiveBroadcastChannel Instance
    public static LiveBroadcastChannel getInstance() {
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
        setContentView(R.layout.activity_live_broadcast);
        mContext = LiveBroadcastChannel.this;
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        initializeView();
    }

    private void initializeView() {
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        img_cam_broadcast = (ImageView) findViewById(R.id.img_cam_broadcast);
        img_cam_broadcast.setOnClickListener(this);

        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);

        btn_status = (Button) findViewById(R.id.btn_status);
        btn_status.setVisibility(View.INVISIBLE);

        img_record_title_broadcast = (ImageView) findViewById(R.id.img_record_title_broadcast);
        img_record_title_broadcast.setOnClickListener(this);

        img_record_des_broadcast = (ImageView) findViewById(R.id.img_record_des_broadcast);
        img_record_des_broadcast.setOnClickListener(this);

        edt_title_broadcast = (EditText) findViewById(R.id.edt_title_broadcast);
        edt_des_broadcast = (EditText) findViewById(R.id.edt_des_broadcast);

        tv_dateTimeCurrent = (TextView) findViewById(R.id.tv_dateTimeCurrent);
        tv_dateTimeCurrent.setText(Utilities.getCurrentDate() + " | "
                + Utilities.getIsraelTime());

        tv_duration = (RelativeTimeTextView) findViewById(R.id.tv_duration);
        tv_duration.setVisibility(View.GONE);

        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);

        btn_check_uncheck = (Button) findViewById(R.id.btn_check_uncheck);
        btn_check_uncheck.setOnClickListener(this);
        btn_check_uncheck.setTag(R.drawable.tick_gray);
        btn_check_uncheck.setBackgroundResource(R.drawable.tick_gray);
        btn_check_uncheck.setVisibility(View.GONE);
        single_status = "false";

        btn_saturday = (Button) findViewById(R.id.btn_saturday);
        btn_saturday.setTag(R.drawable.bg_hollow);
        btn_saturday.setOnClickListener(this);

        btn_friday = (Button) findViewById(R.id.btn_friday);
        btn_friday.setTag(R.drawable.bg_hollow);
        btn_friday.setOnClickListener(this);

        btn_thursday = (Button) findViewById(R.id.btn_thursday);
        btn_thursday.setTag(R.drawable.bg_hollow);
        btn_thursday.setOnClickListener(this);

        btn_wednesday = (Button) findViewById(R.id.btn_wednesday);
        btn_wednesday.setTag(R.drawable.bg_hollow);
        btn_wednesday.setOnClickListener(this);

        btn_tuesday = (Button) findViewById(R.id.btn_tuesday);
        btn_tuesday.setTag(R.drawable.bg_hollow);
        btn_tuesday.setOnClickListener(this);

        btn_Monday = (Button) findViewById(R.id.btn_Monday);
        btn_Monday.setTag(R.drawable.bg_hollow);
        btn_Monday.setOnClickListener(this);

        btn_Sunday = (Button) findViewById(R.id.btn_Sunday);
        btn_Sunday.setTag(R.drawable.bg_hollow);
        btn_Sunday.setOnClickListener(this);

        toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
        toggle_btn.setOnCheckedChangeListener(this);
        toggle_btn.setChecked(false);
        check = false;
        mode = "Manual";

        spinner_time_hr_first = (Spinner) findViewById(R.id.spinner_time_hr_first);
        spinner_time_min_first = (Spinner) findViewById(R.id.spinner_time_min_first);
        spinner_time_hr_second = (Spinner) findViewById(R.id.spinner_time_hr_second);
        spinner_time_min_second = (Spinner) findViewById(R.id.spinner_time_min_second);

        linear_days = (LinearLayout) findViewById(R.id.linear_days);
        linear_spinner = (LinearLayout) findViewById(R.id.linear_spinner);

        linear_days.setVisibility(View.INVISIBLE);
        linear_spinner.setVisibility(View.INVISIBLE);

        seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
        seek_bar_radio.setProgress(0);

        setValuesOnSpinners();

        if (isInternetPresent) {

            if (!getIntent().getStringExtra("id").equals("")) {

                list_live_broadcast = new ArrayList<GetterSetter>();
                new GetLiveChannel().execute();

            } else {
                Picasso.with(this)
                        .load("")
                        .placeholder(R.drawable.below_thubnails)
                        .error(R.drawable.below_thubnails).into(img_cam_broadcast);
            }

        } else {
            Utilities.showAlertDialog(this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        }

    }

    // Get Live Channel Content
    class GetLiveChannel extends AsyncTask<Void, Void, ArrayList<GetterSetter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<GetterSetter> doInBackground(Void... arg0) {

            handle = new ServiceHandler();
            response = handle.makeServiceCall(Config.ROOT_SERVER_CLIENT
                    + Config.GET_LIVE_BROADCAST + Config.VIDEO_ID
                    + getIntent().getStringExtra("id"), GET);

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
                            getset.setImage(jsonObj.getString("image"));
                            getset.setStatus(jsonObj.getString("status"));
                            getset.setPublish_status(jsonObj.getString("single_status"));
                            getset.setPublish_notification(jsonObj.getString("broadcast_status"));
                            getset.setVid_Id(jsonObj.getString("video_id"));
                            getset.setUnique_video_id(jsonObj.getString("unique_video_id"));
                            getset.setDate(jsonObj.getString("date"));
                            getset.setTime(jsonObj.getString("time"));
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
                            list_live_broadcast.add(getset);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Utilities.showAlertDialog(LiveBroadcastChannel.this,
                                "Internet Connection Error",
                                "Your internet connection is too slow...",
                                false);
                    }
                });
            }
            return list_live_broadcast;
        }

        @Override
        protected void onPostExecute(ArrayList<GetterSetter> result) {
            super.onPostExecute(result);

            pDialog.setVisibility(View.GONE);

            if (result != null) {
                if (result.size() > 0) {
                    id = result.get(0).getId();
                    edt_title_broadcast.setText(Utilities.decodeImoString(result.get(0).getTitle()));
                    edt_des_broadcast.setText(Utilities.decodeImoString(result.get(0).getDescriptions()));

                    Picasso.with(mContext)
                            .load(result.get(0).getImage())
                            .placeholder(R.drawable.below_thubnails)
                            .error(R.drawable.below_thubnails).into(img_cam_broadcast);

                    if (result.get(0).getMode().equals("Manual")) {

                        mode = "Manual";
                        toggle_btn.setChecked(false);
                        check = false;
                        btn_check_uncheck.setVisibility(View.GONE);
                        linear_days.setVisibility(View.INVISIBLE);
                        linear_spinner.setVisibility(View.INVISIBLE);

                        if (result.get(0).getStatus().equals("true")
                                && result.get(0).getPublish_status()
                                .equals("true")) {

                            btn_status.setVisibility(View.VISIBLE);
                            btn_status.setBackgroundResource(R.drawable.dot_green);

                        } else {

                            btn_status.setVisibility(View.VISIBLE);
                            btn_status.setBackgroundResource(R.drawable.dot_red);

                        }

                    } else if (result.get(0).getMode().equals("Automatic")) {

                        mode = "Automatic";
                        toggle_btn.setChecked(true);
                        check = true;
                        btn_check_uncheck.setVisibility(View.GONE);
                        linear_days.setVisibility(View.VISIBLE);
                        linear_spinner.setVisibility(View.VISIBLE);

                        array_Hr = getResources().getStringArray(
                                R.array.array_Hr);
                        array_min = getResources().getStringArray(
                                R.array.array_min);

                        for (int i = 0; i < array_Hr.length; i++) {
                            if (result.get(0).getTime_hr().equals(array_Hr[i])) {
                                spinner_time_hr_first.setSelection(Integer
                                        .valueOf(i));
                            }
                        }

                        for (int i = 0; i < array_min.length; i++) {
                            if (result.get(0).getTime_min()
                                    .equals(array_min[i])) {
                                spinner_time_min_first.setSelection(Integer
                                        .valueOf(i));
                            }
                        }

                        for (int i = 0; i < array_Hr.length; i++) {
                            if (result.get(0).getTime_hr_two()
                                    .equals(array_Hr[i])) {
                                spinner_time_hr_second.setSelection(Integer
                                        .valueOf(i));
                            }
                        }

                        for (int i = 0; i < array_min.length; i++) {
                            if (result.get(0).getTime_min_two()
                                    .equals(array_min[i])) {
                                spinner_time_min_second.setSelection(Integer
                                        .valueOf(i));
                            }
                        }

                        if (result.get(0).getSun().equals("Sun")) {
                            btn_Sunday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_Sunday.setTag(R.drawable.bg_solid);
                            btn_Sunday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_Sunday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_Sunday.setTag(R.drawable.bg_hollow);
                            btn_Sunday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getMon().equals("Mon")) {
                            btn_Monday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_Monday.setTag(R.drawable.bg_solid);
                            btn_Monday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_Monday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_Monday.setTag(R.drawable.bg_hollow);
                            btn_Monday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getTues().equals("Tue")) {
                            btn_tuesday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_tuesday.setTag(R.drawable.bg_solid);
                            btn_tuesday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_tuesday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_tuesday.setTag(R.drawable.bg_hollow);
                            btn_tuesday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getWed().equals("Wed")) {
                            btn_wednesday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_wednesday.setTag(R.drawable.bg_solid);
                            btn_wednesday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_wednesday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_wednesday.setTag(R.drawable.bg_hollow);
                            btn_wednesday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getThur().equals("Thu")) {
                            btn_thursday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_thursday.setTag(R.drawable.bg_solid);
                            btn_thursday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_thursday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_thursday.setTag(R.drawable.bg_hollow);
                            btn_thursday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getFri().equals("Fri")) {
                            btn_friday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_friday.setTag(R.drawable.bg_solid);
                            btn_friday.setTextColor(getResources().getColor(
                                    R.color.white));
                        } else {
                            btn_friday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_friday.setTag(R.drawable.bg_hollow);
                            btn_friday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (result.get(0).getSat().equals("Sat")) {
                            btn_saturday
                                    .setBackgroundResource(R.drawable.bg_solid);
                            btn_saturday.setTag(R.drawable.bg_solid);
                            btn_saturday.setTextColor(getResources().getColor(
                                    R.color.white));

                        } else {
                            btn_saturday
                                    .setBackgroundResource(R.drawable.bg_hollow);
                            btn_saturday.setTag(R.drawable.bg_hollow);
                            btn_saturday.setTextColor(getResources().getColor(
                                    R.color.DeepSkyBlue));
                        }

                        if (Utilities.compareDateTimeLive(Utilities
                                .getCurrentDateNew()
                                + " "
                                + result.get(0).getTime_hr()
                                + ":"
                                + result.get(0).getTime_min() + ":00")) {

                            btn_status.setVisibility(View.VISIBLE);
                            btn_status
                                    .setBackgroundResource(R.drawable.dot_red);

                            tv_duration.setVisibility(View.VISIBLE);
                            tv_duration.setTextColor(Color
                                    .parseColor("#000000"));
                            tv_duration.setReferenceTimeForStartTime((Utilities
                                            .getToDate(Utilities.getCurrentDateNew(),
                                                    result.get(0).getTime_hr()
                                                            + ":"
                                                            + result.get(0)
                                                            .getTime_min()
                                                            + ":00")).getTime(),
                                    "LiveBroadcast", "startText");

                        } else if (Utilities.compareDateTimeLive(Utilities
                                .getCurrentDateNew()
                                + " "
                                + result.get(0).getTime_hr_two()
                                + ":"
                                + result.get(0).getTime_min_two() + ":00")) {

                            btn_status.setVisibility(View.VISIBLE);
                            btn_status
                                    .setBackgroundResource(R.drawable.dot_green);

                            tv_duration.setVisibility(View.VISIBLE);
                            tv_duration.setText("");
                            tv_duration.setTextColor(getResources().getColor(
                                    R.color.list_green));
                            tv_duration.setReferenceTimeForStartTime((Utilities
                                            .getToDate(Utilities.getCurrentDateNew(),
                                                    list_live_broadcast.get(0)
                                                            .getTime_hr_two()
                                                            + ":"
                                                            + list_live_broadcast
                                                            .get(0)
                                                            .getTime_min_two()
                                                            + ":00")).getTime(),
                                    "LiveBroadcast", "endText");

                        }

                    } else {

                        mode = "Manual";
                        toggle_btn.setChecked(false);
                        check = false;
                        btn_check_uncheck.setVisibility(View.GONE);
                        linear_days.setVisibility(View.INVISIBLE);
                        linear_spinner.setVisibility(View.INVISIBLE);

                    }

                    if (result.get(0).getPublish_status().equals("true")) {
                        btn_check_uncheck.setTag(R.drawable.tick_icon);
                        btn_check_uncheck
                                .setBackgroundResource(R.drawable.tick_icon);
                        single_status = "true";

                    } else {
                        btn_check_uncheck.setTag(R.drawable.tick_gray);
                        btn_check_uncheck
                                .setBackgroundResource(R.drawable.tick_gray);
                        single_status = "false";
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                if (isUploading == false) {
                    if (check == true) {
                        mode = "Automatic";

                        if (edt_title_broadcast.length() == 0) {
                            Utilities.showToast(LiveBroadcastChannel.this,
                                    "Enter title");

                        } else {

                            if (Integer.parseInt(btn_saturday.getTag().toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_Sunday.getTag()
                                    .toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_Monday.getTag()
                                    .toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_friday.getTag()
                                    .toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_tuesday.getTag()
                                    .toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_wednesday.getTag()
                                    .toString()) != R.drawable.bg_hollow
                                    || Integer.parseInt(btn_thursday.getTag()
                                    .toString()) != R.drawable.bg_hollow) {

                                if (Integer
                                        .parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
                                    sun = "Sun";
                                } else {
                                    sun = "";
                                }

                                if (Integer
                                        .parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
                                    mon = "Mon";
                                } else {
                                    mon = "";
                                }

                                if (Integer.parseInt(btn_tuesday.getTag()
                                        .toString()) == R.drawable.bg_solid) {
                                    tue = "Tue";
                                } else {
                                    tue = "";
                                }

                                if (Integer.parseInt(btn_wednesday.getTag()
                                        .toString()) == R.drawable.bg_solid) {
                                    wed = "Wed";
                                } else {
                                    wed = "";
                                }

                                if (Integer.parseInt(btn_thursday.getTag()
                                        .toString()) == R.drawable.bg_solid) {
                                    thu = "Thu";
                                } else {
                                    thu = "";
                                }

                                if (Integer
                                        .parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
                                    fri = "Fri";
                                } else {
                                    fri = "";
                                }

                                if (Integer.parseInt(btn_saturday.getTag()
                                        .toString()) == R.drawable.bg_solid) {
                                    sat = "Sat";
                                } else {
                                    sat = "";
                                }

                                new UpdateLiveBroadcast().execute(id, Utilities
                                                .encodeImoString(edt_title_broadcast
                                                        .getText().toString()), Utilities
                                                .encodeImoString(edt_des_broadcast
                                                        .getText().toString()),
                                        single_status, mode, spinner_time_hr_first
                                                .getSelectedItem().toString(),
                                        spinner_time_min_first.getSelectedItem()
                                                .toString(), spinner_time_hr_second
                                                .getSelectedItem().toString(),
                                        spinner_time_min_second.getSelectedItem()
                                                .toString(), sun, mon, tue, wed,
                                        thu, fri, sat, liveImagePath, Utilities
                                                .getCurrentDateNew(), Utilities
                                                .getIsraelTime(), liveImageName, imagestatus);

                            } else {
                                Utilities.showToast(LiveBroadcastChannel.this,
                                        "Select Days");
                            }
                        }

                    } else {
                        check = toggle_btn.isChecked();
                        mode = "Manual";

                        if (edt_title_broadcast.length() != 0) {

                            new UpdateLiveBroadcast().execute(id, Utilities
                                            .encodeImoString(edt_title_broadcast.getText()
                                                    .toString()), Utilities
                                            .encodeImoString(edt_des_broadcast.getText()
                                                    .toString()), single_status, mode, "",
                                    "", "", "", "", "", "", "", "", "", "",
                                    liveImagePath, Utilities.getCurrentDateNew(),
                                    Utilities.getIsraelTime(), liveImageName, imagestatus);

                        } else {
                            Utilities.showToast(LiveBroadcastChannel.this,
                                    "Enter title");
                        }
                    }
                } else {
                    Utilities.showToast(LiveBroadcastChannel.this,
                            "Uploading...Please wait");
                }

                break;

            case R.id.btn_check_uncheck:
                if (Integer.parseInt(btn_check_uncheck.getTag().toString()) == R.drawable.tick_icon) {
                    btn_check_uncheck.setBackgroundResource(R.drawable.tick_gray);
                    btn_check_uncheck.setTag(R.drawable.tick_gray);
                    single_status = "false";

                } else if (Integer.parseInt(btn_check_uncheck.getTag().toString()) == R.drawable.tick_gray) {
                    btn_check_uncheck.setBackgroundResource(R.drawable.tick_icon);
                    btn_check_uncheck.setTag(R.drawable.tick_icon);
                    single_status = "true";
                }

                break;

            case R.id.btn_saturday:
                if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_saturday.setBackgroundResource(R.drawable.bg_solid);
                    btn_saturday.setTag(R.drawable.bg_solid);
                    btn_saturday.setTextColor(getResources()
                            .getColor(R.color.white));

                } else if (Integer.parseInt(btn_saturday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_saturday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_saturday.setTag(R.drawable.bg_hollow);
                    btn_saturday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_friday:
                if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_friday.setBackgroundResource(R.drawable.bg_solid);
                    btn_friday.setTag(R.drawable.bg_solid);
                    btn_friday.setTextColor(getResources().getColor(R.color.white));

                } else if (Integer.parseInt(btn_friday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_friday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_friday.setTag(R.drawable.bg_hollow);
                    btn_friday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_thursday:
                if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_thursday.setBackgroundResource(R.drawable.bg_solid);
                    btn_thursday.setTag(R.drawable.bg_solid);
                    btn_thursday.setTextColor(getResources()
                            .getColor(R.color.white));

                } else if (Integer.parseInt(btn_thursday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_thursday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_thursday.setTag(R.drawable.bg_hollow);
                    btn_thursday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_wednesday:
                if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_wednesday.setBackgroundResource(R.drawable.bg_solid);
                    btn_wednesday.setTag(R.drawable.bg_solid);
                    btn_wednesday.setTextColor(getResources().getColor(
                            R.color.white));

                } else if (Integer.parseInt(btn_wednesday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_wednesday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_wednesday.setTag(R.drawable.bg_hollow);
                    btn_wednesday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_tuesday:
                if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_tuesday.setBackgroundResource(R.drawable.bg_solid);
                    btn_tuesday.setTag(R.drawable.bg_solid);
                    btn_tuesday
                            .setTextColor(getResources().getColor(R.color.white));

                } else if (Integer.parseInt(btn_tuesday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_tuesday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_tuesday.setTag(R.drawable.bg_hollow);
                    btn_tuesday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_Monday:
                if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_Monday.setBackgroundResource(R.drawable.bg_solid);
                    btn_Monday.setTag(R.drawable.bg_solid);
                    btn_Monday.setTextColor(getResources().getColor(R.color.white));

                } else if (Integer.parseInt(btn_Monday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_Monday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_Monday.setTag(R.drawable.bg_hollow);
                    btn_Monday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.btn_Sunday:
                if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_hollow) {
                    btn_Sunday.setBackgroundResource(R.drawable.bg_solid);
                    btn_Sunday.setTag(R.drawable.bg_solid);
                    btn_Sunday.setTextColor(getResources().getColor(R.color.white));

                } else if (Integer.parseInt(btn_Sunday.getTag().toString()) == R.drawable.bg_solid) {
                    btn_Sunday.setBackgroundResource(R.drawable.bg_hollow);
                    btn_Sunday.setTag(R.drawable.bg_hollow);
                    btn_Sunday.setTextColor(getResources().getColor(
                            R.color.DeepSkyBlue));

                }
                break;

            case R.id.img_record_title_broadcast:
                if (isInternetPresent) {
                    promptSpeechInput("title");
                } else {
                    Utilities.showAlertDialog(this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
                break;

            case R.id.img_record_des_broadcast:
                if (isInternetPresent) {
                    promptSpeechInput("description");
                } else {
                    Utilities.showAlertDialog(this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
                break;

            case R.id.img_cam_broadcast:
                if (isInternetPresent) {

                    if (isUploading == false) {

                        if (list_live_broadcast != null
                                && !list_live_broadcast.get(0).getVid_Id()
                                .equals("")) {
                            Intent live = new Intent(this, VideoPlayer.class);
                            live.putExtra("video_id", list_live_broadcast.get(0)
                                    .getVid_Id());
                            startActivity(live);
                        }

                    } else {
                        Utilities.showToast(this, "Uploading...Please wait");
                    }

                } else {
                    Utilities.showAlertDialog(this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
                break;

            case R.id.btn_camera:
                if (isInternetPresent) {

                    if (isUploading == false) {
                        Intent liveImages = new Intent(this, LiveBroadcastImages.class);
                        liveImages.putExtra("id", getIntent().getStringExtra("id"));
                        liveImages.putExtra("name_class", "LiveBroadcastChannel");
                        liveImages.putExtra("image_old", getIntent().getStringExtra("image_old"));
                        startActivityForResult(liveImages, REQ_CODE_LIVE_IMAGES);

                    } else {
                        Utilities.showToast(this, "Uploading...Please wait");
                    }

                } else {
                    Utilities.showAlertDialog(this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
                break;

            default:
                break;
        }

    }

    // Set Live Name Image From Selection In Live Broadcast Image Adapter
    public void setLiveImageName(String liveImageName) {
        liveImagePath = "";
        this.liveImageName = liveImageName;
    }


    // Set Spinner Values And Adapters
    private void setValuesOnSpinners() {
        array_Hr = getResources().getStringArray(R.array.array_Hr);
        ArrayAdapter<String> aryadapter_Hr = new ArrayAdapter<String>(this,
                R.layout.spinner_background, array_Hr);
        aryadapter_Hr.setDropDownViewResource(R.layout.spinner_item_background);
        spinner_time_hr_first.setAdapter(aryadapter_Hr);
        spinner_time_hr_second.setAdapter(aryadapter_Hr);

        array_min = getResources().getStringArray(R.array.array_min);
        ArrayAdapter<String> aryadapter_min = new ArrayAdapter<String>(this,
                R.layout.spinner_background, array_min);
        aryadapter_min
                .setDropDownViewResource(R.layout.spinner_item_background);
        spinner_time_min_first.setAdapter(aryadapter_min);
        spinner_time_min_second.setAdapter(aryadapter_min);

        spinner_time_hr_first
                .setOnItemSelectedListener(new CustomOnItemSelectedListenerHR());
        spinner_time_hr_second
                .setOnItemSelectedListener(new CustomOnItemSelectedListenerHRSecond());
        spinner_time_min_first
                .setOnItemSelectedListener(new CustomOnItemSelectedListenerMin());
        spinner_time_min_second
                .setOnItemSelectedListener(new CustomOnItemSelectedListenerMinSecond());

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            check = isChecked;
            btn_check_uncheck.setVisibility(View.GONE);
            linear_days.setVisibility(View.VISIBLE);
            linear_spinner.setVisibility(View.VISIBLE);
            mode = "Automatic";

        } else {

            check = isChecked;
            btn_check_uncheck.setVisibility(View.GONE);
            linear_days.setVisibility(View.INVISIBLE);
            linear_spinner.setVisibility(View.INVISIBLE);
            mode = "Manual";

        }
    }

    // onActivityResult Call...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == RESULT_OK && null != data
                    && requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edt_title_broadcast.setText(result.get(0));

            } else if (resultCode == RESULT_OK && null != data
                    && requestCode == REQ_CODE_SPEECH_INPUT_TWO) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edt_des_broadcast.setText(result.get(0));

            } else if (resultCode == RESULT_OK && null != data
                    && requestCode == REQ_CODE_LIVE_IMAGES) {

                try {

                    // Get the file path and server image name from the URI
                    liveImagePath = data.getStringExtra("imagepaths");
                    liveImageName = data.getStringExtra("imagename");
                    imagestatus = data.getStringExtra("imagestatus");
                    Log.e("imagestatus", "imagestatus??" + imagestatus);
                    Log.e("liveImagePath", "liveImagePath??" + liveImagePath);
                    Log.e("liveImageName", "liveImageName??" + liveImageName);

                    if (liveImagePath != null) {
                        if (liveImagePath.startsWith("http://") || liveImagePath.startsWith("https://")) {
                            img_cam_broadcast.setImageResource(0);
                            Picasso.with(this)
                                    .load(liveImagePath)
                                    .placeholder(R.drawable.below_thubnails)
                                    .error(R.drawable.below_thubnails).into(img_cam_broadcast);
                            liveImagePath = "";
                            Log.e("if", "if");

                        } else {
                            Log.e("else", "else");
                            img_cam_broadcast.setImageResource(0);
                            img_cam_broadcast.setImageBitmap(BitmapFactory
                                    .decodeFile(liveImagePath));
                        }
                    } else {
                        list_live_broadcast = new ArrayList<GetterSetter>();
                        new GetLiveChannel().execute();
                    }

                } catch (Exception e) {
                    Log.e("FileSelectorTestActivity", "File select error", e);
                }
            }
        } catch (Exception e) {
            Utilities.showToast(this, "Something went wrong");

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput(String values) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        if (values.equals("title")) {
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ONE);
            } catch (ActivityNotFoundException a) {
                Utilities.showToast(this, getString(R.string.speech_not_supported));

            }
        } else if (values.equals("description")) {
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_TWO);
            } catch (ActivityNotFoundException a) {
                Utilities.showToast(this, getString(R.string.speech_not_supported));

            }
        }
    }

    // Set Spinner Items Selected Listener For Hour
    public class CustomOnItemSelectedListenerHR implements
            OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    // Set Spinner Items Selected Listener For Hour
    public class CustomOnItemSelectedListenerHRSecond implements
            OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    // Set Spinner Items Selected Listener For Minutes
    public class CustomOnItemSelectedListenerMin implements
            OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    // Set Spinner Items Selected Listener For Minutes
    public class CustomOnItemSelectedListenerMinSecond implements
            OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
                    .getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
        return ret;
    }

    // Update Live Broadcast..
    class UpdateLiveBroadcast extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seek_bar_radio.setProgress(0);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT
                    + Config.UPLOAD_LIVE_BROADCAST);

            try {

                CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress(""
                                        + (int) ((num / (float) totalSize) * 100));
                            }
                        }, Charset.forName("UTF-8"));

                if (!params[0].equals("")) {
                    reqEntity
                            .addPart(
                                    "id",
                                    new StringBody(params[0], Charset
                                            .forName("UTF-8")));
                } else {
                    reqEntity.addPart("id", new StringBody(""));
                }

                if (params[1] != null) {
                    if (!params[1].equals("")) {
                        reqEntity.addPart("title", new StringBody(params[1],
                                Charset.forName("UTF-8")));
                    } else {
                        reqEntity.addPart("title", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("title", new StringBody(""));
                }

                if (params[2] != null) {
                    if (!params[2].equals("")) {
                        reqEntity.addPart("description", new StringBody(
                                params[2], Charset.forName("UTF-8")));
                    } else {
                        reqEntity.addPart("description", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("description", new StringBody(""));
                }

                if (params[3] != null) {
                    if (!params[3].equals("")) {

                        reqEntity.addPart("single_status", new StringBody(
                                params[3], Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("single_status", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("single_status", new StringBody(""));
                }

                if (params[4] != null) {
                    if (!params[4].equals("")) {

                        reqEntity.addPart("mode", new StringBody(params[4],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("mode", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("mode", new StringBody(""));
                }

                if (params[5] != null) {
                    if (!params[5].equals("")) {

                        reqEntity.addPart("time_hr1", new StringBody(params[5],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("time_hr1", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("time_hr1", new StringBody(""));
                }

                if (params[6] != null) {
                    if (!params[6].equals("")) {

                        reqEntity.addPart("time_min1", new StringBody(
                                params[6], Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("time_min1", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("time_min1", new StringBody(""));
                }

                if (params[7] != null) {
                    if (!params[7].equals("")) {

                        reqEntity.addPart("time_hr2", new StringBody(params[7],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("time_hr2", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("time_hr2", new StringBody(""));
                }

                if (params[8] != null) {
                    if (!params[8].equals("")) {

                        reqEntity.addPart("time_min2", new StringBody(
                                params[8], Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("time_min2", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("time_min2", new StringBody(""));
                }

                if (params[9] != null) {
                    if (!params[9].equals("")) {

                        reqEntity.addPart("sun", new StringBody(params[9],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("sun", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("sun", new StringBody(""));
                }

                if (params[10] != null) {
                    if (!params[10].equals("")) {

                        reqEntity.addPart("mon", new StringBody(params[10],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("mon", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("mon", new StringBody(""));
                }

                if (params[11] != null) {
                    if (!params[11].equals("")) {

                        reqEntity.addPart("tue", new StringBody(params[11],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("tue", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("tue", new StringBody(""));
                }

                if (params[12] != null) {
                    if (!params[12].equals("")) {

                        reqEntity.addPart("wed", new StringBody(params[12],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("wed", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("wed", new StringBody(""));
                }

                if (params[13] != null) {
                    if (!params[13].equals("")) {

                        reqEntity.addPart("thu", new StringBody(params[13],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("thu", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("thu", new StringBody(""));
                }

                if (params[14] != null) {
                    if (!params[14].equals("")) {

                        reqEntity.addPart("fri", new StringBody(params[14],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("fri", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("fri", new StringBody(""));
                }

                if (params[15] != null) {
                    if (!params[15].equals("")) {

                        reqEntity.addPart("sat", new StringBody(params[15],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("sat", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("sat", new StringBody(""));
                }

                if (params[16] != null) {
                    if (!params[16].equals("")) {

                        File imageFile = new File(params[16]);
                        FileBody bodyImage = new FileBody(imageFile, "",
                                "UTF-8");
                        reqEntity.addPart("image", bodyImage);
                    } else {
                        reqEntity.addPart("image",
                                new StringBody("", Charset.forName("UTF-8")));
                    }
                } else {
                    reqEntity.addPart("image",
                            new StringBody("", Charset.forName("UTF-8")));
                }

                if (params[17] != null) {
                    if (!params[17].equals("")) {

                        reqEntity.addPart("date", new StringBody(params[17],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("date", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("date", new StringBody(""));
                }

                if (params[18] != null) {
                    if (!params[18].equals("")) {

                        reqEntity.addPart("time", new StringBody(params[18],
                                Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("time", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("time", new StringBody(""));
                }

                if (params[19] != null) {
                    if (!params[19].equals("")) {

                        reqEntity.addPart("image_name", new StringBody(
                                params[19], Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("image_name", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("image_name", new StringBody(""));
                }

                if (params[20] != null) {
                    if (!params[20].equals("")) {

                        reqEntity.addPart("image_status", new StringBody(
                                params[20], Charset.forName("UTF-8")));

                    } else {
                        reqEntity.addPart("image_status", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("image_status", new StringBody(""));
                }

                totalSize = reqEntity.getContentLength();
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                resEntity = response.getEntity();
                response_str = EntityUtils.toString(resEntity);

            } catch (final ClientProtocolException e) {
                Utilities.showRunUiThread(LiveBroadcastChannel.this, e);

            } catch (final IOException e) {
                Utilities.showRunUiThread(LiveBroadcastChannel.this, e);
            }
            return response_str;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            isUploading = true;
            seek_bar_radio.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            seek_bar_radio.setProgress(0);
            isUploading = false;
            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    if (jsonObj.get("success").equals(true)) {

                        Utilities.showToast(LiveBroadcastChannel.this,
                                jsonObj.getString("value"));

                    } else {

                        Utilities.showToast(LiveBroadcastChannel.this,
                                jsonObj.getString("value"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void settextToEndTime() {
        btn_status.setVisibility(View.VISIBLE);
        btn_status.setBackgroundResource(R.drawable.dot_green);
        tv_duration.setText("");
        tv_duration.setTextColor(getResources().getColor(R.color.list_green));
        tv_duration.setReferenceTimeForStartTime((Utilities.getToDate(Utilities
                .getCurrentDateNew(), list_live_broadcast.get(0)
                .getTime_hr_two()
                + ":"
                + list_live_broadcast.get(0).getTime_min_two() + ":00"))
                .getTime(), "LiveBroadcast", "endText");
    }

    public void setVisibilityGone() {
        btn_status.setVisibility(View.VISIBLE);
        btn_status.setBackgroundResource(R.drawable.dot_red);
        tv_duration.setText("");
        tv_duration.setVisibility(View.GONE);
        single_status = "false";
        btn_check_uncheck.setBackgroundResource(R.drawable.tick_gray);
        new UpdateLiveChannelSingleStatus().execute(list_live_broadcast.get(0)
                .getId(), "false");
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public ImageView getLiveImageView() {
        return img_cam_broadcast;
    }

    @Override
    public void onBackPressed() {
        if (isUploading) {

            Utilities.showToast(LiveBroadcastChannel.this,
                    "Uploading...Please wait");

        } else {
            Intent mintent = new Intent();
            setResult(RESULT_OK, mintent);
            finish();
            overridePendingTransition(R.anim.activity_open_scale,
                    R.anim.activity_close_translate);
            super.onBackPressed();
        }

    }

}
