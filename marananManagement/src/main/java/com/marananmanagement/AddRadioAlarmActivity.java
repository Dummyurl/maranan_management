package com.marananmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.Utilities;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class AddRadioAlarmActivity extends Activity implements OnClickListener {
    private ImageView img_cam, img_record_title, img_record_des, img_text_change;
    private EditText edt_title_radio;
    private EditText edt_des_radio;
    private Button btn_upload, btn_play_pause, btn_save_radio_alert;
    private ProgressBar progress_loading;
    private ToggleButton toggle_btn;
    private DatePicker date_picker;
    private TimePicker time_picker;
    private ProgressBar seek_bar_radio;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private static int RESULT_LOAD_IMG = 300;
    private final int REQ_CODE_SPEECH_INPUT_ONE = 100;
    private final int REQ_CODE_SPEECH_INPUT_THREE = 200;
    private String imgDecodableString = "";
    private HttpEntity resEntity;
    public String response_str;
    public Boolean isComingIntent = false;
    private String id = "";
    private ProgressBar pDialog;

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
        setContentView(R.layout.radio_program_upload);
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        initializeView();
    }

    // initializeView Here...
    private void initializeView() {
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        img_cam = (ImageView) findViewById(R.id.img_cam);
        img_cam.setOnClickListener(this);
        img_record_title = (ImageView) findViewById(R.id.img_record_title);
        img_record_title.setOnClickListener(this);
        img_record_des = (ImageView) findViewById(R.id.img_record_des);
        img_record_des.setOnClickListener(this);
        img_text_change = (ImageView) findViewById(R.id.img_text_change);
        img_text_change.setVisibility(View.INVISIBLE);
        edt_title_radio = (EditText) findViewById(R.id.edt_title_radio);
        edt_title_radio.setText(Utilities.decodeImoString(getIntent().getStringExtra("title")));
        edt_des_radio = (EditText) findViewById(R.id.edt_des_radio);
        edt_des_radio.setText(Utilities.decodeImoString(getIntent().getStringExtra("description")));
        btn_save_radio_alert = (Button) findViewById(R.id.btn_save_radio_alert);
        btn_save_radio_alert.setOnClickListener(this);
        btn_save_radio_alert.setVisibility(View.VISIBLE);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        btn_upload.setVisibility(View.INVISIBLE);
        btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
        btn_play_pause.setOnClickListener(this);
        btn_play_pause.setVisibility(View.INVISIBLE);
        btn_play_pause.setTag(R.drawable.play_arrow);
        btn_play_pause.setBackgroundResource(R.drawable.play_arrow);
        btn_play_pause.setVisibility(View.INVISIBLE);
        toggle_btn = (ToggleButton) findViewById(R.id.toggle_btn);
        toggle_btn.setChecked(false);
        toggle_btn.setVisibility(View.INVISIBLE);
        date_picker = (DatePicker) findViewById(R.id.date_picker);
        date_picker.setVisibility(View.INVISIBLE);
        time_picker = (TimePicker) findViewById(R.id.time_picker);
        time_picker.setIs24HourView(true);
        time_picker.setVisibility(View.INVISIBLE);
        progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
        progress_loading.setVisibility(View.INVISIBLE);
        seek_bar_radio = (ProgressBar) findViewById(R.id.seek_bar_radio);
        seek_bar_radio.setVisibility(View.INVISIBLE);
        seek_bar_radio.setProgress(0);

        if (getIntent().getStringExtra("image") != null) {
            isComingIntent = true;
            img_cam.setBackgroundResource(0);
            Picasso.with(this)
                    .load(getIntent().getStringExtra("image"))
                    .placeholder(R.drawable.cam_icon)
                    .error(R.drawable.cam_icon).into(img_cam);
        }

        if (getIntent().getStringExtra("id") != null) {
            id = getIntent().getStringExtra("id");
        } else {
            id = "";
        }
    }

    // onclick Listener...
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_radio_alert:
                if (isInternetPresent) {
                    if (edt_title_radio.getText().toString().trim().length() > 0) {
                        new SendRadioAlerts().execute(id, Utilities.encodeImoString(edt_title_radio.getText().toString().trim())
                                , Utilities.encodeImoString(edt_des_radio.getText().toString().trim()),
                                imgDecodableString);
                    } else {
                        Utilities.showToast(this, "Enter Title");
                    }
                } else {
                    Utilities.showAlertDialog(AddRadioAlarmActivity.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
                break;

            case R.id.img_cam:
//			Intent galleryIntent = new Intent(
//					Intent.ACTION_PICK,
//					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

                break;

            case R.id.img_record_title:
                promptSpeechInput("title");
                break;

            case R.id.img_record_des:
                promptSpeechInput("description");
                break;

            default:
                break;
        }
    }

    // onActivity Result To Get Image From Gallery...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && null != data
                    && requestCode == REQ_CODE_SPEECH_INPUT_ONE) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edt_title_radio.setText(result.get(0));

            } else if (resultCode == RESULT_OK && null != data
                    && requestCode == REQ_CODE_SPEECH_INPUT_THREE) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edt_des_radio.setText(result.get(0));

            } else if (resultCode == RESULT_OK
                    && requestCode == RESULT_LOAD_IMG && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                imgDecodableString = Utilities.compressImage(this, imgDecodableString);

                img_cam.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            }

        } catch (Exception e) {
            Utilities.showToast(this, "Something went wrong");

        }
    }

    // Get Path Using URI...
    public String getPathNew(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor != null ? cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) : 0;
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_THREE);
            } catch (ActivityNotFoundException a) {
                Utilities.showToast(this, getString(R.string.speech_not_supported));

            }
        }

    }

    // Send Radio Programs To Server...
    class SendRadioAlerts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpClient client = new DefaultHttpClient();
            MultipartEntity reqEntity = new MultipartEntity();
            HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT + Config.ADD_RADIO_ALERT);

            try {

                if (params[0] != null) {
                    if (!params[0].equals("")) {
                        reqEntity.addPart("id", new StringBody(params[0], Charset.forName("UTF-8")));
                    } else {
                        reqEntity.addPart("id", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("id", new StringBody(""));
                }

                if (params[1] != null) {
                    if (!params[1].equals("")) {
                        reqEntity.addPart("title", new StringBody(params[1], Charset.forName("UTF-8")));
                    } else {
                        reqEntity.addPart("title", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("title", new StringBody(""));
                }

                if (params[2] != null) {
                    if (!params[2].equals("")) {
                        reqEntity.addPart("description", new StringBody(params[2], Charset.forName("UTF-8")));
                    } else {
                        reqEntity.addPart("description", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("description", new StringBody(""));
                }

                if (params[3] != null) {
                    if (!params[3].equals("")) {
                        File imageFile = new File(params[3]);
                        FileBody bodyImage = new FileBody(imageFile);
                        reqEntity.addPart("image", bodyImage);
                    } else {
                        reqEntity.addPart("image", new StringBody(""));
                    }
                } else {
                    reqEntity.addPart("image", new StringBody(""));
                }

                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                resEntity = response.getEntity();
                response_str = EntityUtils.toString(resEntity);

            } catch (ClientProtocolException e) {
                Log.e("ClientProtException??", "ClientProtocolException?? " + e.toString());
            } catch (IOException e) {
                Log.e("IOException??", "IOException?? " + e.toString());
            }
            return response_str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.setVisibility(View.GONE);
            deleteCompressImageFiles();
            if (result.length() > 0) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.get("success").equals(true)) {
                        Utilities.showToast(AddRadioAlarmActivity.this, json.get("value").toString());
                        Intent mintent = new Intent();
                        setResult(RESULT_OK, mintent);
                        finish();
                        overridePendingTransition(R.anim.activity_open_scale,
                                R.anim.activity_close_translate);
                    } else {
                        Utilities.showToast(AddRadioAlarmActivity.this, json.get("value").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Delete The compress Images Files On SDCard After Upload To The Server..
    private void deleteCompressImageFiles() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/Maranan/Images");
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                new File(file, children[i]).delete();
            }
        }

    }

    // OnBack Press...
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
