package com.lenovohit.administrator.a360floatwindowdemo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class FloatWindowBiggerView extends LinearLayout{

    public  static  int mViewWidth;
    public  static  int mViewHeight;

    public FloatWindowBiggerView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_bigger,this);
         View view = findViewById(R.id.float_bigger_layout);
        mViewWidth = view.getLayoutParams().width;
        mViewHeight = view.getLayoutParams().height;
        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        Button btnBack = (Button) view.findViewById(R.id.btnBack);
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //销毁大小悬浮框，清除掉service
                MyWindowManager.removeSmallWindow(context);
                MyWindowManager.removeBigWindow(context);
                context.stopService(new Intent(context,FloatWindowService.class));
            }
        });
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击返回的时候，关闭大悬浮框，创建小的悬浮框
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }
}
