package com.linxi.administrator.poolweardemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

/**
 * Created by SharkChao on 2017-09-02.
 * 图片管理类  方便对图片进行管理
 */

public class ImageLoader {
    private static LruCache<String,Bitmap> mMermoryCache;//LruCache图片缓存的核心类
    private static ImageLoader mImageLoader;//单例模式
    private final int mImageMemory;//获取图片应该使用的最大缓存

    //对核心的图片缓存类进行初始化操作
    private ImageLoader(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mImageMemory = maxMemory/8;
        mMermoryCache = new LruCache<String,Bitmap>(mImageMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    //单例模式，获取图片管理类的唯一实例
    public static ImageLoader getInstance(){
        if (mImageLoader == null){
            mImageLoader = new ImageLoader();
        }
        return mImageLoader;
    }
    //从缓存类获取一张图片
    public Bitmap getBitmapFromMemory(String key){
        return mMermoryCache.get(key);
    }
    //缓存一张图片到缓存类中
    public void addBitmapToMemory(String key,Bitmap bitmap){
        Bitmap bitmapFromMemory = getBitmapFromMemory(key);
        if (bitmapFromMemory == null){
            mMermoryCache.put(key,bitmap);
        }
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
}
