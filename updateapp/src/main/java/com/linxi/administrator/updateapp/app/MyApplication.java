package com.linxi.administrator.updateapp.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.linxi.administrator.updateapp.utils.Global;

/**
 * Created by SharkChao on 2017-09-23.
 */

public class MyApplication extends Application{
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initGlobal();
    }
    public static Context getInstance(){
        return mContext;
    }

    /**
     * 初始化本地版本和服务器版本
     */
    public void initGlobal(){
        try {
            Global.localVersion = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
            Global.serverVersion = 2;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
