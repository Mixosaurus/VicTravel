package cn.vic.travel.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.vic.travel.R;
import cn.vic.travel.traveltogether.TravelInfoAdapter;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/8/29 10:24
 */
public class PullUpRefeshLayout extends SwipeRefreshLayout {

    private RecyclerView mRecyclerView;         //子控件
    private float mDownY, mUpY;
    private int mScaledTouchSlop;       //控件移动的最小距离
    private int mItemCount;
    private boolean isLoading;
    private OnLoadMoreListener mListener;
    private View mFooterView=null;

    private class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    //private static final String TAG = SwipeRefreshView.class.getSimpleName();

    public PullUpRefeshLayout(@NonNull Context context) {
        super(context);
    }

    public PullUpRefeshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mFooterView = View.inflate(context, R.layout.view_footer, null);
        //移动的距离大于该距离视为拖动控件
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 获取ListView,设置ListView的布局位置
        if ( mRecyclerView == null) {
            // 判断容器有多少个孩子
            if (getChildCount() > 0) {
                // 判断第一个孩子是不是ListView
                 if (getChildAt(0) instanceof RecyclerView) {

                     mRecyclerView = (RecyclerView) getChildAt(0);   // 创建RecyclerView 对象
                     setRecyclerViewOnScroll();      // 设置RecyclerView的滑动监听
                }
            }
        }
    }


    /**
     * 设置RecyclerView的滑动监听
     */
    private void setRecyclerViewOnScroll() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //滚动状态变化时回调
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 移动过程中判断时候能下拉加载更多
                if (canLoadMore()) {
                    loadData();// 加载数据
                }
            }
            //滚动时回调
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 移动的起点
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动过程中判断时候能下拉加载更多
                if (canLoadMore()) {
                    loadData();     // 加载数据
                }
                break;

            case MotionEvent.ACTION_UP:
                // 移动的终点
                mUpY = getY();
                //debugToast("Y: "+ev.getY()+" , "+getY());
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否满足加载更多条件
     */
    private boolean canLoadMore() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) (mRecyclerView.getLayoutManager());
        // 1. 是上拉状态
        boolean condition1 = (mDownY - mUpY) >= mScaledTouchSlop;
        if (condition1) {
            //Log.d(TAG, "------->  是上拉状态");
            //debugToast("上拉");
        }

        // 2. 当前页面可见的item是最后一个条目,一般最后一个条目位置需要大于第一页的数据长度
        boolean condition2 = false;
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
            if (mItemCount > 0) {
                if (mRecyclerView.getAdapter().getItemCount()< mItemCount) {
                    // 第一页未满，禁止下拉
                    //mRecyclerView.getAdapter().createViewHolder(((ViewGroup)mRecyclerView),0);
                    //mRecyclerView.getAdapter().bindViewHolder(mRecyclerView.getAdapter().createViewHolder(((ViewGroup)mRecyclerView),0),0);
                    condition2 = false;
                }
                else {
                    condition2 =layoutManager .findLastVisibleItemPosition() == (mRecyclerView.getAdapter().getItemCount() - 1);
                }
            }
            else {
                // 未设置数据长度，则默认第一页数据不满时也可以上拉
                condition2 =layoutManager .findLastVisibleItemPosition()  == (mRecyclerView.getAdapter().getItemCount() - 1);
            }

        }

        if (condition2) {
            //debugToast("最后一个条目");
            //Log.d(TAG, "------->  是最后一个条目");
        }
        // 3. 正在加载状态
        boolean condition3 = !isLoading;
        if (condition3) {
            //debugToast("正在加载状态");
            //Log.d(TAG, "------->  不是正在加载状态");
        }
        //return condition1 && condition2 && condition3;
        return true;
    }

    /**
     * 处理加载数据的逻辑
     */
    private void loadData() {
        debugToast("loadData()");
        //if (mListener != null) {
            // 设置加载状态，让布局显示出来
            setLoading(true);
            //mListener.onLoadMore();
        //}
    }

    /**
     * 设置加载状态，是否加载传入boolean值进行判断
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        debugToast("显示布局");
        // 修改当前的状态
        isLoading = loading;
        if (isLoading) {
            // 显示布局
            //debugToast("显示布局");

            //mRecyclerView.getAdapter().bindViewHolder(new FooterHolder(mFooterView), mRecyclerView.getAdapter().getItemCount()-1);

            if(mRecyclerView!=null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().bindViewHolder(new FooterHolder(mFooterView), 0);
                }
                else{
                    debugToast("mRecyclerView.getAdapter()==null");
                }
            }
            else {
                debugToast("mRecyclerView==null");
            }

            //mRecyclerView.addFooterView(mFooterView);

        } else {
            // 隐藏布局
             //mRecyclerView.removeFooterView(mFooterView);

            // 重置滑动的坐标
            mDownY = 0;
            mUpY = 0;
        }
    }

    /**
     * 上拉加载的接口回调
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

//    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
//        this.mListener = listener;
//    }

}
