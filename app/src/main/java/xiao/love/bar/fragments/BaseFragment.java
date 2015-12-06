package xiao.love.bar.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        super.onDetach();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mRootView = mLayoutInflater.inflate(getFragmentLayout(), container, false);

        initWidgets();
        initEventHandlers();
        setupOthers();
        if (mPresenter != null) {
            mPresenter.attach(getActivity());
        }

        return mRootView;
    }


    protected abstract int getFragmentLayout();

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
