package xiao.love.bar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.adapters.viewholders.ViewParser;

/**
 * Created by xiaoguochang on 2015/12/4.
 * 通用的Adapter，实现了getCount、getItem等方法,以及封装了getView的逻辑
 */
public abstract class ListViewAdapter<T, H extends ViewParser> extends BaseAdapter {
    protected Context mContext = null;
    protected final List<T> mDataList = new ArrayList<T>();

    public ListViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if(getViewTypeCount() > 1){
                List<H> holders= createViewHolders();
                int itemViewType = getItemViewType(position);

                if(itemViewType >= 0 && itemViewType < holders.size()){
                    convertView = holders.get(itemViewType).inflate(mContext, parent, false);
                }else {
                    throw new RuntimeException("getItemViewType索引越界");
                }
            }else {
                convertView = createViewHolder().inflate(mContext, parent, false);
            }
        }
        H holder = (H) convertView.getTag();

        return convertView;
    }

    /**
     * 创建ViewHolder(viewItem视图类型相同)
     *
     * @return
     */
    protected abstract H createViewHolder();

    /**
     * 创建ViewHolder(viewItem视图类型不相同)
     *
     * @return
     */
    protected abstract List<H> createViewHolders();
}
