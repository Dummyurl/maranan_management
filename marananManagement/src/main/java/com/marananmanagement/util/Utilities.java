package com.marananmanagement.util;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marananmanagement.R;
import com.nispok.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static CutTimer timer;
    public static String curdate;
    public static String dateStop;

    @SuppressWarnings("deprecation")
    public static void showAlertDialog(Context context, String title,
                                       String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.sucess : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /**
     * check email validation
     **/
    public static boolean isEmailValid(String email) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void showToast(Context context, String message) {
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        try {
            Snackbar.with(context).text(message).show((Activity) context);
        } catch (Exception e) {
            Log.e("Exception", "ExceptionSnackBar??" + e.getMessage());
        }
    }

    public static void showToastNew(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Exception", "ExceptionSnackBar??" + e.getMessage());
        }
    }

    /****
     * Method for Setting the Height of the ListView dynamically. Hack to fix
     * the issue of not showing all the items of the ListView when placed inside
     * a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * hide Keyboard
     *
     * @param ctx
     **/
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) ctx).getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Find DataBase Exists or Not
    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    // Get Israel Time
    public static String getIsraelTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        return sdf.format(new Date());
    }

    // Get Israel Date
    public static String getIsraelDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy",
                Locale.ENGLISH);
        // sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        return sdf.format(new Date());
    }

    // Get Current Date
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy",
                Locale.ENGLISH);
        return sdf.format(new Date());
    }

    // Get Current Date
    public static String getCurrentDateNew() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.ENGLISH);
        // sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        return sdf.format(new Date());
    }

    // Compress Image Using Image URI...
    @SuppressWarnings("deprecation")
    public static String compressImage(Context ctx, String imageUri) {
        String filePath = getRealPathFromURI(ctx, imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not
        // loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as
        // 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the
        // image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        // setting inSampleSize value allows to load a scaled down version of
        // the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        // this options allow android to claim the bitmap memory if it runs low
        // on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                middleY - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

            // write the compressed bitmap at the destination specified by
            // filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "Maranan/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private static String getRealPathFromURI(Context ctx, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = ctx.getContentResolver().query(contentUri, null, null,
                null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    // Get File Extension...
    public static String getExtension(String path) {
        String filename = path;
        String filenameArray[] = filename.split("/");
        String extension = filenameArray[filenameArray.length - 1];
        return getFileExt(extension);
    }

    // Get File Extension...
    public static String getFileExt(String fileName) {
        return fileName.substring((fileName.lastIndexOf(".") + 1),
                fileName.length());
    }

    // Copy Paste Audio Files in the folder for changing Path...
    public static String getAudioPath(String frompath) {
        File from = new File(frompath);
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "Maranan/Audio");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".mp3");
        File to = new File(uriSting);
        if (from.exists())
            from.renameTo(to);

        return uriSting;
    }

    // parse date to change date patterns...
    public static String parseDateToddMMyyyy(String datestr) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(datestr);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    // Start Timer Here...
    public static void startTime(Context ctx, TextView tv_time) {
        Thread myThread = null;
        Runnable runnable = new CountDownRunner(ctx, tv_time);
        myThread = new Thread(runnable);
        myThread.start();
    }

    // Get Israel Time...
    public static void doWork(Context ctx, final TextView tv_time) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            public void run() {
                try {
                    tv_time.setText(Utilities.getIsraelTime());
                } catch (Exception e) {
                }
            }
        });
    }

    // Get CountDownRunnable To Count Time Every Secound ANd Update On The
    // textView...
    static class CountDownRunner implements Runnable {
        TextView tv_time;
        Context ctx;

        public CountDownRunner(Context ctx, TextView tv_time) {
            this.tv_time = tv_time;
            this.ctx = ctx;
        }

        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork(ctx, tv_time);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    // Get CutTimer Class Method Call Here...
    public static void getCutTimerClass(Context ctx, long millisInFuture,
                                        long countDownInterval, TextView textView, int position,
                                        final TimerPosition listener, boolean isFirstTime) {
        timer = new CutTimer(ctx, millisInFuture, countDownInterval, textView,
                position, listener, isFirstTime);
        timer.start();

    }

    // Cancel CountDown Timer Call Here...
    public static CutTimer getCountDownTimer() {
        return timer;
    }

    // CutDown Timer Class Call Here To Start Timer on TextView...
    public static class CutTimer extends CountDownTimer {

        TextView textView;
        Context ctx;
        long millisInFuture;
        long countDownInterval;
        Button swipe_check;
        Button swipe_uncheck;
        TimerPosition listener;
        public int position;
        boolean isFirstTime;

        public CutTimer(Context ctx, long millisInFuture,
                        long countDownInterval, TextView textView, int pos,
                        final TimerPosition listener, boolean isFirstTime) {
            super(millisInFuture, countDownInterval);
            // Log.e("millisInFuture", "millisInFuture?? "+millisInFuture);
            this.listener = listener;
            this.position = pos;
            this.textView = textView;
            this.ctx = ctx;
            this.isFirstTime = isFirstTime;
            this.millisInFuture = millisInFuture;
            this.countDownInterval = countDownInterval;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isFirstTime = true;
            // Log.e("onTick", "onTick?? " +millisUntilFinished);
            int days = (int) ((millisUntilFinished / 1000) / 86400);
            int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
            int minutes = (int) (((millisUntilFinished / 1000) - (days * 86400) - (hours * 3600)) / 60);
            @SuppressWarnings("unused")
            int seconds = (int) ((millisUntilFinished / 1000) % 60);
            String Days = String.format("%02d", days);
            String Hours = String.format("%02d", hours);
            String Minutes = String.format("%02d", minutes);
            @SuppressWarnings("unused")
            String hms = Days + " : " + Hours + " : " + Minutes;
            // Log.e("hms", "hms?? " + hms);
            if (!Days.equals("") && !Hours.equals("") && !Minutes.equals("")) {

                textView.setText(ctx.getResources().getString(R.string.left)
                        + " " + Days + " "
                        + ctx.getResources().getString(R.string.days) + " "
                        + Hours + " "
                        + ctx.getResources().getString(R.string.hours) + " "
                        + ctx.getResources().getString(R.string.and) + " "
                        + Minutes + " "
                        + ctx.getResources().getString(R.string.minutes));

            } else {
                textView.setText("");
            }
        }

        @Override
        public void onFinish() {
            // Log.e("tttt", "ttt");
            if (isFirstTime == true) {
                isFirstTime = false;
                listener.timerFinish(position);
            } else {
                isFirstTime = true;
                textView.setText("");
            }
        }
    }

    // Get Date Difference Between Current Date And Future Date....
    public static long getDateDiiferece(String nextdate) {

        long milisecon = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        curdate = dateFormat.format(date);
        dateStop = nextdate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(curdate);
            d2 = format.parse(dateStop);
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            milisecon = (diffDays * 24 * 60 * 60 * 1000 + diffHours * 60 * 60
                    * 1000 + diffMinutes * 60 * 1000 + diffSeconds * 1000);

            // Log.e("curdate", "curdate?? "+curdate);
            // Log.e("dateStop", "dateStop?? "+dateStop);

        } catch (Exception e1) {
            System.out.println("exception " + e1);
        }
        return milisecon;
    }

    public interface TimerPosition {
        void timerFinish(int position);
    }

    // Comapre Date And Time....
    public static boolean compareDateTime(String compareDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        String currDate = dateFormat.format(date);
        Date currentDate = new Date();
        Date convertedDate2 = new Date();
        try {
            currentDate = dateFormat.parse(currDate);
            convertedDate2 = dateFormat.parse(compareDate);

            if (convertedDate2.after(currentDate)) {

                return true;

            } else if (convertedDate2.before(currentDate)) {

                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Comapre Date And Time For Live....
    public static boolean compareDateTimeLive(String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        String currDate = dateFormat.format(date);
        Date currentDate = new Date();
        Date startDateNew = new Date();

        try {
            currentDate = dateFormat.parse(currDate);
            startDateNew = dateFormat.parse(startDate);

            if (startDateNew.after(currentDate)) {
                // Log.e("after", "after-B");
                return true;

            } else if (startDateNew.before(currentDate)) {
                // Log.e("before", "before-P");
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Is SdCard Present OR Not...
    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    // Get Path Using URI...
    public static String getPathNew(Uri uri, Context ctx) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // Delete The compress Images Files On SDCard After Upload To The Server..
    public static void deleteCompressImageFiles() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/Maranan/Images");
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                new File(file, children[i]).delete();
            }
        }

    }

    // Encode Decode EmoJies Text....
    public static String encodeImoString(String string) {
        return EmojiMapUtil.replaceUnicodeEmojis(string);
    }

    // Encode Decode EmoJies Text....
    public static String decodeImoString(String string) {
        return EmojiMapUtil.replaceCheatSheetEmojis(string);
    }

    // show RunOn Ui Thread Methood
    public static void showRunUiThread(final Context ctx, final Exception e) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utilities.showToast(ctx, e.getMessage());
            }
        });
    }

    // Change Date Format yyyy-MM-dd to dd.MM.yyyy
    public static String parseDateToddMMyyyy2(String dates) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd.MM.yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dates);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Date getToDate(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String dateInString = date + " " + time;
        Date dates = null;
        try {
            dates = sdf.parse(dateInString);
        } catch (ParseException e) {
            Log.e("ParseException", "ParseException?? " + e.getCause());
            e.printStackTrace();
        }
        return dates;
    }

    public static void showProgressDialog(ProgressBar pDialog) {
        pDialog.setVisibility(View.VISIBLE);
    }

    public static void dismissProgressDialog(ProgressBar pDialog) {
        pDialog.setVisibility(View.GONE);
    }

    public static int convertDpToPixel(Context ctx, float dp) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

}
