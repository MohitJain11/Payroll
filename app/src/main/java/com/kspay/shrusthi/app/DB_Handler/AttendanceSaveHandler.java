package com.kspay.shrusthi.app.DB_Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kspay.shrusthi.app.Extras.db_class;
import com.kspay.shrusthi.app.models.AttendanceSaveModel;

public class AttendanceSaveHandler extends SQLiteOpenHelper {

    //Felid name
    public static final String employeeId = "employeeId";
    public static final String month = "month";
    public static final String year = "year";
    public static final String punchDate = "punchDate";
    public static final String punchTime = "punchTime";
    public static final String gpslocation = "gpslocation";
    public static final String punchStatus = "punchStatus";
    public static final String punchImage = "punchImage";
    public static final String isSync = "isSync";

    //Table Name
    public static final String TABLE_NAME = "AttendanceSave";

    private static final int DATABASE_VERSION = 8;

    //create query
    public static final String CREATE_QUERY = "CREATE TABLE if not exists " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            employeeId + " TEXT , " +
            month + " TEXT, " +
            year + " TEXT, " +
            punchTime + " TEXT, " +
            gpslocation + " TEXT, " +
            punchStatus + " TEXT, " +
            punchImage + " TEXT, " +
            isSync + " TEXT, " +
            punchDate + " TEXT );";

    private static final String Delete_QUERY = "DELETE FROM " + TABLE_NAME + "";

    public AttendanceSaveHandler(Context context) {
        super(context, db_class.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    //To insert values
    public void addinnformation(AttendanceSaveModel ad, SQLiteDatabase db) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(employeeId, ad.employeeId);
        contentValue.put(month, ad.month);
        contentValue.put(year, ad.year);
        contentValue.put(punchTime, ad.punchTime);
        contentValue.put(gpslocation, ad.gpslocation);
        contentValue.put(punchStatus, ad.punchStatus);
        contentValue.put(punchImage, ad.punchImage);
        contentValue.put(punchDate, ad.punchDate);
        contentValue.put(isSync, ad.isSync);
        db.insert(TABLE_NAME, null, contentValue);
    }

    public Cursor getinformation(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {employeeId,
                month,
                year,
                punchTime,
                gpslocation,
                punchStatus,
                punchImage,
                isSync,
                punchDate
        };
        cursor = db.query(TABLE_NAME, projections, null, null, null, null, null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    //delete
    public void deletequery(SQLiteDatabase db) {
        db.execSQL(Delete_QUERY);
    }

    public Cursor getAttendance(SQLiteDatabase db, String empId) {
        Cursor cursor = db.rawQuery("Select * FROM " + TABLE_NAME + " WHERE " + employeeId + " = '" + empId + "'", null);
        return cursor;
    }

    //
    public Cursor getNoSyncAttendance(SQLiteDatabase db, String empId) {
        Cursor cursor = db.rawQuery("Select * FROM " + TABLE_NAME + " WHERE " + isSync + " = " + "'N' AND " + employeeId + "='" + empId + "'", null);
        return cursor;
    }

    public void updateNoSyncAttendance(SQLiteDatabase db, String tempPunchDate, String tempPunchTime, String empId) {
        ContentValues args = new ContentValues();
        args.put(isSync, "Y");
        String condition = ""+employeeId+"= '"+ empId+"' AND "+punchTime+"='"+tempPunchTime+"'  AND "+punchDate+"='"+tempPunchDate+"'";
        int i = db.update(TABLE_NAME, args, condition, null);
//        Cursor cursor = db.rawQuery("Update " + TABLE_NAME + " set " + isSync + " = " + "'Y'" + " WHERE " + employeeId + " = '" + empId + "' AND " + punchTime + " = '" + tempPunchTime + "' AND " + punchDate + " = '" + tempPunchDate + "'", null);
//        return cursor;
        Log.i("Update return value",i+"");
    }
}
