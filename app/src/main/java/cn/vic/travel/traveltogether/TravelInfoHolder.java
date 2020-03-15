package cn.vic.travel.traveltogether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.vic.travel.R;
import cn.vic.travel.chat.BaseViewHolder;
import cn.vic.travel.chat.OnRecyclerViewListener;
import cn.vic.travel.conversation.Conversation;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/8/23 16:40
 */
public class TravelInfoHolder extends RecyclerView.ViewHolder {
    private TextView tvDeparture;           //出发地
    private TextView tvDestination;         //目的地
    private TextView tvStartDate;           //开始时间
    private TextView tvEndDate;             //结束时间
    private TextView tvPeopleNumber;        //人数
    private TextView tvRemarkers;           //备注
    private TextView tvTitle;//标题
    private TextView tvUserName;//发行人名称
    private Button btnAddTravel;//同行加入按钮
    public OnRecyclerViewListener onRecyclerViewListener;


//    public TravelInfoHolder(Context context, OnRecyclerViewListener listener, View view) {
//    }

    public TravelInfoHolder(Context context, OnRecyclerViewListener listener,  View view) {
        super(view);
        tvTitle= (TextView) view.findViewById(R.id.tv_title);
        tvUserName= (TextView) view.findViewById(R.id.tv_initiator_name);
        tvRemarkers= (TextView) view.findViewById(R.id.tv_contect);
        onRecyclerViewListener=listener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });
    }

    //@Override
    public void bindData(Object o) {
        if(o!=null){
            if(tvRemarkers!=null) {
                final TravelInfo travelInfo = (TravelInfo) o;
                tvTitle.setText(travelInfo.getTitle());
                tvUserName.setText(travelInfo.getUsername());
                tvRemarkers.setText(travelInfo.getRemarks());
            }
        }
    }
}
