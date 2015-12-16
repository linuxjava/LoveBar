package xiao.love.bar.fragments.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xiao.love.bar.R;
import xiao.love.bar.fragments.BaseFragment;
import xiao.love.bar.presenter.BaseFragmentPresenter;


/**
 * Created by guochang on 2015/9/24.
 */
public class MyFragment extends BaseFragment{

    @Override
    protected int getLayout() {
        return R.layout.my_fg_layout;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }
}
