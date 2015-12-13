package xiao.love.bar.adapters.viewholders.impl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.Click;

import xiao.love.bar.R;
import xiao.love.bar.adapters.impl.CollectAdapter;
import xiao.love.bar.adapters.viewholders.XGCRecyclerViewHolder;

/**
 * Created by xiaoguochang on 2015/12/7.
 */
public class CollectHolder extends XGCRecyclerViewHolder {

    public CollectHolder(Context context, CollectAdapter adapter, ViewGroup parent, View itemView) {
        super(context, parent, itemView);
        mAdapter = adapter;
    }

    @Override
    protected void initWidgets() {

    }



//    @Click(R.id.collect_img)
//    public void onCollect() {
//
//    }
}
