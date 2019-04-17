package com.kspay.shrusthi.app.Extras;

import android.database.sqlite.SQLiteDatabase;

public class db_class {   static SQLiteDatabase sqLiteDatabase;
    public static String DATABASE_NAME="USERINFO.DB";
    public static void SQLiteDataBaseBuild(){

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME,null);
    }
}

