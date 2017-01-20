package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.marananmanagement.adapter.SmsAdapter;
import com.marananmanagement.database.SmsDB;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.Utilities;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.ArrayList;

public class SmsActivity extends Activity {
    private static SmsActivity mContext;
    ImageView img_edit_sms;
    DynamicListView list_sms;
    ArrayList<String> list;
    String[] alerts;
    ProgressBar pDialog;
    ArrayList<GetterSetter> listMessage;
    int REQUEST_CODE = 200;
    public SmsAdapter madapter;
    Editor editor;
    SmsDB db;
    SQLiteDatabase sqlitedb;

    // SmsActivity Instance
    public static SmsActivity getInstance() {
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
        setContentView(R.layout.activity_sms);
        mContext = SmsActivity.this;
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        img_edit_sms = (ImageView) findViewById(R.id.img_edit_sms);
        list_sms = (DynamicListView) findViewById(R.id.list_sms);
        Utilities.setListViewHeightBasedOnChildren(list_sms);
        db = new SmsDB(this);

        list_sms.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {

                GetterSetter getset = new GetterSetter();
                getset.setId(listMessage.get(position).getId());
                getset.setMessage(listMessage.get(position).getMessage());

                if (Utilities.doesDatabaseExist(SmsActivity.this, "SmsDatabase")) {

                    if (!db.isIdExist(listMessage.get(position).getId())) {
                        db = new SmsDB(SmsActivity.this);
                        db.insertRecords(sqlitedb, getset);
                    } else {
                        db.updateMessage(listMessage.get(position).getId(), listMessage.get(position).getMessage());
                    }

                } else {

                    if (!db.isIdExist(listMessage.get(position).getId())) {
                        db = new SmsDB(SmsActivity.this);
                        db.insertRecords(sqlitedb, getset);

                    } else {
                        db.updateMessage(listMessage.get(position).getId(), listMessage.get(position).getMessage());
                    }
                }

                Intent in = new Intent(SmsActivity.this, SmsEditPage.class);
                in.putExtra("id", listMessage.get(position).getId());
                startActivityForResult(in, REQUEST_CODE);

            }
        });

        img_edit_sms.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent in = new Intent(SmsActivity.this, SmsEditPage.class);
                startActivityForResult(in, REQUEST_CODE);
            }
        });

        // Swipe to dismiss list view item
        list_sms.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(final ViewGroup listView,
                                  final int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {

                    if (listMessage.size() > 0)
                    /* Delete Message */
                        AsyncRequest.deleteMessages(mContext, listMessage.get(position).getId());
                    db.deleteSingleRecord(listMessage.get(position).getId());
                    listMessage.remove(String.valueOf(position));
                    listMessage.remove(position);
                    madapter.notifyDataSetChanged();
                }
            }
        });
        
		/*Get All Message Request*/
        AsyncRequest.getAllMessages(this, this.getClass().getSimpleName(), pDialog);


    }

    // OnActivity Result To Get Result Here..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {

                if (listMessage.size() > 0) {
                    listMessage.clear();
                }
				/* Get All Message Request */
                AsyncRequest.getAllMessages(this, this.getClass()
                        .getSimpleName(), pDialog);

            }
        }
    }

    /* Set Adpter For Messages */
    public void setAdapterMessage(ArrayList<GetterSetter> listMessage) {
        if (listMessage.size() > 0) {
            this.listMessage = listMessage;
            madapter = new SmsAdapter(SmsActivity.this, listMessage, this
                    .getClass().getSimpleName(), pDialog);
            list_sms.setAdapter(madapter);
            madapter.notifyDataSetChanged();
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
