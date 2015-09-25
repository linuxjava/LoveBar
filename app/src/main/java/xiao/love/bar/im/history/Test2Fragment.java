package xiao.love.bar.im.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xiao.love.bar.component.BaseFragment;

/**
 * Created by guochang on 2015/9/24.
 */
public class Test2Fragment extends BaseFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView v = new TextView(getActivity());
        v.setText("test2");

        return v;
    }
}
