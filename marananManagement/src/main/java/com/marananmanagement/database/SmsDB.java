package com.marananmanagement.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.marananmanagement.util.GetterSetter;

import java.io.File;
import java.util.ArrayList;

public class SmsDB extends SQLiteOpenHelper {

    public static SQLiteDatabase sqlitedb;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SmsDatabase";
    private static final String FILE_DIR = "DataBase";
    public static String TABLE_NAME = "SMS";

    public static final String KEY_ID = "id";
    public static final String DED_ID = "ded_id";
    public static final String MESSAGE = "message";
    public static final String MODE = "mode";
    public static final String TIME_HR = "time_hr";
    public static final String TIME_MIN = "time_min";
    public static final String SUN = "sunday";
    public static final String MON = "monday";
    public static final String TUES = "tuesday";
    public static final String WED = "wednesday";
    public static final String THUR = "thursday";
    public static final String FRI = "friday";
    public static final String SAT = "saturday";

    public static GetterSetter getset;

    // Called Constructor
    public SmsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called Constructor
    public SmsDB(Context context, String name, CursorFactory factory,
                 int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onCreate for create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Query For Add Table */
        String SQL = "";

        SQL = SQL + "CREATE TABLE " + TABLE_NAME;
        SQL = SQL + "(";

        SQL = SQL + "	" + KEY_ID + " INTEGER PRIMARY KEY, ";
        SQL = SQL + "	" + DED_ID + " VARCHAR, ";
        SQL = SQL + "	" + MESSAGE + " VARCHAR, ";
        SQL = SQL + "	" + MODE + " VARCHAR, ";
        SQL = SQL + "	" + TIME_HR + " VARCHAR, ";
        SQL = SQL + "	" + TIME_MIN + " VARCHAR, ";
        SQL = SQL + "	" + SUN + " VARCHAR, ";
        SQL = SQL + "	" + MON + " VARCHAR, ";
        SQL = SQL + "	" + TUES + " VARCHAR, ";
        SQL = SQL + "	" + WED + " VARCHAR, ";
        SQL = SQL + "	" + THUR + " VARCHAR, ";
        SQL = SQL + "	" + FRI + " VARCHAR, ";
        SQL = SQL + "	" + SAT + " VARCHAR ";

        SQL = SQL + ")";

        db.execSQL(SQL);

    }

    // onUpgrade for upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Open DataBase
    public void open() {
        sqlitedb = getWritableDatabase();
    }

    // Get DataBase From SD Card
    public void getDataBaseFromSdCard() {
        File dbfile = new File(Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR + File.separator + DATABASE_NAME);
        @SuppressWarnings("unused")
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    // First Name already exists or not
    public boolean isIdExist(String id) {
        Cursor cursor = null;
        try {
            sqlitedb = this.getWritableDatabase();
            String SQL = "SELECT COUNT(" + DED_ID + ") FROM " + TABLE_NAME
                    + " WHERE " + DED_ID + " = '" + id + "'";
            cursor = sqlitedb.rawQuery(SQL, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {
                    return true;
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    // Insert records
    public void insertRecords(SQLiteDatabase sqlite_dbase, GetterSetter getset) {
        SQLiteStatement insertStmt;
        sqlite_dbase = getWritableDatabase();
        String SQL = "";

        SQL = SQL + "INSERT INTO " + TABLE_NAME;
        SQL = SQL + "(";
        SQL = SQL + " 	" + DED_ID + ", ";
        SQL = SQL + " 	" + MESSAGE + ", ";
        SQL = SQL + " 	" + MODE + ", ";
        SQL = SQL + " 	" + TIME_HR + ", ";
        SQL = SQL + " 	" + TIME_MIN + ", ";
        SQL = SQL + " 	" + SUN + ", ";
        SQL = SQL + " 	" + MON + ", ";
        SQL = SQL + " 	" + TUES + ", ";
        SQL = SQL + " 	" + WED + ", ";
        SQL = SQL + " 	" + THUR + ", ";
        SQL = SQL + " 	" + FRI + ", ";
        SQL = SQL + " 	" + SAT;
        SQL = SQL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        sqlite_dbase.beginTransaction();

        insertStmt = sqlite_dbase.compileStatement(SQL);
        insertStmt.bindString(1, getset.getId());
        insertStmt.bindString(2, getset.getMessage());
        insertStmt.bindString(3, "");
        insertStmt.bindString(4, "");
        insertStmt.bindString(5, "");
        insertStmt.bindString(6, "");
        insertStmt.bindString(7, "");
        insertStmt.bindString(8, "");
        insertStmt.bindString(9, "");
        insertStmt.bindString(10, "");
        insertStmt.bindString(11, "");
        insertStmt.bindString(12, "");
        insertStmt.execute();
        insertStmt.clearBindings();
        sqlite_dbase.setTransactionSuccessful();
        sqlite_dbase.endTransaction();

    }

    // Update message
    public void updateMessage(String id, String message) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + MESSAGE + "='"
                    + message + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // updateMode
    public void updateMode(String id, String mode) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + MODE + "='" + mode
                    + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        sqlitedb.close();
    }

    // updatetime_hr
    public void updatetime_hr(String id, String time_hr) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + TIME_HR + "='"
                    + time_hr + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        sqlitedb.close();
    }

    // Update message
    public void updatetime_min(String id, String time_min) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + TIME_MIN + "='"
                    + time_min + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        sqlitedb.close();
    }

    // Update message
    public void updateSun(String id, String sun) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + SUN + "='" + sun + "'"
                    + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // Update message
    public void updateMon(String id, String mon) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + MON + "='" + mon + "'"
                    + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // Update message
    public void updateTues(String id, String tues) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + TUES + "='" + tues
                    + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // Update message
    public void updateWed(String id, String wed) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + WED + "='" + wed + "'"
                    + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        sqlitedb.close();
    }

    // Update message
    public void updateThur(String id, String thur) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + THUR + "='" + thur
                    + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // Update message
    public void updateFri(String id, String friday) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + FRI + "='" + friday
                    + "'" + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // Update message
    public void updateSat(String id, String sat) {
        Cursor cursor = null;
        try {
            sqlitedb = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAME + " SET " + SAT + "='" + sat + "'"
                    + " WHERE " + DED_ID + "=" + id;
            cursor = sqlitedb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {

                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        sqlitedb.close();
    }

    // getName from table using name
    public GetterSetter getAlertValues(String id) {
        GetterSetter getset;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        String SQL = "";

        SQL = SQL + " SELECT * ";
        SQL = SQL + " FROM " + TABLE_NAME;
        SQL = SQL + " WHERE " + DED_ID + " = " + id;

        cursor = db.rawQuery(SQL, new String[]{});
        getset = new GetterSetter();

        if (cursor.moveToFirst()) {
            getset.setMessage(cursor.getString(2));
            getset.setMode(cursor.getString(3));
            getset.setTime_hr(cursor.getString(4));
            getset.setTime_min(cursor.getString(5));
            getset.setSun(cursor.getString(6));
            getset.setMon(cursor.getString(7));
            getset.setTues(cursor.getString(8));
            getset.setWed(cursor.getString(9));
            getset.setThur(cursor.getString(10));
            getset.setFri(cursor.getString(11));
            getset.setSat(cursor.getString(12));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return getset;
        }
    }

    // Getting All Records
    public ArrayList<GetterSetter> getAllRecords() {
        ArrayList<GetterSetter> contactList = new ArrayList<GetterSetter>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GetterSetter getset = new GetterSetter();
                getset.setNature((cursor.getString(1)));
                getset.setName(cursor.getString(2));
                getset.setSex(cursor.getString(3));
                getset.setThere_Is(cursor.getString(4));
                getset.setName_Optional(cursor.getString(5));
                getset.setBlessing(cursor.getString(6));
                getset.setTime(cursor.getString(10));
                // Adding records to list
                contactList.add(getset);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // delete records
    public void deleteAllRecord() {
        sqlitedb = this.getWritableDatabase();
        sqlitedb.delete(TABLE_NAME, null, null);
        sqlitedb.close();
    }

    // Deleting single contact
    public void deleteSingleRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, DED_ID + " = ?", new String[]{id});
        db.close();
    }

}
