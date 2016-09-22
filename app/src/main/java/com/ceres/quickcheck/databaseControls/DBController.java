package com.ceres.quickcheck.databaseControls;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.ceres.quickcheck.Units.Item;
import com.ceres.quickcheck.Units.CategoryItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 資料庫ＣＲＵＤ步驟
 * Created by apple on 2016/3/9.
 */
public class DBController {

    //總帳
    public static final String ACCOUNT_TABLE = "account_table";
    //母類別,子類別放在裡面當個欄位
    public static final String CATEGORY_TABLE = "category_table";

    //總帳欄位
    public static final String KEY_ID = "_id";
    public static final String MONEY_COLUMN = "money";
    public static final String DATE_COLUMN = "datetime";
    public static final String CATEGORY_COLUMN = "category";//分類
    public static final String SUB_CATEGORY_COLUMN = "subcategory";//子分類
    public static final String RECEIPT_COLUMN = "receipt"; //發票
    public static final String NOTE_COLUMN = "note"; //備註
    public static final String USER_COLUMN = "user";//帳戶
    public static final String PLACE_COLUMN = "place"; //位置
    public static final String ICON_COLOR = "iconcolor"; //icon顏色
    public static final String ICON_STYLE = "iconstyle"; //icon圖示
    /**0為支出，1為收入**/
    public static final String IS_INCOME = "isincome"; //是否為收入

    private Context context;

    //建立總帳資料庫
    public static final String CREATE_ACCOUNT_TABLE =
            "CREATE TABLE " + ACCOUNT_TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MONEY_COLUMN + " FLOAT NOT NULL, " +
            DATE_COLUMN + " DATETIME NOT NULL, " +
            CATEGORY_COLUMN + " TEXT NOT NULL, " +
            SUB_CATEGORY_COLUMN + " TEXT, " +
            RECEIPT_COLUMN + " TEXT, " +
            NOTE_COLUMN + " TEXT, " +
            USER_COLUMN + " TEXT, " +
            PLACE_COLUMN + " TEXT, " +
            IS_INCOME + " INTEGER)";

    //建立類別資料庫
    public static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CATEGORY_TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY_COLUMN + " TEXT NOT NULL, " +
            SUB_CATEGORY_COLUMN + " TEXT, "+
            ICON_STYLE + " INTEGER, " +
            ICON_COLOR + " TEXT, " +
            IS_INCOME + " INTEGER)";

    private SQLiteDatabase db;

    //建構子
    public DBController(Context context){

        db = DBHelper.getDatabase(context);
        this.context = context;
    }

    public void close(){
        db.close();
    }

    //紀錄每筆帳務資料
    public Item insertRecord(Item item){

        ContentValues cv = new ContentValues();

        cv.put(MONEY_COLUMN, item.money);
        cv.put(DATE_COLUMN, item.datetime);
        cv.put(CATEGORY_COLUMN, item.category);
        cv.put(SUB_CATEGORY_COLUMN, item.sub_category);
        cv.put(RECEIPT_COLUMN, item.receipt);
        cv.put(NOTE_COLUMN, item.note);
        cv.put(USER_COLUMN, item.user);
        cv.put(PLACE_COLUMN, item.place);
        cv.put(IS_INCOME, item.isIncome);

        long id = db.insert(ACCOUNT_TABLE, null, cv);
        //設定編號
        item.id = id;

        return  item;
    }

    //記錄類別種類
    public CategoryItem insertCategory(CategoryItem item){
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN, item.parentItem);
        String child_save_format = "";

        for(String sub: item.childItem){
            child_save_format += sub + "," ;
        }
        //去掉最後一個“,”
        child_save_format = child_save_format.substring( 0, child_save_format.length()-1 );

        cv.put(SUB_CATEGORY_COLUMN,child_save_format);
        cv.put(ICON_STYLE,item.iconStyle);
        cv.put(ICON_COLOR,item.iconColor);
        cv.put(IS_INCOME, item.isIncome);

        long id = db.insert(CATEGORY_TABLE, null, cv);
        item.id = id;

        return item;
    }

    //修改指定帳務資料
    public boolean updateRecord(Item item){
        ContentValues cv = new ContentValues();

        cv.put(MONEY_COLUMN, item.money);
        cv.put(DATE_COLUMN, item.datetime);
        cv.put(CATEGORY_COLUMN, item.category);
        cv.put(SUB_CATEGORY_COLUMN, item.sub_category);
        cv.put(RECEIPT_COLUMN, item.receipt);
        cv.put(NOTE_COLUMN, item.note);
        cv.put(USER_COLUMN, item.user);
        cv.put(PLACE_COLUMN, item.place);
        cv.put(IS_INCOME, item.isIncome);

        String where = KEY_ID + "=" + item.id;

        return db.update(ACCOUNT_TABLE, cv, where, null)> 0 ;
    }

    //修改指定類別資料
    public boolean updateCategory(CategoryItem item){
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN, item.parentItem);

        String child_save_format = "";

        for(String sub: item.childItem){
            child_save_format += sub + "," ;
        }
        //去掉最後一個“,”
        child_save_format = child_save_format.substring( 0, child_save_format.length()-1 );

        cv.put(SUB_CATEGORY_COLUMN,child_save_format);

        String where = KEY_ID + "=" + item.id;

        return db.update(CATEGORY_TABLE, cv, where, null)> 0 ;
    }

    //刪除指定帳務資料
    public boolean deleteRecord(long id){
        String where = KEY_ID + "=" + id;

        return db.delete(ACCOUNT_TABLE, where, null) > 0;
    }

    //刪除指定類別資料
    public boolean deleteCategory(long id){
        String where = KEY_ID + "=" + id;

        return db.delete(CATEGORY_TABLE, where, null) > 0;
    }

    /** 當天花費或是收入結算
     *  @param kind 0:全部,1:收入,2:支出**/
    public float showTodayMoney(String date, int kind){
        float sum = 0;

        Cursor cursor =
                db.rawQuery("SELECT SUM( " + MONEY_COLUMN +" ) FROM " + ACCOUNT_TABLE + " WHERE strftime('%Y-%m-%d', "+ DATE_COLUMN +") = '"
                        + date + "' AND " + IS_INCOME + " = 0" ,null);
        float cost_sum = 0;
        if(cursor != null){
            if(cursor.moveToFirst()){
                cost_sum = cursor.getFloat(0);
            }
        }

        Cursor cursor1 =
                db.rawQuery("SELECT SUM( " + MONEY_COLUMN +" ) FROM " + ACCOUNT_TABLE + " WHERE strftime('%Y-%m-%d', "+ DATE_COLUMN +") = '"
                        + date + "' AND " + IS_INCOME + " = 1" ,null);

        float income_sum = 0;
        if(cursor1 != null){
            if(cursor1.moveToFirst()){
                income_sum = cursor1.getFloat(0);
            }
        }

        switch (kind){
            case 0:
                sum = income_sum - cost_sum;
                break;
            case 1:
                sum = income_sum;
                break;
            case 2:
                sum = cost_sum;
                break;
        }


        cursor.close();
        cursor1.close();
        return sum;

    }

    /**當月開銷**/
    public int showMonthMoney(String month){
        int sum = 0;

        Cursor cursor =
                db.rawQuery("SELECT SUM( " + MONEY_COLUMN +" ) FROM " + ACCOUNT_TABLE + " WHERE strftime('%Y-%m', "+ DATE_COLUMN +") = '"
                        + month + "' AND " + IS_INCOME + " = 0" ,null);

        int cost_sum = 0;
        if(cursor != null){
            if(cursor.moveToFirst()){
                cost_sum = cursor.getInt(0);
            }
        }

        Cursor cursor1 =
                db.rawQuery("SELECT SUM( " + MONEY_COLUMN +" ) FROM " + ACCOUNT_TABLE + " WHERE strftime('%Y-%m', "+ DATE_COLUMN +") = '"
                        + month + "' AND " + IS_INCOME + " = 1" ,null);

        int income_sum = 0;
        if(cursor1 != null){
            if(cursor1.moveToFirst()){
                income_sum = cursor1.getInt(0);
            }
        }

        sum = income_sum - cost_sum;
        cursor.close();
        cursor1.close();
        return sum;
    }


    /**在這個月之前有幾個月份**/
    public int countMonths(String currentMonth){
        Cursor cursor =
                db.rawQuery("SELECT COUNT( DISTINCT strftime('%Y-%m', "+ DATE_COLUMN  +" )) FROM " + ACCOUNT_TABLE + " WHERE strftime('%Y-%m', "+ DATE_COLUMN +") <= '"
                        + currentMonth + "'" ,null);

        int count = 0;
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }

        cursor.close();
        return count;
    }

    /**取得指定時間內的類別**/
    public ArrayList<String> getCategoryInPeriod(String startTime, String endTime, boolean isIncome){
        ArrayList<String> category = new ArrayList<>();
        int income = 0;
        if(isIncome){
            income = 1;
        }else {
            income = 0;
        }

        Cursor cursor = db.rawQuery("SELECT DISTINCT "+ CATEGORY_COLUMN +" FROM "+ ACCOUNT_TABLE +
                " WHERE " + DATE_COLUMN + " > '" + startTime + " 00:00:00' AND " +
                DATE_COLUMN + " < '"+ endTime + " 23:59:99' AND " + IS_INCOME + " = " + income ,null);

        while(cursor.moveToNext()){
            category.add(cursor.getString(0));
        }

        return category;
    }


    //取出單筆紀錄
    private Item getRecord(Cursor cursor){
        Item result = new Item();

        result.id = cursor.getLong(0);
        result.money = cursor.getFloat(1);
        result.datetime = cursor.getString(2);
        result.category = cursor.getString(3);
        result.sub_category = cursor.getString(4);
        result.receipt = cursor.getString(5);
        result.note = cursor.getString(6);
        result.user = cursor.getString(7);
        result.place = cursor.getString(8);
        result.isIncome = cursor.getInt(9);

        return result;
    }

    //取出類別記錄
    private CategoryItem getCategory(Cursor cursor){
        CategoryItem result = new CategoryItem();

        result.id=cursor.getLong(0);
        result.parentItem = cursor.getString(1);

        String child_save_format= cursor.getString(2);
        String[] tmp_childItem = child_save_format.split(",");
        ArrayList<String> tmp_child = new ArrayList<String>();
        for(int i = 0; i < tmp_childItem.length ; i++){
            tmp_child.add(tmp_childItem[i]);
        }
        result.childItem = tmp_child;
        result.iconStyle = cursor.getInt(3);
        result.iconColor = cursor.getString(4);
        result.isIncome = cursor.getInt(5);

        return result;
    }

    /**搜尋指定時間內的某類別花費**/
    public int getSumofCategoryInPeriod(String category, String startTime,String endTime){
        int sum = 0;

        Cursor cursor = db.rawQuery("SELECT SUM( " + MONEY_COLUMN + " ) FROM "+ ACCOUNT_TABLE +
                " WHERE " + CATEGORY_COLUMN + " = '" + category +"' AND " +
                DATE_COLUMN +  " > '" + startTime + " 00:00:00' AND " +
                DATE_COLUMN + " < '"+ endTime + " 23:59:99'",null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                sum = cursor.getInt(0);
            }
        }
        return sum;
    }

    //搜尋特定條件下的帳務資料
    public List<Item> queryRecord(String type, String condition){
        List<Item> records = new ArrayList<>();

        String where = type + "=" + condition;

        Cursor result = db.query(ACCOUNT_TABLE, null, where, null, null, null, DATE_COLUMN , null);

        while(result.moveToNext()){
            records.add(getRecord(result));
        }

        result.close();

        return  records;
    }

    //搜尋特定條件下的類別
    public CategoryItem queryCategory(String type, String condition){
        CategoryItem records;

        String where = type + "= '" + condition +"'";

        Cursor result = db.query(CATEGORY_TABLE, null, where, null, null, null, null, null);

        if(result == null){
            return null;
        }

        if(result.getCount() != 0){
            result.moveToFirst();
            records = getCategory(result);
        }else{
            return null;
        }

        result.close();
        return records;
    }


    public List<Item> getAllRecord(){
        List<Item> records = new ArrayList<>();
        Cursor result = db.query(ACCOUNT_TABLE, null, null, null, null, null, DATE_COLUMN +" DESC");

        while(result.moveToNext()){
            records.add(getRecord(result));
        }

        result.close();
        return records;

    }

    public List<CategoryItem> getAllCategory(){
        List<CategoryItem> records = new ArrayList<>();
        Cursor result = db.query(CATEGORY_TABLE, null, null, null, null, null, null);

        while (result.moveToNext()){
            records.add(getCategory(result));
        }

        result.close();
        return records;
    }

    /**
     * 匯出成csv檔
     * **/
    public void exportToCSV(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String current_db_path = context.getApplicationInfo().dataDir + "/databases/" + DBHelper.DB_NAME;
        String backup_db_Path = DBHelper.DB_NAME;

        File currentDB = new File(data, current_db_path);
        File backupDB = new File(sd, backup_db_Path);

        try{
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();

            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            System.out.print("export done!!!");

        }catch (IOException e){
            e.printStackTrace();
        }



    }




}
