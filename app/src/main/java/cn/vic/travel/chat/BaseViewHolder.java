package cn.vic.travel.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Snake 创建于 2018/6/17.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public OnRecyclerViewListener onRecyclerViewListener;
    protected Context context;

    public BaseViewHolder(Context context, OnRecyclerViewListener listener,View view) {
        super(view);
        this.context=context;
        this.onRecyclerViewListener =listener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });

    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t);

    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getContext(), target);
        if (bundle != null)
            intent.putExtra(getContext().getPackageName(), bundle);
        getContext().startActivity(intent);
    }

}