package com.lenovohit.administrator.day1_topnews.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lenovohit.administrator.day1_topnews.bean.ChannelItem;
import com.lenovohit.administrator.day1_topnews.db.DBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SharkChao on 2017-08-15.
 * dao层的目的 将数据库与实体对应起来
 */

public class ChannelDao implements ChannelDaoInterface{
    private DBUtil mDBUtil;
    public ChannelDao(Context context){
        mDBUtil = DBUtil.getInstence(context);
    }
    @Override
    public boolean addCache(ChannelItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",item.getId());
        contentValues.put("name",item.getName());
        contentValues.put("orderId",item.getOrderId());
        contentValues.put("selected",item.getSelected());
        long index = mDBUtil.insertData(contentValues);
        return index>0 ? true:false;
    }

    @Override
    public boolean deleteCache(String whereClause, String[] whereArgs) {
        int count = mDBUtil.deleteData(whereClause, whereArgs);
        return count>0 ? true:false;
    }

    @Override
    public boolean updateCache(ContentValues values, String whereClause, String[] whereArgs) {
        int count = mDBUtil.updateData(values, whereClause, whereArgs);
        return count>0 ? true:false;
    }

    //获取一行数据
    @Override
    public Map<String, String> viewCache(String selection, String[] selectionArgs) {
        Map<String,String>map = new HashMap<>();
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
           for (int i = 0;i < cursor.getColumnCount();i++){
               String columnName = cursor.getColumnName(i);
               String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
               if (columnValue == null){
                   columnValue = "";
               }
               map.put(columnName,columnValue);
           }
        return map;
    }

    @Override
    public List<Map<String, String>> listCache(String selection, String[] selectionArgs) {
        List<Map<String,String>>list = new ArrayList<>();
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()){
            Map<String,String>map = new HashMap<>();
            for (int i = 0;i < cursor.getColumnCount();i++){
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
                if (columnValue == null){
                    columnValue = "";
                }
                map.put(columnName,columnValue);
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public void clearFeedTable() {
        mDBUtil.delete();
    }
}
