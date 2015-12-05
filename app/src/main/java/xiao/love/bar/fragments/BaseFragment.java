package xiao.love.bar.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;

import xiao.love.bar.presenter.BaseFragmentPresenter;

/**
 * Created by xiaoguochang on 2015/12/3.
 */
public abstract class BaseFragment <T, P extends BaseFragmentPresenter<T>> extends Fragment {
    /**
     * 该页面对应的Presenter
     */
    protected P mPresenter;

    @Override
    public void onDetach() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDetach();
    }

    void init(){
        if (mPresenter != null) {
            mPresenter.attach(getActivity());
        }

        initWidgets();
        initEventHandlers();
        setupOthers();
    }

    /**
     * 初始化子视图
     */
    protected void initWidgets() {

    }

    /**
     * 处理各种事件
     */
    protected void initEventHandlers() {

    }

    /**
     * 初始化其他设置
     */
    protected void setupOthers() {

    }

    /**
     * 创建该Fragment对应的Presenter
     *
     * @return
     */
    protected P createPresenter() {
        return null;
    }
}
