package cn.vic.travel.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageSwitcher;

/**
 * Snake 创建于 2018/5/18.
 */

public class SquareImageSwitcher extends ImageSwitcher {
    public SquareImageSwitcher(Context context) {
        super(context);
    }
    public SquareImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SquareImageSwitcher (Context context, AttributeSet attrs, int defStyle){
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        //int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
