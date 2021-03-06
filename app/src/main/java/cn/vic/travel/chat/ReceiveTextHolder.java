package cn.vic.travel.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.vic.travel.R;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/6/18.
 */

public class ReceiveTextHolder extends BaseViewHolder {

    protected ImageView iv_avatar;
    protected TextView tv_time;
    protected TextView tv_message;

    public ReceiveTextHolder(Context context, OnRecyclerViewListener onRecyclerViewListener,View view) {
        super(context,onRecyclerViewListener,view);
        iv_avatar=view.findViewById(R.id.iv_avatar);
        tv_time=view.findViewById(R.id.tv_time);
        tv_message=view.findViewById(R.id.tv_message);

    }

//    public ReceiveTextHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
//        super(context, root, R.layout.item_chat_received_message,onRecyclerViewListener);
//    }

    @OnClick(R.id.iv_avatar)
    public void onAvatarClick(View view) {

    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage)o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        tv_time.setText(time);
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
        String content =  message.getContent();
        tv_message.setText(content);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info==null){
                    toast("由message获得的用户信息为空");
                    return;
                }
                toast("点击" + info.getName() + "的头像");
            }
        });
        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击"+message.getContent());
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });
        tv_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });
    }

    private void toast(CharSequence text){
        Toast.makeText (VicApplication.getInstance(),text,Toast.LENGTH_LONG ).show ();
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}