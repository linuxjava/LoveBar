package xiao.love.bar.fragment.impl;

import xiao.love.bar.R;
import xiao.love.bar.fragment.BaseFragment;
import xiao.love.bar.presenter.BaseFragmentPresenter;

/**
 * Created by xiaoguochang on 2015/12/15.
 */
public class HomeFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.home_fg_layout;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }
}
