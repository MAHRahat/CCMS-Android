package com.mahrahat.ccmsandroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Database Helper, makes it easier to work with database
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ccms_android.db";
    private static final int DB_VERSION = 1;
    private static final String TBL_USER_TBL_NAME = "user";
    private static final String TBL_USER_COLUMN_KEY = "kee";
    private static final String TBL_USER_COLUMN_VAL = "value";
    private static final String TBL_USER_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TBL_USER_TBL_NAME
            + " ( "
            + TBL_USER_COLUMN_KEY + " text primary key, "
            + TBL_USER_COLUMN_VAL + " text "
            + ")";
    private static final String TBL_COMPLAINTS_TBL_NAME = "complaints";
    private static final String TBL_COMPLAINTS_COLUMN_ID = "id";
    private static final String TBL_COMPLAINTS_COLUMN_DESC = "description";
    //private static final String TBL_COMPLAINTS_COLUMN_CREATED = "created";
    private static final String TBL_COMPLAINTS_COLUMN_SUBMITTED = "submitted";
    private static final String TBL_COMPLAINTS_COLUMN_UPDATED = "updated";
    private static final String TBL_COMPLAINTS_COLUMN_STATUS = "status";
    private static final String TBL_COMPLAINTS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TBL_COMPLAINTS_TBL_NAME
            + " ( "
            + TBL_COMPLAINTS_COLUMN_ID + " text primary key , "
            + TBL_COMPLAINTS_COLUMN_DESC + " text , "
            //+ TBL_COMPLAINTS_COLUMN_CREATED + " text , "
            + TBL_COMPLAINTS_COLUMN_SUBMITTED + " text , "
            + TBL_COMPLAINTS_COLUMN_UPDATED + " text , "
            + TBL_COMPLAINTS_COLUMN_STATUS + " text "
            + " ) ";
    private static SQLiteDatabase db;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static DBHelper dbInstance;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DBHelper.context = context;
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBHelper(context.getApplicationContext());
            db = dbInstance.getWritableDatabase();
        }
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_USER_CREATE);
        db.execSQL(TBL_COMPLAINTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_USER_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_COMPLAINTS_TBL_NAME);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        if (dbInstance != null) {
            db.close();
        }
    }


    public void insertValueOfKee(String kee, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TBL_USER_COLUMN_KEY, kee);
        contentValues.put(TBL_USER_COLUMN_VAL, value);
        db.insert(TBL_USER_TBL_NAME, null, contentValues);
    }

    public String readValueOfKee(String kee) {
        String value = null;
        Cursor cursor = db.query(TBL_USER_TBL_NAME,
                null,
                TBL_USER_COLUMN_KEY + " = ?",
                new String[]{kee},
                null,
                null,
                null);
        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex(TBL_USER_COLUMN_VAL));
        }
        cursor.close();
        return value;
    }

    public int updateValueOfKee(String kee, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TBL_USER_COLUMN_VAL, value);
        int retVal = db.update(TBL_USER_TBL_NAME, contentValues, TBL_USER_COLUMN_KEY + " = ? ", new String[]{kee});
        return retVal;
    }

    public int deleteValueOfKee(String kee) {
        return db.delete(TBL_USER_TBL_NAME, TBL_USER_COLUMN_KEY + " = ? ", new String[]{kee});
    }


    public void insertComplaint(Complaint complaint) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TBL_COMPLAINTS_COLUMN_ID, complaint.getComplaintId());
        contentValues.put(TBL_COMPLAINTS_COLUMN_DESC, complaint.getDescription());
        contentValues.put(TBL_COMPLAINTS_COLUMN_SUBMITTED, complaint.getTimeSubmitted());
        contentValues.put(TBL_COMPLAINTS_COLUMN_UPDATED, complaint.getTimeUpdated());
        contentValues.put(TBL_COMPLAINTS_COLUMN_STATUS, complaint.getStatus());
        db.insert(TBL_COMPLAINTS_TBL_NAME, null, contentValues);
    }

    public Complaint readComplaint(String complaintID) {
        Complaint complaint = null;
        Cursor cursor = db.query(TBL_COMPLAINTS_TBL_NAME,
                null,
                TBL_COMPLAINTS_COLUMN_ID + " = ? ",
                new String[]{complaintID},
                null,
                null,
                null);
        if (cursor.moveToNext()) {
            complaint = new Complaint();
            complaint.setComplaintId(complaintID);
            complaint.setDescription(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_DESC)));
            complaint.setStatus(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_STATUS)));
            complaint.setTimeSubmitted(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_SUBMITTED)));
            complaint.setTimeUpdated(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_UPDATED)));
        }
        cursor.close();
        return complaint;
    }

    public List<Complaint> readAllComplaints() {
        List<Complaint> listOfComplaints = new ArrayList<>();
        Cursor cursor = db.query(TBL_COMPLAINTS_TBL_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            Complaint complaint = new Complaint();
            complaint.setComplaintId(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_ID)));
            complaint.setDescription(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_DESC)));
            complaint.setStatus(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_STATUS)));
            complaint.setTimeSubmitted(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_SUBMITTED)));
            complaint.setTimeUpdated(cursor.getString(cursor.getColumnIndex(TBL_COMPLAINTS_COLUMN_UPDATED)));
            listOfComplaints.add(complaint);
        }
        cursor.close();
        return listOfComplaints;
    }

    public int updateComplaint(Complaint complaint) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TBL_COMPLAINTS_COLUMN_ID, complaint.getComplaintId());
        contentValues.put(TBL_COMPLAINTS_COLUMN_DESC, complaint.getDescription());
        contentValues.put(TBL_COMPLAINTS_COLUMN_STATUS, complaint.getStatus());
        contentValues.put(TBL_COMPLAINTS_COLUMN_SUBMITTED, complaint.getTimeSubmitted());
        contentValues.put(TBL_COMPLAINTS_COLUMN_UPDATED, complaint.getTimeUpdated());
        int retVal = db.update(TBL_COMPLAINTS_TBL_NAME, contentValues, TBL_COMPLAINTS_COLUMN_ID + " = ? ", new String[]{complaint.getComplaintId()});
        return retVal;
    }

    public int deleteComplaint(Complaint complaint) {
        int retVal = db.delete(TBL_COMPLAINTS_TBL_NAME, TBL_USER_COLUMN_KEY + " = ? ", new String[]{complaint.getComplaintId()});
        return retVal;
    }

    public void deleteAllComplaints() {
        db.delete(TBL_COMPLAINTS_TBL_NAME, null, null);
    }


    /**
     * When the user logs out, this method deletes all related local data.
     */
    public void deleteDataOnLogOut() {
        db.delete(TBL_USER_TBL_NAME, null, null);
        db.delete(TBL_COMPLAINTS_TBL_NAME, null, null);
    }
}
