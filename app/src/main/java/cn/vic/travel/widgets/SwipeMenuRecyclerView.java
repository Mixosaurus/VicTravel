package cn.vic.travel.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import cn.vic.travel.R;

/**
 * Snake 创建于 2018/9/14 22:03
 */
public class SwipeMenuRecyclerView extends RecyclerView {
    private static final String TAG = "SwipeMenuRecyclerView";

    private int mPrivateFlags;

    /**
     * A flag indicates that the itemView {@link #mItemView} is being dragged
     *
     * @see #isDraggingItemView()
     */
    private static final int PFLAG_ITEM_BEING_DRAGGED = 1;

    /**
     * A flag indicates that the menu of itemView {@link #mItemView} has been opened
     *
     * @see #isItemMenuOpen()
     */
    private static final int PFLAG_ITEM_MENU_OPENED = 1 << 1;

    /**
     * A flag indicates that the menu of itemView {@link #mItemView} has been opened
     * on this view receiving {@link MotionEvent#ACTION_DOWN}.
     */
    private static final int PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN = 1 << 2;

    /**
     * If set, itemView {@link #mItemView} will be able to be scrolled
     *
     * @see #isItemScrollingEnabled()
     * @see #setItemScrollingEnabled(boolean)
     */
    private static final int PFLAG_ITEM_SCROLLING_ENABLED = 1 << 3;

    /** Distance to travel before drag may begin */
    protected final float mTouchSlop;

    private int mDownX;
    private int mDownY;

    private final float[] mTouchX = new float[2];
    private final float[] mTouchY = new float[2];

    private VelocityTracker mVelocityTracker;

    /**
     * The bounds of the currently touched itemView {@link #mItemView} (relative to current view).
     */
    private final Rect mTouchedItemBounds = new Rect();
    /**
     * The bounds of the currently touched itemView's menu (relative to current view).
     */
    private final Rect mTouchedItemMenuBounds = new Rect();

    /** The itemView currently being dragged */
    private ViewGroup mItemView;
    /**
     * The itemView previously dragged.<br>
     * <b>Note that this will be equal to {@link #mItemView} after user raises his/her last finger
     * that is touching the screen till he/she downs his/her finger on other itemView area.</b>
     */
    private ViewGroup mPreItemView;

    /**
     * The width of the itemView's menu.<br>
     * <b>Note: </b>This is the final scroll distance while itemView {@link #mItemView} is scrolling
     * towards the horizontal start.
     */
    private int mItemMenuWidth;
    /** The width of the previous itemView's menu */
    private int mPreItemMenuWidth;

    /** Minimum gesture speed to automatically scroll itemView {@link #mItemView} */
    private final float mAutoScrollItemMinVelocityX; // 200 dp/s

    /**
     * Time interval in milliseconds of automatically scrolling itemView {@link #mItemView}
     */
    private static final int DURATION_AUTO_SCROLL_ITEM_MENU = 500; // ms

    private static final Interpolator sViscousFluidInterpolator = new ViscousFluidInterpolator(6.66f);
    private static final Interpolator sOvershootInterpolator = new OvershootInterpolator(1.0f);

    /** @return whether the itemView is being dragged by user */
    public boolean isDraggingItemView() {
        return (mPrivateFlags & PFLAG_ITEM_BEING_DRAGGED) != 0;
    }

    /** @return whether the itemView's menu is open */
    public boolean isItemMenuOpen() {
        return (mPrivateFlags & PFLAG_ITEM_MENU_OPENED) != 0;
    }

    /**
     * @return whether it is enabled to scroll itemView or not
     * @see #setItemScrollingEnabled(boolean)
     */
    public boolean isItemScrollingEnabled() {
        return (mPrivateFlags & PFLAG_ITEM_SCROLLING_ENABLED) != 0;
    }

    /**
     * Enables the scrolling of itemView.<br>
     * <b>Note that this will only work on vertical layout.</b>
     *
     * @see #isItemScrollingEnabled()
     */
    public void setItemScrollingEnabled(boolean enabled) {
        if (enabled)
            mPrivateFlags |= PFLAG_ITEM_SCROLLING_ENABLED;
        else
            mPrivateFlags &= ~PFLAG_ITEM_SCROLLING_ENABLED;
    }

    public SwipeMenuRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeMenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final float dp = context.getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.getTouchSlop() * dp;
        mAutoScrollItemMinVelocityX = 200f * dp;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuRecyclerView, defStyle, 0);
        setItemScrollingEnabled(a.getBoolean(R.styleable
                .SwipeMenuRecyclerView_itemScrollingEnabled, true));
        a.recycle();
    }

    private boolean isItemViewLayoutRtl() {
        return ViewCompat.getLayoutDirection(mItemView) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean intercept = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) e.getX();
                mDownY = (int) e.getY();
                markCurrTouchPoint(mDownX, mDownY);

                for (int i = getChildCount() - 1; i >= 0; i--) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != VISIBLE) continue;

                    if (!(child instanceof ViewGroup)) continue;
                    ViewGroup itemView = (ViewGroup) child;

                    child.getHitRect(mTouchedItemBounds);
                    if (!mTouchedItemBounds.contains(mDownX, mDownY)) continue;

                    int itemMenuWidth = 0;
                    final int itemChildCount = itemView.getChildCount();
                    View itemLastChild = itemView.getChildAt(itemChildCount > 1 ?
                            itemChildCount - 1 : 1);
                    if (itemLastChild instanceof FrameLayout) {
                        FrameLayout menu = (FrameLayout) itemLastChild;
                        for (int j = 0; j < menu.getChildCount(); j++) {
                            itemMenuWidth += ((FrameLayout) menu.getChildAt(j))
                                    .getChildAt(0).getWidth();
                        }
                    }
                    if (itemMenuWidth > 0) {
                        mItemView = itemView;
                        mItemMenuWidth = itemMenuWidth;
                    }
                    break;
                }

                if ((mPrivateFlags & PFLAG_ITEM_MENU_OPENED) == 0) {
                    mPrivateFlags &= ~PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN;
                    // If there exists itemView's menu opened
                } else {
                    mPrivateFlags |= PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN;

                    if (mPreItemView == mItemView) {
                        final int left = isItemViewLayoutRtl() ? 0 :
                                mItemView.getRight() - mItemMenuWidth;
                        final int right = left + mItemMenuWidth;
                        mTouchedItemMenuBounds.set(left, mTouchedItemBounds.top,
                                right, mTouchedItemBounds.bottom);
                        // If the user's finger downs on the opened itemView's menu,
                        // do not intercept the subsequent touch events (MotionEvent#ACTION_MOVE...)
                        // as we receive the action down event.
                        if (mTouchedItemMenuBounds.contains(mDownX, mDownY)) {
                            // Disallow our parent ViewGroups to intercept them too.
                            requestParentDisallowInterceptTouchEvent();
                            break;
                            // If the user's finger downs on the area of the fully scrolled itemView
                            // but not on its menu, then we need to intercept them.
                        } else if (mTouchedItemBounds.contains(mDownX, mDownY)) {
                            requestParentDisallowInterceptTouchEvent();
                            return true;
                            // If the user's finger downs outside the area in which this view displays
                            // the itemViews, we should make current opened itemView's menu hidden
                            // but not intercept the subsequent touch events.
                        } else {
                            releaseItemView();
                            break;
                        }
                    }
                    // If the previous scrolled itemView is not the current one, make it hidden
                    // and intercept the touch events.
                    releasePreItemView();
                    requestParentDisallowInterceptTouchEvent();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                markCurrTouchPoint(e.getX(), e.getY());

                intercept = tryHandleItemScrollingEvent();
                // If the user initially put his/her finger down on the opened itemView's menu area,
                // disallow our parent class to intercept the touch events since we will do that
                // as the user tends to horizontally scroll the current touched itemView.
                if ((mPrivateFlags & PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN) != 0
                        && mTouchedItemMenuBounds.contains(mDownX, mDownY)) {
                    return intercept;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // If the user initially put his/her finger down on the opened itemView's menu and
                // has not scrolled it, hide it as his/her last finger touching the screen is lifted.
                if ((mPrivateFlags & PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN) != 0
                        && mTouchedItemMenuBounds.contains(mDownX, mDownY)) {
                    releaseItemView();
                }
                break;
        }
        return intercept || super.onInterceptTouchEvent(e);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                if ((mPrivateFlags & PFLAG_ITEM_BEING_DRAGGED) != 0) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                markCurrTouchPoint(e.getX(), e.getY());

                // if the user is dragging itemView
                if ((mPrivateFlags & PFLAG_ITEM_BEING_DRAGGED) != 0) {
                    if (mVelocityTracker == null)
                        mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(e);

                    // It will be positive when the user's finger slides towards the right.
                    float dx = mTouchX[mTouchX.length - 1] - mTouchX[mTouchX.length - 2];
                    // It will be positive when the itemView scrolls towards the right.
                    final float transX = mItemView.getChildAt(0).getTranslationX();
                    final boolean rtl = isItemViewLayoutRtl();
                    final int finalXFromEndToStart = rtl ? mItemMenuWidth : -mItemMenuWidth;
                    // Swipe the itemView towards the horizontal start over the width of
                    // the itemView's menu (#mItemMenuWidth).
                    if (!rtl && dx + transX < finalXFromEndToStart
                            || rtl && dx + transX > finalXFromEndToStart) {
                        dx = dx / 3f;
                        // Swipe the itemView towards the end of horizontal to (0,0).
                    } else if (!rtl && dx + transX > 0 || rtl && dx + transX < 0) {
                        dx = 0 - transX;
                    }
                    smoothTranslateItemViewXBy(mItemView, dx, 0);
                    // Consume this touch event and not invoke the method onTouchEvent(e) of
                    // the parent class to temporarily make this view unable to scroll up or down.
                    return true;
                } else {
                    // If there existed itemView fully scrolled out as the user initially
                    // put his/her finger down, always consume the touch event and only
                    // when it has a tend of scrolling horizontally will we handle this event.
                    if ((mPrivateFlags & PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN) != 0
                            | tryHandleItemScrollingEvent()) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((mPrivateFlags & PFLAG_ITEM_BEING_DRAGGED) != 0) {
                    final boolean rtl = isItemViewLayoutRtl();
                    final float transX = mItemView.getChildAt(0).getTranslationX();
                    // itemView's menu closed
                    if (transX == 0f) {
                        mPrivateFlags &= ~PFLAG_ITEM_MENU_OPENED;

                        // itemView's menu opened
                    } else if (!rtl && transX == -mItemMenuWidth
                            || rtl && transX == mItemMenuWidth) {
                        mPrivateFlags |= PFLAG_ITEM_MENU_OPENED;

                        // scrollX > 0f && scrollX < mItemMenuWidth
                        // || scrollX > mItemMenuWidth
                    } else {
                        final float dx = rtl ? mTouchX[mTouchX.length - 1] - mTouchX[mTouchX.length - 2]
                                : mTouchX[mTouchX.length - 2] - mTouchX[mTouchX.length - 1];

                        mVelocityTracker.computeCurrentVelocity(1000);
                        final float velocityX = Math.abs(mVelocityTracker.getXVelocity());
                        // If the speed at which the user's finger lifted is greater than 200 dp/s
                        // while user was scrolling itemView towards the horizontal start,
                        // make it automatically scroll to open and show its menu.
                        if (dx > 0 && velocityX >= mAutoScrollItemMinVelocityX) {
                            mPrivateFlags |= PFLAG_ITEM_MENU_OPENED;
                            smoothTranslateItemViewXTo(mItemView, rtl ? mItemMenuWidth
                                    : -mItemMenuWidth, DURATION_AUTO_SCROLL_ITEM_MENU);

                            // If the speed at which the user's finger lifted is greater than 200 dp/s
                            // while user was scrolling itemView towards the end of horizontal,
                            // make its menu hidden.
                        } else if (dx < 0 && velocityX >= mAutoScrollItemMinVelocityX) {
                            releaseItemView();

                        } else {
                            final float middle = mItemMenuWidth / 2f;
                            // If the sliding distance is less than half of the slidable distance,
                            // make itemView's menu hidden,
                            if (Math.abs(transX) < middle) {
                                releaseItemView();

                                // else make its menu open.
                            } else {
                                mPrivateFlags |= PFLAG_ITEM_MENU_OPENED;
                                smoothTranslateItemViewXTo(mItemView, rtl ? mItemMenuWidth
                                        : -mItemMenuWidth, DURATION_AUTO_SCROLL_ITEM_MENU);
                            }
                        }
                    }

                    resetTouch();
                    mPreItemView = mItemView;
                    mPreItemMenuWidth = mItemMenuWidth;
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                if ((mPrivateFlags & PFLAG_ITEM_BEING_DRAGGED) != 0) {
                    resetTouch();
                    releaseItemView();
                    return true;
                }
                // 1. If the itemView previously scrolled equals the current one and
                // the user hasn't scrolled it since he/she initially put his/her finger down,
                // hide it on the finger lifted.
                // 2. If previously opened itemView differs from the one currently touched,
                // and the current one has not been scrolled at all, set mItemView to null.
                if ((mPrivateFlags & PFLAG_ITEM_MENU_OPENED_ON_ACTION_DOWN) != 0) {
                    releaseItemView();
                    return true;
                }
                break;
        }

        if (super.onTouchEvent(e)) {
            setVerticalScrollBarEnabled(true);
            return true;
        }
        return false;
    }

    private boolean tryHandleItemScrollingEvent() {
        // Unable to scroll it
        if ((mPrivateFlags & PFLAG_ITEM_SCROLLING_ENABLED) == 0 || getChildCount() <= 0) {
            return false;
        }
        // There's no scrollable itemView being touched by user.
        if (mItemView == null) {
            return false;
        }
        // The list may be currently scrolling
        if (getScrollState() != SCROLL_STATE_IDLE) {
            return false;
        }
        // The layout's orientation may not be vertical.
        if (getLayoutManager().canScrollHorizontally()) {
            return false;
        }

        final float absDX = Math.abs(mTouchX[mTouchX.length - 1] - mDownX);
        final float absDY = Math.abs(mTouchY[mTouchY.length - 1] - mDownY);
        if (absDX > absDY && absDX > mTouchSlop) {
            mPrivateFlags |= PFLAG_ITEM_BEING_DRAGGED;
            requestParentDisallowInterceptTouchEvent();
            // Make the vertical scroll bar disappear while the itemView is being dragged.
            setVerticalScrollBarEnabled(false);
            return true;
        }
        return false;
    }

    private void markCurrTouchPoint(float x, float y) {
        System.arraycopy(mTouchX, 1, mTouchX, 0, mTouchX.length - 1);
        mTouchX[mTouchX.length - 1] = x;
        System.arraycopy(mTouchY, 1, mTouchY, 0, mTouchY.length - 1);
        mTouchY[mTouchY.length - 1] = y;
    }

    private void requestParentDisallowInterceptTouchEvent() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private void resetTouch() {
        mPrivateFlags &= ~PFLAG_ITEM_BEING_DRAGGED;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Makes the current itemView whose menu is open scroll back to its original position.
     */
    public void releaseItemView() {
        releaseItemViewInternal(mItemView, DURATION_AUTO_SCROLL_ITEM_MENU);
        mPreItemView = mItemView = null;
        mPreItemMenuWidth = mItemMenuWidth = 0;
    }

    private void releasePreItemView() {
        releaseItemViewInternal(mPreItemView, DURATION_AUTO_SCROLL_ITEM_MENU);
        mPreItemView = null;
        mPreItemMenuWidth = 0;
    }

    private void releaseItemViewInternal(ViewGroup itemView, int duration) {
        if (itemView != null) {
            smoothTranslateItemViewXTo(itemView, 0, duration);
            mPrivateFlags &= ~PFLAG_ITEM_MENU_OPENED;
        }
    }

    private void smoothTranslateItemViewXBy(ViewGroup itemView, float dx, int duration) {
        if (dx == 0) return;

        final int itemChildCount = itemView.getChildCount();
        View itemVisibleFrame = itemView.getChildAt(0);
        View itemLastFrame = itemView.getChildAt(itemChildCount - 1);

        Interpolator interpolator = null;
        final float transX = itemVisibleFrame.getTranslationX();
        if (duration > 0) {
            final boolean rtl = isItemViewLayoutRtl();
            interpolator = (!rtl && dx < 0 || rtl && dx > 0) ?
                    sOvershootInterpolator : sViscousFluidInterpolator;
        }
        for (int i = 0; i < itemChildCount; i++) {
            View itemChild = itemView.getChildAt(i);
            if (duration > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ViewPropertyAnimator vpa = itemChild.animate()
                            .translationXBy(dx)
                            .setInterpolator(interpolator).setDuration(duration);
                    if (i != itemChildCount - 1) {
                        vpa.withLayer();
                    }
                    vpa.start();
                } else {
                    Animator animator = ObjectAnimator.ofFloat(itemChild,
                            "translationX", transX, transX + dx);
                    animator.setInterpolator(interpolator);
                    animator.setDuration(duration).start();
                }
            } else {
                itemChild.setTranslationX(transX + dx);
            }
        }

        if (itemLastFrame instanceof ViewGroup) {
            ViewGroup menu = (ViewGroup) itemLastFrame;
            final int menuWidth = itemView == mItemView ? mItemMenuWidth : mPreItemMenuWidth;

            float menuItemDX = 0;
            for (int i = 1; i < menu.getChildCount(); i++) {
                View menuItem = menu.getChildAt(i);
                menuItemDX -= dx * ((ViewGroup) menu.getChildAt(i - 1)).getChildAt(0)
                        .getWidth() / menuWidth;

                final float menuItemTransX = menuItem.getTranslationX();
                final float newMenuItemTransX = menuItemTransX + menuItemDX;
                if (duration > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        menuItem.animate().translationX(newMenuItemTransX).withLayer()
                                .setInterpolator(interpolator).setDuration(duration).start();
                    } else {
                        Animator animator = ObjectAnimator.ofFloat(menuItem,
                                "translationX", menuItemTransX, newMenuItemTransX);
                        animator.setInterpolator(interpolator);
                        animator.setDuration(duration).start();
                    }
                } else {
                    menuItem.setTranslationX(newMenuItemTransX);
                }
            }
        }
    }

    private void smoothTranslateItemViewXTo(ViewGroup itemView, float x, int duration) {
        smoothTranslateItemViewXBy(itemView, x - itemView.getChildAt(0)
                .getTranslationX(), duration);
    }
}
