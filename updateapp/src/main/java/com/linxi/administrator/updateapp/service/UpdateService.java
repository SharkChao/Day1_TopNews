package com.linxi.administrator.updateapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.linxi.administrator.updateapp.R;
import com.linxi.administrator.updateapp.activity.MainActivity;
import com.linxi.administrator.updateapp.utils.Global;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SharkChao on 2017-09-23.
 */

public class UpdateService extends Service{

    private static final  int DOWNLOAD_COMPLETE = 1;
    private static final  int DOWNLOAD_FAIL = 2;
    private File updateDir;
    private File updateFile;
    private NotificationManager mManager;
    private Notification mNotification;
    private Notification.Builder mBuilder;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD_COMPLETE:
                    Uri uri = Uri.fromFile(updateFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(UpdateService.this,0,intent,0);
                    mNotification.defaults = Notification.DEFAULT_SOUND;//铃声提醒
                    mBuilder.setContentText("下载完成，点击安装");
                    mBuilder.setContentIntent(pendingIntent);
                    mManager.notify(1,mBuilder.build());
                    break;
                case DOWNLOAD_FAIL:
                    Intent intent1 = new Intent(UpdateService.this,MainActivity.class);
                    PendingIntent pendingIntent1 = PendingIntent.getActivity(UpdateService.this,0,intent1,0);
                    mNotification.defaults = Notification.DEFAULT_SOUND;//铃声提醒
                    mBuilder.setContentText("下载失败，点击返回主页面");
                    mBuilder.setContentIntent(pendingIntent1);
                    mManager.notify(1,mBuilder.build());
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())){
            updateDir = new File(Environment.getExternalStorageDirectory(), Global.downloadDir);
            updateFile = new File(updateDir.getPath(),"news.apk");
        }
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("提示标题")
                .setContentText("正在下载，请稍等")
                .setContentIntent(setPendIntent())
                .setProgress(100,0,false)
                .setAutoCancel(true);
        mNotification = mBuilder.build();
        //通知状态栏
        mManager.notify(1,mNotification);

        new Thread(new downloadRunnable()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 设置远程的intent
     * @return
     */
    public PendingIntent setPendIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * 新开一个线程去下载apk
     */
    public class downloadRunnable implements Runnable{
        Message mMessage =mHandler.obtainMessage();
        @Override
        public void run() {

            if(!updateDir.exists()){
                updateDir.mkdirs();
            }
            if (!updateFile.exists()){
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                long downloadUpdateFile = downloadUpdateFile("http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk", updateFile);
                if (downloadUpdateFile > 0){
                    mMessage.what = DOWNLOAD_COMPLETE;
                    mHandler.sendMessage(mMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mMessage.what = DOWNLOAD_FAIL;
                mHandler.sendMessage(mMessage);
            }
        }
    }
    public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        //这样的下载代码很多，我就不做过多的说明
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if(currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while((readsize = is.read(buffer)) > 0){
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                //为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if((downloadCount == 0)||(int) (totalSize*100/updateTotalSize)-10>downloadCount){
                    downloadCount += 10;
                   mBuilder.setProgress(100,downloadCount,false);
                   mManager.notify(1,mBuilder.build());
                }
            }
        } finally {
            if(httpConnection != null) {
                httpConnection.disconnect();
            }
            if(is != null) {
                is.close();
            }
            if(fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }
}
