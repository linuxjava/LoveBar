package xiao.love.bar.adapter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiao.love.bar.R;
import xiao.love.bar.adapter.XGCRecyclerViewAdapter;
import xiao.love.bar.adapter.viewholder.impl.CollectHolder;
import xiao.love.bar.entity.TestData;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏adapter
 */
public class CollectAdapter extends XGCRecyclerViewAdapter<TestData, CollectHolder> {

    public CollectAdapter(Context context) {
        super(context);
    }

    @Override
    protected CollectHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_collect, parent, false);
        return new CollectHolder(context, this, parent, itemView, viewType);
    }

    @Override
    protected void setItemChildListener(CollectHolder holder) {
        holder.setItemChildClickListener(R.id.img_collect);
    }

    @Override
    protected void setItemData(int position, CollectHolder holder, TestData model) {
        holder.setText(R.id.text_name, model.content);
    }
}
