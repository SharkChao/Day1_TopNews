package com.lenovohit.administrator.leftandrightdemo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by SharkChao on 2017-08-22.
 * 可以左右滑动的view
 */

public class SlideView extends RelativeLayout implements View.OnTouchListener{

    //屏幕宽度
    private final int mScreenWidth;
    //屏幕滑动灵敏度
    private final int mTouchSlop;
    //左侧布局
    private View mLeftView;
    //右侧布局
    private View mRightView;
    private View mContentView;
    private MarginLayoutParams mLeftParams;
    private MarginLayoutParams mRightParams;
    private RelativeLayout.LayoutParams mContentParams;
    private VelocityTracker mVelocityTracker;
     //滑动状态的一种，表示未进行任何滑动。
    public static final int DO_NOTHING = 0;
     // 滑动状态的一种，表示正在滑出左侧菜单。
    public static final int SHOW_LEFT_MENU = 1;
     //滑动状态的一种，表示正在滑出右侧菜单。
    public static final int SHOW_RIGHT_MENU = 2;
     //滑动状态的一种，表示正在隐藏左侧菜单。
    public static final int HIDE_LEFT_MENU = 3;
     //滑动状态的一种，表示正在隐藏右侧菜单。
    public static final int HIDE_RIGHT_MENU = 4;
    //当前滑动状态
    private int currentState = -1;
     // 左侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
    private boolean isLeftMenuVisible;
     // 右侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
    private boolean isRightMenuVisible;
     //滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
    public static final int SNAP_VELOCITY = 200;
    //是否正在滑动。
    private boolean isSliding;
    private float mXDown;
    private float mYDown;
    private float mXMove;
    private float mYMove;
    private float mXUp;
     // 用于监听滑动事件的View。
    private View mBindView;
    private Context mContext;

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    /**
     * 绑定监听滑动事件的View。
     *
     * @param bindView
     *            需要绑定的View对象。
     */
    public void setScrollEvent(View bindView) {
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            mLeftView = getChildAt(0);
            mRightView = getChildAt(1);
            mContentView = getChildAt(2);
            mLeftParams = (MarginLayoutParams) mLeftView.getLayoutParams();
            mRightParams = (MarginLayoutParams) mRightView.getLayoutParams();
            mContentParams = (RelativeLayout.LayoutParams) mContentView.getLayoutParams();
            //需要设置内容布局占满证整个屏幕
            mContentParams.width = mScreenWidth;
            mContentView.setLayoutParams(mContentParams);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getRawX();
                mYDown = event.getRawY();
                currentState = DO_NOTHING;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                mYMove = event.getRawY();
                int distanceX = (int) (mXMove - mXDown);
                int distanceY = (int) (mYMove - mYDown);
                //根据x的偏移量我们可以计算出当前的状态
                checkSlideState(distanceX,distanceY);
                switch (currentState){
                    case DO_NOTHING:
                        break;
                    case SHOW_LEFT_MENU:
                        mContentParams.rightMargin = -distanceX;
                        checkLeftBorder();
                        mContentView.setLayoutParams(mContentParams);
                        break;
                    case SHOW_RIGHT_MENU:
                        mContentParams.leftMargin = distanceX;
                        checkRightBorder();
                        mContentView.setLayoutParams(mContentParams);
                        break;
                    case HIDE_LEFT_MENU:
                        mContentParams.leftMargin = mLeftParams.width + distanceX;
                        checkLeftBorder();
                        mContentView.setLayoutParams(mContentParams);
                        break;
                    case HIDE_RIGHT_MENU:
                        mContentParams.rightMargin = mRightParams.width - distanceX;
                        checkRightBorder();
                        mContentView.setLayoutParams(mContentParams);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                mXUp = event.getRawX();
                int upDistance = (int) (mXUp - mXDown);
                if (isSliding){
                    switch (currentState){
                        case DO_NOTHING:
                            break;
                        case SHOW_LEFT_MENU:
                            if (shouldScrollToLeftMenu()){
                                scrollToLeftMenu();
                            }else {
                                scrollToContentFromLeftMenu();
                            }
                            break;
                        case SHOW_RIGHT_MENU:
                            if (shouldScrollToRightMenu()){
                                scrollToRightMenu();
                            }else {
                                scrollToContentFromRightMenu();
                            }
                            break;
                        case HIDE_LEFT_MENU:
                            if (shouldScrollToContentFromLeftMenu()){
                                scrollToContentFromLeftMenu();
                            }else {
                                scrollToLeftMenu();
                            }
                            break;
                        case HIDE_RIGHT_MENU:
                            if (shouldScrollToContentFromRightMenu()){
                                scrollToContentFromRightMenu();
                            }else {
                                scrollToRightMenu();
                            }
                            break;
                    }
                }else if (upDistance < mTouchSlop && isLeftMenuVisible){
                    scrollToContentFromLeftMenu();
                }else if (upDistance < mTouchSlop && isRightMenuVisible){
                    scrollToContentFromRightMenu();
                }
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        if (v.isEnabled()) {
            if (isSliding) {
                // 正在滑动时让控件得不到焦点
                unFocusBindView();
                return true;
            }
            if (isLeftMenuVisible || isRightMenuVisible) {
                // 当左侧或右侧布局显示时，将绑定控件的事件屏蔽掉
                return true;
            }
            return false;
        }
        return true;
    }
    //创建一个测速的工具
    private void createVelocityTracker(MotionEvent event){
        if (mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    //通过滑动的距离来判断当前的滑动状态
    private void checkSlideState(int distanceX,int distanceY){
        if (isLeftMenuVisible){
            if (!isSliding && Math.abs(distanceX) >= mTouchSlop && distanceX <0){
                //左侧菜单栏完全展示，想要收回左侧菜单栏的时候
                isSliding = true;
                currentState = HIDE_LEFT_MENU;
            }
        }else if (isRightMenuVisible){
            if (!isSliding && Math.abs(distanceX) >= mTouchSlop && distanceX >0){
                //此时右侧菜单栏全部展现，想要收回右侧菜单栏的时候
                isSliding = true;
                currentState = HIDE_RIGHT_MENU;
            }
        }else {
            //此时只有主内容布局展现，想要侧滑出左侧或者右侧菜单栏的情况
            if (!isSliding && Math.abs(distanceX) >= mTouchSlop && distanceX > 0 && Math.abs(distanceY) < mTouchSlop){
                //侧滑出左侧菜单栏的情况
                isSliding = true;
                currentState = SHOW_LEFT_MENU;
                mContentParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                mContentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mContentView.setLayoutParams(mContentParams);
                //如果用户想让左侧菜单栏显示，需要先把右侧菜单栏隐藏掉
                mLeftView.setVisibility(View.VISIBLE);
                mRightView.setVisibility(View.GONE);
            }else if (!isSliding && Math.abs(distanceX) >= mTouchSlop && distanceX < 0 && Math.abs(distanceY) < mTouchSlop){
                //侧滑出右侧菜单栏的情况
                isSliding = true;
                currentState = SHOW_RIGHT_MENU;
                mContentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                mContentParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                mContentView.setLayoutParams(mContentParams);
                //如果想让右侧菜单栏显示，需要先把左侧菜单栏隐藏掉
                mLeftView.setVisibility(View.GONE);
                mRightView.setVisibility(View.VISIBLE);
            }
        }
    }
    private void checkLeftBorder(){
        if (mContentParams.rightMargin > 0){
            mContentParams.rightMargin = 0;
        }else if (mContentParams.rightMargin < -mLeftParams.width){
            mContentParams.rightMargin = -mLeftParams.width;
        }
    }
    private void checkRightBorder(){
        if (mContentParams.leftMargin > 0){
            mContentParams.leftMargin = 0;
        }else if (mContentParams.leftMargin < -mRightParams.width){
            mContentParams.leftMargin = -mRightParams.width;
        }
    }
    /**
     * 判断是否应该滚动将左侧菜单展示出来。如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将左侧菜单展示出来。
     *
     * @return 如果应该将左侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToLeftMenu() {
        return mXUp - mXDown > mLeftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }
    /**
     * 判断是否应该滚动将右侧菜单展示出来。如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将右侧菜单展示出来。
     *
     * @return 如果应该将右侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToRightMenu() {
        return mXDown - mXUp > mRightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }
    /**
     * 判断是否应该从左侧菜单滚动到内容布局，如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从左侧菜单滚动到内容布局。
     *
     * @return 如果应该从左侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromLeftMenu() {
        return mXUp - mXDown > mLeftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }
    /**
     * 判断是否应该从右侧菜单滚动到内容布局，如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从右侧菜单滚动到内容布局。
     *
     * @return 如果应该从右侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromRightMenu() {
        return mXUp - mXDown > mRightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }
    /**
     * 将界面滚动到左侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToLeftMenu() {
        new LeftMenuScrollTask().execute(-30);
    }

    /**
     * 将界面滚动到右侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToRightMenu() {
        new RightMenuScrollTask().execute(-30);
    }

    /**
     * 将界面从左侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToContentFromLeftMenu() {
        new LeftMenuScrollTask().execute(30);
    }

    /**
     * 将界面从右侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToContentFromRightMenu() {
        new RightMenuScrollTask().execute(30);
    }
    class LeftMenuScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int rightMargin = mContentParams.rightMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                rightMargin = rightMargin + speed[0];
                if (rightMargin < -mLeftParams.width) {
                    rightMargin = -mLeftParams.width;
                    break;
                }
                if (rightMargin > 0) {
                    rightMargin = 0;
                    break;
                }
                publishProgress(rightMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sleep(15);
                    }
                });
            }
            if (speed[0] > 0) {
                isLeftMenuVisible = false;
            } else {
                isLeftMenuVisible = true;
            }
            isSliding = false;
            return rightMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... rightMargin) {
            mContentParams.rightMargin = rightMargin[0];
            mContentView.setLayoutParams(mContentParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer rightMargin) {
            mContentParams.rightMargin = rightMargin;
            mContentView.setLayoutParams(mContentParams);
        }
    }

    class RightMenuScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = mContentParams.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin < -mRightParams.width) {
                    leftMargin = -mRightParams.width;
                    break;
                }
                if (leftMargin > 0) {
                    leftMargin = 0;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sleep(15);
                    }
                });
            }
            if (speed[0] > 0) {
                isRightMenuVisible = false;
            } else {
                isRightMenuVisible = true;
            }
            isSliding = false;
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            mContentParams.leftMargin = leftMargin[0];
            mContentView.setLayoutParams(mContentParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            mContentParams.leftMargin = leftMargin;
            mContentView.setLayoutParams(mContentParams);
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param millis
     *            指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 使用可以获得焦点的控件在滑动的时候失去焦点。
     */
    private void unFocusBindView() {
        if (mBindView != null) {
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }
}
