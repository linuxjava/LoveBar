package xiao.love.bar.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiao.love.bar.component.resource.ViewFinder;

/**
 * Created by xiaoguochang on 2015/12/4.
 */
public abstract class ViewHolder implements ViewParser{
    protected Context mContext;
    protected ViewFinder mViewFinder;
    protected View mItemView;

    @Override
    public View inflate(Context context, ViewGroup parent, boolean attachToRoot) {
        mContext = context;
        mItemView = LayoutInflater.from(context).inflate(getItemLayout(), parent, attachToRoot);
        // 设置tag
        mItemView.setTag(this);
        mViewFinder = new ViewFinder(mItemView);

        initWidgets();

        return mItemView;
    }

    /**
     * 获取ItemView的布局Id
     *
     * @return Item View布局
     */
    protected abstract int getItemLayout();

    /**
     * 初始化各个子视图
     */
    protected void initWidgets() {

    }

    public <T extends View> T findViewById(int id) {
        return mViewFinder.findViewById(id);
    }
}
