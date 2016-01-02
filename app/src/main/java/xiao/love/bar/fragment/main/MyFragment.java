package xiao.love.bar.fragment.main;

import xiao.love.bar.R;
import xiao.love.bar.fragment.BaseFragment;
import xiao.love.bar.mvppresenter.BaseFragmentPresenter;


/**
 * Created by guochang on 2015/9/24.
 */
public class MyFragment extends BaseFragment{

    @Override
    protected int getLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }
}
