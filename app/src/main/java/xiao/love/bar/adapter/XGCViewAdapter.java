package xiao.love.bar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xiao.love.bar.adapter.viewholder.XGCViewHolder;

/**
 * Created by xiaoguochang on 2015/12/4.
 * 通用的Adapter，实现了getCount、getItem等方法,以及封装了getView的逻辑
 */
public abstract class XGCViewAdapter<T, H extends XGCViewHolder> extends BaseAdapter {
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

    public XGCViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if(getViewTypeCount() > 1){
                //不同类型item的处理
                Map<Integer, H> holders= createViewHolders();
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
        //设置itemview的position
        holder.setPosition(position);
        //设置监听回调
        holder.setOnItemChildClickListener(mOnItemChildClickListener);
        holder.setOnItemChildLongClickListener(mOnItemChildLongClickListener);
        holder.setOnItemChildCheckedChangeListener(mOnItemChildCheckedChangeListener);
        //设置item child事件
        setItemChildListener(holder);
        //设置item数据
        setItemData(position, holder, getItem(position));

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
     * 包含不同类型视图时，需要为每一个视图构造一个ViewHolder
     * @return Integer:视图的类型；H：每个视图所对应的ViewHolder
     */
    protected abstract Map<Integer, H> createViewHolders();

    /**
     * 为item的孩子节点设置监听器，并不是每一个数据列表都要为item的子控件添加事件监听器，
     * 所以这里采用了空实现，需要设置事件监听器时重写该方法即可
     *
     * @param holder
     */
    protected abstract void setItemChildListener(H holder);

    /**
     * 设置每项数据到View上
     *
     * @param position Item索引
     * @param holder ViewHolder
     * @param model 数据实体
     */
    protected abstract void setItemData(int position, H holder, T model);

    /**
     * 设置item子控件点击事件监听器
     * 用于回调activity或fragment的监听
     *
     * @param onItemChildClickListener
     */
    public void setOnItemChildClickListener(XGCOnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    /**
     * 设置item子控件长按事件监听器
     * 用于回调activity或fragment的监听
     *
     * @param onItemChildLongClickListener
     */
    public void setOnItemChildLongClickListener(XGCOnItemChildLongClickListener onItemChildLongClickListener) {
        mOnItemChildLongClickListener = onItemChildLongClickListener;
    }

    /**
     * 设置item子控件选中状态变化事件监听器
     * 用于回调activity或fragment的监听
     *
     * @param onItemChildCheckedChangeListener
     */
    public void setOnItemChildCheckedChangeListener(XGCOnItemChildCheckedChangeListener onItemChildCheckedChangeListener) {
        mOnItemChildCheckedChangeListener = onItemChildCheckedChangeListener;
    }

    /**
     * 获取数据集合
     *
     * @return
     */
    public List<T> getDatas() {
        return mDatas;
    }

    /**
     * 在集合头部添加新的数据集合
     *
     * @param datas
     */
    public void addNewDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(0, datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 在集合尾部添加更多数据集合（上拉从服务器获取更多的数据集合，例如新浪微博列表上拉加载更晚时间发布的微博数据）
     *
     * @param datas
     */
    public void addMoreDatas(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表（第一次从服务器加载数据，或者下拉刷新当前界面数据表）
     *
     * @param datas
     */
    public void setDatas(List<T> datas) {
        if (datas != null) {
            mDatas = datas;
        } else {
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, T model) {
        mDatas.add(position, model);
        notifyDataSetChanged();
    }

    /**
     * 在集合头部添加数据条目
     *
     * @param model
     */
    public void addFirstItem(T model) {
        addItem(0, model);
    }

    /**
     * 在集合末尾添加数据条目
     *
     * @param model
     */
    public void addLastItem(T model) {
        addItem(mDatas.size(), model);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, T newModel) {
        mDatas.set(location, newModel);
        notifyDataSetChanged();
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(T oldModel, T newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    /**
     * 交换两个数据条目的位置
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mDatas, fromPosition, toPosition);
        notifyDataSetChanged();
    }

    /**
     * 删除指定索引数据条目
     *
     * @param position
     */
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 删除指定数据条目
     *
     * @param model
     */
    public void removeItem(T model) {
        mDatas.remove(model);
        notifyDataSetChanged();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }
}
