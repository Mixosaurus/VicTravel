package cn.vic.travel.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.R;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/6/17.
 */

public class SendTextHolder extends BaseViewHolder{

    protected ImageView iv_avatar;
    protected ImageView iv_fail_resend;
    protected TextView tv_time;
    protected TextView tv_message;
    protected TextView tv_send_status;
    protected ProgressBar progress_load;

    BmobIMConversation conversation;

//    public SendTextHolder(View itemView) {
//        super(itemView);
//        iv_avatar=itemView.findViewById(R.id.iv_avatar);
//        iv_fail_resend=itemView.findViewById(R.id.iv_fail_resend);
//        tv_time=itemView.findViewById(R.id.tv_time);
//        tv_message=itemView.findViewById(R.id.tv_message);
//        tv_send_status=itemView.findViewById(R.id.tv_send_status);
//        progress_load=itemView.findViewById(R.id.progress_load);
//    }

    public SendTextHolder(Context context, BmobIMConversation c, OnRecyclerViewListener listener,View view) {
        super(context,listener,view);
        this.conversation =c;
        iv_avatar=itemView.findViewById(R.id.iv_avatar);
        iv_fail_resend=itemView.findViewById(R.id.iv_fail_resend);
        tv_time=itemView.findViewById(R.id.tv_time);
        tv_message=itemView.findViewById(R.id.tv_message);
        tv_send_status=itemView.findViewById(R.id.tv_send_status);
        progress_load=itemView.findViewById(R.id.progress_load);
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage)o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);     //加载头像
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        tv_message.setText(content);
        tv_time.setText(time);
        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SEND_FAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            if(progress_load==null||iv_fail_resend==null){
                debugToast("NULL");
            }
            else{
                iv_fail_resend.setVisibility(View.GONE);
                progress_load.setVisibility(View.VISIBLE);
            }

        } else {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }

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

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击" + info.getName() + "的头像");
            }
        });

        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversation.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progress_load.setVisibility(View.VISIBLE);
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_send_status.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if(e==null){
                            tv_send_status.setVisibility(View.VISIBLE);
                            tv_send_status.setText("已发送");
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        }else{
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            progress_load.setVisibility(View.GONE);
                            tv_send_status.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void toast(CharSequence s){
        Toast.makeText (VicApplication.getInstance(),s,Toast.LENGTH_LONG ).show ();
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
