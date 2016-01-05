package xiao.love.bar.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import xiao.love.bar.mvppresenter.BaseFragmentPresenter;

/**
 * Created by xiaoguochang on 2015/12/3.
 */
public abstract class BaseFragment<T, P extends BaseFragmentPresenter<T>> extends Fragment {
    protected Activity mContext;
    /**
     * 该页面对应的Presenter
     */
    protected P mPresenter;
    /**
     * 布局加载LayoutInflater
     */
    protected LayoutInflater mLayoutInflater;
    /**
     * 根视图
     */
    protected View mRootView;

    @Override
    public void onDetach() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        //解绑视图
        ButterKnife.unbind(this);
        super.onDetach();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mRootView = mLayoutInflater.inflate(getLayout(), container, false);

        mContext = getActivity();
        //绑定视图
        ButterKnife.bind(this, mRootView);
        initWidgets();
        if (mPresenter != null) {
            mPresenter.attach(mContext);
        }

        return mRootView;
    }

    /**
     * 视图layout资源
     *
     * @return
     */
    protected abstract int getLayout();

    /**
     * 初始化子视图
     */
    protected abstract void initWidgets();

    /**
     * 创建该Fragment对应的Presenter
     *
     * @return
     */
    protected abstract P createPresenter();

    protected View getView(int id){
        return mRootView.findViewById(id);
    }
}
