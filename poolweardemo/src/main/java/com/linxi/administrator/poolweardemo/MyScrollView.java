package com.linxi.administrator.poolweardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SharkChao on 2017-09-02.
 */

public class MyScrollView extends ScrollView implements View.OnTouchListener{

    //当前加载到哪一个页面
    private int page;
    //每个页面需要加载多少张图片
    private static int PAGE_SIZE = 15;
    private final ImageLoader mImageLoader;
    private static HashSet<LoadImageTask>mHashSet;
    //MyScrollView的高度
    private static int mScrollViewHeight;
    //布局控件
    private static View ScrollLayout;
    //第一列的宽度
    private int columnWidth;
    //判断OnLayout是否加载过
    private boolean isLoad;
     //当前第一列的高度
    private int firstColumnHeight;
     //当前第二列的高度
    private int secondColumnHeight;
     //当前第三列的高度
    private int thirdColumnHeight;
     //第一列的布局
    private LinearLayout firstColumn;
     //第二列的布局
    private LinearLayout secondColumn;
     //第三列的布局
    private LinearLayout thirdColumn;

    private static int lastScrollY = -1;
    private List<ImageView> imageViewList = new ArrayList<ImageView>();

    private static Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            MyScrollView myScrollView = (MyScrollView) msg.obj;
            int scrollY = myScrollView.getScrollY();
            // 如果当前的滚动位置和上次相同，表示已停止滚动
            if (scrollY == lastScrollY) {
                // 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
                if (mScrollViewHeight + scrollY >=ScrollLayout.getHeight()
                        && mHashSet.isEmpty()) {
                    myScrollView.LoadMoreImages();
                }
                myScrollView.checkVisibility();
            } else {
                lastScrollY = scrollY;
                Message message = new Message();
                message.obj = myScrollView;
                // 5毫秒后再次对滚动位置进行判断
                handler.sendMessageDelayed(message, 5);
            }
        };

    };

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mImageLoader = ImageLoader.getInstance();
        //初始化HashSet 对所有图片的下载任务进行保存
        mHashSet = new HashSet<>();
        setOnTouchListener(this);
    }

    //进行一些关键的初始化的操作，1获取MyScrollView的高度值，2加载第一列的宽度值，3在这里加载第一页的图片
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !isLoad){
            mScrollViewHeight = getHeight();
            //首先获取此控件的直接子view
            ScrollLayout = getChildAt(0);
            firstColumn = findViewById(R.id.first_column);
            secondColumn = findViewById(R.id.second_column);
            thirdColumn = findViewById(R.id.third_column);
            columnWidth = firstColumn.getWidth();
            //开始加载更多图片
            isLoad = true;
            LoadMoreImages();
        }
    }

    //加载下一页的图片，每张图片都会开启一个线程去下载
    private void LoadMoreImages() {
        if (hasSDCard()){
            //如果有sd卡的话，才可以进行后续操作。
            //应该都了解这个是什么意思吧 [0,15)[15,30)
            int startIndex = page*PAGE_SIZE;
            int endIndex = page*PAGE_SIZE+PAGE_SIZE;
            if (startIndex < Images.imageUrls.length){
                Toast.makeText(getContext(), "正在加载中，请稍等。。。。", Toast.LENGTH_SHORT).show();
                if (endIndex > Images.imageUrls.length){
                    endIndex = Images.imageUrls.length;
                }
                //此方法会执行一定数量的图片加载异步线程
                for (int i = startIndex; i < endIndex; i++){
                    LoadImageTask loadImageTask = new LoadImageTask();
                    mHashSet.add(loadImageTask);
                    loadImageTask.execute(Images.imageUrls[i]);
                }
                //页面数加1
                page++;
            }else {
                Toast.makeText(getContext(), "已没有更多图片可以加载。。。。", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), "抱歉，请先插入SD卡，再进行相关操作", Toast.LENGTH_SHORT).show();
        }
    }

    //对触摸事件进行处理
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
    public void checkVisibility() {
        for (int i = 0; i < imageViewList.size(); i++) {
            ImageView imageView = imageViewList.get(i);
            int borderTop = (Integer) imageView.getTag(R.string.border_top);
            int borderBottom = (Integer) imageView
                    .getTag(R.string.border_bottom);
            if (borderBottom > getScrollY()
                    && borderTop < getScrollY() + mScrollViewHeight) {
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = mImageLoader.getBitmapFromMemory(imageUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    LoadImageTask task = new LoadImageTask(imageView);
                    task.execute(imageUrl);
                }
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
    //异步加载图片工具类
    class  LoadImageTask extends AsyncTask<String ,Void,Bitmap>{
        private ImageView mImageView;
        private String mImageUrl;

        public LoadImageTask(){

        }
        public LoadImageTask(ImageView imageView){
            mImageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            mImageUrl = strings[0];
            Bitmap bitmap = mImageLoader.getBitmapFromMemory(mImageUrl);
            if (bitmap == null){
                bitmap = loadImage(mImageUrl);
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                int scaledHeight = (int) (bitmap.getHeight() / ratio);
                addImage(bitmap, columnWidth, scaledHeight);
            }
            mHashSet.remove(this);
        }
        //加载图片从网络
        private Bitmap loadImage(String imageUrl) {
            File imageFile = new File(getImagePath(imageUrl));
            if (!imageFile.exists()) {
                downloadImage(imageUrl);
            }
            if (imageUrl != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), columnWidth);
                if (bitmap != null) {
                    mImageLoader.addBitmapToMemory(imageUrl, bitmap);
                    return bitmap;
                }
            }
            return null;
        }
        //获取图片的内存卡地址
        private String getImagePath(String imageUrl) {
            int lastSlashIndex = imageUrl.lastIndexOf("/");
            String imageName = imageUrl.substring(lastSlashIndex + 1);
            String imageDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/PhotoWallFalls/";
            File file = new File(imageDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String imagePath = imageDir + imageName;
            return imagePath;
        }
        private void downloadImage(String imageUrl) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagePath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), columnWidth);
                if (bitmap != null) {
                    mImageLoader.addBitmapToMemory(imageUrl, bitmap);
                }
            }
        }
        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    imageWidth, imageHeight);
            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setTag(R.string.image_url, mImageUrl);
                findColumnToAdd(imageView, imageHeight).addView(imageView);
                imageViewList.add(imageView);
            }
        }
        private LinearLayout findColumnToAdd(ImageView imageView,
                                             int imageHeight) {
            if (firstColumnHeight <= secondColumnHeight) {
                if (firstColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(R.string.border_top, firstColumnHeight);
                    firstColumnHeight += imageHeight;
                    imageView.setTag(R.string.border_bottom, firstColumnHeight);
                    return firstColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            } else {
                if (secondColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(R.string.border_top, secondColumnHeight);
                    secondColumnHeight += imageHeight;
                    imageView
                            .setTag(R.string.border_bottom, secondColumnHeight);
                    return secondColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            }
        }
    }
    //判断是否有sdCard，如果有返回true 否则返回false
    private boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

}
