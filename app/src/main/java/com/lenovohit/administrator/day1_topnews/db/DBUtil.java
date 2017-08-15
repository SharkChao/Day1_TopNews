package com.lenovohit.administrator.day1_topnews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by SharkChao on 2017-08-15.
 * 封装一层
 */

public class DBUtil {
    private static DBUtil mDBUtil;
    private Context mContext;
    private SqlHelper mSQLHelp;
    private SQLiteDatabase mSQLiteDatabase;
    private DBUtil(Context context){
        this.mContext = context;
        mSQLHelp = new SqlHelper(context);
        mSQLiteDatabase = mSQLHelp.getWritableDatabase();
    }
    public static DBUtil getInstence(Context context){
        if (mDBUtil == null){
            synchronized (DBUtil.class){
                if (mDBUtil == null){
                    mDBUtil = new DBUtil(context);
                }
            }
        }
        return mDBUtil;
    }
    /**
     * 关闭数据库
     */
    public void close() {
        mSQLHelp.close();
        mSQLHelp = null;
        mSQLiteDatabase.close();
        mSQLiteDatabase = null;
        mDBUtil = null;
    }
    /**
     * 添加数据
     */
    public long insertData(ContentValues values) {
        long insert = mSQLiteDatabase.insert(SqlHelper.TABLE_NAME, null, values);
        return insert;
    }
    /**
     * 更新数据
     *
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public int updateData(ContentValues values, String whereClause,
                           String[] whereArgs) {
        int count = mSQLiteDatabase.update(SqlHelper.TABLE_NAME, values, whereClause,
                whereArgs);
        return count;
    }
    /**
     * 删除数据
     *
     * @param whereClause
     * @param whereArgs
     */
    public int deleteData(String whereClause, String[] whereArgs) {
        int count = mSQLiteDatabase.delete(SqlHelper.TABLE_NAME, whereClause, whereArgs);
        return count;
    }

    /**
     * 查询数据
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public Cursor selectData(String[] columns, String selection,
                             String[] selectionArgs, String groupBy, String having,
                             String orderBy) {
        Cursor cursor = mSQLiteDatabase.query(SqlHelper.TABLE_NAME,columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    public void delete(){
        //先将表数据清空
        String sql = "DELETE FROM " + SqlHelper.TABLE_NAME + ";";
        SQLiteDatabase db = mSQLHelp.getWritableDatabase();
        db.execSQL(sql);

        //把序列号设置成0
        String updateSql = "update sqlite_sequence set seq=0 where name='"
                + SqlHelper.TABLE_NAME + "'";
        db.execSQL(updateSql);
    }
}
