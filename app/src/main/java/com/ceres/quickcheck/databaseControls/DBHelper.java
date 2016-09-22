package com.ceres.quickcheck.databaseControls;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 建立資料庫
 * Created by apple on 2016/3/9.
 */
public class DBHelper extends SQLiteOpenHelper{

    public final static String DB_NAME = "quickcheck.db";
    private final static int DB_VERSION = 1;
    private static SQLiteDatabase database;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context){
        if(database == null || database.isOpen()){
            database = new DBHelper(context, DB_NAME, null, DB_VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBController.CREATE_ACCOUNT_TABLE);
        db.execSQL(DBController.CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBController.ACCOUNT_TABLE); //TODO 應該設為針對table做更新
        db.execSQL("DROP TABLE IF EXISTS " + DBController.CATEGORY_TABLE);
        onCreate(db);
    }
}
