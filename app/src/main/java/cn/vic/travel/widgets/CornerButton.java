package cn.vic.travel.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import cn.vic.travel.R;
import cn.vic.travel.Utils;

import static cn.vic.travel.Utils.debugToast;

/**
 * 圆角按钮
 * Snake 创建于 2018/6/30.
 */

public class CornerButton extends android.support.v7.widget.AppCompatButton{
    GradientDrawable gradientDrawable;      //控件样式
    float radius=25f;     //圆角半径
    int widthInDp=110;  //宽度，以dp为单位
    int heightInDP=40; //高度，以dp为单位

    public CornerButton(Context context) {
        super(context);
        initView();
    }
    public CornerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public CornerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setWidth(110);  //默认宽度
        setHeight(40);  //默认高度
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(getResources().getColor(R.color.colorPrimary));       //设置默认背景颜色
        gradientDrawable.setCornerRadius(new Utils().convertToPx(radius));          //设置圆角半径
        setTextColor(getResources().getColor(R.color.colorWhite));      //设置默认文字颜色
        setBackgroundDrawable(gradientDrawable);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                setColor(event.getAction());    //按下改变样式
                return false;       //防止Click事件被屏蔽
            }
        });
    }

    //设置点击效果
    protected void setColor(int state){
        //按下
        if (state == MotionEvent.ACTION_DOWN) {
            gradientDrawable.setColor(getResources().getColor(R.color.colorPrimaryDark));       //背景颜色
            setTextColor(getResources().getColor(R.color.colorDivider));        //文字颜色
        }
        //抬起
        if(state == MotionEvent.ACTION_UP){
            gradientDrawable.setColor(getResources().getColor(R.color.colorPrimary));       //背景颜色
            setTextColor(getResources().getColor(R.color.colorWhite));        //文字颜色
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int desiredWidth = 155;
//        int desiredHeight = 155;
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int width;
//        int height;
//
//        // 如果控件宽度是指定大小，宽度为指定的尺寸
//        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) { // 没有限制，默认内容大小
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            width = desiredWidth;
//        }
//
//        // 如果控件高度是指定大小，高度为指定的尺寸
//        if (heightMode == MeasureSpec.EXACTLY) {
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {// 没有限制，默认内容大小
//            height = Math.min(desiredHeight, heightSize);
//        } else {
//            height = desiredHeight;
//        }
//        // 设定控件大小
//        //setMeasuredDimension(width, height);
        setMeasuredDimension(getWidthInPx(),getHeightInPx());
    }

    //将dp转为像素
    private int getWidthInPx(){
         return new Utils().convertToPx(widthInDp);
    }

    //将dp转为像素
    private int getHeightInPx(){
        return new Utils().convertToPx(heightInDP);
    }

}
