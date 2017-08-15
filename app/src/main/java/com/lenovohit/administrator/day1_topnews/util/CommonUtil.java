package com.lenovohit.administrator.day1_topnews.util;

import android.content.Context;

/**
 * Created by SharkChao on 2017-08-14.
 * 公用工具类
 */

public class CommonUtil {
    /**
     * dip转为 px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     *  px 转为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
