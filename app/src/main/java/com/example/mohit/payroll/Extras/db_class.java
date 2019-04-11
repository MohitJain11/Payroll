package com.example.mohit.payroll.Extras;

import android.database.sqlite.SQLiteDatabase;

public class db_class {   static SQLiteDatabase sqLiteDatabase;
    public static String DATABASE_NAME="USERINFO.DB";
    public static void SQLiteDataBaseBuild(){

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME,null);
    }
}

