package com.marananmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.libraries.PullToRefreshBase;
import com.handmark.pulltorefresh.libraries.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.libraries.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.libraries.PullToRefreshListView;
import com.marananmanagement.adapter.ManagementAdapter;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.interfaces.Constant;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.MarshMallowPermission;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener, Constant {
    private static MainActivity mContext;
    private ManagementAdapter mAdapter;
    private int REQUEST_CODE = 100;
    private int REQUEST_CODE_RADIO = 200;
    private int REQUEST_CODE_SMS = 300;
    private int REQUEST_CODE_NEWSLETTER = 400;
    private int REQUEST_CODE_CHANNEL = 500;
    private int REQUEST_CODE_FINANCIAL = 600;
    private int REQUEST_CODE_TEL = 700;
    private ArrayList<GetterSetter> listDedications;
    private PullToRefreshListView mPullRefreshListView;
    private ListView actualListView;
    private DrawerLayout drawerlayout;
    private ActionBarDrawerToggle drawertoggle;
    private ImageView menu_icon;
    private Button btn_sms_sidebar, btn_caution_sidebar, btn_radio_sidebar,
            btn_video_sidebar, btn_dedication, btn_awareness, btn_newsletter,
            btn_newsletter_collapse, btn_tel;
    private RelativeLayout ll_btn;
    private ArrayList<String> values;
    private ListView lv_years;
    private ProgressBar pDialog;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);


    // MainActivity Instance
    public static MainActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Handle UnCaughtException Exception Handler
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//				this));
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setVisibility(View.GONE);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ll_btn = (RelativeLayout) findViewById(R.id.ll_btn);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        btn_sms_sidebar = (Button) findViewById(R.id.btn_sms_sidebar);
        btn_sms_sidebar.setOnClickListener(this);
        btn_caution_sidebar = (Button) findViewById(R.id.btn_caution_sidebar);
        btn_caution_sidebar.setOnClickListener(this);
        btn_radio_sidebar = (Button) findViewById(R.id.btn_radio_sidebar);
        btn_radio_sidebar.setOnClickListener(this);
        btn_video_sidebar = (Button) findViewById(R.id.btn_video_sidebar);
        btn_video_sidebar.setOnClickListener(this);
        btn_dedication = (Button) findViewById(R.id.btn_dedication);
        btn_dedication.setOnClickListener(this);
        btn_awareness = (Button) findViewById(R.id.btn_awareness);
        btn_awareness.setOnClickListener(this);
        btn_newsletter = (Button) findViewById(R.id.btn_newsletter);
        btn_newsletter.setOnClickListener(this);
        btn_newsletter_collapse = (Button) findViewById(R.id.btn_newsletter_collapse);
        btn_newsletter_collapse.setOnClickListener(this);
        btn_newsletter_collapse
                .setBackgroundResource(R.drawable.collapse_arrow);
        btn_newsletter_collapse.setTag(R.drawable.collapse_arrow);
        menu_icon = (ImageView) findViewById(R.id.img_menu_icon);
        menu_icon.setOnClickListener(this);
        lv_years = (ListView) findViewById(R.id.lv_years);

        btn_tel = (Button) findViewById(R.id.btn_tel);
        btn_tel.setOnClickListener(this);

        lv_years.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                btn_newsletter_collapse
                        .setBackgroundResource(R.drawable.collapse_arrow);
                btn_newsletter_collapse.setTag(R.drawable.collapse_arrow);
                lv_years.setVisibility(View.GONE);
                Intent newsIntent = new Intent(MainActivity.this,
                        NewsLetterList.class);
                newsIntent.putExtra("date", values.get(position));
                startActivityForResult(newsIntent, REQUEST_CODE_NEWSLETTER);
                drawerlayout.closeDrawer(ll_btn);
            }
        });

        drawertoggle = new ActionBarDrawerToggle(this, drawerlayout,
                R.drawable.menu_icon, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name) {
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons

                // drawerlayout.closeDrawer(ll_btn);
            }

            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons

            }
        };
        drawerlayout.setDrawerListener(drawertoggle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (marshMallowPermission.checkPermissionForAccessWifiState() == true
                    && marshMallowPermission.checkPermissionForInternet() == true
                    && marshMallowPermission.checkPermissionForReadExternalStorage() == true
                    && marshMallowPermission.checkPermissionForACCESS_NETWORK_STATE() == true
                    && marshMallowPermission.checkPermissionForGET_ACCOUNTS() == true
                    && marshMallowPermission.checkPermissionForWAKE_LOCK() == true
                    && marshMallowPermission.checkPermissionForRECORD_AUDIO() == true
                    && marshMallowPermission.checkPermissionForWriteExternalStorage() == true
                    && marshMallowPermission.checkPermissionForREAD_PHONE_STATE() == true
                    && marshMallowPermission.checkPermissionForVIBRATE() == true
                    && marshMallowPermission.checkPermissionForCAMERA() == true
                    && marshMallowPermission.checkPermissionForSEND_SMS() == true
                    && marshMallowPermission.checkPermissionForRECEIVE_SMS() == true) {

				/* Get AsyncRequest */
                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass().getSimpleName(), pDialog);

            } else {
                marshMallowPermission.commonPermissionForApp();
            }
        } else {
            /* Get AsyncRequest */
            AsyncRequest.getUniqueDates(this);
            AsyncRequest.getAllDedications(this, this.getClass().getSimpleName(), pDialog);
        }

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                getApplicationContext(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        // Update the LastUpdatedLabel
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);

                        // Do work to refresh the list here.
                        AsyncRequest.getAllDedications(mContext, this
                                .getClass().getSimpleName(), pDialog);
                    }
                });

        // Add an end-of-list listener
        mPullRefreshListView
                .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {

                    }
                });

        actualListView = mPullRefreshListView.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        actualListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(mContext, ManagementActivity.class);
                intent.putExtra("id", listDedications.get(arg2 - 1).getId());
                intent.putExtra("time", listDedications.get(arg2 - 1).getTime());
                intent.putExtra("email", listDedications.get(arg2 - 1).getEmail());
                intent.putExtra("first_name", listDedications.get(arg2 - 1).getName());
                intent.putExtra("f_status", listDedications.get(arg2 - 1).getF_status());
                intent.putExtra("m_status", listDedications.get(arg2 - 1).getM_status());
                intent.putExtra("l_status", listDedications.get(arg2 - 1).getL_status());
                intent.putExtra("there_Is", listDedications.get(arg2 - 1).getThere_Is());
                intent.putExtra("last_name", listDedications.get(arg2 - 1).getName_Optional());
                intent.putExtra("f_sex_status", listDedications.get(arg2 - 1).getF_sex_status());
                intent.putExtra("m_sex_status", listDedications.get(arg2 - 1).getM_sex_status());
                intent.putExtra("size", String.valueOf(listDedications.size()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {

                if (listDedications != null)
                    if (listDedications.size() > 0) {
                        listDedications.clear();
                    }

                if (values != null && values.size() > 0) {
                    values.clear();
                }

                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass()
                        .getSimpleName(), pDialog);

            } else if (requestCode == REQUEST_CODE_RADIO) {

                if (listDedications != null)
                    if (listDedications.size() > 0) {
                        listDedications.clear();
                    }

                if (values != null && values.size() > 0) {
                    values.clear();
                }
				
				/* Get AsyncRequest */
                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass()
                        .getSimpleName(), pDialog);

            } else if (requestCode == REQUEST_CODE_SMS) {

                if (listDedications != null)
                    if (listDedications.size() > 0) {
                        listDedications.clear();
                    }
                if (values != null && values.size() > 0) {
                    values.clear();
                }
				
				/* Get AsyncRequest */
                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass()
                        .getSimpleName(), pDialog);

            } else if (requestCode == REQUEST_CODE_NEWSLETTER) {

                if (listDedications != null)
                    if (listDedications.size() > 0) {
                        listDedications.clear();
                    }
                if (values != null && values.size() > 0) {
                    values.clear();
                }
				
				/* Get AsyncRequest */
                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass()
                        .getSimpleName(), pDialog);

            } else if (requestCode == REQUEST_CODE_FINANCIAL) {

                if (listDedications != null)
                    if (listDedications.size() > 0) {
                        listDedications.clear();
                    }
                if (values != null && values.size() > 0) {
                    values.clear();
                }
				
				/* Get AsyncRequest */
                AsyncRequest.getUniqueDates(this);
                AsyncRequest.getAllDedications(this, this.getClass()
                        .getSimpleName(), pDialog);

            }

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawertoggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawertoggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sms_sidebar:
                Intent sms = new Intent(this, SmsActivity.class);
                startActivityForResult(sms, REQUEST_CODE_SMS);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_caution_sidebar:
                Intent dedication = new Intent(this, AlertList.class);
                startActivityForResult(dedication, REQUEST_CODE);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_radio_sidebar:
                Intent in = new Intent(this, RadioPrograms.class);
                startActivityForResult(in, REQUEST_CODE_RADIO);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_video_sidebar:
                Intent channel = new Intent(this,
                        ChannelManagement.class);
                startActivityForResult(channel, REQUEST_CODE_CHANNEL);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_dedication:
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_awareness:
                Intent awareness = new Intent(this,
                        FinancialManagement.class);
                startActivityForResult(awareness, REQUEST_CODE_FINANCIAL);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_tel:
                Intent tel = new Intent(this, TelActivity.class);
                startActivityForResult(tel, REQUEST_CODE_TEL);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_newsletter:
                btn_newsletter_collapse
                        .setBackgroundResource(R.drawable.collapse_arrow);
                btn_newsletter_collapse.setTag(R.drawable.collapse_arrow);
                lv_years.setVisibility(View.GONE);
                Intent newsIntent = new Intent(MainActivity.this, NewsLetterList.class);
                newsIntent.putExtra("date", "");
                startActivityForResult(newsIntent, REQUEST_CODE_NEWSLETTER);
                drawerlayout.closeDrawer(ll_btn);
                break;

            case R.id.btn_newsletter_collapse:
                if ((Integer) btn_newsletter_collapse.getTag() == R.drawable.collapse_arrow) {
                    btn_newsletter_collapse
                            .setBackgroundResource(R.drawable.expand_arrow);
                    btn_newsletter_collapse.setTag(R.drawable.expand_arrow);
                    lv_years.setVisibility(View.VISIBLE);

                } else {
                    btn_newsletter_collapse
                            .setBackgroundResource(R.drawable.collapse_arrow);
                    btn_newsletter_collapse.setTag(R.drawable.collapse_arrow);
                    lv_years.setVisibility(View.GONE);
                }
                break;

            case R.id.img_menu_icon:
                drawerlayout.openDrawer(ll_btn);
                break;

            default:
                break;
        }

    }

    /* Set Adpater After Getting Result From Server... */
    public void setAdapter(ArrayList<GetterSetter> listDedications) {
        mPullRefreshListView.onRefreshComplete();
        if (listDedications != null && listDedications.size() > 0) {
            this.listDedications = listDedications;
            mAdapter = new ManagementAdapter(this, listDedications);
            mPullRefreshListView.setAdapter(mAdapter);
        }
    }

    /* Set Adpater After Getting Dates From Server... */
    public void setAdapterForDates(ArrayList<String> values) {
        if (values != null && values.size() > 0) {
            this.values = values;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.newsletter_years_items, R.id.newsletter_list_item,
                    values);
            lv_years.setAdapter(adapter);

        }

    }

    // Request Call Back Method To check permission is granted by user or not for MarshMallow...
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case COMMON_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // This method will be executed once the timer is over
                    // Start your app main activity

                    /* Get AsyncRequest */
                    AsyncRequest.getUniqueDates(this);
                    AsyncRequest.getAllDedications(this, this.getClass().getSimpleName(), pDialog);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
