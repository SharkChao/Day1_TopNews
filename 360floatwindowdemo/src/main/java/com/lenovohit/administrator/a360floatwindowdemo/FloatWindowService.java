package com.lenovohit.administrator.a360floatwindowdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class FloatWindowService extends Service{
    private Timer mTimer;
    private Handler mHandler = new Handler();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimer == null){
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new RefreshTask(),0,500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }
    class  RefreshTask extends TimerTask{

        @Override
        public void run() {
            //如果在桌面，悬浮窗显示，则让其更新
            if (CommonUtil.isHome(getApplicationContext()) && MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
            //如果在桌面，悬浮框隐藏，则让其显示
            if (CommonUtil.isHome(getApplicationContext()) && !MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext());
                    }
                });
            }
            //如果不在桌面，悬浮框显示，则让其隐藏
            if (!CommonUtil.isHome(getApplicationContext()) && MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.removeSmallWindow(getApplicationContext());
                        MyWindowManager.removeBigWindow(getApplicationContext());
                    }
                });
            }
            //如果不在桌面，悬浮框不显示，不做任何操作
        }
    }
}
