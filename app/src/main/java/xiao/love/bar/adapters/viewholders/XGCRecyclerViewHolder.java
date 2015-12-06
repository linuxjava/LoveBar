package xiao.love.bar.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import xiao.love.bar.adapters.XGCOnItemChildCheckedChangeListener;
import xiao.love.bar.adapters.XGCOnItemChildClickListener;
import xiao.love.bar.adapters.XGCOnItemChildLongClickListener;
import xiao.love.bar.component.resource.ViewFinder;

/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewHolder extends RecyclerView.ViewHolder{
    protected Context mContext;
    /**
     * 视图查找器
     */
    protected ViewFinder mViewFinder;
    /**
     * ItemView视图的parent
     */
    protected ViewGroup mParentView;
    /**
     * ItemView视图
     */
    protected View mItemView;
    /**
     * view页面activity或fragemnt对Item中子控件的点击回调监听
     */
    protected XGCOnItemChildClickListener mOnItemChildClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的长按回调监听
     */
    protected XGCOnItemChildLongClickListener mOnItemChildLongClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的选择回调监听
     */
    protected XGCOnItemChildCheckedChangeListener mOnItemChildCheckedChangeListener;
    /**
     * item所在的位置
     */
    protected int mPosition;

    public XGCRecyclerViewHolder(View itemView) {
        super(itemView);
    }


    public View inflate(Context context, ViewGroup parent, boolean attachToRoot) {
        return null;
    }


}
