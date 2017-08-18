package com.lenovohit.administrator.a360floatwindowdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class MyWindowManager {
    private static FloatWindowBiggerView sBiggerView;
    private static FloatWindowSmallView sSmallView;
    //小悬浮框的参数
    private static WindowManager.LayoutParams smallWindowParams;
    //大悬浮框的参数
    private static WindowManager.LayoutParams bigWindowParams;
    //用于在桌面上显示或者移除悬浮框
    private static WindowManager sWindowManager;
    /**
     * 用于获取手机可用内存
     */
    private static ActivityManager mActivityManager;
    //创建小悬浮框
    public static void createSmallWindow(Context context){
        WindowManager windowManager = getWindowManager(context);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        if (sSmallView == null){
            sSmallView = new FloatWindowSmallView(context);
            if (smallWindowParams == null){
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.mViewWidth;
                smallWindowParams.height = FloatWindowSmallView.mViewHeight;
                smallWindowParams.x = width;
                smallWindowParams.y = height / 2;
            }
            sSmallView.setParams(smallWindowParams);
            windowManager.addView(sSmallView,smallWindowParams);
        }
    }
    public static void removeSmallWindow(Context context) {
        if (sSmallView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(sSmallView);
            sSmallView = null;
        }
    }
    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (sBiggerView == null) {
            sBiggerView = new FloatWindowBiggerView(context);
            if (bigWindowParams == null) {
                bigWindowParams = new WindowManager.LayoutParams();
                bigWindowParams.x = screenWidth / 2 - FloatWindowBiggerView.mViewWidth / 2;
                bigWindowParams.y = screenHeight / 2 - FloatWindowBiggerView.mViewHeight / 2;
                bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                bigWindowParams.width = FloatWindowBiggerView.mViewWidth;
                bigWindowParams.height = FloatWindowBiggerView.mViewHeight;
            }
            windowManager.addView(sBiggerView, bigWindowParams);
        }
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeBigWindow(Context context) {
        if (sBiggerView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(sBiggerView);
            sBiggerView = null;
        }
    }
    //获取windowmanager
    public static WindowManager getWindowManager(Context context){
        if (sWindowManager == null){
             sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return sWindowManager;
    }
    /**
     * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
     *
     * @param context
     *            可传入应用程序上下文。
     */
    public static void updateUsedPercent(Context context) {
        if (sSmallView != null) {
            TextView percentView = (TextView) sSmallView.findViewById(R.id.tvSmallPercent);
            percentView.setText(getUsedPercentValue(context));
        }
    }
    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }
    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }
    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }
    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return sSmallView != null || sBiggerView != null;
    }
}
