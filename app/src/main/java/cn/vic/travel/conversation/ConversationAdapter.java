package cn.vic.travel.conversation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.vic.travel.R;
import cn.vic.travel.Utils;
import cn.vic.travel.chat.BaseViewHolder;
import cn.vic.travel.chat.OnRecyclerViewListener;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/7/29 21:00
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Conversation> conversations=new ArrayList<>();

    private OnSwipeItemListener onRecyclerViewListener;      //RecyclerView监听器

//    public ConversationAdapter(Context context) {
//        //super(context, datas);
//    }

    /**
     * 添加新消息
     * 添加前清空原链表
     * @param conversations
     */
    public void addMessages(List<Conversation> conversations) {
        this.conversations.clear();
        this.conversations.addAll(0, conversations);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        conversations.remove(position);
        notifyItemRemoved(position);
     }

    public Conversation getConversation(int position){
        return conversations.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation,parent,false);
        return new ConversationHolder(parent.getContext(),onRecyclerViewListener,view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ConversationHolder)holder).bindData(conversations.get(position));
        holder.itemView.setTag(position);
        //conversations.get(position).getConversationName();
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    //设置RecyclerView监听器
    public void setSwipeItemListener(OnSwipeItemListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }



}
