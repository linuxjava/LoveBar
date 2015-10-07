package xiao.love.bar.im.collect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import xiao.love.bar.R;

/**
 * Created by guochang on 2015/9/28.
 */
public class CollectAdapter extends BGARecyclerViewAdapter<Object> {

    public CollectAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView, R.layout.collect_item);
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
        viewHolderHelper.setItemChildClickListener(R.id.collect_img);
        viewHolderHelper.setItemChildClickListener(R.id.chat_img);
    }

    @Override
    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int i, Object o) {
        bgaViewHolderHelper.setText(R.id.name, o.toString());
    }
}
