package xiao.love.bar.adapters.impl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import xiao.love.bar.R;
import xiao.love.bar.adapters.XGCRecyclerViewAdapter;
import xiao.love.bar.adapters.viewholders.XGCRecyclerViewHolder;
import xiao.love.bar.adapters.viewholders.impl.CollectHolder;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏adapter
 */
public class CollectAdapter extends XGCRecyclerViewAdapter<Object, CollectHolder>{

    public CollectAdapter(Context context) {
        super(context);
    }

//    @Override
//    protected Map<Integer, CollectHolder> createViewHolders(Context context, ViewGroup parent, int viewType) {
//        Map<Integer, CollectHolder> map = new HashMap<>();
//        View itemView = LayoutInflater.from(mContext).inflate(R.layout.collect_item, parent, false);
//        map.put(0, new CollectHolder(context, this, parent, itemView));
//
//        return map;
//    }

    @Override
    protected CollectHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.collect_item, parent, false);
        return new CollectHolder(context, this, parent, itemView);
    }

    @Override
    protected void setItemChildListener(CollectHolder holder) {
        holder.setItemChildClickListener(R.id.collect_img);
    }

    @Override
    protected void setItemData(int position, CollectHolder holder, Object model) {
        holder.setText(R.id.name, model.toString());
    }


}
//public class CollectAdapter extends BGARecyclerViewAdapter<Object> {
//
//    public CollectAdapter(Context context, RecyclerView recyclerView) {
//        super(recyclerView, R.layout.collect_item);
//    }
//
//    @Override
//    protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
//        viewHolderHelper.setItemChildClickListener(R.id.collect_img);
//        viewHolderHelper.setItemChildClickListener(R.id.chat_img);
//    }
//
//    @Override
//    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int i, Object o) {
//        bgaViewHolderHelper.setText(R.id.name, o.toString());
//    }
//}
