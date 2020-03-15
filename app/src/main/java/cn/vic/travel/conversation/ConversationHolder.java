package cn.vic.travel.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.vic.travel.R;
import cn.vic.travel.chat.BaseViewHolder;
import cn.vic.travel.chat.OnRecyclerViewListener;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/8/7 16:20
 */
public class ConversationHolder extends RecyclerView.ViewHolder {

    private ImageView ivRecentAvatar;
    private TextView tv_recent_name;
    private TextView tv_recent_msg;
    private TextView tv_recent_time;
    private TextView tv_recent_unread;
    private PrivateConversation conversation;
    private Context context;
    private OnSwipeItemListener onSwipeItemListener;
    private Button btnDeleteItem;
    private View conversItem;

    public ConversationHolder(Context context, OnSwipeItemListener listener, View view) {
        super(view);
        onSwipeItemListener=listener;
        this.context=context;
        ivRecentAvatar=(ImageView)view.findViewById(R.id.iv_recent_avatar);
        tv_recent_name=(TextView) view.findViewById(R.id.tv_recent_name);
        tv_recent_msg=(TextView) view.findViewById(R.id.tv_recent_msg);
        tv_recent_time=(TextView) view.findViewById(R.id.tv_time);
        tv_recent_unread=(TextView) view.findViewById(R.id.tv_recent_unread );
        btnDeleteItem=(Button)view.findViewById(R.id.delete);
        conversItem=view.findViewById(R.id.rl_conversItem);

        conversItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeItemListener.onItemClick(getAdapterPosition());
            }
        });

        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeItemListener.onItemDelete(getAdapterPosition());
            }
        });

    }

    //@Override
    public void bindData(Object o) {
        if(o!=null){
            conversation=(PrivateConversation)o;
            tv_recent_name.setText(conversation.getConversationName());
            tv_recent_msg.setText(conversation.getLastMessageContent());
        }
    }

}
