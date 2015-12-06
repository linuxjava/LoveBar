package xiao.love.bar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewHolder;
import xiao.love.bar.adapters.viewholders.XGCRecyclerViewHolder;

/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewAdapter<T, H extends XGCRecyclerViewHolder> extends RecyclerView.Adapter<XGCRecyclerViewHolder>{
    protected Context mContext = null;
    /**
     * 数据
     */
    private List<T> mDatas = new ArrayList<T>();
    /**
     * view页面activity或fragemnt对Item中子控件的点击回调监听
     */
    private XGCOnItemChildClickListener mOnItemChildClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的长按回调监听
     */
    private XGCOnItemChildLongClickListener mOnItemChildLongClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的选择回调监听
     */
    private XGCOnItemChildCheckedChangeListener mOnItemChildCheckedChangeListener;

    public XGCRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public XGCRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(XGCRecyclerViewHolder holder, int position) {

    }

    /**
     * 创建ViewHolder(viewItem视图类型相同)
     *
     * @return
     */
    protected abstract H createViewHolder();

    /**
     * 创建ViewHolder(viewItem视图类型不相同)
     * 包含不同类型视图时，需要为每一个视图构造一个ViewHolder
     * @return Integer:视图的类型；H：每个视图所对应的ViewHolder
     */
    protected abstract Map<Integer, H> createViewHolders();
}
