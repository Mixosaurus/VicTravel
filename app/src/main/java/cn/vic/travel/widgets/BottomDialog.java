package cn.vic.travel.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import cn.vic.travel.R;

/**
 * Snake 创建于 2018/5/26.
 * 底部弹出菜单
 */

public class BottomDialog extends Dialog{
    private Context mContext;
    private int mWidth;
    private int mTransparent=1;
    private LinearLayout rootLinearLayout;

    public interface BottonDialogListener {
        void onButton1Listener();
        void onButton2Listener();
    }

    public BottomDialog(@NonNull Context context) {
        super(context);
        this.mContext=context;
        initView();
    }
    public BottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext=context;
        initView();
    }
    protected BottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext=context;
        initView();
    }
    private void initView() {
        rootLinearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.bottom_dialog, null);
        //View view = View.inflate(mContext, R.layout.bottom_dialog, null);
        setContentView(rootLinearLayout);
        Window dialogWindow =this.getWindow();      //获取当前控件(Dialog)窗口
        if(dialogWindow!=null){
            rootLinearLayout.measure(0, 0);       //必须调用，否则无法得到rootLinearLayout尺寸
            dialogWindow.setGravity(Gravity.BOTTOM);        //设置dialog的布局样式，使其位于底部
            dialogWindow.setWindowAnimations(R.style.DialogAnimation); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.width = ((Activity)mContext).getResources().getDisplayMetrics().widthPixels;     //将窗口宽度设为屏幕宽度
            lp.height = rootLinearLayout.getMeasuredHeight();       //将窗口高度设为LinearLayout含有控件的高度
            lp.alpha = mTransparent;        // 透明度
            dialogWindow.setAttributes(lp);
        }
    }

    public void setOnClickListener(final BottonDialogListener bottonDialogListener){
        rootLinearLayout.findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottonDialogListener.onButton1Listener();
                dismiss();
            }
        });
        rootLinearLayout.findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bottonDialogListener.onButton2Listener();
                dismiss();
            }
        });
        rootLinearLayout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show(){
        super.show();
    }

}