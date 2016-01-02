package xiao.love.bar.fragment.main;

import android.content.Intent;

import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.fragment.BaseFragment;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.mvppresenter.BaseFragmentPresenter;

/**
 * Created by guochang on 2015/9/24.
 */
public class ContactFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_contact;
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
