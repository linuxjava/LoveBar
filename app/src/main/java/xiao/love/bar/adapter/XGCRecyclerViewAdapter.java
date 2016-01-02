package xiao.love.bar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.adapter.viewholder.XGCRecyclerViewHolder;

/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewAdapter<M, VH extends XGCRecyclerViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext = null;
    /**
     * 数据
     */
    private List<M> mDatas = new ArrayList<M>();
    /**
     * 整个item的点击监听
     */
    protected XGCOnRVItemClickListener mOnRVItemClickListener;
    /**
     * 整个item的长按监听
     */
    protected XGCOnRVItemLongClickListener mOnRVItemLongClickListener;
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
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        Map<Integer, VH> holders = createViewHolders(mContext, parent, viewType);
//        VH holder = holders.get(viewType);
        VH holder = createViewHolder(mContext, parent, viewType);
        //设置监听回调
        holder.setOnRVItemClickListener(mOnRVItemClickListener);
        holder.setOnRVItemLongClickListener(mOnRVItemLongClickListener);
        holder.setOnItemChildClickListener(mOnItemChildClickListener);
        holder.setOnItemChildLongClickListener(mOnItemChildLongClickListener);
        holder.setOnItemChildCheckedChangeListener(mOnItemChildCheckedChangeListener);
        //初始化item监听事件
        setItemChildListener(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        //设置item数据
        setItemData(position, holder, getItem(position));
    }

    /**
     * 创建ViewHolder(viewItem视图类型不相同)
     * 包含不同类型视图时，需要为每一个视图构造一个ViewHolder
     *
     * @return Integer:视图的类型；VH：每个视图所对应的ViewHolder
     */
    //protected abstract Map<Integer, VH> createViewHolders(Context context, ViewGroup parent, int viewType);

    /**
     * 创建ViewHolder(viewItem视图类型相同)
     * 包含不同类型视图时，需要为每一个视图构造一个ViewHolder
     *
     * @return Integer:视图的类型；VH：每个视图所对应的ViewHolder
     */
    protected abstract VH createViewHolder(Context context, ViewGroup parent, int viewType);

    /**
     * 为item的孩子节点设置监听器，并不是每一个数据列表都要为item的子控件添加事件监听器，
     * 所以这里采用了空实现，需要设置事件监听器时重写该方法即可
     *
     * @param holder
     */
    protected abstract void setItemChildListener(VH holder);

    /**
     * 设置每项数据到View上
     *
     * @param position Item索引
     * @param holder   ViewHolder
     * @param model    数据实体
     */
    protected abstract void setItemData(int position, VH holder, M model);


    /**
     * 设置item的点击事件监听器
     *
     * @param onRVItemClickListener
     */
    public void setOnRVItemClickListener(XGCOnRVItemClickListener onRVItemClickListener) {
        mOnRVItemClickListener = onRVItemClickListener;
    }

    /**
     * 设置item的长按事件监听器
     *
     * @param onRVItemLongClickListener
     */
    public void setOnRVItemLongClickListener(XGCOnRVItemLongClickListener onRVItemLongClickListener) {
        mOnRVItemLongClickListener = onRVItemLongClickListener;
    }

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

    public M getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * 获取数据集合
     *
     * @return
     */
    public List<M> getDatas() {
        return mDatas;
    }

    /**
     * 在集合头部添加新的数据集合
     *
     * @param datas
     */
    public void addNewDatas(List<M> datas) {
        if (datas != null) {
            mDatas.addAll(0, datas);
            notifyItemRangeInserted(0, datas.size());
        }
    }

    /**
     * 在集合尾部添加更多数据集合
     *
     * @param datas
     */
    public void addMoreDatas(List<M> datas) {
        if (datas != null) {
            mDatas.addAll(mDatas.size(), datas);
            notifyItemRangeInserted(mDatas.size(), datas.size());
        }
    }

    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表
     *
     * @param datas
     */
    public void setDatas(List<M> datas) {
        if (datas != null) {
            mDatas = datas;
        } else {
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 删除指定索引数据条目
     *
     * @param position
     */
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除指定数据条目
     *
     * @param model
     */
    public void removeItem(M model) {
        removeItem(mDatas.indexOf(model));
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, M model) {
        mDatas.add(position, model);
        notifyItemInserted(position);
    }

    /**
     * 在集合头部添加数据条目
     *
     * @param model
     */
    public void addFirstItem(M model) {
        addItem(0, model);
    }

    /**
     * 在集合末尾添加数据条目
     *
     * @param model
     */
    public void addLastItem(M model) {
        addItem(mDatas.size(), model);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, M newModel) {
        mDatas.set(location, newModel);
        notifyItemChanged(location);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(M oldModel, M newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    /**
     * 移动数据条目的位置
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        mDatas.add(toPosition, mDatas.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}
