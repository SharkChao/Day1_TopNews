package com.lenovohit.administrator.day1_topnews.dao;

import android.content.ContentValues;

import com.lenovohit.administrator.day1_topnews.bean.ChannelItem;

import java.util.List;
import java.util.Map;

/**
 * Created by SharkChao on 2017-08-15.
 */

public interface ChannelDaoInterface {
    //添加一条栏目
     boolean addCache(ChannelItem item);
    //删除一条栏目
     boolean deleteCache(String whereClause, String[] whereArgs);
    //修改一条栏目
     boolean updateCache(ContentValues values, String whereClause, String[] whereArgs);

     Map<String, String> viewCache(String selection, String[] selectionArgs);

     List<Map<String, String>> listCache(String selection, String[] selectionArgs);

     void clearFeedTable();
}
