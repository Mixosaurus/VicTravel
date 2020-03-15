package cn.vic.travel.traveltogether;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.vic.travel.R;
import cn.vic.travel.chat.OnRecyclerViewListener;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/8/10 10:18
 * 下拉刷新实现
 */
public class TravelInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TravelInfo> travelInfos = new ArrayList<>();
    private static final int TYPE_ITEM = 0;       //普通item
    private static final int TYPE_FOOTER = 1;       //footer
    private int mLoadMoreStatus = 0;        //上拉加载更多状态-默认为0
    public static final int PULLUP_LOAD_MORE = 0;   //上拉加载更多
    public static final int LOADING_MORE = 1;   //正在加载中
    public static final int NO_LOAD_MORE = 2;   //没有加载更多 隐藏
    private int latestInfoSize;         //最新的数据长度
    private int INFO_LIMIT;             //单次最大数据量
    private OnRecyclerViewListener onRecyclerViewListener;      //RecyclerView监听器

    /**
     * 返回最新的数据长度，该长度为最新查询结果去掉重复数据数的长度
     * @return 最新的数据长度
     */
    public int getLatestInfoSize() {
        return latestInfoSize;
    }

    /**
     *设置数据最大长度
     */
    public void setInfoLimit(int InfoLimit) {
        this.INFO_LIMIT = InfoLimit;
    }


    public TravelInfo getTravelInfo(int position) {
        return travelInfos.get(position);
    }

    public void clearMessages() {
        travelInfos.clear();
    }

    //删除重复数据
    public void deleteRepeatedMessage(List<TravelInfo> travelInfosList){
        int pristineListSize= travelInfosList.size();
        int i=0;
        for(TravelInfo travelInfo:travelInfosList){
            if(travelInfos.contains(travelInfo)){
                travelInfos.remove(travelInfo);
                i++;
            }
        }
        latestInfoSize =pristineListSize-i;
    }

    /**
     * 批量添加数据
     *
     * @param travelInfos 数据
     */
    public void addMessages(List<TravelInfo> travelInfos) {
        this.travelInfos.addAll(travelInfos);
        notifyDataSetChanged();
    }

    /**
     * 在指定位置添加数据
     *
     * @param travelInfosList 数据
     * @param index       索引
     */
    public void addMessagesToStart(List<TravelInfo> travelInfosList, int index) {
        this.travelInfos.addAll(index, travelInfosList);
        notifyDataSetChanged();
    }

    /**
     * 单个添加数据
     *
     * @param travelInfo 消息
     */
    public void addMessage(TravelInfo travelInfo) {
        this.travelInfos.add(travelInfo);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount() && getItemCount() >= INFO_LIMIT) {
            return TYPE_FOOTER;     //若当前数据量大于或等于INFO_LIMIT时将最后一个item设置为footerView
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_info, parent, false);
                return new TravelInfoHolder(parent.getContext(),onRecyclerViewListener, view);
            }
            case TYPE_FOOTER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_footer, parent, false);
                return new FooterHolder(parent.getContext(), view);
            }
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TravelInfoHolder) {
            TravelInfo travelInfo = travelInfos.get(position);
            ((TravelInfoHolder) holder).bindData(travelInfo);
        }
        else if (holder instanceof FooterHolder) {
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    ((FooterHolder) holder).setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    ((FooterHolder) holder).setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    ((FooterHolder) holder).hideLoadingBar();        //隐藏加载更多
                    break;
            }
        }
    }


    //设置RecyclerView监听器
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @Override
    public int getItemCount() {
        return travelInfos.size();
    }


    /**
     * 更新加载更多状态
     *
     * @param status 更新状态
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }


}
