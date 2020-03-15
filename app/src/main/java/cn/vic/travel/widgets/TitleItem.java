package cn.vic.travel.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Snake 创建于 2018/5/29.
 */

public class TitleItem extends RelativeLayout {
    public TitleItem(Context context) {
        super(context);
    }
    public TitleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TitleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        heightMeasureSpec =50;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
