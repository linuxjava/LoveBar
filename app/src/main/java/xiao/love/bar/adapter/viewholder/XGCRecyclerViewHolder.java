package xiao.love.bar.adapter.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import xiao.love.bar.adapter.XGCOnItemChildCheckedChangeListener;
import xiao.love.bar.adapter.XGCOnItemChildClickListener;
import xiao.love.bar.adapter.XGCOnItemChildLongClickListener;
import xiao.love.bar.adapter.XGCOnRVItemClickListener;
import xiao.love.bar.adapter.XGCOnRVItemLongClickListener;
import xiao.love.bar.adapter.XGCRecyclerViewAdapter;
import xiao.love.bar.component.resource.ViewFinder;

/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    protected Context mContext;
    /**
     * 视图查找器
     */
    protected ViewFinder mViewFinder;
    /**
     * RecyclerViewAdapter
     */
    protected XGCRecyclerViewAdapter mAdapter;
    /**
     * ItemView视图的parent
     */
    protected ViewGroup mParentView;
    /**
     * ItemView视图
     */
    protected int mItemViewType;
    /**
     * ItemView视图
     */
    protected View mItemView;
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
    protected XGCOnItemChildClickListener mOnItemChildClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的长按回调监听
     */
    protected XGCOnItemChildLongClickListener mOnItemChildLongClickListener;
    /**
     * view页面activity或fragemnt对Item中子控件的选择回调监听
     */
    protected XGCOnItemChildCheckedChangeListener mOnItemChildCheckedChangeListener;

    public XGCRecyclerViewHolder(Context context, XGCRecyclerViewAdapter adapter, ViewGroup parent, View itemView, int viewType) {
        super(itemView);
        mContext = context;
        mParentView = parent;
        mItemViewType = viewType;
        mItemView = itemView;
        mAdapter = adapter;
        mItemView.setOnClickListener(this);
        mItemView.setOnLongClickListener(this);
        mViewFinder = new ViewFinder(mItemView);

        //绑定视图
        ButterKnife.bind(this, mItemView);

        initWidgets();
    }

    /**
     * 初始化各个子视图
     */
    protected abstract void initWidgets();

    public <T extends View> T findViewById(int id) {
        return mViewFinder.findViewById(id);
    }

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
     * 为id为viewId的item子控件设置点击事件监听器
     *
     * @param viewId
     */
    public void setItemChildClickListener(int viewId) {
        findViewById(viewId).setOnClickListener(this);
    }

    /**
     * 为id为viewId的item子控件设置长按事件监听器
     *
     * @param viewId
     */
    public void setItemChildLongClickListener(int viewId) {
        findViewById(viewId).setOnLongClickListener(this);
    }

    /**
     * 为id为viewId的item子控件设置选中状态变化事件监听器
     *
     * @param viewId
     */
    public void setItemChildCheckedChangeListener(int viewId) {
        if (findViewById(viewId) instanceof CompoundButton) {
            ((CompoundButton) findViewById(viewId)).setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnRVItemClickListener) {
            //整个item的点击回调
            mOnRVItemClickListener.onRVItemClick(mParentView, v, getAdapterPosition());
        } else {
            if (mOnItemChildClickListener != null) {
                mOnItemChildClickListener.onItemChildClick(mParentView, v, getAdapterPosition());
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnRVItemLongClickListener) {
            return mOnRVItemLongClickListener.onRVItemLongClick(mParentView, v, getAdapterPosition());
        } else {
            if (mOnItemChildLongClickListener != null) {
                return mOnItemChildLongClickListener.onItemChildLongClick(mParentView, v, getAdapterPosition());
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mOnItemChildCheckedChangeListener != null) {
            mOnItemChildCheckedChangeListener.onItemChildCheckedChanged(mParentView, buttonView, getAdapterPosition(), isChecked);
        }
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

    /**
     * 设置对应id的控件的文本内容
     *
     * @param viewId
     * @param text
     * @return
     */
    public void setText(int viewId, String text) {
        TextView view = findViewById(viewId);
        view.setText(text);
    }

    /**
     * 设置对应id的控件的文本内容
     *
     * @param viewId
     * @param stringResId 字符串资源id
     * @return
     */
    public void setText(int viewId, int stringResId) {
        TextView view = findViewById(viewId);
        view.setText(stringResId);
    }

    /**
     * 设置对应id的控件的html文本内容
     *
     * @param viewId
     * @param source html文本
     * @return
     */
    public void setHtml(int viewId, String source) {
        TextView view = findViewById(viewId);
        view.setText(Html.fromHtml(source));
    }

    /**
     * 设置对应id的控件的CharSequence
     *
     * @param viewId
     * @param text
     * @param type
     */
    public void setText(int viewId, CharSequence text, TextView.BufferType type) {
        TextView view = findViewById(viewId);
        view.setText(text, type);
    }

    /**
     * 设置对应id的控件是否选中
     *
     * @param viewId
     * @param checked
     * @return
     */
    public void setChecked(int viewId, boolean checked) {
        Checkable view = findViewById(viewId);
        view.setChecked(checked);
    }

    /**
     * 设置视图是否可见
     *
     * @param viewId
     * @param visibility
     */
    public void setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
    }

    /**
     * @param viewId
     * @param imageResId 图像资源id
     * @return
     */
    public void setImageResource(int viewId, int imageResId) {
        ImageView view = findViewById(viewId);
        view.setImageResource(imageResId);
    }

    /**
     * 设置ImageView的Bitmap
     *
     * @param viewId
     * @param bitmap
     */
    public void setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = findViewById(viewId);
        view.setImageBitmap(bitmap);
    }

    /**
     * 设置ImageView的Drawable
     *
     * @param viewId
     * @param drawable
     */
    public void setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = findViewById(viewId);
        view.setImageDrawable(drawable);
    }

    /**
     * @param viewId
     * @param textColorResId 颜色资源id
     * @return
     */
    public void setTextColorRes(int viewId, int textColorResId) {
        TextView view = findViewById(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorResId));
    }

    /**
     * @param viewId
     * @param textColor 颜色值
     * @return
     */
    public void setTextColor(int viewId, int textColor) {
        TextView view = findViewById(viewId);
        view.setTextColor(textColor);
    }

    /**
     * @param viewId
     * @param backgroundResId 背景资源id
     * @return
     */
    public void setBackgroundRes(int viewId, int backgroundResId) {
        View view = findViewById(viewId);
        view.setBackgroundResource(backgroundResId);
    }

    /**
     * @param viewId
     * @param color  颜色值
     * @return
     */
    public void setBackgroundColor(int viewId, int color) {
        View view = findViewById(viewId);
        view.setBackgroundColor(color);
    }

    /**
     * @param viewId
     * @param colorResId 颜色值资源id
     * @return
     */
    public void setBackgroundColorRes(int viewId, int colorResId) {
        View view = findViewById(viewId);
        view.setBackgroundColor(mContext.getResources().getColor(colorResId));
    }

    /**
     * 是否可点击
     *
     * @param viewId
     * @param b
     */
    public void setClickable(int viewId, boolean b) {
        View view = findViewById(viewId);
        view.setClickable(b);
    }
}
