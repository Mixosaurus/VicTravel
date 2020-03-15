package cn.vic.travel.traveltogether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.vic.travel.R;

/**
 * Snake 创建于 2018/9/2 16:58
 */
public class FooterHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlLoad;
    TextView tvLoad;

    public FooterHolder(Context context, View view) {
        super(view);
        tvLoad=(TextView) view.findViewById(R.id.tv_loading);
        rlLoad =(RelativeLayout)view.findViewById(R.id.rl_loading);
    }

    public void hideLoadingBar(){
        rlLoad.setVisibility(View.GONE);
    }
    public void setText(String text){
        tvLoad.setText(text);
    }

}
