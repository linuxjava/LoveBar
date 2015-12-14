package xiao.love.bar.adapters.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiao.love.bar.R;
import xiao.love.bar.adapters.XGCRecyclerViewAdapter;
import xiao.love.bar.adapters.viewholders.impl.CollectHolder;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏adapter
 */
public class CollectAdapter extends XGCRecyclerViewAdapter<Object, CollectHolder> {

    public CollectAdapter(Context context) {
        super(context);
    }

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
