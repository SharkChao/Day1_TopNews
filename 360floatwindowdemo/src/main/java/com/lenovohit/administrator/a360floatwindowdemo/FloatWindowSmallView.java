package com.lenovohit.administrator.a360floatwindowdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class FloatWindowSmallView extends LinearLayout {

    private Context context;
    private  WindowManager mWindowManager ;
    private WindowManager.LayoutParams mParams;
    public static  int mViewWidth;
    public static  int mViewHeight;
    private float mXInView;
    private float mYInView;
    private float mXInScreen;
    private float mYInScreen;
    private float mXInScreenDown;
    private float mYInScrrenDown;

    public FloatWindowSmallView(Context context) {
        super(context);
        this.context = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small,this);
        View view = findViewById(R.id.small_window_layout);
        mViewWidth = view.getLayoutParams().width;
        mViewHeight = view.getLayoutParams().height;
        TextView tvPercent = (TextView) findViewById(R.id.tvSmallPercent);
        tvPercent.setText(MyWindowManager.getUsedPercentValue(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //手指按下
                mXInView = event.getX();
                mYInView = event.getY();
                mXInScreenDown = event.getRawX();
                mYInScrrenDown = event.getRawY()-CommonUtil.getStatusBarHeight(context);
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY()-CommonUtil.getStatusBarHeight(context);
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY();
                updateFloatViewSmall();
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起
                if (Math.abs(mXInScreenDown -mXInScreen)<20 && Math.abs(mYInScrrenDown - mYInScreen)<20){
                    openBigFloatWindow();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }
    /**
     * 打开大的悬浮窗
     */
    private void openBigFloatWindow() {
        MyWindowManager.createBigWindow(context);
        MyWindowManager.removeSmallWindow(context);
    }

    /**
     * 更新小的悬浮窗的内容
     */
    private void updateFloatViewSmall() {
      mParams.x =(int) (mXInScreen - mXInView);
        mParams.y = (int)(mYInScreen-mYInView);
        mWindowManager.updateViewLayout(this,mParams);

    }
}
