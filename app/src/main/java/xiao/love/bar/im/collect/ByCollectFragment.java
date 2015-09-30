package xiao.love.bar.im.collect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xiao.love.bar.component.BaseFragment;

/**
 * Created by guochang on 2015/9/28.
 */
public class ByCollectFragment extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("test2");
        return tv;
    }
}
