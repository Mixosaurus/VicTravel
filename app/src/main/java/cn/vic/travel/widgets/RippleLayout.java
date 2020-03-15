package cn.vic.travel.widgets;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Snake 创建于 2018/8/1 10:00
 * ConstraintLayout，用以实现该布局内的控件点击水波纹效果
 */
public class RippleLayout extends ConstraintLayout {
    private View mTouchTarget;      //触摸目标，即用户触摸的view
    private boolean mIsPressed;
    private long INVALIDATE_DURATION=5;     //失效时间
    //private Runnable mDispatchUpTouchEventRunnable;

    private class DispatchUpTouchEventRunnable implements Runnable {
        public MotionEvent event;

        @Override
        public void run() {
            if (mTouchTarget == null || !mTouchTarget.isEnabled()) {
                return;
            }

            if (isTouchPointInView(mTouchTarget, (int)event.getRawX(), (int)event.getRawY())) {
                mTouchTarget.dispatchTouchEvent(event);
            }
        }
    };

    DispatchUpTouchEventRunnable mDispatchUpTouchEventRunnable=new DispatchUpTouchEventRunnable();

    public RippleLayout(Context context) {
        super(context);
    }

    /**
     *
     * @param event 触摸事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //屏幕绝对坐标
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();     //触摸事件
        //判断事件的按下、抬起、取消事件是否落在同一元素上
        if (action == MotionEvent.ACTION_DOWN) {
            View touchTarget = getTouchTarget(this, x, y);      //获取坐标所在的view
            //判断view是否可点击和可用
            if (touchTarget.isClickable() && touchTarget.isEnabled()) {
                mTouchTarget = touchTarget;     //找到目标view
                initParametersForChild(event, touchTarget);
                postInvalidateDelayed(INVALIDATE_DURATION);
            }
        }
        else if (action == MotionEvent.ACTION_UP) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
            mDispatchUpTouchEventRunnable.event = event;
            postDelayed(mDispatchUpTouchEventRunnable, 400);
            return true;
        }
        else if (action == MotionEvent.ACTION_CANCEL) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
        }

        return super.dispatchTouchEvent(event);
    }

    private void initParametersForChild(MotionEvent event, View touchTarget) {

    }

    /**
     * 遍历view树寻找用户所点击的view
     * @param view view
     * @param x 屏幕x坐标
     * @param y 屏幕y坐标
     * @return 坐标所在的
     */
    private View getTouchTarget(View view, int x, int y) {
        View target = null;
        ArrayList<View> TouchableViews = view.getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                target = child;
                break;
            }
        }

        return target;
    }

    /**
     * 判断坐标是否位于view内部
     * @param view 待判断view
     * @param x 屏幕绝对x坐标
     * @param y 屏幕绝对y坐标
     */
    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top && y <= bottom
                && x >= left && x <= right) {
            return true;
        }
        return false;
    }


//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        if (!mShouldDoAnimation || mTargetWidth <= 0 || mTouchTarget == null) {
//            return;
//        }
//
//        if (mRevealRadius > mMinBetweenWidthAndHeight / 2) {
//            mRevealRadius += mRevealRadiusGap * 4;
//        } else {
//            mRevealRadius += mRevealRadiusGap;
//        }
//        int[] location = new int[2];
//        mTouchTarget.getLocationOnScreen(location);
//        int left = location[0] - mLocationInScreen[0];
//        int top = location[1] - mLocationInScreen[1];
//        int right = left + mTouchTarget.getMeasuredWidth();
//        int bottom = top + mTouchTarget.getMeasuredHeight();
//
//        canvas.save();
//        canvas.clipRect(left, top, right, bottom);
//        canvas.drawCircle(mCenterX, mCenterY, mRevealRadius, mPaint);
//        canvas.restore();
//
//        if (mRevealRadius <= mMaxRevealRadius) {
//            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
//        } else if (!mIsPressed) {
//            mShouldDoAnimation = false;
//            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
//        }
//    }


}
