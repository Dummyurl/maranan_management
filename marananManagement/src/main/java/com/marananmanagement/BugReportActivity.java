package com.marananmanagement;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;


public class BugReportActivity extends Activity {
	public static final String STACKTRACE = "fbreader.stacktrace";

	private String getVersionName() {
		try {
			final PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return info.versionName + " (" + info.versionCode + ")";
		} catch (Exception e) {
			return "";
		}
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.bug_report_view);
		final StringBuilder reportText = new StringBuilder();

		reportText.append("Model:").append(Build.MODEL).append("\n");
		reportText.append("Device:").append(Build.DEVICE).append("\n");
		reportText.append("Product:").append(Build.PRODUCT).append("\n");
		reportText.append("Manufacturer:").append(Build.MANUFACTURER)
				.append("\n");
		reportText.append("Version:").append(Build.VERSION.RELEASE)
				.append("\n");
		reportText.append(getIntent().getStringExtra(STACKTRACE));

		final TextView reportTextView = (TextView) findViewById(R.id.report_text);
		reportTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		reportTextView.setClickable(false);
		reportTextView.setLongClickable(false);

		final String versionName = getVersionName();
		reportTextView.append("EBandroid " + versionName
				+ " has been exception, sorry.\n\n");
		reportTextView.append(reportText);

		findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
	}
}
