package xiao.love.bar.im.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import xiao.love.bar.R;
import xiao.love.bar.component.BaseFragment;
import xiao.love.bar.im.chat.ChatActivity;

/**
 * Created by guochang on 2015/9/24.
 */
@EFragment(R.layout.contact_layout)
public class ContactFragment extends BaseFragment {
    @AfterViews
    void init(){

    }

    @Click(R.id.xiaoguochan1)
    public void onContact1(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", "xiaoguochang1");
        startActivity(intent);
    }

    @Click(R.id.xiaoguochan2)
    public void onContact2(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", "xiaoguochang2");
        startActivity(intent);
    }
}
