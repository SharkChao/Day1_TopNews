package com.lenovohit.administrator.day1_topnews.bean;

import android.database.SQLException;
import android.util.Log;

import com.lenovohit.administrator.day1_topnews.dao.ChannelDao;
import com.lenovohit.administrator.day1_topnews.db.SqlHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelManage {
	public static ChannelManage channelManage;
	//默认的用户频道
	public static List<ChannelItem> defaultUserChannels = new ArrayList<>();
	//默认的其他频道
	public static List<ChannelItem> defaultOtherChannels = new ArrayList<>();
	private ChannelDao channelDao;
	/** 判断数据库中是否存在用户数据 */
	private boolean userExist = false;
	static {
		defaultUserChannels.add(new ChannelItem("1", "推荐", "1", "1"));
		defaultUserChannels.add(new ChannelItem("2", "热点", "2", "2"));
		defaultUserChannels.add(new ChannelItem("3", "娱乐", "3", "3"));
		defaultUserChannels.add(new ChannelItem("4", "时尚", "4", "4"));
		defaultUserChannels.add(new ChannelItem("5", "科技", "5", "5"));
		defaultUserChannels.add(new ChannelItem("6", "体育", "6", "6"));
		defaultUserChannels.add(new ChannelItem("7", "军事", "7", "7"));

		defaultOtherChannels.add(new ChannelItem("8", "财经", "8", "8"));
		defaultOtherChannels.add(new ChannelItem("9", "汽车", "9", "9"));
		defaultOtherChannels.add(new ChannelItem("10", "房产", "10", "10"));
		defaultOtherChannels.add(new ChannelItem("11", "社会", "11", "11"));
		defaultOtherChannels.add(new ChannelItem("12", "情感", "12", "12"));
		defaultOtherChannels.add(new ChannelItem("13", "女人", "13", "13"));
		defaultOtherChannels.add(new ChannelItem("14", "旅游", "14", "14"));
		defaultOtherChannels.add(new ChannelItem("15", "健康", "15", "15"));
		defaultOtherChannels.add(new ChannelItem("16", "美女", "16", "16"));
		defaultOtherChannels.add(new ChannelItem("17", "游戏", "17", "17"));
		defaultOtherChannels.add(new ChannelItem("18", "数码", "18", "18"));
	}
	private ChannelManage(SqlHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());
		// NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
		return;
	}

	/**
	 * 初始化频道管理类
	 * @throws SQLException
	 */
	public static ChannelManage getManage(SqlHelper dbHelper)throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	public List<ChannelItem> getUserChannel() {
		Object cacheList = channelDao.listCache(SqlHelper.SELECTED + "= ?",new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(maplist.get(i).get(SqlHelper.ID));
				navigate.setName(maplist.get(i).get(SqlHelper.NAME));
				navigate.setOrderId(maplist.get(i).get(SqlHelper.ORDERID));
				navigate.setSelected(maplist.get(i).get(SqlHelper.SELECTED));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}

	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache(SqlHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()){
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate= new ChannelItem();
				navigate.setId(maplist.get(i).get(SqlHelper.ID));
				navigate.setName(maplist.get(i).get(SqlHelper.NAME));
				navigate.setOrderId(maplist.get(i).get(SqlHelper.ORDERID));
				navigate.setSelected(maplist.get(i).get(SqlHelper.SELECTED));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<ChannelItem>) cacheList;
	}

	/**
	 * 保存用户频道到数据库
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i+"");
			channelItem.setSelected(1+"");
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 保存其他频道到数据库
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i+"");
			channelItem.setSelected(0+"");
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultChannel(){
		Log.d("deleteAll", "deleteAll");
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}
