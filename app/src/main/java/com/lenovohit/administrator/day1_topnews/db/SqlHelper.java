package com.lenovohit.administrator.day1_topnews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SharkChao on 2017-08-15.
 * 数据库辅助类
 */

public class SqlHelper extends SQLiteOpenHelper{

    public  static final String db_name = "database.db";//数据库名称
    public  static final int db_version = 1;//数据库版本
    public static final String TABLE_NAME = "channel";
    public static final String ID = "id";//行id
    public static final String NAME = "name";//行名称
    public  static final String  ORDERID = "orderId";//排序id
    public static final String SELECTED = "selected";//是否被选中

    private Context mContext;

    public SqlHelper(Context context) {
        super(context, db_name, null, db_version);
        this.mContext = context;
    }

    public Context getContext(){
        return mContext;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID + " INTEGER , " +
                NAME + " TEXT , " +
                ORDERID + " INTEGER , " +
                SELECTED + " SELECTED)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
