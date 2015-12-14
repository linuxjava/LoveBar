package xiao.love.bar.fragments.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.fragments.BaseFragment;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.presenter.BaseFragmentPresenter;

/**
 * Created by guochang on 2015/9/24.
 */
public class ContactFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.contact_layout;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    @OnClick(R.id.xiaoguochan1)
    public void onContact1(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", "xiaoguochang1");
        startActivity(intent);
    }

    @OnClick(R.id.xiaoguochan2)
    public void onContact2(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", "xiaoguochang2");
        startActivity(intent);
    }


}
